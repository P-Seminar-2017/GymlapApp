package de.gymnasium_lappersdorf.gymlapapp.Home

import android.content.Context
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException

data class Article(
        val title: String,
        val text: String,
        val date: String,
        val href: String,
        val img: String
)

/*
* Object that handles all the downloading, caching and webscraping
* for the articles on the webpage
* */
object Articles {
    //webpage to be used for scraping
    const val URL = "https://www.gymnasium-lappersdorf.de/home/"
    //currently loaded list of articles
    private var articles: List<Article> = emptyList()
    //if the cache has already been refreshed
    private var updated: Boolean = false

    /*
    * initializes the object and calls [onRecieveArticles],
    * when cached or downloaded articles are avaliable
    * needs to be called first
    * */
    suspend fun initialize(context: Context, onRecieveArticles: (List<Article>) -> Unit) {
        if (!updated) {
            val deferred1 = GlobalScope.async(Dispatchers.Main) {
                onRecieveArticles(getCached(context))
            }
            val deferred2 = GlobalScope.async(Dispatchers.Main) {
                onRecieveArticles(getDownload(URL, context))
            }
            deferred1.await()
            deferred2.await()
            updated = true
        } else {
            onRecieveArticles(articles)
        }
    }

    /*
    * returns scraped articles from the cache
    * */
    private suspend fun getCached(context: Context): List<Article> {
        //site can be cached in shared preferences, because it is just one string
        val deferred = GlobalScope.async { readCache(context) }
        return scrape(deferred.await()).also { articles = it }
    }

    /*
    * returns scraped articles form the web and stores them in the cache
    * */
    suspend fun getDownload(url: String = URL, context: Context): List<Article> {
        val deferred = GlobalScope.async {
            val result: String
            try {
                result = Jsoup.connect(url).get().body().toString()
                writeCache(result, context)
                scrape(result).also { articles = it }
            } catch (e: IOException) {
                scrape(readCache(context))
            }
        }
        return deferred.await()
    }

    /*
    * reads the stored webpage from the cache
    * */
    private fun readCache(context: Context): String {
        //shared preferences can be used here, because just one string gets stored anyway
        val prefs = context.getSharedPreferences("home_cache", Context.MODE_PRIVATE)
        return prefs.getString("site_cache", "*CACHE_EMPTY*")
    }

    /*
    * writes [input] to the webpage cache
    * */
    private fun writeCache(input: String, context: Context) {
        //shared preferences can be used here, because just one string gets stored anyway
        val prefs = context.getSharedPreferences("home_cache", Context.MODE_PRIVATE)
        prefs.edit().putString("site_cache", input).apply()
    }

    /*
    * scrapes the article-information of a [input] string
    * should only be used for https://www.gymnasium-lappersdorf.de/home/
    * */
    private fun scrape(input: String): List<Article> {
        fun getFirstByClass(input: Element, filter: String): Element {
            return input.getElementsByClass(filter)[0]
        }

        fun getFirstByTag(input: Element, filter: String): Element {
            return input.getElementsByTag(filter)[0]
        }

        val articles = emptyList<Article>().toMutableList()
        //scraping for https://www.gymnasium-lappersdorf.de/home/
        val doc = Jsoup.parse(input)

        //all articles:
        val elements = doc.getElementsByClass("article")

        for (element in elements) {
            //title
            val title = getFirstByTag(getFirstByClass(element, "header"), "a").attr("title")
            //text:
            val text = getFirstByClass(element, "teaser-text").getElementsByAttribute("itemprop").text()
            //date:
            val date = getFirstByTag(getFirstByClass(element, "news-list-date"), "time").text()
            //input
            val href =
                    "https://www.gymnasium-lappersdorf.de" + getFirstByTag(getFirstByClass(element, "header"), "a").attr("href")
            //img
            val img = "https://www.gymnasium-lappersdorf.de" + getFirstByTag(
                    getFirstByClass(element, "news-img-wrap"),
                    "img"
            ).attr("src")
            articles.add(Article(title, text, date, href, img))
        }
        return articles
    }

}
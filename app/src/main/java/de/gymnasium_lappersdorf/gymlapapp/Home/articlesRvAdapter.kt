package de.gymnasium_lappersdorf.gymlapapp.Home

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.newsitemview.view.*

class ArticlesRvAdapter(private var dataset: List<Article>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    fun setDataset(dataset: List<Article>) {
        this.dataset = dataset
    }

    override fun getItemCount(): Int = dataset.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.newsitemview, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //set view properties
        holder.articleView.title_home.text = dataset[position].title
        holder.articleView.text_home.text = dataset[position].text
        holder.articleView.date_home.text = dataset[position].date
        //set chrome-custom-tab for onClick
        holder.articleView.newsitem_home.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(context.resources.getColor(R.color.colorPrimary))
                    .enableUrlBarHiding()
                    .setShowTitle(true)
                    .setCloseButtonIcon(BitmapFactory.decodeResource(context.resources, R.drawable.back))
            builder.build().launchUrl(context, Uri.parse(dataset[position].href))
        }
        //set image with glide
        Glide.with(context)
                .load(dataset[position].img)
                .apply(RequestOptions()
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.imagebroken))
                .into(holder.articleView.img_home)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val articleView = view
}
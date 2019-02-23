package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import de.gymnasium_lappersdorf.gymlapapp.R
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.DatabaseHandler
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.*


/**
 * 07.12.2018 | created by Lukas S
 */
class HausaufgabenOnlineFragment : Fragment() {

    private val klassenArray = arrayOf("Alle", "a", "b", "c", "d", "e")

    private var homeworkList: ArrayList<Hausaufgabe>? = null

    private lateinit var v: View
    private var countLabel: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var homeworkRvAdapter: HomeworkRvAdapter? = null

    //Internet
    private var jsonHandler: JsonHandlerHomework? = null
    private var onlineSQLHandlerHomework: OnlineSQLHandlerHomework? = null
    private var snackbarConn: Snackbar? = null
    private var refreshLayout: SwipeRefreshLayout? = null

    private var stufe: Int = 0
    private lateinit var klasse: String

    //Database
    private var dbh: HausaufgabenDatabaseHandler? = null
    private lateinit var databaseHandler: DatabaseHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_homework_tab_online, container, false)
        return v
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = v.findViewById<View>(R.id.homework_rv) as RecyclerView

        homeworkRvAdapter = HomeworkRvAdapter(arrayOfNulls(0), activity, this, HomeworkRvAdapter.DatasetChangeListener { h ->
            val index = homeworkList!!.indexOf(h)
            homeworkList!![index].notificationId = h.notificationId
            dbh!!.updateHomework(homeworkList!![index])
        })
        recyclerView!!.adapter = homeworkRvAdapter

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = linearLayoutManager

        countLabel = v.findViewById<View>(R.id.homework_count_label) as TextView

        snackbarConn = Snackbar.make(v, "Keine Verbindung", Snackbar.LENGTH_INDEFINITE)

        refreshLayout = v.findViewById(R.id.swiperefresh_homework)
        refreshLayout!!.setOnRefreshListener { initDownload() }

        databaseHandler = DatabaseHandler()

        loadFilter()

        updateDataset()
    }

    private fun updateDataset() {
        dbh = HausaufgabenDatabaseHandler(activity)
        homeworkList = ArrayList()

        if (dbh!!.homeworkCount > 0) {
            //load content of db
            val hw_db = dbh!!.completeHomework

            Collections.addAll(homeworkList, *hw_db)

            //Removing all local homeworkList
            val it = homeworkList!!.iterator()
            var temp: Hausaufgabe
            while (it.hasNext()) {
                temp = it.next()
                if (!temp.isFromInternet) {
                    it.remove()
                }
            }
        }

        if (homeworkList!!.size == 0) initDownload()

        filter(stufe, klasse)
    }

    private fun updateLabel() {
        val count = homeworkRvAdapter!!.itemCount

        if (klasse == klassenArray[0])
            countLabel!!.text = "${(if (count == 0) "Kein" else homeworkRvAdapter!!.itemCount)}${if (homeworkRvAdapter!!.itemCount > 1) " Einträge" else " Eintrag"} für die $stufe. Klasse"
        else
            countLabel!!.text = "${(if (count == 0) "Kein" else homeworkRvAdapter!!.itemCount)}${if (homeworkRvAdapter!!.itemCount > 1) " Einträge" else " Eintrag"} für $stufe $klasse"
    }

    fun filter(stufe: Int, klasse: String) {
        val temp_new_items = ArrayList<Hausaufgabe>()
        this.stufe = stufe
        this.klasse = klasse

        for (i in homeworkList!!.indices) {

            if (klasse == klassenArray[0]) {
                //Alle sind ausgewählt

                if (stufe == homeworkList!![i].stufe && !homeworkList!![i].isDone) {
                    temp_new_items.add(homeworkList!![i])
                }

            } else {
                //Es wird pro klasse unterschieden

                if (stufe == homeworkList!![i].stufe && klasse == homeworkList!![i].kurs && !homeworkList!![i].isDone) {
                    temp_new_items.add(homeworkList!![i])
                }
            }

        }

        homeworkRvAdapter!!.dataset = temp_new_items.toTypedArray()
        homeworkRvAdapter!!.notifyDataSetChanged()
        updateLabel()
    }

    private fun initDownload() {
        if (isNetworkConnected(activity!!)) {
            refreshLayout!!.isRefreshing = true
            onlineSQLHandlerHomework = OnlineSQLHandlerHomework("http://api.gymlap.de/request.php", OnlineSQLHandlerHomework.RequestTypes.ALL, OnlineSQLHandlerHomework.SQLCallback { jsonHandler ->
                this@HausaufgabenOnlineFragment.jsonHandler = jsonHandler
                processData()
            })

            onlineSQLHandlerHomework!!.execute()
        } else {
            snackbarConn!!.show()
            refreshLayout!!.isRefreshing = false
        }
    }

    private fun processData() {

        if (jsonHandler!!.getSuccess()) {

            for (i in 0 until jsonHandler!!.length) {
                val hw_type: Hausaufgabe.Types = when (jsonHandler!!.getType(i)) {
                    "DATE" -> Hausaufgabe.Types.DATE
                    "NEXT" -> Hausaufgabe.Types.NEXT
                    "NEXT2" -> Hausaufgabe.Types.NEXT2
                    else -> Hausaufgabe.Types.DATE
                }
                val neu: Hausaufgabe

                //Text from database is URI encoded
                var text = jsonHandler!!.getText(i)
                var fach = jsonHandler!!.getFach(i)
                try {
                    text = URLDecoder.decode(text.replace("+", "%2B"), "UTF-8").replace("%2B", "+")
                    fach = URLDecoder.decode(fach.replace("+", "%2B"), "UTF-8").replace("%2B", "+")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }

                neu = Hausaufgabe(
                        fach,
                        text,
                        jsonHandler!!.getDate(i),
                        Integer.parseInt(jsonHandler!!.getKlasse(i)),
                        jsonHandler!!.getStufe(i),
                        hw_type)

                neu.internetId = jsonHandler!!.getID(i)

                if (homeworkList!!.indexOf(neu) != -1) {
                    //Homework alredy exists -> update it
                    val index = homeworkList!!.indexOf(neu)
                    neu.databaseId = homeworkList!![index].databaseId
                    neu.internetId = homeworkList!![index].internetId
                    neu.isDone = homeworkList!![index].isDone

                    homeworkList!!.removeAt(index)
                    homeworkList!!.add(neu)
                    //Updating database
                    dbh!!.updateHomework(neu)
                } else {
                    //Add new homework to database
                    val id = dbh!!.addHomework(neu)
                    neu.databaseId = id
                    homeworkList!!.add(neu)
                }

            }

            //Removing all internet homeworkList that aren't mentioned in the new data
            val it = homeworkList!!.iterator()
            var temp: Hausaufgabe
            while (it.hasNext()) {
                temp = it.next()
                if (!jsonHandler!!.contains(temp) && !temp.isFromInternet) {
                    it.remove()
                    dbh!!.deleteHomework(temp)
                }
            }
            filter(stufe, klasse)
        } else {
            //No success
            Toast.makeText(activity, "${jsonHandler!!.errorCode}: ${jsonHandler!!.error}", Toast.LENGTH_SHORT).show()
        }

        snackbarConn!!.dismiss()
        refreshLayout!!.isRefreshing = false
    }

    //checks for internet connection
    private fun isNetworkConnected(context: Context): Boolean {
        val con = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = con.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun hideSnackbarIfShown() {
        if (snackbarConn != null)
            if (snackbarConn!!.isShown)
                snackbarConn!!.dismiss()
    }

    fun showSnackbarIfNoNetwork() {
        if (!isNetworkConnected(activity!!)) snackbarConn!!.show()
    }

    private fun loadFilter() {
        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
        stufe = sharedPref.getInt(getString(R.string.shared_pref_filter_stufe), 5)
        klasse = sharedPref.getString(getString(R.string.shared_pref_filter_klasse), klassenArray[0])
                ?: klassenArray[0]
    }

}

package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner


import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.TextInputEditText
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.ArrayList
import java.util.Collections

import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * 07.12.2018 | created by Lukas S
 */
class HausaufgabenOnlineFragment : HausaufgabenTabFragment() {

    //Internet
    private var jsonHandler: JsonHandlerHomework? = null
    private var onlineSQLHandlerHomework: OnlineSQLHandlerHomework? = null
    private var snackbarConn: Snackbar? = null
    private var snackBarAPIKey: Snackbar? = null
    private var refreshLayout: SwipeRefreshLayout? = null

    //Dialog API Key
    private var apikeyDialog: AlertDialog? = null
    private var keyInput: TextInputEditText? = null

    fun setRefreshLayout(refreshLayout: SwipeRefreshLayout) {
        this.refreshLayout = refreshLayout
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        createAPIKeyDialog()

        recyclerView = v.findViewById<View>(R.id.homework_rv) as RecyclerView

        homeworkRvAdapter = HomeworkRvAdapter(arrayOfNulls(0), activity, HomeworkRvAdapter.DatasetChangeListener { h ->
            val index = homeworkList!!.indexOf(h)
            homeworkList!![index].notificationId = h.notificationId
            dbh!!.updateHomework(homeworkList!![index])
        })
        recyclerView!!.adapter = homeworkRvAdapter

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = linearLayoutManager

        countLabel = v.findViewById<View>(R.id.homework_count_label) as TextView

        fab = v.findViewById<View>(R.id.add_homework_fab) as FloatingActionButton
        fab!!.visibility = View.INVISIBLE
        fabContainer = v.findViewById<View>(R.id.fab_container) as CoordinatorLayout

        snackbarConn = Snackbar.make(fabContainer!!, "Keine Verbindung", Snackbar.LENGTH_INDEFINITE)

        snackBarAPIKey = Snackbar.make(fabContainer!!, "Offline Modus", Snackbar.LENGTH_INDEFINITE)
        snackBarAPIKey!!.setAction("Online") { showAPIKeyDialog() }

        stufe = 5
        klasse = klassenArray[0]

        updateDataset()
        updateLabel()
    }

    override fun updateDataset() {
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

    //creating dialog once for better performance
    private fun createAPIKeyDialog() {
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.homework_api_key_dialog, null)

        keyInput = dialogView.findViewById(R.id.homework_input_api_key)

        val builder = AlertDialog.Builder(v.context)
        builder.setTitle("API Schlüssel eintragen")

        builder.setView(dialogView)

        builder.setPositiveButton("OK") { dialog, which ->
            saveAPIKey(keyInput!!.text!!.toString())
            initDownload()
        }
        builder.setNegativeButton("Zurück") { dialog, which ->
            dialog.cancel()
            initDownload()
        }

        builder.setOnCancelListener { initDownload() }

        apikeyDialog = builder.create()
    }

    private fun showAPIKeyDialog() {
        //Default value
        val defKey = loadAPIKey()
        if (defKey != null) keyInput!!.setText(defKey)

        apikeyDialog!!.show()
    }

    fun initDownload() {
        if (isNetworkConnected(activity!!)) {
            refreshLayout!!.isRefreshing = true
            onlineSQLHandlerHomework = OnlineSQLHandlerHomework(loadAPIKey(), "http://api.lakinator.bplaced.net/request.php", OnlineSQLHandlerHomework.RequestTypes.ALL, OnlineSQLHandlerHomework.SQLCallback { jsonHandler ->
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
                val hw_type: Hausaufgabe.Types
                val neu: Hausaufgabe

                when (jsonHandler!!.getType(i)) {
                    "DATE" -> hw_type = Hausaufgabe.Types.DATE
                    "NEXT" -> hw_type = Hausaufgabe.Types.NEXT
                    "NEXT2" -> hw_type = Hausaufgabe.Types.NEXT2
                    else -> hw_type = Hausaufgabe.Types.DATE
                }

                //TODO if "next" or "next2" -> calculate time with values from stundenplan

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
                        Hausaufgabe.Types.DATE)

                neu.internetId = jsonHandler!!.getID(i)

                if (homeworkList!!.indexOf(neu) != -1) {
                    //Homework alredy exists -> update it
                    val index = homeworkList!!.indexOf(neu)
                    neu.databaseId = homeworkList!![index].databaseId
                    neu.internetId = homeworkList!![index].internetId
                    neu.isDone = homeworkList!![index].isDone

                    homeworkList!![index] = neu
                    //Updating database
                    dbh!!.updateHomework(homeworkList!![index])
                } else {
                    homeworkList!!.add(neu)

                    //Add new homework to database
                    val id = dbh!!.addHomework(homeworkList!![i])
                    homeworkList!![i].databaseId = id
                }

            }

            //Removing all internet homeworkList that aren't mentioned in the new data
            val it = homeworkList!!.iterator()
            var temp: Hausaufgabe
            while (it.hasNext()) {
                temp = it.next()
                if (!jsonHandler!!.contains(temp) && temp.isFromInternet) {
                    it.remove()
                    dbh!!.deleteHomework(temp)
                }
            }

            snackBarAPIKey!!.dismiss()
            filter(stufe, klasse)
        } else {
            //No success
            snackBarAPIKey!!.show()
        }

        snackbarConn!!.dismiss()
        refreshLayout!!.isRefreshing = false
    }

    private fun saveAPIKey(key: String) {
        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.shared_pref_api), key)
        editor.apply()
    }

    private fun loadAPIKey(): String? {
        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
        //default value is null !!
        return sharedPref.getString(getString(R.string.shared_pref_api), null)
    }

    companion object {

        //checks for internet connection
        fun isNetworkConnected(context: Context): Boolean {
            val con = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = con.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }
    }

}

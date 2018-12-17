package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.TextView

import java.util.ArrayList
import java.util.Collections

import de.gymnasium_lappersdorf.gymlapapp.R

import android.app.Activity.RESULT_OK

/**
 * 07.12.2018 | created by Lukas S
 */
class HausaufgabenLokalFragment : HausaufgabenTabFragment() {

    private val REQUEST_ID = 1

    private var itemTouchHelper: ItemTouchHelper? = null

    private var snackbarRevert: Snackbar? = null

    //Recycler swipe removing
    private var lastItem: Hausaufgabe? = null
    private var lastItemPosition = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        recyclerView = v.findViewById<View>(R.id.homework_rv) as RecyclerView

        homeworkRvAdapter = HomeworkRvAdapter(arrayOfNulls(0), activity, HomeworkRvAdapter.DatasetChangeListener { h ->
            val index = homeworks!!.indexOf(h)
            homeworks!![index].notificationId = h.notificationId
            dbh!!.updateHomework(homeworks!![index])
        })
        recyclerView!!.adapter = homeworkRvAdapter

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView!!.layoutManager = linearLayoutManager

        countLabel = v.findViewById<View>(R.id.homework_count_label) as TextView


        fab = v.findViewById<View>(R.id.add_homework_fab) as FloatingActionButton
        fab!!.setOnClickListener {
            val i = Intent(activity, AddHomeworkActivity::class.java)
            i.putExtra("STUFE", stufe)
            i.putExtra("KLASSE", if (klasse === klassenArray[0]) "a" else klasse)
            startActivityForResult(i, REQUEST_ID)
        }
        fabContainer = v.findViewById<View>(R.id.fab_container) as CoordinatorLayout

        snackbarRevert = Snackbar.make(fabContainer!!, "Hausaufgabe erledigt", Snackbar.LENGTH_LONG)
        snackbarRevert!!.setAction("Rückgängig") {
            if (lastItem != null) {
                val pos = homeworks!!.indexOf(lastItem!!)
                homeworks!![pos].isDone = false
                homeworkRvAdapter!!.restoreItem(homeworks!![pos], lastItemPosition)
                updateLabel()
                recyclerView!!.smoothScrollToPosition(lastItemPosition)
                lastItem = null
                //Updating database
                dbh!!.updateHomework(homeworks!![pos])
            }
        }

        //Swipe card to remove
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val pos = homeworks!!.indexOf(homeworkRvAdapter!!.dataset[viewHolder.adapterPosition])
                homeworks!![pos].isDone = true
                lastItem = homeworks!![pos]
                lastItemPosition = viewHolder.adapterPosition
                homeworkRvAdapter!!.removeItem(viewHolder.adapterPosition)
                updateLabel()
                snackbarRevert!!.show()
                //Updating database
                dbh!!.updateHomework(homeworks!![pos])
            }
        }

        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper!!.attachToRecyclerView(recyclerView)

        stufe = 5
        klasse = klassenArray[0]

        updateDataset()
        updateLabel()
    }

    //Adding new homework to "homeworks"
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_ID) {

            if (resultCode == RESULT_OK) {
                val id = data!!.extras!!.getLong("HW_ID")

                val temp = dbh!!.getHomework(id)

                homeworks!!.add(temp)

                filter(temp.stufe, temp.kurs!!)
            }

        }
    }

    override fun updateDataset() {
        dbh = HausaufgabenDatabaseHandler(activity)
        homeworks = ArrayList()

        if (dbh!!.homeworkCount > 0) {
            //load content of db
            val hw_db = dbh!!.allHomeworks

            Collections.addAll(homeworks, *hw_db)

            //Removing all local homeworks that are marked as done and all online homeworks
            val it = homeworks!!.iterator()
            var temp: Hausaufgabe
            while (it.hasNext()) {
                temp = it.next()
                if (temp.isDone && !temp.isFromInternet) {
                    it.remove()
                    dbh!!.deleteHomework(temp)
                } else if (temp.isFromInternet) {
                    it.remove()
                }
            }
        } else {
            homeworks = ArrayList() //TODO: Remove
        }

        filter(stufe, klasse)
    }

}

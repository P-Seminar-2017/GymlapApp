package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList

import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * 07.12.2018 | created by Lukas S
 */
abstract class HausaufgabenTabFragment : Fragment() {

    protected val klassenArray = arrayOf("Alle", "a", "b", "c", "d", "e")

    protected var homeworkList: ArrayList<Hausaufgabe>? = null

    protected lateinit var v: View
    protected var countLabel: TextView? = null
    protected var recyclerView: RecyclerView? = null
    protected var linearLayoutManager: LinearLayoutManager? = null
    protected var homeworkRvAdapter: HomeworkRvAdapter? = null

    protected var fab: FloatingActionButton? = null
    protected var fabContainer: CoordinatorLayout? = null

    protected var stufe: Int = 0
    protected lateinit var klasse: String

    //Database
    protected var dbh: HausaufgabenDatabaseHandler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_homework_tab, container, false)
        return v
    }

    protected fun updateLabel() {
        val count = homeworkRvAdapter!!.itemCount

        if (klasse === klassenArray[0])
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

    abstract fun updateDataset()

}

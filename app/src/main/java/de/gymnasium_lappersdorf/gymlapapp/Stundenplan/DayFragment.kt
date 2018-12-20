package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.gymnasium_lappersdorf.gymlapapp.App
import de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner.Hausaufgabe
import de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner.HausaufgabenDatabaseHandler
import de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner.HausaufgabenFragment
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.fragment_stundenplaner_day.*
import javax.inject.Inject

/*
* fragment for displaying a single day with lessons
* */
class DayFragment : Fragment() {

    @Inject
    lateinit var databaseHandler: DatabaseHandler
    private lateinit var hwDatabaseHandler: HausaufgabenDatabaseHandler
    var day: Long = 0
    private lateinit var rvAdapter: RvAdapter

    init {
        App.appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_stundenplaner_day, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hwDatabaseHandler = HausaufgabenDatabaseHandler(context)
        val hw: MutableList<Hausaufgabe> = mutableListOf()
        for (n in hwDatabaseHandler.completeHomework.toList()) {
            val day = when (this.day.toInt()) {
                0 -> "Montag"
                1 -> "Dienstag"
                2 -> "Mittwoch"
                3 -> "Donnerstag"
                4 -> "Freitag"
                else -> ""
            }
            if (n.dayOfWeek == day && !n.isDone) {
                hw.add(n)
            }
        }
        //init rv
        val layoutManager = LinearLayoutManager(context)
        stundenplaner_rv.layoutManager = layoutManager
        //rvAdapter = RvAdapter(databaseHandler.getDay(this.day)!!.lessons)
        rvAdapter = RvAdapter(mutableListOf(), hw) {
            val fragment = HausaufgabenFragment()
            parentFragment!!.fragmentManager!!.beginTransaction()
                    .replace(R.id.content_frame_main, fragment, fragment.tag)
                    .commit()
        }
        stundenplaner_rv.adapter = rvAdapter
    }

    override fun onStart() {
        super.onStart()
        //refreshing rv in case of added/edited lessons
        val dataset = databaseHandler.getDay(this.day)!!.lessons
        rvAdapter.dataset = dataset
        rvAdapter.notifyDataSetChanged()
        if (dataset.size == 0) {
            stundenplaner_rv.visibility = View.GONE
        } else {
            stundenplaner_rv.visibility = View.VISIBLE
        }
    }
}
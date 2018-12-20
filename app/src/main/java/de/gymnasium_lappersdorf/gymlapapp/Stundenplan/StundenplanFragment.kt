package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.fragment_stundenplaner.*
import java.util.*

/*
* fragment for displaying all days
* */
class StundenplanFragment : Fragment() {

    private lateinit var tb: Toolbar
    private var day: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_stundenplaner, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "Stundenplan"
        tb = activity!!.findViewById(R.id.toolbar_main)
        stundenplan_fab.setOnClickListener {
            //start Activity to add new hour
            val intent = Intent(activity, LessonActivity::class.java)
            intent.putExtra("day", this.day.toLong())
            startActivity(intent)
        }
        //add tabs
        val adapter = TabAdapter(childFragmentManager)
        pager.adapter = adapter
        tab_layout.addTab(tab_layout.newTab().setText("Mo"))
        tab_layout.addTab(tab_layout.newTab().setText("Di"))
        tab_layout.addTab(tab_layout.newTab().setText("Mi"))
        tab_layout.addTab(tab_layout.newTab().setText("Do"))
        tab_layout.addTab(tab_layout.newTab().setText("Fr"))
        pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                pager.currentItem = tab!!.position
                day = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        //setting current page to current day
        val cal = Calendar.getInstance()
        day = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            else -> 0
        }
        pager.currentItem = day
    }

    override fun onResume() {
        super.onResume()
        tb.elevation = 0F
    }

    override fun onPause() {
        super.onPause()
        tb.elevation = 4F
    }
}
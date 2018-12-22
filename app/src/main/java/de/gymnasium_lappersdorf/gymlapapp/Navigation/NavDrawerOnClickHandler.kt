package de.gymnasium_lappersdorf.gymlapapp.Navigation

import android.content.Context
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner.HausaufgabenFragment
import de.gymnasium_lappersdorf.gymlapapp.Home.HomeFragment
import de.gymnasium_lappersdorf.gymlapapp.Info.InfoFragment
import de.gymnasium_lappersdorf.gymlapapp.MainActivity
import de.gymnasium_lappersdorf.gymlapapp.R
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.StundenplanFragment
import kotlinx.android.synthetic.main.activity_main.*

/*
* adapter for handling all the main fragments for the navigation drawer
* */
class NavDrawerOnClickHandler(val context: Context) : NavigationView.OnNavigationItemSelectedListener {

    private val h by lazy { HomeFragment() }
    private val s by lazy { StundenplanFragment() }
    private val hw by lazy { HausaufgabenFragment() }
    private val inf by lazy { InfoFragment() }

    companion object {
        var currentFragment: Int = R.id._home
    }

    init {
        setFragment(currentFragment)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        setFragment(item.itemId)
        item.isChecked = false
        return true
    }

    private fun setFragment(item: Int) {
        currentFragment = item

        fun getFragment(): Fragment {
            return when (item) {
                R.id._home -> h
                R.id.stundenplaner -> s
                R.id.hausaufgaben -> hw
                R.id.information -> inf
                else -> h
            }
        }
        (context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_frame_main, getFragment())
                .commit()
        (context as MainActivity).drawer_layout_main.closeDrawers()
    }
}
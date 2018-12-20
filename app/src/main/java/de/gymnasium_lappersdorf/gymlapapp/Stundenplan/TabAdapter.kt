package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/*
* Adapter for handling tabs for all the days
* */
class TabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    companion object {
        const val NUM_ITEMS = 5
    }

    /*
    * @returns a dayFragment for [position]
    * */
    override fun getItem(position: Int): Fragment {
        val dayFragment = DayFragment()
        dayFragment.day = position.toLong()
        return dayFragment
    }

    /*
    * @return the item count
    * */
    override fun getCount(): Int = NUM_ITEMS
}
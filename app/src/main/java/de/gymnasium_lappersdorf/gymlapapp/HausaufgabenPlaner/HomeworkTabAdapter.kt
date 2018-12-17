package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * 07.12.2018 | created by Lukas S
 */
class HomeworkTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragments: Array<HausaufgabenTabFragment> = arrayOf(HausaufgabenOnlineFragment(), HausaufgabenLokalFragment())

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return NUM_TABS
    }

    companion object {
        private const val NUM_TABS = 2
    }
}

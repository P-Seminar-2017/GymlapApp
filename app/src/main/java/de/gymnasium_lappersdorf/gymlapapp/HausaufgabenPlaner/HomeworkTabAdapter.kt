package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * 07.12.2018 | created by Lukas S
 */
class HomeworkTabAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val online = HausaufgabenOnlineFragment()
    private val local = HausaufgabenLokalFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> getOnlineFragment()
            1 -> getLokalFragment()
            else -> getOnlineFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    fun getOnlineFragment() : HausaufgabenOnlineFragment {
        return online
    }

    fun getLokalFragment() : HausaufgabenLokalFragment {
        return local
    }
}

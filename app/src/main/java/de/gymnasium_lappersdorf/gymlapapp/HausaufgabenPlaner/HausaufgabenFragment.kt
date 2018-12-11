package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner

import java.util.Arrays

import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * 01.06.2018 | created by Lukas S
 */

class HausaufgabenFragment : Fragment(), NumberPicker.OnValueChangeListener, AdapterView.OnItemSelectedListener {

    private var v: View? = null
    private var tb: Toolbar? = null
    private var refreshLayout: SwipeRefreshLayout? = null

    //Tab
    private var vp: ViewPager? = null
    private var tl: TabLayout? = null
    private var adapter: HomeworkTabAdapter? = null

    //Dialog
    private var filterDialog: AlertDialog? = null
    private var stufenPicker: NumberPicker? = null
    private var stufe: Int = 0
    private var klasse: String? = null
    private var klasseSpinner: Spinner? = null

    private val klassenArray = arrayOf("Alle", "a", "b", "c", "d", "e")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_homework, container, false)
        setHasOptionsMenu(true)
        createFilterDialog()
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
        inflater!!.inflate(R.menu.homework_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "Hausaufgaben"

        tb = activity!!.findViewById(R.id.toolbar_main)
        refreshLayout = v!!.findViewById(R.id.swiperefresh_homework)

        refreshLayout!!.setOnRefreshListener {
            val index = 0
            val tab = tl!!.getTabAt(index)
            tab!!.select()
            (adapter!!.getItem(index) as HausaufgabenOnlineFragment).initDownload()
        }

        vp = view.findViewById(R.id.pager_homework)
        adapter = HomeworkTabAdapter(childFragmentManager)
        (adapter!!.getItem(0) as HausaufgabenOnlineFragment).setRefreshLayout(refreshLayout)
        vp!!.adapter = adapter

        tl = view.findViewById(R.id.tab_layout_homework)
        tl!!.addTab(tl!!.newTab().setText("Online"))
        tl!!.addTab(tl!!.newTab().setText("Lokal"))
        vp!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tl))
        tl!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                vp!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        resetFilterAttributes()
    }

    override fun onResume() {
        super.onResume()
        try {
            tb!!.elevation = 0f
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        try {
            tb!!.elevation = 4f
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.filter_item -> showFilterDialog()
            else -> {
            }
        }

        return super.onOptionsItemSelected(item)
    }

    //Numberpicker
    override fun onValueChange(numberPicker: NumberPicker, oldVal: Int, newVal: Int) {

        if (newVal >= 11 && newVal != oldVal) {
            val adapter = ArrayAdapter<CharSequence>(v!!.context, android.R.layout.simple_spinner_item, getKurse(newVal))
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            klasseSpinner!!.adapter = adapter
        } else if (newVal <= 10 && oldVal >= 11 && newVal != oldVal) {
            val adapter = ArrayAdapter<CharSequence>(v!!.context, android.R.layout.simple_spinner_item, klassenArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            klasseSpinner!!.adapter = adapter
        }

        stufe = newVal
        filter(stufe, klasse)
    }

    //Spinner
    override fun onItemSelected(adapterView: AdapterView<*>, view: View, pos: Int, id: Long) {
        klasse = klasseSpinner!!.selectedItem as String
        filter(stufe, klasse)
    }

    //Spinner
    override fun onNothingSelected(adapterView: AdapterView<*>) {}

    //creating dialog once for better performance
    private fun createFilterDialog() {

        val builder = AlertDialog.Builder(v!!.context)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.homework_filter_dialog, null)
        builder.setTitle("Deine Klasse")
        builder.setView(dialogView)

        stufenPicker = dialogView.findViewById(R.id.filter_numberpicker)
        stufenPicker!!.maxValue = 13
        stufenPicker!!.minValue = 5
        stufenPicker!!.wrapSelectorWheel = false
        stufenPicker!!.setOnValueChangedListener(this)

        klasseSpinner = dialogView.findViewById(R.id.filter_spinner)
        val adapter = ArrayAdapter<CharSequence>(v!!.context, android.R.layout.simple_spinner_item, klassenArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        klasseSpinner!!.adapter = adapter
        klasseSpinner!!.onItemSelectedListener = this

        builder.setPositiveButton("Fertig") { dialogInterface, i ->
            filter(stufe, klasse)
        }
        builder.setNegativeButton("Reset") { dialogInterface, i ->
            resetFilterAttributes()
            filter(stufe, klasse)
        }

        filterDialog = builder.create()
    }

    private fun showFilterDialog() {
        //Default values
        stufenPicker!!.value = stufe
        klasseSpinner!!.setSelection(Arrays.asList(*klassenArray).indexOf(klasse))

        filterDialog!!.show()
    }

    private fun filter(stufe: Int, klasse: String?) {
        (adapter!!.getItem(0) as HausaufgabenTabFragment).filter(stufe, klasse!!)
        (adapter!!.getItem(1) as HausaufgabenTabFragment).filter(stufe, klasse)
    }

    private fun resetFilterAttributes() {
        stufe = 5
        klasse = klassenArray[0]
    }

    private fun getKurse(stufe: Int): Array<String> {
        val list: MutableList<String> = ArrayList()
        list.addAll(0, HausaufgabenDatabaseHandler(activity).getKurse(stufe).asList())
        list.add(0, klassenArray[0])
        return list.toTypedArray()
    }

}

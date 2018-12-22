package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView

import de.gymnasium_lappersdorf.gymlapapp.R

/**
 * 01.06.2018 | created by Lukas S
 */

class HausaufgabenItemView : LinearLayout {
    private var cardView: CardView? = null
    private var viewType: TextView? = null
    private var viewFach: TextView? = null
    private var viewKlasseStufe: TextView? = null
    private var viewHausaufgaben: TextView? = null
    private var btnReminder: ImageButton? = null

    private var hw: Hausaufgabe? = null

    interface Callback {
        fun onClick()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        View.inflate(context, R.layout.hausaufgabenitemview, this)
        cardView = findViewById(R.id.homework_card_view)
        viewType = findViewById(R.id.text_type)
        viewFach = findViewById(R.id.text_fach)
        viewKlasseStufe = findViewById(R.id.text_klasse_stufe)
        viewHausaufgaben = findViewById(R.id.text_homework)
        btnReminder = findViewById(R.id.reminder_btn)
    }

    fun setHomework(hw: Hausaufgabe) {
        this.hw = hw
        when (hw.type) {
            Hausaufgabe.Types.DATE -> viewType!!.text = "Hausaufgabe/Notiz für " + hw.dateFormatted
            Hausaufgabe.Types.NEXT -> viewType!!.text = "Hausaufgabe aufgegeben am " + hw.dateFormatted + ", Abgabe: Nächste Lesson"
            Hausaufgabe.Types.NEXT2 -> viewType!!.text = "Hausaufgabe aufgegeben am " + hw.dateFormatted + ", Abgabe: Übernächste Lesson"
        }

        viewFach!!.text = hw.fach
        if (hw.stufe >= 0) viewKlasseStufe!!.text = hw.stufe.toString() + " " + hw.kurs
        else viewKlasseStufe!!.text = ""
        viewHausaufgaben!!.text = hw.text

        updateButton()
    }

    fun setRemindButtonCallback(callback: Callback) {
        this.btnReminder!!.setOnClickListener { callback.onClick() }
    }

    fun updateButton() {
        if (hw!!.isSetAsNotification(context))
            btnReminder?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
        else
            btnReminder?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
    }

}

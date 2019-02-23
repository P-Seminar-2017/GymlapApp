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
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.DatabaseHandler

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

    private lateinit var databaseHandler: DatabaseHandler

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
        databaseHandler = DatabaseHandler()
    }

    fun setHomework(hw: Hausaufgabe) {
        this.hw = hw

        if (hw.type == Hausaufgabe.Types.NEXT || hw.type == Hausaufgabe.Types.NEXT2) {
            val daysOfSubject = when (databaseHandler.getSubject(hw.fach!!)) {
                null -> emptyList()
                else -> databaseHandler.getDaysForSubject(hw.fach!!)
            }

            if (daysOfSubject.isEmpty()) {
                if (hw.type == Hausaufgabe.Types.NEXT) viewType!!.text = "Hausaufgabe/Notiz für nächste Stunde"
                else viewType!!.text = "Hausaufgabe/Notiz für übernächste Stunde"
            } else {
                viewType!!.text = "Hausaufgabe/Notiz für nächsten " + hw.getNextDay(daysOfSubject)
            }

        } else {
            viewType!!.text = "Hausaufgabe/Notiz für " + hw.dateFormatted
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
        if (hw!!.type != Hausaufgabe.Types.DATE) {
            btnReminder?.isEnabled = false
            btnReminder?.alpha = .5f
            return
        }

        btnReminder?.isEnabled = true
        btnReminder?.alpha = 1f

        if (hw!!.isSetAsNotification(context))
            btnReminder?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorAccent, null))
        else
            btnReminder?.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
    }

}

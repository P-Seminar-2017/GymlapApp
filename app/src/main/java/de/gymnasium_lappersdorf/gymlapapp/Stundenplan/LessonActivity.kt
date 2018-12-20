package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import de.gymnasium_lappersdorf.gymlapapp.App
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.activity_add_lesson.*
import javax.inject.Inject

/*
* activity for creating and editing lessons
* */
class LessonActivity : AppCompatActivity() {

    private enum class LessonType {
        SINGLE, DOUBLE
    }

    @Inject
    lateinit var databaseHandler: DatabaseHandler
    private var id: Long = 0
    private var day: Long = 0
    private var editing: Boolean = false
    private var lessonType: LessonType = LessonType.SINGLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson)
        App.appComponent.inject(this)
        //getting intent extras
        this.id = intent!!.extras!!.getLong("lessonid", 0)
        this.day = intent!!.extras!!.getLong("day", 0)
        this.editing = intent!!.extras!!.getBoolean("editing", false)
        //setting tile
        this.title = if (editing) "Stunde bearbeiten" else "Stunde hinzufügen"
        //typeSpinnner initialisation
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.stundenauswahl, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        TypeSpinner.adapter = adapter
        TypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                lessonType = if (adapterView!!.getItemAtPosition(position) == "Doppelstunde") LessonType.DOUBLE else LessonType.SINGLE
                autofill()
                Log.i("compare", "autofill")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        //setting up edit information
        if (editing) {
            Log.i("compare", "editing")
            val lesson = databaseHandler.getLesson(id)!!
            starttime.text = lesson.start
            stoptime.text = lesson.end
            number.setText(lesson.number)
            //set subject spinner
            subject_view.setSelected(lesson.subject.target.name)
            //set type spinner
            val hourDiff = lesson.end.split(":")[0].toInt() - lesson.start.split(":")[0].toInt()
            val minDiff = lesson.end.split(":")[1].toInt() - lesson.start.split(":")[1].toInt()
            val diff = hourDiff * 60 + minDiff
            TypeSpinner.setSelection(if (diff > 45) 0 else 1, true)
        }
    }

    /*
    * autofills time and number information
    * */
    private fun autofill() {
        /*
        * autofills lesson number for next lesson
        * */
        fun autofillNumber(last: String, lastTime: String) {
            //parsing numbers
            val lastNum: Int = try {
                last.toInt()
            } catch (e: NumberFormatException) {
                last.split("-")[1].toInt()
            }
            //lunchbreak
            val prefs = this@LessonActivity.getSharedPreferences("lunchbreak", Context.MODE_PRIVATE)
            val lunchbreakOffset = if (prefs.getBoolean("auto_lunchbreak", false) && lastTime == "13:05") {
                1
            } else {
                0
            }
            //adding numbers
            val newNum: String = when (lessonType) {
                LessonType.SINGLE -> (lunchbreakOffset + lastNum + 1).toString()
                LessonType.DOUBLE -> (lunchbreakOffset + lastNum + 1).toString() + "-" + (lunchbreakOffset + lastNum + 2).toString()
            }
            //setting text
            number.setText(newNum)
        }

        /*
        * autofills start- and stop-time for next lesson
        * */
        fun autofillTime(last: String) {
            //breaks
            var lastTime = when (last) {
                "10:10" -> "10:35"
                "12:05" -> "12:20"
                else -> last
            }
            //lunchbreak
            val prefs = this@LessonActivity.getSharedPreferences("lunchbreak", Context.MODE_PRIVATE)
            if (prefs.getBoolean("auto_lunchbreak", false)) {
                if (lastTime == "13:05") lastTime = "13:40"
            }
            //adding lesson time
            var lastHour = lastTime.split(":")[0].toInt()
            var lastMin = lastTime.split(":")[1].toInt()
            lastMin += when (lessonType) {
                LessonType.SINGLE -> 45
                LessonType.DOUBLE -> 90
            }
            //break during hour
            if (lastTime == "09:25" && lessonType == LessonType.DOUBLE) {
                //first break during lesson
                lastMin += 25
            }
            if (lastTime == "11:20" && lessonType == LessonType.DOUBLE) {
                //second break during lesson
                lastMin += 15
            }
            //flatten time
            while (lastMin > 59) {
                lastMin -= 60
                lastHour += 1
            }
            //setting text
            val newTime = (if (lastHour < 10) "0" else "") + lastHour + ":" + (if (lastMin < 10) "0" else "") + lastMin
            starttime.text = lastTime
            stoptime.text = newTime
        }

        val day = databaseHandler.getDay(this.day)!!
        if (editing) {
            //editing lesson
            autofillTime(starttime.text.toString())
            val num = number.text.toString()
            val lastNum = num.split("-")[0].toInt() - 1
            autofillNumber(lastNum.toString(), starttime.text.toString())
        } else {
            //creating new lesson
            if (day.lessons.size > 0) {
                val lastLesson = day.lessons[day.lessons.size - 1]
                autofillTime(lastLesson.end)
                autofillNumber(lastLesson.number, lastLesson.end)
            } else {
                autofillNumber("0", "7:55")
                autofillTime("7:55")
            }
        }
    }

    /*
    * open timePicker to input time to [textView]
    * */
    private fun timePicker(textView: TextView) {
        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            var min = "" + minute
            if (minute.toString().length == 1) {
                min = "0$minute"
            }
            val time = "$hour:$min"
            textView.text = time
        }, 7, 55, true)
        timePicker.show()
    }

    /*
    * validates input fields
    * */
    private fun validateInput(): Boolean {
        //using list for future expanding
        val inputs = listOf<TextInputEditText>(number)
        for (e in inputs) {
            if (e.text.toString() == "") {
                e.error = "Darf nicht leer sein"
                return false
            }
        }
        return true
    }

    /*
    * startTime onClick
    * */
    fun startClicked(view: View) {
        timePicker(starttime)
    }

    /*
    * stopTime onClick
    * */
    fun stopClicked(view: View) {
        timePicker(stoptime)
    }

    /*
    * accept OnClick
    * */
    fun accept(view: View) {
        if (validateInput()) {
            val day = databaseHandler.getDay(this.day)!!
            if (subject_view.validate()) {
                val subject = subject_view.getSubject()
                val lesson = Lesson(id, number.text.toString(), starttime.text.toString(), stoptime.text.toString())
                day.lessons.add(lesson)
                subject.lessons.add(lesson)
                databaseHandler.setDay(day)
                databaseHandler.setSubject(subject)
                finish()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.add_lesson_menu, menu)
        val prefs = this.getSharedPreferences("lunchbreak", Context.MODE_PRIVATE)
        menu!!.getItem(0).isChecked = prefs.getBoolean("auto_lunchbreak", false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //back button
        when (item!!.itemId) {
            R.id.auto_lunchbreak -> {
                val prefs = this.getSharedPreferences("lunchbreak", Context.MODE_PRIVATE)
                val editor = prefs.edit()
                editor.putBoolean("auto_lunchbreak", !item.isChecked)
                editor.apply()
                item.isChecked = !item.isChecked
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
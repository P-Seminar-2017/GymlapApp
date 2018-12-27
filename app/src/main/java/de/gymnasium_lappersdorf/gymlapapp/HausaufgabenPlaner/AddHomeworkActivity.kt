package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import de.gymnasium_lappersdorf.gymlapapp.R
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.SubjectView
import java.util.*

/**
 * 03.06.2018 | created by Lukas S
 */

class AddHomeworkActivity : AppCompatActivity() {

    private var isEdit = false

    private var textViewDate: TextView? = null
    private var textInputText: TextInputEditText? = null

    private var newHomework: Hausaufgabe? = null

    private var subjectView: SubjectView? = null

    //Database
    private var dbh: HausaufgabenDatabaseHandler? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_homework)

        textViewDate = findViewById(R.id.textview_date)
        textInputText = findViewById(R.id.homework_input_text)
        subjectView = findViewById(R.id.subject_view)

        dbh = HausaufgabenDatabaseHandler(this)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        val edit_id = intent.getLongExtra("EDIT_ID", -1)

        if (edit_id > 0) {
            title = "Hausaufgabe anpassen"
            isEdit = true

            val edit = dbh!!.getHomework(edit_id)
            newHomework = Hausaufgabe(edit.fach, edit.text,
                    edit.timestamp,
                    -1,
                    "a",
                    Hausaufgabe.Types.DATE
            )
            newHomework!!.databaseId = edit_id
            newHomework!!.notificationId = edit.notificationId

            subjectView!!.setSelected(edit.fach!!)
            textInputText!!.setText(edit.text)
        } else {
            title = "Hausaufgabe hinzufügen"
            isEdit = false

            newHomework = Hausaufgabe(null, null,
                    calendar.timeInMillis,
                    -1,
                    "a",
                    Hausaufgabe.Types.DATE
            )
        }

        updateLabels()
    }

    fun onClickDate(v: View) {
        datePicker()
    }

    fun onSubmit(v: View) {

        if (subjectView!!.validate() && checkInputs()) {

            newHomework!!.fach = subjectView!!.getSubject().name
            newHomework!!.text = textInputText!!.text!!.toString().trim { it <= ' ' }

            //Add or update new homework in database
            val dataIntent = Intent()

            val id = if (isEdit) {
                dbh!!.updateHomework(newHomework)
                newHomework!!.databaseId
            } else {
                dbh!!.addHomework(newHomework)
            }

            dataIntent.putExtra("HW_ID", id)

            setResult(Activity.RESULT_OK, dataIntent)

            finish()
        }

    }

    private fun datePicker() {
        val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth, 7, 30)
            newHomework!!.timestamp = cal.timeInMillis
            updateLabels()
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1)

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 1)

        dialog.datePicker.minDate = cal.timeInMillis

        cal.timeInMillis = newHomework!!.timestamp

        dialog.datePicker.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))

        dialog.show()
    }

    private fun updateLabels() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = newHomework!!.timestamp

        //String tag = cal.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.getDefault());
        val monat = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        textViewDate!!.text = cal.get(Calendar.DAY_OF_MONTH).toString() + ". " + monat + " " + cal.get(Calendar.YEAR)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkInputs(): Boolean {

        if (textInputText!!.text!!.toString().trim { it <= ' ' } == "") {
            textInputText!!.error = "Darf nicht leer sein"
            return false
        }

        return true
    }
}

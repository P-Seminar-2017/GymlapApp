package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import java.util.Arrays
import java.util.Calendar
import java.util.Locale

import de.gymnasium_lappersdorf.gymlapapp.R
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.SubjectView

/**
 * 03.06.2018 | created by Lukas S
 */

class AddHomeworkActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener {

    private val KLASSEN_ARRAY = arrayOf("a", "b", "c", "d", "e")
    private var textViewDate: TextView? = null
    private var textViewKlasse: TextView? = null
    private var textInputText: TextInputEditText? = null

    private var dialogKlasse: AlertDialog? = null
    private var stufenPicker: NumberPicker? = null
    private var klasseSpinner: Spinner? = null
    private var stufenEdittext: EditText? = null

    private var newHomework: Hausaufgabe? = null

    private var subjectView: SubjectView? = null

    //Database
    private var dbh: HausaufgabenDatabaseHandler? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_homework)

        title = "Hausaufgabe hinzufügen"

        textViewDate = findViewById(R.id.textview_date)
        textViewKlasse = findViewById(R.id.textview_klasse)
        textInputText = findViewById(R.id.homework_input_text)
        subjectView = findViewById(R.id.subject_view)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        newHomework = Hausaufgabe(null, null,
                calendar.timeInMillis,
                intent.getIntExtra("STUFE", 5),
                intent.getStringExtra("KLASSE"),
                Hausaufgabe.Types.DATE
        )

        dbh = HausaufgabenDatabaseHandler(this)
        updateLabels()

        createClassSelectionDialog()
    }

    fun onClickDate(v: View) {
        datePicker()
    }

    fun onClickKlasse(v: View) {
        showClassSelectionDialog()
    }

    fun onSubmit(v: View) {

        if (subjectView!!.validate() && checkInputs()) {

            newHomework!!.fach = subjectView!!.getSubject().name
            newHomework!!.text = textInputText!!.text!!.toString().trim { it <= ' ' }

            //Add new homework to database
            val id = dbh!!.addHomework(newHomework)
            val dataIntent = Intent()
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

        dialog.show()
    }

    //creating dialog once for better performance
    private fun createClassSelectionDialog() {

        val d = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.homework_filter_dialog, null)
        d.setTitle("Deine Klasse")
        d.setView(dialogView)

        stufenPicker = dialogView.findViewById(R.id.filter_numberpicker)
        stufenPicker!!.maxValue = 13
        stufenPicker!!.minValue = 5
        stufenPicker!!.wrapSelectorWheel = false
        stufenPicker!!.setOnValueChangedListener(this)

        klasseSpinner = dialogView.findViewById(R.id.filter_spinner)
        val adapter = ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, KLASSEN_ARRAY)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        klasseSpinner!!.adapter = adapter

        stufenEdittext = dialogView.findViewById(R.id.filter_edittext)

        d.setPositiveButton("Fertig") { dialogInterface, i ->
            //Gets overwritten later
        }

        d.setNegativeButton("Zurück") { dialogInterface, i -> }

        dialogKlasse = d.create()
    }

    private fun showClassSelectionDialog() {
        //Default values
        stufenPicker!!.value = newHomework!!.stufe

        if (newHomework!!.stufe < 11) {
            klasseSpinner!!.setSelection(Arrays.asList(*KLASSEN_ARRAY).indexOf(newHomework!!.kurs))
            klasseSpinner!!.visibility = View.VISIBLE
            stufenEdittext!!.visibility = View.GONE
        } else {
            klasseSpinner!!.setSelection(0)
            stufenEdittext!!.setText(newHomework!!.kurs)
            klasseSpinner!!.visibility = View.GONE
            stufenEdittext!!.visibility = View.VISIBLE
        }

        dialogKlasse!!.show()

        //overriding submit button
        dialogKlasse!!.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            newHomework!!.stufe = stufenPicker!!.value

            if (newHomework!!.stufe < 11) {
                newHomework!!.kurs = KLASSEN_ARRAY[klasseSpinner!!.selectedItemPosition]
                updateLabels()
                dialogKlasse!!.dismiss()
            } else {

                if (stufenEdittext!!.text.toString().trim { it <= ' ' } == "") {
                    //don't dismiss dialog because no string is given
                    Toast.makeText(dialogKlasse!!.context, "Kurs darf nicht leer sein", Toast.LENGTH_SHORT).show()
                } else {
                    newHomework!!.kurs = stufenEdittext!!.text.toString().trim { it <= ' ' }
                    updateLabels()
                    dialogKlasse!!.dismiss()
                }

            }
        }

    }

    private fun updateLabels() {
        val cal = Calendar.getInstance()
        cal.timeInMillis = newHomework!!.timestamp

        //String tag = cal.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.getDefault());
        val monat = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())

        textViewDate!!.text = cal.get(Calendar.DAY_OF_MONTH).toString() + ". " + monat + " " + cal.get(Calendar.YEAR)
        textViewKlasse!!.text = newHomework!!.stufe.toString() + " " + newHomework!!.kurs
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

    override fun onValueChange(numberPicker: NumberPicker, oldVal: Int, newVal: Int) {

        if (oldVal != newVal) {

            if (newVal > 10) {
                klasseSpinner!!.setSelection(0)
                klasseSpinner!!.visibility = View.GONE
                stufenEdittext!!.visibility = View.VISIBLE
                stufenEdittext!!.text = null
            } else {
                klasseSpinner!!.visibility = View.VISIBLE
                stufenEdittext!!.visibility = View.GONE
                stufenEdittext!!.text = null
                dialogKlasse!!.setMessage(null)
            }

        }

    }
}

package de.gymnasium_lappersdorf.gymlapapp.Stundenplan

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import de.gymnasium_lappersdorf.gymlapapp.App
import de.gymnasium_lappersdorf.gymlapapp.R
import kotlinx.android.synthetic.main.subject_view.view.*
import javax.inject.Inject

/*
* View that can be used to create, edit,
* delete and choose subjects from the database
* */
class SubjectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    @Inject
    lateinit var databaseHandler: DatabaseHandler
    private var subjectAdapter: ArrayAdapter<String>

    init {
        inflate(context, R.layout.subject_view, this)
        App.appComponent.inject(this)
        val subjects = arrayListOf("Fach auswählen...")
        for (s: Subject in databaseHandler.getSubjects()) subjects.add(s.name)
        subjectAdapter = object : ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, subjects) {
            override fun isEnabled(position: Int): Boolean {
                if (position == 0) return false
                return true
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val tv = view as TextView
                if (position == 0) tv.setTextColor(Color.GRAY)
                else tv.setTextColor(Color.BLACK)
                return view
            }
        }
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        SubjectSpinner.adapter = subjectAdapter
        SubjectSpinner.post {
            (SubjectSpinner.selectedView as TextView).setTextColor(Color.GRAY)
        }
        add_subject.setOnClickListener { addSubject() }
        edit_subject.setOnClickListener { if (validate()) editSubject(getSubject()) }
        del_subject.setOnClickListener { if (validate()) deleteSubject(getSubject()) }
    }

    /*
    * @returns if the user has chosen a subject
    * shows error message to promt user action
    * */
    fun validate(): Boolean {
        val selected = SubjectSpinner.selectedItemPosition != 0
        if (!selected) {
            val textView = SubjectSpinner.selectedView as TextView
            textView.error = ""
            textView.text = "Fach muss ausgewählt werden"
            textView.setTextColor(Color.RED)
        }
        return selected
    }

    /*
    * @returns the currently selected subject
    * make sure to call validate first to avoid
    * an exception when no subject is selected
    * */
    fun getSubject(): Subject = databaseHandler
        .getSubject(subjectAdapter.getItem(SubjectSpinner.selectedItemPosition)!!)!!


    /*
    * selects a [subject] based on a string identifier
    * */
    fun setSelected(subject: String) {
        val subjects = databaseHandler.getSubjects()
        for ((index, e: Subject) in subjects.withIndex()) {
            if (e.name == subject) {
                SubjectSpinner.setSelection(index + 1, true)
                SubjectSpinner.post {
                    (SubjectSpinner.selectedView as TextView).setTextColor(Color.BLACK)
                }
            }
        }
    }

    /*
    * adds a subject to spinner and database
    * */
    private fun addSubject() {
        openDialog {
            databaseHandler.setSubject(it)
            subjectAdapter.add(it.name)
            SubjectSpinner.adapter = subjectAdapter
        }
    }

    /*
    * edits a [subject] and inserts it into spinner and database
    * */
    private fun editSubject(subject: Subject) {
        openDialog(subject) {
            databaseHandler.setSubject(it)
        }
    }

    /*
    * deletes a [subject] from spinner and database
    * */
    private fun deleteSubject(subject: Subject) {
        if (SubjectSpinner.selectedItemPosition != 0) {
            if (databaseHandler.getSubject(subject.name)!!.lessons.size == 0) {
                //subject is safe to delete
                subjectAdapter.remove(subject.name)
                SubjectSpinner.adapter = subjectAdapter
                databaseHandler.rmSubject(subject)
            } else {
                Toast.makeText(context, "Stunden mit diesem Fach müssen zuerst gelöscht werden", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Fach muss zum Löschen ausgewählt werden", Toast.LENGTH_SHORT).show()
        }
    }

    /*
    * opens a dialog to create or edit a subject
    * */
    private fun openDialog(editSubject: Subject? = null, onNewSubject: (subject: Subject) -> Unit) {
        val view = LayoutInflater.from(context).inflate(R.layout.add_subject_view, null)
        val subject: TextInputEditText = view.findViewById(R.id.subject)
        val teacher: TextInputEditText = view.findViewById(R.id.teacher)
        val room: TextInputEditText = view.findViewById(R.id.room)
        val course: TextInputEditText = view.findViewById(R.id.course)
        if (editSubject != null) {
            //setting known properties if editing
            subject.setText(editSubject.name)
            teacher.setText(editSubject.teacher)
            room.setText(editSubject.room)
            course.setText(editSubject.course)
        }
        val builder = AlertDialog.Builder(context)
            .setTitle(if (editSubject == null) "Fach hinzufügen" else "Fach bearbeiten")
            .setView(view)
            .setPositiveButton("Speichern", null) //set to null -> overriding onClick later
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    if (subject.text.toString() == "") {
                        //name empty
                        subject.error = "Darf nicht leer sein!"
                    } else {
                        if (editSubject == null) {
                            //subject gets added
                            if (databaseHandler.getSubject(subject.text.toString()) != null) {
                                //subject already exists
                                subject.error = "Fach existiert bereits!"
                            } else {
                                //new subject can be created
                                onNewSubject(Subject(
                                    0,
                                    subject.text.toString(),
                                    course.text.toString(),
                                    teacher.text.toString(),
                                    room.text.toString()
                                ))
                                dialog.dismiss()
                            }
                        } else {
                            //subject get edited
                            onNewSubject(Subject(
                                editSubject.id,
                                subject.text.toString(),
                                course.text.toString(),
                                teacher.text.toString(),
                                room.text.toString()
                            ))
                            dialog.dismiss()
                        }
                    }
                }
        }
        dialog.show()
    }
}
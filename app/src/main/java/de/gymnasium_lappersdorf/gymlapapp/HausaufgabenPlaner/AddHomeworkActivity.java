package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import de.gymnasium_lappersdorf.gymlapapp.R;
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.DatabaseHandler;
import de.gymnasium_lappersdorf.gymlapapp.Stundenplan.Subject;

/**
 * 03.06.2018 | created by Lukas S
 */

public class AddHomeworkActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String[] KLASSEN_ARRAY = new String[]{"a", "b", "c", "d", "e"};

    private TextView textViewDate, textViewKlasse;
    private TextInputEditText textInputText;

    private AlertDialog dialogKlasse;
    private NumberPicker stufenPicker;
    private Spinner klasseSpinner;
    private EditText stufenEdittext;

    private String selectedSubject;
    private ArrayAdapter<String> subjectAdapter;
    private Spinner subjectSpinner;

    private Hausaufgabe newHomework;

    //Database
    private HausaufgabenDatabaseHandler dbh;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_homework);

        setTitle("Hausaufgabe hinzufügen");

        textViewDate = findViewById(R.id.textview_date);
        textViewKlasse = findViewById(R.id.textview_klasse);
        textInputText = findViewById(R.id.homework_input_text);
        subjectSpinner = findViewById(R.id.subject_spinner);

        //subjectSpinner initialisation
        ArrayList<String> subjects = new ArrayList<>();
        for (Subject s : DatabaseHandler.INSTANCE.getSubjects()) {
            subjects.add(s.getName());
            System.out.println(s.getName());
        }
        subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSubject = subjectAdapter.getItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        newHomework = new Hausaufgabe(null, null, calendar.getTimeInMillis(), 5, "a", Hausaufgabe.Types.DATE);

        dbh = new HausaufgabenDatabaseHandler(this);
        updateLabels();

        createClassSelectionDialog();
    }

    public void onClickDate(View v) {
        datePicker();
    }

    public void onClickKlasse(View v) {
        showClassSelectionDialog();
    }

    public void onSubmit(View v) {

        if (checkInputs() && selectedSubject != null) {

            newHomework.setFach(selectedSubject);
            newHomework.setText(textInputText.getText().toString().trim());

            //Add new homework to database
            long id = dbh.addHomework(newHomework);
            Intent dataIntent = new Intent();
            dataIntent.putExtra("HW_ID", id);

            setResult(RESULT_OK, dataIntent);

            finish();
        }

    }

    private void datePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, dayOfMonth, 7, 30);
                newHomework.setTimestamp(cal.getTimeInMillis());
                updateLabels();
            }

        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1);

        dialog.show();
    }

    //creating dialog once for better performance
    private void createClassSelectionDialog() {

        AlertDialog.Builder d = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.homework_filter_dialog, null);
        d.setTitle("Deine Klasse");
        d.setView(dialogView);

        stufenPicker = dialogView.findViewById(R.id.filter_numberpicker);
        stufenPicker.setMaxValue(12);
        stufenPicker.setMinValue(5);
        stufenPicker.setWrapSelectorWheel(false);
        stufenPicker.setOnValueChangedListener(this);

        klasseSpinner = dialogView.findViewById(R.id.filter_spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, KLASSEN_ARRAY);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        klasseSpinner.setAdapter(adapter);

        stufenEdittext = dialogView.findViewById(R.id.filter_edittext);

        d.setPositiveButton("Fertig", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Gets overwritten later
            }
        });

        d.setNegativeButton("Zurück", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        dialogKlasse = d.create();
    }

    private void showClassSelectionDialog() {
        //Default values
        stufenPicker.setValue(newHomework.getStufe());

        if (newHomework.getStufe() < 11) {
            klasseSpinner.setSelection(Arrays.asList(KLASSEN_ARRAY).indexOf(newHomework.getKurs()));
            klasseSpinner.setVisibility(View.VISIBLE);
            stufenEdittext.setVisibility(View.GONE);
        } else {
            klasseSpinner.setSelection(0);
            stufenEdittext.setText(newHomework.getKurs());
            klasseSpinner.setVisibility(View.GONE);
            stufenEdittext.setVisibility(View.VISIBLE);
        }

        dialogKlasse.show();

        //overriding submit button
        dialogKlasse.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newHomework.setStufe(stufenPicker.getValue());

                if (newHomework.getStufe() < 11) {
                    newHomework.setKurs(KLASSEN_ARRAY[klasseSpinner.getSelectedItemPosition()]);
                    updateLabels();
                    dialogKlasse.dismiss();
                } else {

                    if (stufenEdittext.getText().toString().trim().equals("")) {
                        //don't dismiss dialog because no string is given
                        Toast.makeText(dialogKlasse.getContext(), "Kurs darf nicht leer sein", Toast.LENGTH_SHORT).show();
                    } else {
                        newHomework.setKurs(stufenEdittext.getText().toString().trim());
                        updateLabels();
                        dialogKlasse.dismiss();
                    }

                }
            }
        });

    }

    public void addSubject(View view) {
        View v = getLayoutInflater().inflate(R.layout.add_subject_view, null);
        final TextInputEditText subject, teacher, room, course;
        subject = v.findViewById(R.id.subject);
        teacher = v.findViewById(R.id.teacher);
        room = v.findViewById(R.id.room);
        course = v.findViewById(R.id.course);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(v);
        builder.setTitle("Fach hinzufügen");
        builder.setPositiveButton("Hinzufügen", null);//set to null -> overriding onClick
        final android.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (subject.getText().toString().equals("")) {
                            //Name empty
                            subject.setError("Darf nicht leer sein");
                        } else {
                            if (DatabaseHandler.INSTANCE.getSubject(subject.getText().toString()) != null) {
                                //Subject already exists
                                subject.setError("Fach existiert bereits");
                            } else {
                                //valid input
                                Subject newsubject = new Subject(0, subject.getText().toString(), course.getText().toString(), teacher.getText().toString(), room.getText().toString());
                                DatabaseHandler.INSTANCE.setSubject(newsubject);
                                subjectAdapter.add(newsubject.getName());
                                subjectSpinner.setAdapter(subjectAdapter);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void updateLabels() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(newHomework.getTimestamp());

        //String tag = cal.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.getDefault());
        String monat = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        textViewDate.setText(cal.get(Calendar.DAY_OF_MONTH) + ". " + monat + " " + cal.get(Calendar.YEAR));
        textViewKlasse.setText(newHomework.getStufe() + " " + newHomework.getKurs());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkInputs() {

        if (textInputText.getText().toString().trim().equals("")) {
            textInputText.setError("Darf nicht leer sein");
            return false;
        }

        return true;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

        if (oldVal != newVal) {

            if (newVal > 10) {
                klasseSpinner.setSelection(0);
                klasseSpinner.setVisibility(View.GONE);
                stufenEdittext.setVisibility(View.VISIBLE);
                stufenEdittext.setText(null);
            } else {
                klasseSpinner.setVisibility(View.VISIBLE);
                stufenEdittext.setVisibility(View.GONE);
                stufenEdittext.setText(null);
                dialogKlasse.setMessage(null);
            }

        }

    }
}

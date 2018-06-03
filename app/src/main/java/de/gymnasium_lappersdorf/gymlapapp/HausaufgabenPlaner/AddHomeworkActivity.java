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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 03.06.2018 | created by Lukas S
 */

public class AddHomeworkActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String[] KLASSEN_ARRAY = new String[]{"a", "b", "c", "d"};

    private TextView textViewDate, textViewKlasse;
    private TextInputEditText textInputFach, textInputText;

    private AlertDialog dialogKlasse;
    private NumberPicker stufenPicker;
    private Spinner klasseSpinner;
    private EditText stufenEdittext;

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
        textInputFach = findViewById(R.id.homework_input_fach);
        textInputText = findViewById(R.id.homework_input_text);

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

        if (checkInputs()) {

            newHomework.setFach(textInputFach.getText().toString().trim());
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

        if (textInputFach.getText().toString().trim().equals("")) {
            textInputFach.setError("Darf nicht leer sein");
            return false;
        } else {
            textInputFach.setError(null);
        }

        if (textInputText.getText().toString().trim().equals("")) {
            textInputText.setError("Darf nicht leer sein");
            return false;
        } else {
            textInputFach.setError(null);
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

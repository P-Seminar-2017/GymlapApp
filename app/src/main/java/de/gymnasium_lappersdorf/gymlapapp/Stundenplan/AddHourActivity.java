package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import de.gymnasium_lappersdorf.gymlapapp.R;

public class AddHourActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner hourSpinner, daySpinner;
    TextView timestarttxt1, timestoptxt1, timestarttxt2, timestoptxt2;
    RelativeLayout timestart1, timestop1, timestart2, timestop2;
    LinearLayout einzelPrefs, doppelPrefs, pausePrefs;
    TextInputEditText einzelNummer, einzelLesson, einzelCourse, einzelTeacher, einzelRoom;
    TextInputEditText doppelNummer1, doppelNummer2, doppelLesson, doppelCourse, doppelTeacher, doppelRoom;
    TextInputEditText pauseTitel;
    int day;
    StundenplanDatabaseHandler dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hour);

        setTitle("Stunde hinzufügen");

        Intent i = getIntent();
        try {
            day = i.getExtras().getInt("day");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        dbh = new StundenplanDatabaseHandler(this, "day"+day);

        hourSpinner = findViewById(R.id.spinner);
        daySpinner = findViewById(R.id.tag_spinner);

        timestarttxt1 = findViewById(R.id.vontxt1);
        timestoptxt1 = findViewById(R.id.bistxt1);
        timestarttxt2 = findViewById(R.id.vontxt2);
        timestoptxt2 = findViewById(R.id.bistxt2);

        timestart1 = findViewById(R.id.layoutstart1);
        timestop1 = findViewById(R.id.layoutstop1);
        timestart2 = findViewById(R.id.layoutstart2);
        timestop2 = findViewById(R.id.layoutstop2);

        einzelPrefs = findViewById(R.id.eigenschaften_einzel);
        doppelPrefs = findViewById(R.id.eigenschaften_doppel);
        pausePrefs = findViewById(R.id.eigenschaften_pause);

        einzelNummer = findViewById(R.id.stundenname_einzel);
        einzelLesson = findViewById(R.id.fach_einzel);
        einzelCourse = findViewById(R.id.kurs_einzel);
        einzelTeacher = findViewById(R.id.lehrer_einzel);
        einzelRoom = findViewById(R.id.raum_einzel);

        doppelNummer1 = findViewById(R.id.stundenname_doppel1);
        doppelNummer2 = findViewById(R.id.stundenname_doppel2);
        doppelLesson = findViewById(R.id.fach_doppel);
        doppelCourse = findViewById(R.id.kurs_doppel);
        doppelTeacher = findViewById(R.id.lehrer_doppel);
        doppelRoom = findViewById(R.id.raum_doppel);

        pauseTitel = findViewById(R.id.stundenname_pause);

        ArrayAdapter<CharSequence> stundeAdapter = ArrayAdapter.createFromResource(this, R.array.stundenauswahl, android.R.layout.simple_spinner_item);
        stundeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourSpinner.setAdapter(stundeAdapter);
        hourSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> tagAdapter = ArrayAdapter.createFromResource(this, R.array.tage, android.R.layout.simple_spinner_item);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(tagAdapter);
        daySpinner.setOnItemSelectedListener(this);
        daySpinner.setSelection(day);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch ("" + adapterView.getItemAtPosition(i)) {
            case "Einzelstunde":
                setEinzel();
                break;
            case "Doppelstunde":
                setDoppel();
                break;
            case "Pause":
                setPause();
                break;
            case "Montag":
                day = 0;
                break;
            case "Dienstag":
                day = 1;
                break;
            case "Mittwoch":
                day = 2;
                break;
            case "Donnerstag":
                day = 3;
                break;
            case "Freitag":
                day = 4;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //hourSpinner selection cancelled
    }

    private void setEinzel() {
        timestart1.setVisibility(View.VISIBLE);
        timestop1.setVisibility(View.VISIBLE);
        timestart2.setVisibility(View.GONE);
        timestop2.setVisibility(View.GONE);

        einzelPrefs.setVisibility(View.VISIBLE);
        doppelPrefs.setVisibility(View.GONE);
        pausePrefs.setVisibility(View.GONE);
    }

    private void setDoppel() {
        timestart1.setVisibility(View.VISIBLE);
        timestop1.setVisibility(View.VISIBLE);
        timestart2.setVisibility(View.VISIBLE);
        timestop2.setVisibility(View.VISIBLE);

        einzelPrefs.setVisibility(View.GONE);
        doppelPrefs.setVisibility(View.VISIBLE);
        pausePrefs.setVisibility(View.GONE);
    }

    private void setPause() {
        timestart1.setVisibility(View.VISIBLE);
        timestop1.setVisibility(View.VISIBLE);
        timestart2.setVisibility(View.GONE);
        timestop2.setVisibility(View.GONE);

        einzelPrefs.setVisibility(View.GONE);
        doppelPrefs.setVisibility(View.GONE);
        pausePrefs.setVisibility(View.VISIBLE);
    }


    public void layoutvonclicked1(View view) {
        timePicker(timestarttxt1);
    }

    public void layoutbisclicked1(View view) {
        timePicker(timestoptxt1);
    }

    public void layoutbisclicked2(View view) {
        timePicker(timestoptxt2);
    }

    public void layoutvonclicked2(View view) {
        timePicker(timestarttxt2);
    }

    public void accept(View view) {
        if (allFilledOut()){
            switch (hourSpinner.getSelectedItemPosition()){
                case 0:
                    //single-hour
                    Einzelstunde einzel = new Einzelstunde(einzelNummer.getText().toString(), timestarttxt1.getText().toString(), timestoptxt1.getText().toString(), einzelLesson.getText().toString(), einzelCourse.getText().toString(), einzelTeacher.getText().toString(), einzelRoom.getText().toString());
                    dbh.setTable("day"+day);
                    dbh.addHour(einzel);
                    finish();
                    break;
                case 1:
                    //double-hour
                    Doppelstunde doppel = new Doppelstunde(doppelNummer1.getText().toString(), doppelNummer2.getText().toString(), timestarttxt1.getText().toString(), timestoptxt1.getText().toString(), timestarttxt2.getText().toString(), timestoptxt2.getText().toString(), doppelLesson.getText().toString(), doppelCourse.getText().toString(), doppelTeacher.getText().toString(), doppelRoom.getText().toString());
                    dbh.setTable("day"+day);
                    dbh.addHour(doppel);
                    finish();
                    break;
                case 2:
                    //break
                    Pause pause = new Pause(timestarttxt1.getText().toString(), timestoptxt1.getText().toString(), pauseTitel.getText().toString());
                    dbh.setTable("day"+day);
                    dbh.addHour(pause);
                    finish();
                    break;
            }
        }
    }

    private void timePicker(final TextView t){
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String i2 = ""+i1;
                if (String.valueOf(i1).length()==1) {
                    i2 = "0"+i1;
                }
                t.setText(i+":"+i2);
            }
        }, 7, 55, true);
        timePicker.show();
    }

    public boolean allFilledOut(){
        TextInputEditText[] e;
        switch (hourSpinner.getSelectedItemPosition()){
            case 0:
                //single-hour
                e = new TextInputEditText[]{einzelNummer, einzelLesson, einzelCourse, einzelTeacher, einzelRoom};
                break;
            case 1:
                //double-hour
                e = new TextInputEditText[]{doppelNummer1, doppelNummer2, doppelLesson, doppelCourse, doppelTeacher, doppelRoom};
                break;
            case 2:
                //break
                e = new TextInputEditText[]{pauseTitel};
                break;
            default:
                e = new TextInputEditText[]{};
        }

        for (int i = 0; i < e.length; i++){
            if (e[i].getText().toString().equals("")){
                e[i].setError("Darf nicht leer sein");
                return false;
            }
        }
        return true;
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
}

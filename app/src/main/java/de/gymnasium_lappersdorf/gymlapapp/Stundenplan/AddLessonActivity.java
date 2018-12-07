package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import javax.inject.Inject;

import de.gymnasium_lappersdorf.gymlapapp.App;
import de.gymnasium_lappersdorf.gymlapapp.R;

public class AddLessonActivity extends AppCompatActivity {

    @Inject
    DatabaseHandler databaseHandler;

    Spinner typeSpinner;
    TextView startTime, stopTime;
    RelativeLayout startLayout, stopLayout;
    TextInputEditText number;
    SubjectView subjectView;

    Long day;
    String lessonType = "Doppelstunde";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        App.appComponent.inject(this);

        setTitle("Stunde hinzufügen");

        //view initialisation
        typeSpinner = findViewById(R.id.TypeSpinner);
        startTime = findViewById(R.id.starttime);
        stopTime = findViewById(R.id.stoptime);
        startLayout = findViewById(R.id.layoutstart);
        stopLayout = findViewById(R.id.layoutstop);
        number = findViewById(R.id.number);
        subjectView = findViewById(R.id.subject_view);

        //get day
        Intent i = getIntent();
        try {
            day = i.getExtras().getLong("day");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //typeSpinner initialisation
        ArrayAdapter<CharSequence> stundeAdapter = ArrayAdapter.createFromResource(this, R.array.stundenauswahl, android.R.layout.simple_spinner_item);
        stundeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(stundeAdapter);
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                lessonType = "" + adapterView.getItemAtPosition(i);
                autofill();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void vonClicked(View view) {
        timePicker(startTime);
    }

    public void bisClicked(View view) {
        timePicker(stopTime);
    }

    public boolean validateInput() {
        TextInputEditText[] inputs = {number};
        for (TextInputEditText e : inputs) {
            if (e.getText().toString().equals("")) {
                e.setError("Darf nicht leer sein");
                return false;
            }
        }
        return true;
    }

    public void accept(View view) {
        if (validateInput()) {
            //save to DB
            Day day = databaseHandler.getDay(this.day);
            if (subjectView.validate()) {
                Subject subject = subjectView.getSubject();
                Lesson lesson = new Lesson(0, number.getText().toString(), startTime.getText().toString(), stopTime.getText().toString());
                day.lessons.add(lesson);
                subject.lessons.add(lesson);
                databaseHandler.setDay(day);
                databaseHandler.setSubject(subject);
                finish();
            }
        }
    }

    private void timePicker(final TextView t) {
        TimePickerDialog timePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                String i2 = "" + i1;
                if (String.valueOf(i1).length() == 1) {
                    i2 = "0" + i1;
                }
                t.setText(i + ":" + i2);
            }
        }, 7, 55, true);
        timePicker.show();
    }

    private void autofill() {
        Day day = databaseHandler.getDay(this.day);
        if (day.lessons.size() > 0) {
            Lesson lastLesson = day.lessons.get(day.lessons.size() - 1);
            autofillTime(lastLesson.getEnd());
            autofillNumber(lastLesson.getNumber(), lastLesson.getEnd());
        } else {
            autofillNumber("0", "7:55");
            autofillTime("7:55");
        }
    }

    private void autofillNumber(String last, String lastTime) {
        int lastNum;
        int lunchbreakOffset = 0;
        SharedPreferences prefs = this.getSharedPreferences("lunchbreak", MODE_PRIVATE);
        if (prefs.getBoolean("auto_lunchbreak", false) && lastTime.equals("13:05")) {
            lunchbreakOffset = 1;
        }
        try {
            lastNum = Integer.parseInt(last);
        } catch (NumberFormatException e) {
            lastNum = Integer.parseInt(last.split("-")[1]);
        }
        String newnum;
        switch (lessonType) {
            case ("Einzelstunde"):
                newnum = lunchbreakOffset + lastNum + 1 + "";
                break;
            case ("Doppelstunde"):
                newnum = (lunchbreakOffset + lastNum + 1) + "-" + (lunchbreakOffset + lastNum + 2);
                break;
            default:
                newnum = "";
        }
        number.setText(newnum);
    }

    private void autofillTime(String last) {
        //breaks
        if (last.equals("10:10")) last = "10:35";
        if (last.equals("12:05")) last = "12:20";
        SharedPreferences prefs = this.getSharedPreferences("lunchbreak", MODE_PRIVATE);
        if (prefs.getBoolean("auto_lunchbreak", false)) {
            if (last.equals("13:05")) last = "13:40";
        }
        startTime.setText(last);

        int lastHour = Integer.parseInt(last.split(":")[0]);
        int lastMin = Integer.parseInt(last.split(":")[1]);
        String newTime;
        switch (lessonType) {
            case ("Einzelstunde"):
                lastMin += 45;
                break;
            case ("Doppelstunde"):
                lastMin += 90;
                break;
        }
        while (lastMin > 59) {
            lastMin -= 60;
            lastHour += 1;
        }
        newTime = (lastHour < 10 ? "0" : "") + lastHour + ":" + (lastMin < 10 ? "0" : "") + lastMin;
        stopTime.setText(newTime);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //back button
        switch (item.getItemId()) {
            case R.id.auto_lunchbreak:
                //whether a lunchbreak should be used in autofill()
                SharedPreferences prefs = this.getSharedPreferences("lunchbreak", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("auto_lunchbreak", !item.isChecked());
                editor.apply();
                item.setChecked(!item.isChecked());
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_lesson_menu, menu);
        SharedPreferences prefs = this.getSharedPreferences("lunchbreak", MODE_PRIVATE);
        menu.getItem(0).setChecked(prefs.getBoolean("auto_lunchbreak", false));
        return true;
    }
}

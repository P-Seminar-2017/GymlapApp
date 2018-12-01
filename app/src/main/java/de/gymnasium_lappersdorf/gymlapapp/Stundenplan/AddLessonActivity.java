package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;

import javax.inject.Inject;

import de.gymnasium_lappersdorf.gymlapapp.App;
import de.gymnasium_lappersdorf.gymlapapp.R;

public class AddLessonActivity extends AppCompatActivity {

    @Inject
    DatabaseHandler databaseHandler;

    Spinner typeSpinner, subjectSpinner;
    TextView startTime, stopTime;
    RelativeLayout startLayout, stopLayout;
    TextInputEditText number;
    Long day;

    String lessonType = "Doppelstunde";
    String selectedSubject;
    ArrayAdapter<String> subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        App.appComponent.inject(this);

        setTitle("Stunde hinzufügen");

        //view initialisation
        typeSpinner = findViewById(R.id.TypeSpinner);
        subjectSpinner = findViewById(R.id.SubjectSpinner);
        startTime = findViewById(R.id.starttime);
        stopTime = findViewById(R.id.stoptime);
        startLayout = findViewById(R.id.layoutstart);
        stopLayout = findViewById(R.id.layoutstop);
        number = findViewById(R.id.number);

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
        //subjectSpinner initialisation
        ArrayList<String> subjects = new ArrayList<>();
        for (Subject s : databaseHandler.getSubjects()) {
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
            Subject subject = databaseHandler.getSubject(this.selectedSubject);
            Lesson lesson = new Lesson(0, number.getText().toString(), startTime.getText().toString(), stopTime.getText().toString());
            day.lessons.add(lesson);
            subject.lessons.add(lesson);
            databaseHandler.setDay(day);
            databaseHandler.setSubject(subject);
            finish();
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

    public void addSubject(View view) {
        View v = getLayoutInflater().inflate(R.layout.add_subject_view, null);
        final TextInputEditText subject, teacher, room, course;
        subject = v.findViewById(R.id.subject);
        teacher = v.findViewById(R.id.teacher);
        room = v.findViewById(R.id.room);
        course = v.findViewById(R.id.course);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setTitle("Fach hinzufügen");
        builder.setPositiveButton("Hinzufügen", null);//set to null -> overriding onClick
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (subject.getText().toString().equals("")) {
                            //Name empty
                            subject.setError("Darf nicht leer sein");
                        } else {
                            if (databaseHandler.getSubject(subject.getText().toString()) != null) {
                                //Subject already exists
                                subject.setError("Fach existiert bereits");
                            } else {
                                //valid input
                                Subject newsubject = new Subject(0, subject.getText().toString(), course.getText().toString(), teacher.getText().toString(), room.getText().toString());
                                databaseHandler.setSubject(newsubject);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_lesson_menu, menu);
        SharedPreferences prefs = this.getSharedPreferences("lunchbreak", MODE_PRIVATE);
        menu.getItem(0).setChecked(prefs.getBoolean("auto_lunchbreak", false));
        return true;
    }
}

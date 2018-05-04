package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.gymnasium_lappersdorf.gymlapapp.R;

public class Stundenplanview extends RelativeLayout {

    //TextViews for showcasing properties of a lesson
    //*view*1 / *view*2 is referring to double hour lessons
    TextView hour1, hour2, time1, time2, lesson, course, teacher, room, breakTime, breakTitle;
    //Simple view thats seperates the hour from the lesson name
    View seperator;
    //ViewGroup that contains the 2nd hour of a double lesson; needs to be
    //hidden, if it's a single lesson
    LinearLayout group2;
    //Parent layout of view
    RelativeLayout lesson_outer;
    LinearLayout break_outer;

    public Stundenplanview(Context context) {
        super(context);
        init();
    }

    public Stundenplanview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Stundenplanview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //initializes all the needed views
        inflate(getContext(), R.layout.stundenplanview, this);
        hour1 = findViewById(R.id.stundenplan_hour1);
        hour2 = findViewById(R.id.stundenplan_hour2);
        time1 = findViewById(R.id.stundenplan_time1);
        time2 = findViewById(R.id.stundenplan_time2);
        lesson = findViewById(R.id.stundenplan_lesson);
        course = findViewById(R.id.stundenplan_course);
        teacher = findViewById(R.id.stundenplan_teacher);
        room = findViewById(R.id.stundenplan_room);
        seperator = findViewById(R.id.stundenplan_seperator);
        group2 = findViewById(R.id.stundenplan_hour_group2);
        lesson_outer = findViewById(R.id.lesson_outer);
        break_outer = findViewById(R.id.break_outer);
        breakTime = findViewById(R.id.stundenplan_breakTime);
        breakTitle = findViewById(R.id.stundenplan_breakTitle);
    }

    //setters

    public void setParams(Stunde s) {
        int i = s.getType();
        setType(i);
        switch (i) {
            case 0:
                setHour1(((Einzelstunde) s).getHour());
                setTime1(((Einzelstunde) s).getStart() + "-\n" + ((Einzelstunde) s).getEnd());
                setLesson(((Einzelstunde) s).getLesson());
                setCourse(((Einzelstunde) s).getCourse());
                setTeacher(((Einzelstunde) s).getTeacher());
                setRoom(((Einzelstunde) s).getRoom());
                break;
            case 1:
                setHour1(((Doppelstunde) s).getHour1());
                setHour2(((Doppelstunde) s).getHour2());
                setTime1(((Doppelstunde) s).getHour1start() + "-\n" + ((Doppelstunde) s).getHour1end());
                setTime2(((Doppelstunde) s).getHour2start() + "-\n" + ((Doppelstunde) s).getHour2end());
                setLesson(((Doppelstunde) s).getLesson());
                setCourse(((Doppelstunde) s).getCourse());
                setTeacher(((Doppelstunde) s).getTeacher());
                setRoom(((Doppelstunde) s).getRoom());
                break;
            case 2:
                setBreakTime(((Pause) s).getStart() + "-" + ((Pause) s).getEnd());
                setBreakTitle(((Pause) s).getTitle());
                break;

        }


    }

    private void setHour1(String hour1) {
        this.hour1.setText(hour1);
    }

    private void setHour2(String hour2) {
        this.hour2.setText(hour2);
    }

    private void setTime1(String time1) {
        this.time1.setText(time1);
    }

    private void setTime2(String time2) {
        this.time2.setText(time2);
    }

    private void setLesson(String lesson) {
        this.lesson.setText(lesson);
    }

    private void setCourse(String course) {
        this.course.setText(course);
    }

    private void setTeacher(String teacher) {
        this.teacher.setText(teacher);
    }

    private void setRoom(String room) {
        this.room.setText(room);
    }

    private void setBreakTime(String breakTime) {
        this.breakTime.setText(breakTime);
    }

    private void setBreakTitle(String breakTitle) {
        this.breakTitle.setText(breakTitle);
    }

    //sets the type of hour: break, double- or single hour;
    private void setType(int n) {
        switch (n) {
            case 0:
                //single-hour
                lesson_outer.setVisibility(VISIBLE);
                group2.setVisibility(GONE);
                break_outer.setVisibility(GONE);
                break;
            case 1:
                //double-hour
                lesson_outer.setVisibility(VISIBLE);
                group2.setVisibility(VISIBLE);
                break_outer.setVisibility(GONE);
                break;
            case 2:
                //break
                lesson_outer.setVisibility(GONE);
                group2.setVisibility(GONE);
                break_outer.setVisibility(VISIBLE);
        }
    }
}

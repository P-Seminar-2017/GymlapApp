package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.gymnasium_lappersdorf.gymlapapp.R;

public class Stundenplanview extends RelativeLayout {

    //java callback-f***ery
    public interface ClickResponse {
        void singleClickResponse();

        void longClickResponse();
    }

    public ClickResponse clickResponse = null;

    public void setClickResponse(ClickResponse clickResponse) {
        this.clickResponse = clickResponse;
    }

    TextView number, subject, time, course, teacher, room;
    CardView layout, expansion;
    ImageView arrow;
    public boolean expanded;
    Context context;

    public Stundenplanview(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public Stundenplanview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public Stundenplanview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View v = inflate(getContext(), R.layout.stundenplanview, this);
        number = v.findViewById(R.id.number);
        subject = v.findViewById(R.id.subject);
        time = v.findViewById(R.id.time);
        course = v.findViewById(R.id.course);
        teacher = v.findViewById(R.id.teacher);
        room = v.findViewById(R.id.room);
        arrow = v.findViewById(R.id.arrow);
        layout = v.findViewById(R.id.cardView);
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickResponse.singleClickResponse();
            }
        });
        layout.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickResponse.longClickResponse();
                return true;
            }
        });
        expansion = v.findViewById(R.id.expansion);
    }

    public void toggleExpansion() {
        if (expanded) {
            expansion.setVisibility(GONE);
            expanded = false;
            arrow.setImageDrawable(context.getDrawable(R.drawable.arrowdown));
        } else {
            expansion.setVisibility(VISIBLE);
            expanded = true;
            arrow.setImageDrawable(context.getDrawable(R.drawable.arrowup));
        }
    }

    public void setLesson(Lesson lesson) {
        number.setText(lesson.getNumber());
        String timeString = lesson.getStart() + "-" + lesson.getEnd();
        time.setText(timeString);
        Subject subject = lesson.getSubject().getTarget();
        this.subject.setText(subject.getName());
        this.course.setText(String.format("Kurs: %s", subject.getCourse()));
        this.teacher.setText(String.format("Lehrer: %s", subject.getTeacher()));
        this.room.setText(String.format("Raum: %s", subject.getRoom()));
    }
}

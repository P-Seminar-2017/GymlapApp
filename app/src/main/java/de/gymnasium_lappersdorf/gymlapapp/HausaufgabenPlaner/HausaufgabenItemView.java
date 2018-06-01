package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * 01.06.2018 | created by Lukas S
 */

public class HausaufgabenItemView extends LinearLayout {
    private CardView cardView;
    private TextView viewType, viewFach, viewKlasseStufe, viewHausaufgaben;
    private Button btnReminder;

    private Hausaufgabe hw;

    public interface Callback {
        void onClick();
    }

    public HausaufgabenItemView(Context context) {
        super(context);
        init();
    }

    public HausaufgabenItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HausaufgabenItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.hausaufgabenitemview, this);
        cardView =  findViewById(R.id.homework_card_view);
        viewType = findViewById(R.id.text_type);
        viewFach = findViewById(R.id.text_fach);
        viewKlasseStufe = findViewById(R.id.text_klasse_stufe);
        viewHausaufgaben = findViewById(R.id.text_homework);
        btnReminder = findViewById(R.id.reminder_btn);
    }

    public void setHomework(Hausaufgabe hw) {
        this.hw = hw;
        switch (hw.getType()) {
            case DATE:
                viewType.setText("Hausaufgabe/Notiz für " + hw.getDateFormatted());
                break;
            case NEXT:
                viewType.setText("Hausaufgabe aufgegeben am " + hw.getDateFormatted() + ", Abgabe: Nächste Stunde");
                break;
            case NEXT2:
                viewType.setText("Hausaufgabe aufgegeben am " + hw.getDateFormatted() + ", Abgabe: Übernächste Stunde");
                break;
        }

        viewFach.setText(hw.getFach());
        viewKlasseStufe.setText(hw.getStufe() + " | " + hw.getKurs());
        viewHausaufgaben.setText(hw.getText());
    }

    public void setRemindButtonCallback(final Callback callback) {
        this.btnReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick();
            }
        });
    }

}

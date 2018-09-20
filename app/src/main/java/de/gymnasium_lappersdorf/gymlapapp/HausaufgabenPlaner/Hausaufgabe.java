package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

import de.gymnasium_lappersdorf.gymlapapp.R;

/**
 * Created by lmatn on 01.03.2018.
 */

public class Hausaufgabe {
    private long databaseId; //-1 if not in database already
    private long internetId; //-1 if not from internet
    private String fach;
    private String text;
    private long date;
    private boolean done;
    private int stufe;
    private String kurs;
    private Types type;


    public Hausaufgabe(String fach, String text, long date, int stufe, String kurs, Types type) {
        this.internetId = -1;
        this.databaseId = -1;
        this.fach = fach;
        this.text = text;
        this.date = date;
        this.stufe = stufe;
        this.kurs = kurs;
        this.done = false;
        this.type = type;
    }

    public enum Types {
        DATE, NEXT, NEXT2
    }

    public String getDateFormatted() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        return String.format("%s.%s.%s",
                c.get(Calendar.DATE),
                (c.get(Calendar.MONTH) + 1),
                c.get(Calendar.YEAR));
    }

    public String getDayOfWeek(Context ctx) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        return ctx.getResources().getStringArray(R.array.tage)[c.get(Calendar.DAY_OF_WEEK)-2];
    }

    public long getInternetId() {
        return internetId;
    }

    public void setInternetId(long internetId) {
        this.internetId = internetId;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setFach(String fach) {
        this.fach = fach;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(long date) {
        this.date = date;
    }

    public void setStufe(int stufe) {
        this.stufe = stufe;
    }

    public void setKurs(String kurs) {
        this.kurs = kurs;
    }

    public long getTimestamp() {
        return date;
    }

    public String getFach() {
        return fach;
    }

    public String getText() {
        return text;
    }

    public boolean isDone() {
        return done;
    }

    public String getKurs() {
        return kurs;
    }

    public int getStufe() {
        return stufe;
    }

    public Types getType() {
        return type;
    }

    public boolean isFromInternet() {
        return internetId != -1;
    }

    @Override
    public boolean equals(Object obj) {

        if (this.getClass() == obj.getClass()) {
            Hausaufgabe other = (Hausaufgabe) obj;

            if (this.databaseId == other.getDatabaseId() && !isFromInternet()) {
                return true;
            } else if (this.internetId == other.getInternetId() && isFromInternet()) {
                return true;
            }
        }

        return false;
    }
}


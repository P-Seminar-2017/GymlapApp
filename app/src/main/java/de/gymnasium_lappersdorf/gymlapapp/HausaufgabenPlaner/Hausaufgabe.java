package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lmatn on 01.03.2018.
 */

public class Hausaufgabe {
    private int databaseId;
    private int internetId;
    private String fach;
    private String text;
    private long date;
    private boolean done;
    private int stufe;
    private String kurs;
    private Types type;
    private boolean fromInternet;


    public Hausaufgabe(int internetId, String fach, String text, long date, int stufe, String kurs, Types type, boolean fromInternet) {
        this.internetId = internetId;
        this.fach = fach;
        this.text = text;
        this.date = date;
        this.stufe = stufe;
        this.kurs = kurs;
        this.done = false;
        this.type = type;
        this.fromInternet = fromInternet;
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


    public int getInternetId() {
        return internetId;
    }

    public void setInternetId(int internetId) {
        this.internetId = internetId;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int databaseId) {
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
        return fromInternet;
    }
}


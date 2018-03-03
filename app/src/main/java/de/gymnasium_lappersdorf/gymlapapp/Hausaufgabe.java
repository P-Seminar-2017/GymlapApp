package de.gymnasium_lappersdorf.gymlapapp;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lmatn on 01.03.2018.
 */

public class Hausaufgabe {
    private int mid;
    private String fach;
    private String quest;
    private long date;
    private boolean done;
    private int stufe;
    private String kurs;
    private Types types;


    public Hausaufgabe(int id, String fach, String quest, long date, int stufe, String kurs, Types types) {
        this.mid = id;
        this.fach = fach;
        this.quest = quest;
        this.date = date;
        this.stufe = stufe;
        this.kurs = kurs;
        done = false;
        this.types = types;

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


    public int getId() {
        return mid;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public void setFach(String fach) {
        this.fach = fach;
    }

    public void setQuest(String quest) {
        this.quest = quest;
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

    public String getQuest() {
        return quest;
    }

    public boolean getDOne() {
        return done;
    }

    public String getKurs() {
        return kurs;
    }

    public int getStufe() {
        return stufe;
    }

    public Types getTypes() {
        return types;
    }
}


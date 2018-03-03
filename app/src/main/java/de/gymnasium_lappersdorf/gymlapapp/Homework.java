package de.gymnasium_lappersdorf.gymlapapp;

import java.util.Calendar;
import java.util.Date;

/**
 * 21.01.2018
 * Created by Lukas S
 */


public class Homework {
    private int id;
    private long date;
    private Types type;
    private String fach, klasse, stufe, text;

    public enum Types {
        DATE, NEXT, NEXT2
    }

    public Homework(int id, long date, Types type, String fach, String klasse, String stufe, String text) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.fach = fach;
        this.klasse = klasse;
        this.stufe = stufe;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public String getDateFormatted() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(date));
        return String.format("%s.%s.%s",
                c.get(Calendar.DATE),
                (c.get(Calendar.MONTH) + 1),
                c.get(Calendar.YEAR));
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getFach() {
        return fach;
    }

    public void setFach(String fach) {
        this.fach = fach;
    }

    public String getKlasse() {
        return klasse;
    }

    public void setKlasse(String klasse) {
        this.klasse = klasse;
    }

    public String getStufe() {
        return stufe;
    }

    public void setStufe(String stufe) {
        this.stufe = stufe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return String.format("[%s, %s, %s, %s, %s, %s, %s]", id, type.name(), getDateFormatted(), fach, klasse, stufe, text);
    }
}

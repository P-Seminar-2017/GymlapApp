package de.gymnasium_lappersdorf.gymlapapp;

import android.os.Parcel;

/**
 * Created by Leon on 02.12.2017.
 */

public class Einzelstunde extends Stunde {

    String hour, start, end, lesson, course, teacher, room;
    int id;
    final static int TYPE = 0;

    public Einzelstunde(String hour, String start, String end, String lesson, String course, String teacher, String room) {
        this.hour = hour;
        this.start = start;
        this.end = end;
        this.lesson = lesson;
        this.course = course;
        this.teacher = teacher;
        this.room = room;
    }

    //setters

    public void setId(int id){
        this.id = id;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    //getters

    public int getID(){
        return id;
    }

    public String getHour() {
        return hour;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getLesson() {
        return lesson;
    }

    public String getCourse() {
        return course;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRoom() {
        return room;
    }

    //returns type of Hour: 0=single-hour, 1=double-hour, 2=break
    public int getType() {
        return TYPE;
    }


}

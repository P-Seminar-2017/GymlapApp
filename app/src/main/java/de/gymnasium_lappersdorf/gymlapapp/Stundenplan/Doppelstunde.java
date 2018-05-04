package de.gymnasium_lappersdorf.gymlapapp.Stundenplan;

/**
 * Created by Leon on 02.12.2017.
 */

public class Doppelstunde extends Stunde {

    String hour1, hour2, hour1start, hour1end, hour2start, hour2end, lesson, course, teacher, room;
    int id;
    final static int TYPE = 1;

    public Doppelstunde(String hour1, String hour2, String hour1start, String hour1end, String hour2start, String hour2end, String lesson, String course, String teacher, String room) {
        this.hour1 = hour1;
        this.hour2 = hour2;
        this.hour1start = hour1start;
        this.hour1end = hour1end;
        this.hour2start = hour2start;
        this.hour2end = hour2end;
        this.lesson = lesson;
        this.course = course;
        this.teacher = teacher;
        this.room = room;
    }

    //setters

    public void setId(int id){
        this.id = id;
    }

    public void setHour1(String hour1) {
        this.hour1 = hour1;
    }

    public void setHour2(String hour2) {
        this.hour2 = hour2;
    }

    public void setHour1start(String hour1start) {
        this.hour1start = hour1start;
    }

    public void setHour1end(String hour1end) {
        this.hour1end = hour1end;
    }

    public void setHour2start(String hour2start) {
        this.hour2start = hour2start;
    }

    public void setHour2end(String hour2end) {
        this.hour2end = hour2end;
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

    public String getHour1() {
        return hour1;
    }

    public String getHour2() {
        return hour2;
    }

    public String getHour1start() {
        return hour1start;
    }

    public String getHour1end() {
        return hour1end;
    }

    public String getHour2start() {
        return hour2start;
    }

    public String getHour2end() {
        return hour2end;
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



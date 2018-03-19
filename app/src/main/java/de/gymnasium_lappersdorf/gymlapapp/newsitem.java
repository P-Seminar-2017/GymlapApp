package de.gymnasium_lappersdorf.gymlapapp;

/**
 * Created by Leon on 03.01.2018.
 */

public class newsitem {

    String date, title, href, img, txt;

    public newsitem(String date, String title, String href, String img, String txt) {
        this.date = date;
        this.title = title;
        this.href = href;
        this.img = img;
        this.txt = txt;
    }

    //getters

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public String getImg() {
        return img;
    }

    public String getTxt() {
        return txt;
    }

    //setters

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}

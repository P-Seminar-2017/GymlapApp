package de.gymnasium_lappersdorf.gymlapapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Leon on 02.12.2017.
 */

public class Pause extends Stunde {

    String start, end, title;
    int id;
    final static int TYPE = 2;

    public Pause(String start, String end, String title) {
        this.start = start;
        this.end = end;
        this.title = title;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(start);
        parcel.writeString(end);
        parcel.writeString(title);
        parcel.writeInt(id);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Pause createFromParcel(Parcel in) {
            return new Pause(in);
        }

        public Pause[] newArray(int size) {
            return new Pause[size];
        }
    };

    public Pause(Parcel in){
        start = in.readString();
        end =in.readString();
        title = in.readString();
        id = in.readInt();
    }
    
    
    
    //setters

    public void setId(int id){
        this.id = id;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    //getters

    public int getID(){
        return id;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String getTitle() {
        return title;
    }

    //returns type of Hour: 0=single-hour, 1=double-hour, 2=break
    public int getType() {
        return TYPE;
    }


    @Override
    public int describeContents() {
        return 0;
    }

  
}

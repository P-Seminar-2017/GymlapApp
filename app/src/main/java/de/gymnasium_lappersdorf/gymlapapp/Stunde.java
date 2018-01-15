package de.gymnasium_lappersdorf.gymlapapp;

import android.os.Parcelable;

/**
 * Created by Leon on 17.11.2017.
 */

public abstract class Stunde implements Parcelable{

    //returns type of Hour: 0=single-hour, 1=double-hour, 2=break
    public abstract int getType();

    //returns primary key of the object in the database
    public abstract int getID();


}

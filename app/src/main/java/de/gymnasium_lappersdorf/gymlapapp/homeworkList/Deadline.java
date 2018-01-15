package de.gymnasium_lappersdorf.gymlapapp.homeworkList;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import de.gymnasium_lappersdorf.gymlapapp.Stunde;

/**
 * Created by jonas on 1/15/2018.
 */
public class Deadline implements Parcelable, Comparable<Deadline> {

    public static byte TYPE_HOUR = 0;
    public static byte TYPE_DATE = 1;

    private byte type;
    private Stunde stunde;
    private long timestamp =-1;

    public Deadline(Stunde stunde) {
        this.stunde = stunde;
        type = TYPE_HOUR;
    }

    public Deadline(long timestamp){
        type = TYPE_DATE;
        this.timestamp = timestamp;
    }

    protected Deadline(Parcel in) {
        type = in.readByte();
        stunde = in.readParcelable(Stunde.class.getClassLoader());
        timestamp = in.readLong();
    }

    public static final Creator<Deadline> CREATOR = new Creator<Deadline>() {
        @Override
        public Deadline createFromParcel(Parcel in) {
            return new Deadline(in);
        }

        @Override
        public Deadline[] newArray(int size) {
            return new Deadline[size];
        }
    };

    public Stunde getStunde() {
        return stunde;
    }

    public long getTimestamp() {

        //todo add imnplementation in the our case
        return timestamp;
    }

    /**
     * @return determines wheter the set deadline is an class hour or a set date
     */
    public byte getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }




    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(type);
        dest.writeLong(timestamp);
        dest.writeParcelable(stunde , 0);
    }

    @Override
    public int compareTo(@NonNull Deadline deadline) {

        long thisTimestamp = 0;
        long compareTimestamp = 0;
        if(deadline.getType() == TYPE_HOUR){
            Stunde hour = deadline.getStunde();
            //todo implement and update the current timstamp
        }else {
            compareTimestamp = deadline.getTimestamp();
        }
        if(this.getType() == TYPE_HOUR){
            //todo implement
        }else{
            thisTimestamp = this.getTimestamp();
        }

        if(thisTimestamp < compareTimestamp)
            return -1;

        if(thisTimestamp > compareTimestamp){
            return 1;
        }

        return 0;


    }
}
package de.gymnasium_lappersdorf.gymlapapp.homeworkList;

import android.os.Parcel;
import android.os.Parcelable;

import de.gymnasium_lappersdorf.gymlapapp.homeworkList.Deadline;

/**
 * Created by jonas on 1/15/2018.
 * A class representation of a homework
 */

public class HomeworkModel implements Parcelable{
    private String message;
    private String courseName;
    private Deadline deadline;
    private long durationSeconds;
    private boolean done;

    public HomeworkModel(String message, String courseName,Deadline deadline, long durationSeconds, boolean done) {
        this.message = message;
        this.courseName = courseName;
        this.deadline = deadline;
        this.done = done;
        this.durationSeconds = durationSeconds;
    }

    public boolean isDone() {
        return done;
    }


    public String getCourseName() {
        return courseName;
    }

    protected HomeworkModel(Parcel in) {
        message = in.readString();
        deadline = in.readParcelable(Deadline.class.getClassLoader());
        durationSeconds = in.readLong();
    }

    public static final Creator<HomeworkModel> CREATOR = new Creator<HomeworkModel>() {
        @Override
        public HomeworkModel createFromParcel(Parcel in) {
            return new HomeworkModel(in);
        }

        @Override
        public HomeworkModel[] newArray(int size) {
            return new HomeworkModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeParcelable(deadline, 0);
        dest.writeLong(durationSeconds);
    }

    public String getMessage() {
        return message;
    }

    public Deadline getDeadline() {
        return deadline;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }
}



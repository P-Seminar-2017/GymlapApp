package de.gymnasium_lappersdorf.gymlapapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 02.12.2017.
 */

public class StundenplanDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "stundenplan";
    private String TABLE_HOURS; //Table that corresponds to each day

    //Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_HOUR1 = "hour1";
    private static final String KEY_HOUR2 = "hour2";
    private static final String KEY_HOUR1Start = "hour1start";
    private static final String KEY_HOUR2START = "hour2start";
    private static final String KEY_HOUR1STOP = "hour1end";
    private static final String KEY_HOUR2STOP = "hour2end";
    private static final String KEY_TITLE = "tile";
    private static final String KEY_COURSE = "course";
    private static final String KEY_TEACHER = "teacher";
    private static final String KEY_ROOM = "room";
    private static final String KEY_TYPE = "type";

    public StundenplanDatabaseHandler(Context context, String table) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        TABLE_HOURS = table;
    }



    //Creates new Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //hardcoded, suckers :P
        //will be revamped later
        String CREATE_CONTACTS_TABLE1 = "CREATE TABLE " + "day0" + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HOUR1 + " TEXT," + KEY_HOUR2
                + " TEXT," + KEY_HOUR1Start + " TEXT," + KEY_HOUR2START + " TEXT," + KEY_HOUR1STOP
                + " TEXT," + KEY_HOUR2STOP + " TEXT," + KEY_TITLE + " TEXT," + KEY_COURSE + " TEXT,"
                + KEY_TEACHER + " TEXT," + KEY_ROOM + " TEXT," + KEY_TYPE + " INT" + ")";
        String CREATE_CONTACTS_TABLE2 = "CREATE TABLE " + "day1" + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HOUR1 + " TEXT," + KEY_HOUR2
                + " TEXT," + KEY_HOUR1Start + " TEXT," + KEY_HOUR2START + " TEXT," + KEY_HOUR1STOP
                + " TEXT," + KEY_HOUR2STOP + " TEXT," + KEY_TITLE + " TEXT," + KEY_COURSE + " TEXT,"
                + KEY_TEACHER + " TEXT," + KEY_ROOM + " TEXT," + KEY_TYPE + " INT" + ")";
        String CREATE_CONTACTS_TABLE3 = "CREATE TABLE " + "day2" + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HOUR1 + " TEXT," + KEY_HOUR2
                + " TEXT," + KEY_HOUR1Start + " TEXT," + KEY_HOUR2START + " TEXT," + KEY_HOUR1STOP
                + " TEXT," + KEY_HOUR2STOP + " TEXT," + KEY_TITLE + " TEXT," + KEY_COURSE + " TEXT,"
                + KEY_TEACHER + " TEXT," + KEY_ROOM + " TEXT," + KEY_TYPE + " INT" + ")";
        String CREATE_CONTACTS_TABLE4 = "CREATE TABLE " + "day3" + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HOUR1 + " TEXT," + KEY_HOUR2
                + " TEXT," + KEY_HOUR1Start + " TEXT," + KEY_HOUR2START + " TEXT," + KEY_HOUR1STOP
                + " TEXT," + KEY_HOUR2STOP + " TEXT," + KEY_TITLE + " TEXT," + KEY_COURSE + " TEXT,"
                + KEY_TEACHER + " TEXT," + KEY_ROOM + " TEXT," + KEY_TYPE + " INT" + ")";
        String CREATE_CONTACTS_TABLE5 = "CREATE TABLE " + "day4" + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_HOUR1 + " TEXT," + KEY_HOUR2
                + " TEXT," + KEY_HOUR1Start + " TEXT," + KEY_HOUR2START + " TEXT," + KEY_HOUR1STOP
                + " TEXT," + KEY_HOUR2STOP + " TEXT," + KEY_TITLE + " TEXT," + KEY_COURSE + " TEXT,"
                + KEY_TEACHER + " TEXT," + KEY_ROOM + " TEXT," + KEY_TYPE + " INT" + ")";

        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE1);
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE2);
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE3);
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE4);
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE5);
    }

    //Overrides old Database with new Database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HOURS);
        onCreate(sqLiteDatabase);
    }

    //insert hour into database
    public void addHour(Stunde s){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        switch (s.getType()){
            case 0:
                //single-hour
                values.put(KEY_HOUR1, ((Einzelstunde) s).getHour());
                values.put(KEY_HOUR1Start, ((Einzelstunde) s).getStart());
                values.put(KEY_HOUR1STOP, ((Einzelstunde) s).getEnd());
                values.put(KEY_TITLE, ((Einzelstunde) s).getLesson());
                values.put(KEY_COURSE, ((Einzelstunde) s).getCourse());
                values.put(KEY_TEACHER, ((Einzelstunde) s).getTeacher());
                values.put(KEY_ROOM, ((Einzelstunde) s).getRoom());
                values.put(KEY_TYPE, s.getType());
                break;
            case 1:
                values.put(KEY_HOUR1, ((Doppelstunde) s).getHour1());
                values.put(KEY_HOUR2, ((Doppelstunde) s).getHour2());
                values.put(KEY_HOUR1Start, ((Doppelstunde) s).getHour1start());
                values.put(KEY_HOUR1STOP, ((Doppelstunde) s).getHour1end());
                values.put(KEY_HOUR2START, ((Doppelstunde) s).getHour2start());
                values.put(KEY_HOUR2STOP, ((Doppelstunde) s).getHour2end());
                values.put(KEY_TITLE, ((Doppelstunde) s).getLesson());
                values.put(KEY_COURSE, ((Doppelstunde) s).getCourse());
                values.put(KEY_TEACHER, ((Doppelstunde) s).getTeacher());
                values.put(KEY_ROOM, ((Doppelstunde) s).getRoom());
                values.put(KEY_TYPE, s.getType());
                //double-hour
                break;
            case 2:
                //break
                values.put(KEY_HOUR1Start, ((Pause) s).getStart());
                values.put(KEY_HOUR1STOP, ((Pause) s).getEnd());
                values.put(KEY_TITLE, ((Pause) s).getTitle());
                values.put(KEY_TYPE, s.getType());
                break;
        }
        db.insert(TABLE_HOURS, null, values);
        db.close();
    }

    //read hour form Database
    public Stunde getHour(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HOURS, new String[]{KEY_ID, KEY_HOUR1, KEY_HOUR2, KEY_HOUR1Start,
        KEY_HOUR2START, KEY_HOUR1STOP, KEY_HOUR2STOP, KEY_TITLE, KEY_COURSE, KEY_TEACHER, KEY_ROOM, KEY_TYPE}, KEY_ID +
        "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        //checks wether saved object is single-, double-hour or break
        //and returns the object
        switch (Integer.parseInt(cursor.getString(11))){
            case 0:
                 Einzelstunde s = new Einzelstunde(cursor.getString(1),  cursor.getString(3), cursor.getString(5), cursor.getString(7)
                , cursor.getString(8), cursor.getString(9), cursor.getString(10));
                 s.setId(id);
                 return s;
            case 1:
                Doppelstunde d = new Doppelstunde(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(5)
                , cursor.getString(4), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9)
                , cursor.getString(10));
                d.setId(id);
                return d;
            case 2:
                Pause p = new Pause(cursor.getString(3), cursor.getString(5), cursor.getString(7));
                p.setId(id);
                return p;
        }
            return null;
    }

    //returns all hours in an array
    public Stunde[] getAllHours(){
        List<Stunde> hourlist = new ArrayList<Stunde>();
        String query = "SELECT * FROM " + TABLE_HOURS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            do {
                int id = Integer.parseInt(cursor.getString(0));
                hourlist.add(getHour(id));
            }while (cursor.moveToNext());
        }
        Stunde [] s = new Stunde[hourlist.size()];
        s = hourlist.toArray(s);
        return s;
    }

    //gets number of all
    public int getHourCount(){
        String query = "SELECT  * FROM " + TABLE_HOURS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        return cursor.getCount();
    }

    //updates an hour in the database with a new one
    public int updateHour(Stunde s){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        switch (s.getType()){
            case 0:
                //single-hour
                values.put(KEY_HOUR1, ((Einzelstunde) s).getHour());
                values.put(KEY_HOUR1Start, ((Einzelstunde) s).getStart());
                values.put(KEY_HOUR1STOP, ((Einzelstunde) s).getEnd());
                values.put(KEY_TITLE, ((Einzelstunde) s).getLesson());
                values.put(KEY_COURSE, ((Einzelstunde) s).getCourse());
                values.put(KEY_TEACHER, ((Einzelstunde) s).getTeacher());
                values.put(KEY_ROOM, ((Einzelstunde) s).getRoom());
                values.put(KEY_TYPE, s.getType());
                break;
            case 1:
                values.put(KEY_HOUR1, ((Doppelstunde) s).getHour1());
                values.put(KEY_HOUR2, ((Doppelstunde) s).getHour2());
                values.put(KEY_HOUR1Start, ((Doppelstunde) s).getHour1start());
                values.put(KEY_HOUR1STOP, ((Doppelstunde) s).getHour1end());
                values.put(KEY_HOUR2START, ((Doppelstunde) s).getHour2start());
                values.put(KEY_HOUR2STOP, ((Doppelstunde) s).getHour2end());
                values.put(KEY_TITLE, ((Doppelstunde) s).getLesson());
                values.put(KEY_COURSE, ((Doppelstunde) s).getCourse());
                values.put(KEY_TEACHER, ((Doppelstunde) s).getTeacher());
                values.put(KEY_ROOM, ((Doppelstunde) s).getRoom());
                values.put(KEY_TYPE, s.getType());
                //double-hour
                break;
            case 2:
                //break
                values.put(KEY_HOUR1Start, ((Pause) s).getStart());
                values.put(KEY_HOUR1STOP, ((Pause) s).getEnd());
                values.put(KEY_TITLE, ((Pause) s).getTitle());
                values.put(KEY_TYPE, s.getType());
                break;
        }


        // updating row
        return db.update(TABLE_HOURS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(s.getID()) });
    }

    //removes an hour from the database
    public void deleteHour(Stunde s){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HOURS, KEY_ID + "= ?", new String[]{String.valueOf(s.getID())});
        db.close();
    }

    public void setTable(String t){
        TABLE_HOURS = t;
    }

}

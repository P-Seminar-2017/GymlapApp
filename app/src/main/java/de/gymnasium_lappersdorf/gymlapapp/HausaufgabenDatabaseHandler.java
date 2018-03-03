package de.roze.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class HausaufgabenDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "hausaufgabenliste";


    //Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FACH = "fach";
    private static final String KEY_QUEST = "quest";
    private static final String KEY_TIMETOBEDONE = "timetobedone";
    private static final String KEY_DONE = "done";
    private static final String KEY_STUFE = "stufe";
    private static final String KEY_KURS = "kurs";

    public HausaufgabenDatabaseHandler(Context context, String table) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //Creates new Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE1 = "CREATE TABLE " + "" + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FACH + " TEXT," + KEY_QUEST
                + " TEXT," + KEY_TIMETOBEDONE + " TEXT," + KEY_DONE + " TEXT," + KEY_KURS
                + " TEXT," + KEY_STUFE + " TEXT" + ")";


        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE1);

    }

    //Overrides old Database with new Database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(sqLiteDatabase);
    }

    //insert homework into database
    public void addHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(KEY_ID, h.getId());
        values.put(KEY_FACH, h.getFach());
        values.put(KEY_QUEST, h.getQuest());
        values.put(KEY_TIMETOBEDONE, h.getTimestamp());
        values.put(KEY_DONE, h.getDOne());
        values.put(KEY_STUFE, h.getStufe());
        values.put(KEY_KURS, h.getKurs());


        db.insert(DATABASE_NAME, null, values);
        db.close();
    }

    //read Homework form Database
    public Hausaufgabe getHomework(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_NAME, new String[]{KEY_ID, KEY_FACH, KEY_QUEST, KEY_TIMETOBEDONE,
                KEY_DONE, KEY_KURS, KEY_STUFE,}, KEY_ID +
                "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return null;
    }


    //returns all homeworks in an array
    public Hausaufgabe[] getAllHomeworks() {
        List<Hausaufgabe> homeworklist = new ArrayList<Hausaufgabe>();
        String query = "SELECT * FROM ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                homeworklist.add(getHomework(id));
            } while (cursor.moveToNext());
        }
        Hausaufgabe[] s = new Hausaufgabe[homeworklist.size()];
        s = homeworklist.toArray(s);
        return s;
    }

    //gets number of all
    public int getHomeworkCount() {
        String query = "SELECT  * FROM ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        return cursor.getCount();
    }

    //updates a homework in the database with a new one
    public int updateHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, h.getId());
        values.put(KEY_FACH, h.getFach());
        values.put(KEY_QUEST, h.getQuest());
        values.put(KEY_TIMETOBEDONE, h.getTimestamp());
        values.put(KEY_DONE, h.getDOne());
        values.put(KEY_STUFE, h.getStufe());
        values.put(KEY_KURS, h.getKurs());


        // updating row
        return db.update(DATABASE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(h.getId())});
    }

    //removes an homework from the database
    public void deleteHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_NAME, KEY_ID + "= ?", new String[]{String.valueOf(h.getId())});
        db.close();
    }


}

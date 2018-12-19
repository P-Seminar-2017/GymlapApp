package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class HausaufgabenDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "hausaufgabenliste";
    private static final String DATABASE_TABLE = "hausaufgabentable";


    //Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_INTERNET_ID = "internet_id";
    private static final String KEY_NOTIFICATION_ID = "notification_id";
    private static final String KEY_FACH = "fach";
    private static final String KEY_TEXT = "quest";
    private static final String KEY_TIMETOBEDONE = "timetobedone";
    private static final String KEY_DONE = "done";
    private static final String KEY_STUFE = "stufe";
    private static final String KEY_KURS = "kurs";

    public HausaufgabenDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //Creates new Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE " + DATABASE_TABLE + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_INTERNET_ID + " INT," +
                KEY_NOTIFICATION_ID + " INT," +
                KEY_FACH + " TEXT," +
                KEY_TEXT + " TEXT," +
                KEY_TIMETOBEDONE + " TEXT," +
                KEY_DONE + " INT," +
                KEY_KURS + " TEXT," +
                KEY_STUFE + " INT" +
                ")";


        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    //Overrides old Database with new Database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    //insert homework into database
    public long addHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();


        values.put(KEY_INTERNET_ID, h.getInternetId());
        values.put(KEY_NOTIFICATION_ID, h.getNotificationId());
        values.put(KEY_FACH, h.getFach());
        values.put(KEY_TEXT, h.getText());
        values.put(KEY_TIMETOBEDONE, h.getTimestamp());
        values.put(KEY_DONE, h.isDone() ? 1 : 0);
        values.put(KEY_STUFE, h.getStufe());
        values.put(KEY_KURS, h.getKurs());


        long id = db.insert(DATABASE_TABLE, null, values);
        db.close();
        return id;
    }

    //read Homework from Database
    public Hausaufgabe getHomework(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_INTERNET_ID, KEY_NOTIFICATION_ID, KEY_FACH, KEY_TEXT, KEY_TIMETOBEDONE, KEY_DONE, KEY_KURS, KEY_STUFE},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Hausaufgabe h = new Hausaufgabe(cursor.getString(3), cursor.getString(4), Long.parseLong(cursor.getString(5)),
                cursor.getInt(8), cursor.getString(7), Hausaufgabe.Types.DATE);

        h.setDone(cursor.getInt(6) == 1);
        h.setDatabaseId(cursor.getLong(0));
        h.setInternetId(cursor.getLong(1));
        h.setNotificationId(cursor.getInt(2));

        cursor.close();
        return h;
    }


    //returns all homework in an array
    public Hausaufgabe[] getCompleteHomework() {
        List<Hausaufgabe> homeworklist = new ArrayList<>();
        String query = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                homeworklist.add(getHomework(id));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return homeworklist.toArray(new Hausaufgabe[0]);
    }

    //gets number of all
    public int getHomeworkCount() {
        String query = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //updates a homework in the database with a new one
    public int updateHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INTERNET_ID, h.getInternetId());
        values.put(KEY_NOTIFICATION_ID, h.getNotificationId());
        values.put(KEY_FACH, h.getFach());
        values.put(KEY_TEXT, h.getText());
        values.put(KEY_TIMETOBEDONE, h.getTimestamp());
        values.put(KEY_DONE, h.isDone());
        values.put(KEY_STUFE, h.getStufe());
        values.put(KEY_KURS, h.getKurs());


        // updating row
        return db.update(DATABASE_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(h.getDatabaseId())});
    }

    //removes an homework from the database
    public void deleteHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_ID + " = ?", new String[]{String.valueOf(h.getDatabaseId())});
        db.close();
    }

    public String[] getKurse(int stufe) {
        ArrayList<String> homeworklist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_KURS},
                KEY_STUFE + "=?",
                new String[]{String.valueOf(stufe)}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                String kurs = cursor.getString(0);
                if (!homeworklist.contains(kurs)) homeworklist.add(kurs);
            } while (cursor.moveToNext());
        }

        return homeworklist.toArray(new String[0]);
    }

}

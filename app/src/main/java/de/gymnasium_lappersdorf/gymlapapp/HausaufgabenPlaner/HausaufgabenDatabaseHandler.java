package de.gymnasium_lappersdorf.gymlapapp.HausaufgabenPlaner;

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
    private static final String DATABASE_TABLE = "hausaufgabentable";


    //Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_INTERNET_ID = "internet_id";
    private static final String KEY_FACH = "fach";
    private static final String KEY_TEXT = "quest";
    private static final String KEY_TIMETOBEDONE = "timetobedone";
    private static final String KEY_DONE = "done";
    private static final String KEY_STUFE = "stufe";
    private static final String KEY_KURS = "kurs";
    private static final String KEY_FROMINTERNET = "frominternet";

    public HausaufgabenDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //Creates new Database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE1 = "CREATE TABLE " + DATABASE_TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_INTERNET_ID + " INT," + KEY_FACH + " TEXT," + KEY_TEXT
                + " TEXT," + KEY_TIMETOBEDONE + " TEXT," + KEY_DONE + " INT," + KEY_KURS
                + " TEXT," + KEY_STUFE + " INT," + KEY_FROMINTERNET + " INT" + ")";


        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE1);

    }

    //Overrides old Database with new Database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }

    //insert homework into database
    public void addHomework(Hausaufgabe h) {
        Hausaufgabe[] hausis = getAllHomeworks();

        //Only insert if not existing already
        for (Hausaufgabe hausi : hausis) {
            if (h.getDatabaseId() == hausi.getDatabaseId()) {
                updateHomework(h);
            } else {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues values = new ContentValues();


                values.put(KEY_INTERNET_ID, h.getInternetId());
                values.put(KEY_FACH, h.getFach());
                values.put(KEY_TEXT, h.getText());
                values.put(KEY_TIMETOBEDONE, h.getTimestamp());
                values.put(KEY_DONE, h.isDone() ? 1 : 0);
                values.put(KEY_STUFE, h.getStufe());
                values.put(KEY_KURS, h.getKurs());
                values.put(KEY_FROMINTERNET, h.isFromInternet() ? 1 : 0);


                db.insert(DATABASE_TABLE, null, values);
                db.close();
            }
        }
    }

    //read Homework from Database
    public Hausaufgabe getHomework(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_INTERNET_ID, KEY_FACH, KEY_TEXT, KEY_TIMETOBEDONE,
                KEY_DONE, KEY_KURS, KEY_STUFE, KEY_FROMINTERNET}, KEY_ID +
                "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Hausaufgabe h = new Hausaufgabe(cursor.getInt(1), cursor.getString(2), cursor.getString(3), Long.parseLong(cursor.getString(4)),
                cursor.getInt(7), cursor.getString(6), Hausaufgabe.Types.DATE, cursor.getInt(8) == 1);
        h.setDone(cursor.getInt(5) == 1);
        h.setDatabaseId(cursor.getInt(0));

        cursor.close();
        return h;
    }


    //returns all homeworks in an array
    public Hausaufgabe[] getAllHomeworks() {
        List<Hausaufgabe> homeworklist = new ArrayList<Hausaufgabe>();
        String query = "SELECT * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                homeworklist.add(getHomework(id));
            } while (cursor.moveToNext());
        }
        cursor.close();
        Hausaufgabe[] s = new Hausaufgabe[homeworklist.size()];
        s = homeworklist.toArray(s);
        return s;
    }

    //gets number of all
    public int getHomeworkCount() {
        String query = "SELECT  * FROM " + DATABASE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.close();
        return cursor.getCount();
    }

    //updates a homework in the database with a new one
    public int updateHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INTERNET_ID, h.getInternetId());
        values.put(KEY_FACH, h.getFach());
        values.put(KEY_TEXT, h.getText());
        values.put(KEY_TIMETOBEDONE, h.getTimestamp());
        values.put(KEY_DONE, h.isDone());
        values.put(KEY_STUFE, h.getStufe());
        values.put(KEY_KURS, h.getKurs());
        values.put(KEY_FROMINTERNET, h.isFromInternet());


        // updating row
        return db.update(DATABASE_TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(h.getDatabaseId())});
    }

    //removes an homework from the database
    public void deleteHomework(Hausaufgabe h) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, KEY_ID + "= ?", new String[]{String.valueOf(h.getDatabaseId())});
        db.close();
    }


}

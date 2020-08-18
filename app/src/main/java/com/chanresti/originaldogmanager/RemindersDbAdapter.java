package com.chanresti.originaldogmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mwangi on 13/03/2018.
 */

public class RemindersDbAdapter {
    //these are the column names
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_DATESTRING="datestring";
    public static final String COL_IMPORTANT = "important";
    public static final String COL_MILLS="tmills";

    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_DATESTRING = INDEX_ID + 2;
    public static final int INDEX_IMPORTANT = INDEX_ID + 3;
    //used for logging
    private static final String TAG = "RemindersAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "dba_remindrs";
    private static final String TABLE_NAME = "tbl_remindrs";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;
    //SQL statement used to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT, " +
                    COL_DATESTRING + " TEXT, " +
                    COL_IMPORTANT + " INTEGER, " +
                    COL_MILLS + " INTEGER );";

    public RemindersDbAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }
    //open
    public void open() throws SQLException {
        Log.d("MainTag"+TAG, "open database");
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }}
        //CREATE

        public void createReminder(String name,String datetime, boolean important) {
            ContentValues values = new ContentValues();
            long mills=gettimeinmills(datetime);
            values.put(COL_CONTENT, name);
            values.put(COL_DATESTRING, datetime);
            values.put(COL_IMPORTANT, important ? 1 : 0);
            values.put(COL_MILLS,mills);
            mDb.insert(TABLE_NAME, null, values);
        }
// a method to get time in mills from a date in string form
    private long gettimeinmills(String datetime) {
        DateFormat df=new SimpleDateFormat("HH:mm  dd/MM/yyyy");
        Date date=null;
        try {
            date=df.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
       return calendar.getTimeInMillis();
    }

    //READ
    public Reminder fetchReminderById(int id) {
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT, COL_DATESTRING, COL_IMPORTANT}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();
        return new Reminder(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getString(INDEX_DATESTRING),
                cursor.getInt(INDEX_IMPORTANT)
        );
    }
    //a method Getting Reminders with the given datestring it returns an arraylist because many reminders could be scheduled for the same time
    public ArrayList<Reminder> fetchReminderByDateString(String datedata) {

        Log.d("MainTag"+TAG, "fetchreminderbydatestring has been called"+datedata);

        Reminder reminder;
        ArrayList<Reminder> a=new ArrayList<>();
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT, COL_DATESTRING, COL_IMPORTANT}, COL_DATESTRING+ "=?",
                new String[]{datedata}, null, null, null, null
        );
        if (cursor != null){
            while (cursor.moveToNext()){
        reminder= new Reminder(cursor.getInt(INDEX_ID), cursor.getString(INDEX_CONTENT), cursor.getString(INDEX_DATESTRING), cursor.getInt(INDEX_IMPORTANT));
                Log.d("MainTag"+TAG, "While loop in fetchreminderbydatestring: This is the reminder:"+reminder.getContent()+","+reminder.getDatestring());
        a.add(reminder);}
        }
        if (a.size()>0){
            Log.d("MainTag"+TAG, "fetchreminderbydatestring I am returning "+"\t"+a.size()+"Reminders"+a);}
        else {
            Log.d("MainTag"+TAG, " We have no reminders"+"\t"+a.size());
        }
        return a;

    }
    //a method Returning an arraylist of  dates that are eligible for scheduling
    public  ArrayList<Date> fetchDates() {
        Log.d("MainTag"+TAG, " fetchdates has been called"+"\t");
        Date date1=new Date();
        Log.d("MainTag"+TAG, " fetchdates this is the time I am using to add dates to the arraylist. If it is before this date I skip it"+"\t"+date1.toString());
            ArrayList<Date> a = new ArrayList<Date>();
        Date date = null;
        a.clear();
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT, COL_DATESTRING, COL_IMPORTANT}, null,
                null, null, null, null
        );
        if (cursor != null){
            while (cursor.moveToNext()){

            String dtime=cursor.getString(INDEX_DATESTRING);
                Log.d("MainTag"+TAG, " fetchdates in while loop"+"\t"+dtime);
            DateFormat df=new SimpleDateFormat("HH:mm  dd/MM/yyyy");
            try {
                date = df.parse(dtime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date.after(date1)){
            a.add(date);
                Log.d("MainTag"+TAG, " fetchdates in while loop"+"\t"+"This date has been added"+date.toString());}
            else{
                Log.d("MainTag"+TAG, " fetchdates in while loop"+"\t"+"This date has not been added"+date.toString());
            }
            }

        }
        if (a.size()>0){
            Log.d("MainTag"+TAG, " fetchdates ArrayList<Dates> returning:"+"\t"+"Size"+"\t"+a.size()+"\t"+"content"+"\t"+a);}
        else {
            Log.d("MainTag"+TAG, " fetchdates ArrayList<Dates> returning:"+"\t"+"Size"+"\t"+a.size()+"\t"+"content"+"\t"+"Nothing here");
        }
        return a;
    }
    //a method to get a cursor containing all reminders in the database. This cursor will be used in the adapter of the listview in RemindersActivity
    public Cursor fetchAllReminders() {
        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT, COL_DATESTRING, COL_IMPORTANT,COL_MILLS},
                null, null, null, null, COL_MILLS+" DESC"
        );
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //UPDATE
    public void updateReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        long mills=gettimeinmills(reminder.getDatestring());
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_DATESTRING, reminder.getDatestring());
        values.put(COL_IMPORTANT, reminder.getImportant());
        values.put(COL_MILLS,mills);
        mDb.update(TABLE_NAME, values,
                COL_ID + "=?", new String[]{String.valueOf(reminder.getId())});
    }
    //DELETE
    public void deleteReminderById(int nId) {
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(nId)});

    }



 private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }}

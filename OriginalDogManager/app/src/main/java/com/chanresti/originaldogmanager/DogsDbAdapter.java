package com.chanresti.originaldogmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mwangi on 07/04/2018.
 */
//a class containing methods for CRUD operations in the dogs database
public class DogsDbAdapter {
    public static final String COL_ID = "_id";
    public static final String COL_URISTRING = "uristring";
    public static final String COL_NAME="dogname";
    public static final String COL_WEIGHT = "weight";
    public static final String  COL_GENDER="gender";
    public static final String COL_BREED="breed";
    public static final  String COL_HABITS="habits";
    public static final String COL_AGE="age";
    //these are the corresponding indices
    public static final int INDEX_ID = 0;
    public static final int INDEX_URISTRING = INDEX_ID + 1;
    public static final int INDEX_NAME = INDEX_ID + 2;
    public static final int INDEX_WEIGHT = INDEX_ID + 3;
    public static final int INDEX_GENDER = INDEX_ID + 4;
    public static final int INDEX_BREED = INDEX_ID + 5;
    public static final int INDEX_HABITS = INDEX_ID + 6;
    public static final int INDEX_AGE = INDEX_ID + 7;
    //used for logging
    private static final String TAG = "RemindersAdapter";

    private DogsDbAdapter.DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private static final String DATABASE_NAME = "dba_dgs";
    private static final String TABLE_NAME = "tbl_dgs";
    private static final int DATABASE_VERSION = 1;
    private final Context mCtx;
    //SQL statement used to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_URISTRING + " TEXT, " +
                    COL_NAME + " TEXT, " +
                    COL_WEIGHT + " TEXT, " +
                    COL_GENDER + " TEXT, " +
                    COL_BREED + " TEXT, " +
                    COL_HABITS + " TEXT, " +
                    COL_AGE + " TEXT );";
    public DogsDbAdapter(Context mCtx) {
        this.mCtx = mCtx;
    }
    public void open() throws SQLException {
        Log.d("MainTag"+TAG, "open database");
        mDbHelper = new DogsDbAdapter.DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    //close
    public void close() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }}

    public void createDog(String URISTRING,String NAME ,String WEIGHT,String GENDER,String BREED,String HABITS,String AGE ) {
        ContentValues values = new ContentValues();
        values.put(COL_URISTRING, URISTRING);
        values.put(COL_NAME , NAME );
        values.put(COL_WEIGHT, WEIGHT);
        values.put(COL_GENDER, GENDER);
        values.put(COL_BREED, BREED);
        values.put(COL_HABITS, HABITS);
        values.put(COL_AGE, AGE);


        mDb.insert(TABLE_NAME, null, values);
    }
    public Cursor fetchDogByName(String Name) {
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,COL_URISTRING, COL_NAME, COL_WEIGHT, COL_GENDER, COL_BREED, COL_HABITS,
                        COL_AGE}, COL_NAME + "=?",
                new String[]{Name}, null, null, null, null
        );
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;

    }

    public void updateDog(Dog dog) {
        ContentValues values = new ContentValues();
        values.put(COL_URISTRING, dog.getUriString());
        values.put(COL_NAME, dog.getDogName());
        values.put(COL_WEIGHT, dog.getWeight());
        values.put(COL_GENDER, dog.getGender());
        values.put(COL_BREED, dog.getBreed());
        values.put(COL_HABITS, dog.getHabits());
        values.put(COL_AGE, dog.getAge());

        mDb.update(TABLE_NAME, values,
                COL_ID + "=?", new String[]{String.valueOf(dog.getdId())});
    }

    public ArrayList<String> fetchDogNames() {
       ArrayList<String> a=new ArrayList<>();

        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,COL_URISTRING, COL_NAME, COL_WEIGHT, COL_GENDER, COL_BREED, COL_HABITS,
                        COL_AGE}, null,
             null, null, null, null
        );
        if (cursor != null){
            while (cursor.moveToNext()){

                String dog=cursor.getString(INDEX_NAME);
                a.add(dog);
            }

        }
        if(a.size()<=0){
            a.add("No Dogs");
        }


        return a;
    }
    public Dog getDogFromCursor (Cursor cursor){
        Dog dog=null;
        if (cursor != null){

     dog=new Dog(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_URISTRING),
                cursor.getString(INDEX_NAME),
                cursor.getString(INDEX_WEIGHT),
                cursor.getString( INDEX_GENDER),
                cursor.getString(INDEX_BREED),
                cursor.getString(INDEX_HABITS ),
                cursor.getString(INDEX_AGE));
        }
        return dog;
    }
    public void deleteDogById(int nId) {
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
    }

}

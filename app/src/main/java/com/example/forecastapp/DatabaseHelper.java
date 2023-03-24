package com.example.forecastapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "SearchHistory2";
    public final static String TABLE_NAME = "history";
    public final static String ID = "_id";
    public final static String CITY = "city";
    public final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME +
            "("+ID+" integer primary key autoincrement, " +
            CITY+" text not null);";
    private static final String DATABASE_DELETE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DATABASE_DELETE);
        onCreate(sqLiteDatabase);
    }
}

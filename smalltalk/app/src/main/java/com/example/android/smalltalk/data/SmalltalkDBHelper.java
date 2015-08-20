package com.example.android.smalltalk.data;

/**
 * Created by shauna on 8/19/15.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.smalltalk.data.SmalltalkContract.ContactEntry;
import com.example.android.smalltalk.data.SmalltalkContract.TopicEntry;
import com.example.android.smalltalk.data.SmalltalkContract.GroupEntry;

/**
 * Manages a local database for weather data.
 */
public class SmalltalkDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "smalltalk.db";

    public SmalltalkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        
        final String SQL_CREATE_CONTACTS_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactEntry.COLUMN_CONTACT_NAME + " TEXT NOT NULL, " +
                ContactEntry.COLUMN_CONTACT_DETAILS + " TEXT NOT NULL, " +
                " UNIQUE (" + ContactEntry.COLUMN_CONTACT_NAME + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACTS_TABLE);
        
        final String SQL_CREATE_TOPICS_TABLE = "CREATE TABLE " + TopicEntry.TABLE_NAME + " (" +
                TopicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TopicEntry.COLUMN_TOPIC_NAME + " TEXT NOT NULL, " +
                TopicEntry.COLUMN_TOPIC_DETAILS + " TEXT NOT NULL, " +
                " UNIQUE (" + TopicEntry.COLUMN_TOPIC_NAME + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_TOPICS_TABLE);
        
        final String SQL_CREATE_GROUPS_TABLE = "CREATE TABLE " + GroupEntry.TABLE_NAME + " (" +
                GroupEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GroupEntry.COLUMN_GROUP_NAME + " TEXT NOT NULL, " +
                GroupEntry.COLUMN_GROUP_DETAILS + " TEXT NOT NULL, " +
                " UNIQUE (" + GroupEntry.COLUMN_GROUP_NAME + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_GROUPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

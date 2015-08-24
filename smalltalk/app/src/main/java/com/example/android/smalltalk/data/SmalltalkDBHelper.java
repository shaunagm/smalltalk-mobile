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
import com.example.android.smalltalk.data.SmalltalkContract.ContactGroupJunction;
import com.example.android.smalltalk.data.SmalltalkContract.TopicContactJunction;
import com.example.android.smalltalk.data.SmalltalkContract.TopicGroupJunction;


/**
 * Manages a local database for weather data.
 */
public class SmalltalkDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "smalltalk.db";

    private static SmalltalkDBHelper sInstance;

    public static synchronized SmalltalkDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you don't accidentally leak an
        // Activity's context. See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SmalltalkDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private SmalltalkDBHelper(Context context) {
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

        final String SQL_CREATE_CONTACTGROUPJUNCTION_TABLE = "CREATE TABLE " + ContactGroupJunction.TABLE_NAME + " (" +
                ContactGroupJunction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContactGroupJunction.COLUMN_CONTACT_KEY + " INTEGER NOT NULL, " +
                ContactGroupJunction.COLUMN_GROUP_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + ContactGroupJunction.COLUMN_CONTACT_KEY + ") REFERENCES " +
                ContactEntry.TABLE_NAME + " (" + ContactEntry._ID + "), " +
                " FOREIGN KEY (" + ContactGroupJunction.COLUMN_GROUP_KEY + ") REFERENCES " +
                GroupEntry.TABLE_NAME + " (" + GroupEntry._ID + "), " +
                "UNIQUE (" + ContactGroupJunction.COLUMN_CONTACT_KEY +  ", " + ContactGroupJunction.COLUMN_GROUP_KEY + "));";
        sqLiteDatabase.execSQL(SQL_CREATE_CONTACTGROUPJUNCTION_TABLE);

        final String SQL_CREATE_TOPICCONTACTJUNCTION_TABLE = "CREATE TABLE " + TopicContactJunction.TABLE_NAME + " (" +
                TopicContactJunction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TopicContactJunction.COLUMN_TOPIC_KEY + " INTEGER NOT NULL, " +
                TopicContactJunction.COLUMN_CONTACT_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TopicContactJunction.COLUMN_TOPIC_KEY + ") REFERENCES " +
                TopicEntry.TABLE_NAME + " (" + TopicEntry._ID + "), " +
                " FOREIGN KEY (" + TopicContactJunction.COLUMN_CONTACT_KEY + ") REFERENCES " +
                ContactEntry.TABLE_NAME + " (" + ContactEntry._ID + "), " +
                "UNIQUE (" + TopicContactJunction.COLUMN_TOPIC_KEY +  ", " + TopicContactJunction.COLUMN_CONTACT_KEY + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_TOPICCONTACTJUNCTION_TABLE);

        final String SQL_CREATE_TOPICGROUPJUNCTION_TABLE = "CREATE TABLE " + TopicGroupJunction.TABLE_NAME + " (" +
                TopicGroupJunction._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TopicGroupJunction.COLUMN_TOPIC_KEY + " INTEGER NOT NULL, " +
                TopicGroupJunction.COLUMN_GROUP_KEY + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + TopicGroupJunction.COLUMN_TOPIC_KEY + ") REFERENCES " +
                TopicEntry.TABLE_NAME + " (" + TopicEntry._ID + "), " +
                " FOREIGN KEY (" + TopicGroupJunction.COLUMN_GROUP_KEY + ") REFERENCES " +
                GroupEntry.TABLE_NAME + " (" + GroupEntry._ID + "), " +
                "UNIQUE (" + TopicGroupJunction.COLUMN_TOPIC_KEY +  ", " + TopicGroupJunction.COLUMN_GROUP_KEY + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_TOPICGROUPJUNCTION_TABLE);
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

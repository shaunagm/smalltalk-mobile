package com.example.android.smalltalk;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.widget.Toast;

import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by shauna on 8/20/15.
 */
public class SmalltalkUtilities {

    static String[ ] mContactNames = {
            "Buffy Summers",
            "Willow Rosenberg",
            "Xander Harris",
            "Dawn Summers"

    };

    static String[ ] mContactDetails = {
            "Slayer!",
            "Witch.",
            "Comfortador",
            "The Key"
    };

    static String[] mGroupNames = {
            "Scoobies",
            "Slayers",
            "Vampires",
            "Everybody!"
    };

    static String[] mGroupDetails = {
            "We fight evil!",
            "The chosen ones",
            "Grrr arrrgh",
            "Happy meals with legs"
    };

    static String[] mTopicNames = {
            "Apocalypse",
            "Stakes"
    };

    static String[] mTopicDetails = {
            "We\'ve all been there",
            "What are they"
    };

    public static void exportDB(Context context) {

        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.example.android.smalltalk" +"/databases/"+"smalltalk.db";
        String backupDBPath = "smalltalk.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public static void addContacts(SQLiteDatabase db) {
        for(int i=0; i < mContactNames.length; i++) {
            ContentValues values = new ContentValues();
            values.put(SmalltalkContract.ContactEntry.COLUMN_CONTACT_NAME, mContactNames[i]);
            values.put(SmalltalkContract.ContactEntry.COLUMN_CONTACT_DETAILS, mContactDetails[i]);
            long newID = db.insert(SmalltalkContract.ContactEntry.TABLE_NAME, null, values);
        }
        db.close();
    }

    public static void addGroups(SQLiteDatabase db) {
        for(int i=0; i < mGroupNames.length; i++) {
            ContentValues values = new ContentValues();
            values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_NAME, mGroupNames[i]);
            values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_DETAILS, mGroupDetails[i]);
            long newID = db.insert(SmalltalkContract.GroupEntry.TABLE_NAME, null, values);
        }
        db.close();
    }


    public static void addTopics(SQLiteDatabase db) {
        for(int i=0; i < mTopicNames.length; i++) {
            ContentValues values = new ContentValues();
            values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_NAME, mTopicNames[i]);
            values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_DETAILS, mTopicDetails[i]);
            long newID = db.insert(SmalltalkContract.TopicEntry.TABLE_NAME, null, values);
        }
        db.close();
    }

}

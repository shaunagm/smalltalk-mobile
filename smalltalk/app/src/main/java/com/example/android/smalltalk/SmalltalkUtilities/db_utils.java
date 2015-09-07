package com.example.android.smalltalk.SmalltalkUtilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.smalltalk.data.SmalltalkDBHelper;

/**
 * Created by shauna on 9/7/15.
 */
public class db_utils {

    public static void createContactGroupRelationship(Context context, String contact_id, String group_id){
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase writeDB = mdbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contact_id", contact_id);
        values.put("group_id", String.valueOf(group_id));
        writeDB.insert("contact_group", null, values);
    }

    // Checks that an object exists in the database and, if it does, returns the row_id. Otherwise, returns 0.
    public static String getOrCreate(Context context, String item_type, String name) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String queryString = String.format("SELECT * FROM %s WHERE name = '%s' COLLATE NOCASE;", item_type, name);
        Cursor cursor = readDb.rawQuery(queryString, new String[]{});
        if (cursor.getCount() == 0) {
            SQLiteDatabase writeDb = mdbHelper.getReadableDatabase();
            ContentValues new_values = new ContentValues();
            new_values.put("name", name);
            long id = writeDb.insert(item_type, null, new_values);
            return String.valueOf(id);
        } else {
            cursor.moveToNext();
            return cursor.getString(cursor.getColumnIndex("_id"));
        }
    }

    public static Cursor getListCursorGivenType(Context context, String item_type, int show_archived) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String baseString;
        if (item_type.equals("topics") && (show_archived == 0)) {
            baseString = "SELECT * FROM %s WHERE archive = 0 ORDER BY name ASC;";
        } else {
            baseString = "SELECT * FROM %s ORDER BY name ASC;";
        }

        String queryString = String.format(baseString, item_type);
        return readDb.rawQuery(queryString, new String[]{});
    }

}

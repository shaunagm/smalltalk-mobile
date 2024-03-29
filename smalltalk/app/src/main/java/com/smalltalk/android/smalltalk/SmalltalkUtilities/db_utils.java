package com.smalltalk.android.smalltalk.SmalltalkUtilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.smalltalk.android.smalltalk.data.SmalltalkDBHelper;

public class db_utils {

    public static long createObject(Context context, String type, String name, String details, String uri) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase writeDB = mdbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("details", details);
        if (type.equals("topic")) {
            values.put("URI", uri);
        }
        return writeDB.insert(type.toLowerCase() + "s", null, values);
    }

    public static void createContactGroupRelationship(Context context, String contact_id, String group_id){
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase writeDB = mdbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contact_id", contact_id);
        values.put("group_id", String.valueOf(group_id));
        writeDB.insert("contact_group", null, values);
    }

    // Checks if an object exists.  Returns ID or empty string.
    public static String checkExists(Context context, String name, String item_type) {
        name = DatabaseUtils.sqlEscapeString(name);
        name = name.substring(1, name.length()-1);
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String queryString = String.format("SELECT * FROM %s WHERE name = '%s' COLLATE NOCASE;", item_type, name);
        Cursor cursor = readDb.rawQuery(queryString, new String[]{});
        if (cursor.getCount() == 0) {
            return "0";  // Don't return "", since that will match an empty id textfield
        } else {
            cursor.moveToNext();
            return cursor.getString(cursor.getColumnIndex("_id"));
        }
    }


    // Checks that an object exists in the database and, if it does, returns the row_id. Otherwise, creates it..
    public static String getOrCreate(Context context, String item_type, String name) {
        name = DatabaseUtils.sqlEscapeString(name);
        name = name.substring(1, name.length()-1);
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

    public static Cursor getListCursorGivenType(Context context, String item_type, int show_archived, int show_starred) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String whereString = "";
        if (item_type.equals("topics")) {
            if (show_archived == 0 || show_starred == 1) {
                whereString += " WHERE";
            }
            if ((show_archived == 0)) {
                whereString += " archive = 0 ";
            }
            if (show_archived == 0 && show_starred == 1) {
                whereString += "AND";
            }
            if ((show_starred == 1)) {
                whereString += " star = 1 ";
            }
        }
        String baseString = "SELECT * FROM %s " + whereString + "ORDER BY name ASC;";
        String queryString = String.format(baseString, item_type);
        return readDb.rawQuery(queryString, new String[]{});
    }

    public static Cursor searchQuery(Context context, String search_type, String query) {
        query = DatabaseUtils.sqlEscapeString(query);
        query = query.substring(1, query.length()-1);
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String queryString = "SELECT * FROM " + search_type + " WHERE " + search_type + ".'name' LIKE " +
                "'%" + query + "%' OR " + search_type + ".'details' LIKE '%" + query + "%' COLLATE NOCASE;";
        return readDb.rawQuery(queryString, new String[]{});
    }

}

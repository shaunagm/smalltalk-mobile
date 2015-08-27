package com.example.android.smalltalk.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shauna on 8/27/15.
 */
public class SmalltalkObject {

    String name;
    String id;
    String details;
    String object_type;
    String object_type_plural;

    public SmalltalkObject(String name, String id, String object_type, String details){
        this.name = name;
        this.id = id;
        this.details = details;
        this.object_type = object_type;
        this.object_type_plural = object_type + "s";
    }

    public SmalltalkObject(Context context, String id, String object_type) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                object_type + "s", id);
        Cursor cursor = readDb.rawQuery(queryString, new String[]{});
        cursor.moveToNext();
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        this.details = cursor.getString(cursor.getColumnIndexOrThrow("details"));
        this.id = id;
        this.object_type = object_type;
        this.object_type_plural = object_type + "s";
    }

    // Create second constructor method that uses only object_type and name, then calls the main
    // method once it's used those to look up id & details?

    public String getName() {
        return this.name;
    }

    public String getDetails() {
        return this.details;
    }

    public String getType() {
        return this.object_type;
    }

    public Cursor getRowCursor(Context context) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                this.object_type_plural, this.id);
        return readDb.rawQuery(queryString, new String[]{});
    }

    public Cursor getAllItemsCursor(Context context, String all_item_type) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String QueryString = String.format("SELECT * FROM %s;", all_item_type);
        return readDb.rawQuery(QueryString, new String[]{});
    }

    public ArrayList<String> getAllItemsNames(Context context, String all_item_type) {
        Cursor cursor = getAllItemsCursor(context, all_item_type);
        ArrayList<String> names = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
        }
        return names;
    }

    public Pair getAllItemsNamesAndIDs(Context context, String all_item_type) {
        Cursor cursor = getAllItemsCursor(context, all_item_type);;
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> IDs = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("_id");
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
            IDs.add(cursor.getString(idIndex));
        }
        Pair names_and_IDs = new Pair(names, IDs);
        return names_and_IDs;
    }

    public String getRelationshipTableName(String related_type){
        String[] typeStrings = { this.object_type, related_type };
        Arrays.sort(typeStrings);
        return TextUtils.join("_", typeStrings);
    }

    public Cursor getRelatedItemsCursor(Context context, String related_type) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        String QueryString = String.format("SELECT * FROM %s WHERE _ID IN ( SELECT %s_id FROM %s WHERE %s_id = %s);",
                related_type, related_type.replace("s", ""), getRelationshipTableName(related_type.replace("s", "")),
                this.object_type, this.id);
        return readDb.rawQuery(QueryString, new String[]{});
    }

    public ArrayList<String> getRelatedNames(Context context, String related_type) {
        Cursor cursor = getRelatedItemsCursor(context, related_type);
        ArrayList<String> names = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
        }
        return names;
    }

    public Pair getRelatedNamesAndIDs(Context context, String related_type) {
        Cursor cursor = getRelatedItemsCursor(context, related_type);
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> IDs = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("_id");
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
            IDs.add(cursor.getString(idIndex));
        }
        Pair names_and_IDs = new Pair(names, IDs);
        return names_and_IDs;
    }

    public void updateObject(Context context, String name, String details) {
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase writeDb = mdbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("name", name);
        newValues.put("details", details);
        writeDb.update(this.object_type_plural, newValues, "_id=?", new String[]{this.id});
        writeDb.close();
    }

    public void removeRelationship(Context context, String related_type, String related_item_id) {
        related_type = related_type.toLowerCase().replace("s","");
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase writeDb = mdbHelper.getWritableDatabase();
        String query = String.format("DELETE FROM %s WHERE %s_id = %s AND %s_id = %s;",
                getRelationshipTableName(related_type), this.object_type, this.id, related_type, related_item_id);
        writeDb.execSQL(query);
        writeDb.close();
    }

    public void addRelationship(Context context, String related_type, String related_item_id) {
        related_type = related_type.toLowerCase().replace("s","");
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(context);
        SQLiteDatabase writeDb = mdbHelper.getWritableDatabase();
        String query = String.format("INSERT INTO %s (%s_id, %s_id) VALUES (%s, %s);",
                getRelationshipTableName(related_type), this.object_type, related_type, this.id, related_item_id);
        writeDb.execSQL(query);
        writeDb.close();
    }

}

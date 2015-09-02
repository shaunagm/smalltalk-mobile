package com.example.android.smalltalk.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.example.android.smalltalk.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shauna on 8/27/15.
 */
public class SmalltalkObject {

    String name;
    String id;
    String details;
    String object_type;
    String object_type_plural;
    String URI;
    Integer star;
    Integer archive;
    Context mContext;
    SmalltalkDBHelper mdbHelper;
    SQLiteDatabase mWriteDb;
    SQLiteDatabase mReadDb;
    HashMap<String, int[][]> mResourceIDs;
    CachedStatus mCachedStatus;

    // Private class used for quick toggling.
    public final class CachedStatus {
        String type = "";
        String id = "";
        int star_status = 99;
        int star_lock_status = 99;
        int archive_status = 99;
        int archive_lock_status = 99;

        public void refreshCachedStatus(String type, String id, int star_status, int star_lock_status,
                                        int archive_status, int archive_lock_status) {
            this.type = type;
            this.id = id;
            this.star_status = star_status;
            this.star_lock_status = star_lock_status;
            this.archive_status = archive_status;
            this.archive_lock_status = archive_lock_status;
        }
    }

    // Constructors

    public SmalltalkObject(String name, String id, String object_type, String details, String URI){
        this.name = name;
        this.id = id;
        this.details = details;
        this.object_type = object_type;
        this.object_type_plural = object_type + "s";
        this.URI = URI;
    }

    public SmalltalkObject(Context context, String id, String object_type) {
        mContext = context;
        mdbHelper = SmalltalkDBHelper.getInstance(mContext);
        mWriteDb = mdbHelper.getWritableDatabase();
        mReadDb = mdbHelper.getReadableDatabase();
        mCachedStatus = new CachedStatus();

        String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                object_type + "s", id);
        Cursor cursor = mReadDb.rawQuery(queryString, new String[]{});
        cursor.moveToNext();
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        this.details = cursor.getString(cursor.getColumnIndexOrThrow("details"));
        this.id = id;
        this.object_type = object_type;
        this.object_type_plural = object_type + "s";
        if (this.object_type.equals("topic")) {
            this.URI = cursor.getString(cursor.getColumnIndexOrThrow("URI"));
            this.star = cursor.getInt(cursor.getColumnIndexOrThrow("star"));
            this.archive = cursor.getInt(cursor.getColumnIndexOrThrow("archive"));
        } else {
            this.URI = "";
        }
    }

    // Getters

    public String getName() {
        return this.name;
    }

    public String getDetails() {
        return this.details;
    }

    public String getType() {
        return this.object_type;
    }

    public String getURI() {
        return this.URI;
    }

    public Integer getStarStatus() {
        return this.star;
    }

    public Integer getArchiveStatus() {
        return this.archive;
    }

    public String[] getRelatedHeaders() {
        String[] headers = new String[2];
        switch (this.object_type) {
            case "contact":
                headers = new String[]{"Groups", "Topics"};
                break;
            case "group":
                headers = new String[]{"Contacts", "Topics"};
                break;
            case "topic":
                headers = new String[]{"Contacts", "Groups"};
                break;
        };
        return headers;
    }

    // Database Utils

    public String getRelationshipTableName(String related_type){
        String related_type_formatted = related_type.toLowerCase().replace("s","");
        String[] typeStrings = { this.object_type, related_type_formatted };
        Arrays.sort(typeStrings);
        return TextUtils.join("_", typeStrings);
    }

    // Reading from the database

    public Cursor getRowCursor() {
        String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                this.object_type_plural, this.id);
        return mReadDb.rawQuery(queryString, new String[]{});
    }

    public Cursor getAllItemsCursor(String all_item_type) {
        String QueryString = String.format("SELECT * FROM %s;", all_item_type);
        return mReadDb.rawQuery(QueryString, new String[]{});
    }

    public Cursor getRelatedItemsCursor(String related_type) {
        related_type = related_type.toLowerCase().replace("s","");
        String QueryString = String.format("SELECT * FROM %ss WHERE _ID IN ( SELECT %s_id FROM %s WHERE %s_id = %s);",
                related_type, related_type.replace("s", ""), getRelationshipTableName(related_type.replace("s", "")),
                this.object_type, this.id);
        return mReadDb.rawQuery(QueryString, new String[]{});
    }

    public Cursor getRelationshipCursor(String related_type, String related_id) {
        related_type = related_type.toLowerCase().replace("s", "");
        String QueryString = String.format("SELECT * FROM %s WHERE %s_ID = %s AND %s_ID = %s;",
                getRelationshipTableName(related_type), this.object_type, this.id, related_type, related_id);
        return mReadDb.rawQuery(QueryString, new String[]{});
    }

    public Integer getRelationshipStatus(String status_type, String related_type, String related_id) {
        Cursor cursor = getRelationshipCursor(related_type, related_id);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex(status_type));
    }

    public void getAllImageStatus(String related_type, String related_id) {
        Cursor cursor = getRelationshipCursor(related_type, related_id);
        cursor.moveToNext();
        this.mCachedStatus.star_status = cursor.getInt(cursor.getColumnIndex("star"));
        this.mCachedStatus.star_lock_status = cursor.getInt(cursor.getColumnIndex("star_lock"));
        this.mCachedStatus.archive_status = cursor.getInt(cursor.getColumnIndex("archive"));
        this.mCachedStatus.archive_lock_status = cursor.getInt(cursor.getColumnIndex("archive_lock"));
    }

    public Integer getImageResourceStatus(String image_type, String related_type, String related_id) {

        if (mResourceIDs == null) {
            mResourceIDs = new HashMap<String, int[][]>();
            mResourceIDs.put("star", new int[][]{
                    {R.drawable.small_star_off_unlocked, R.drawable.small_star_off_locked},
                    {R.drawable.small_star_on_unlocked, R.drawable.small_star_on_locked}});
            mResourceIDs.put("archive", new int[][]{
                    {R.drawable.small_archive_off_unlocked, R.drawable.small_archive_off_locked},
                    {R.drawable.small_archive_on_unlocked, R.drawable.small_archive_on_locked}});
        }

        if (!(mCachedStatus.type.equals(related_type) && mCachedStatus.id.equals(related_id))) {
            getAllImageStatus(related_type, related_id);
        }

        if (image_type.equals("star")) {
            return mResourceIDs.get("star")[mCachedStatus.star_status][mCachedStatus.star_lock_status];
        } else {
            return mResourceIDs.get("archive")[mCachedStatus.archive_status][mCachedStatus.archive_lock_status];
        }
    }

    // Database updates

    public void updateObject(String name, String details, String URI) {
        ContentValues newValues = new ContentValues();
        newValues.put("name", name);
        newValues.put("details", details);
        if (!URI.equals("null")) {
            newValues.put("URI", URI);
        }
        mWriteDb.update(this.object_type_plural, newValues, "_id=?", new String[]{this.id});
    }

    // Allows us to call updateObject on non-topic objects, which have no URI field to update.
    public void updateObject(String name, String details) {
        updateObject(name, details, "null");
    }

    public void removeRelationship(String related_type, String related_item_id) {
        related_type = related_type.toLowerCase().replace("s","");
        String query = String.format("DELETE FROM %s WHERE %s_id = %s AND %s_id = %s;",
                getRelationshipTableName(related_type), this.object_type, this.id, related_type, related_item_id);
        mWriteDb.execSQL(query);
    }

    public void addRelationship(String related_type, String related_item_id) {
        related_type = related_type.toLowerCase().replace("s","");
        String query = String.format("INSERT INTO %s (%s_id, %s_id) VALUES (%s, %s);",
                getRelationshipTableName(related_type), this.object_type, related_type, this.id, related_item_id);
        mWriteDb.execSQL(query);
    }

    public Integer toggleStatus(String type) {
        if (type.equals("star")) {
            return toggleStarStatus();
        } else {
            return toggleArchiveStatus();
        }
    }

    public Integer toggleStarStatus() {
        ContentValues newValues = new ContentValues();
        newValues.put("star", 1 - this.star);
        mWriteDb.update(this.object_type_plural, newValues, "_id=?", new String[]{this.id});
        this.star = 1 - this.star;
        return this.star;
    }

    public Integer toggleArchiveStatus() {
        ContentValues newValues = new ContentValues();
        newValues.put("archive", 1 - this.archive);
        mWriteDb.update(this.object_type_plural, newValues, "_id=?", new String[]{this.id});
        this.archive = 1 - this.archive;
        return this.archive;
    }

    public Integer toggleRelationshipStatus(String relationship_type, String related_type, String related_id) {
        Integer old_status = getRelationshipStatus(relationship_type, related_type, related_id);
        ContentValues newValues = new ContentValues();
        newValues.put(relationship_type, 1 - old_status);
        String item_column_name = this.object_type + "_id";
        String related_column_name = related_type.toLowerCase().replace("s","") + "_id";
        mWriteDb.update(getRelationshipTableName(related_type), newValues,
                item_column_name + " = ? AND " + related_column_name + " = ?",
                new String[]{this.id, related_id});
        return  1 - old_status;
    }


    // Returns arrays of strings, rather than cursor (still uses db)

    public ArrayList<String> getAllItemsNames(String all_item_type) {
        Cursor cursor = getAllItemsCursor(all_item_type);
        ArrayList<String> names = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
        }
        return names;
    }

    public Pair getAllItemsNamesAndIDs(String all_item_type) {
        Cursor cursor = getAllItemsCursor(all_item_type);;
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

    public ArrayList<String> getRelatedNames(String related_type) {
        Cursor cursor = getRelatedItemsCursor(related_type);
        ArrayList<String> names = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
        }
        return names;
    }

    public Pair getRelatedNamesAndIDs(String related_type) {
        Cursor cursor = getRelatedItemsCursor(related_type);
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

}

package com.example.android.smalltalk.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.example.android.smalltalk.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
    Integer show_archive;
    Context mContext;
    SmalltalkDBHelper mdbHelper;
    SQLiteDatabase mWriteDb;
    SQLiteDatabase mReadDb;
    HashMap<String, int[][]> mResourceIDs;
    CachedStatus mCachedStatus;

    // Constructors
    public SmalltalkObject(Context context, String id, String object_type, int show_archived) {
        mContext = context;
        mdbHelper = SmalltalkDBHelper.getInstance(mContext);
        mWriteDb = mdbHelper.getWritableDatabase();
        mReadDb = mdbHelper.getReadableDatabase();
        mCachedStatus = new CachedStatus();

        this.id = id;
        this.object_type = object_type;
        this.object_type_plural = object_type + "s";
        this.show_archive = show_archived;

        Cursor cursor = getRowCursor();
        this.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        this.details = cursor.getString(cursor.getColumnIndexOrThrow("details"));
        if (this.object_type.equals("topic")) {
            this.URI = cursor.getString(cursor.getColumnIndexOrThrow("URI"));
            this.star = cursor.getInt(cursor.getColumnIndexOrThrow("star"));
            this.archive = cursor.getInt(cursor.getColumnIndexOrThrow("archive"));
        } else {
            this.URI = "";
        }

        mResourceIDs = new HashMap<String, int[][]>();
        mResourceIDs.put("star", new int[][]{
                {R.drawable.small_star_off_unlocked, R.drawable.small_star_off_locked},
                {R.drawable.small_star_on_unlocked, R.drawable.small_star_on_locked}});
        mResourceIDs.put("archive", new int[][]{
                {R.drawable.small_archive_off_unlocked, R.drawable.small_archive_off_locked},
                {R.drawable.small_archive_on_unlocked, R.drawable.small_archive_on_locked}});
    }

    public SmalltalkObject(Context context, String id, String object_type) {
        this(context, id, object_type, 0);
    }

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

    public ArrayList<String> getItemInfoArray(Cursor cursor, String return_type) {
        ArrayList<String> items = new ArrayList<String>();
        int itemIndex = cursor.getColumnIndex(return_type);
        cursor.moveToPosition(-1); // Need to move cursor back before first row so the while loop works
        while(cursor.moveToNext()) {
            items.add(cursor.getString(itemIndex));
        }
        return items;
    }

    public Pair getItemInfoPair(Cursor cursor) {
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> IDs = new ArrayList<String>();
        int nameIndex = cursor.getColumnIndex("name");
        int idIndex = cursor.getColumnIndex("_id");
        cursor.moveToPosition(-1); // Need to move cursor back before first row so the while loop works
        while(cursor.moveToNext()) {
            names.add(cursor.getString(nameIndex));
            IDs.add(cursor.getString(idIndex));
        }
        Pair names_and_IDs = new Pair(names, IDs);
        return names_and_IDs;
    }

    // Database Utils

    public String getRelationshipTableName(String related_type){
        String related_type_formatted = related_type.toLowerCase().replace("s", "");
        String[] typeStrings = { this.object_type, related_type_formatted };
        Arrays.sort(typeStrings);
        return TextUtils.join("_", typeStrings);
    }

    public Cursor getPrimedCursor(String queryString) {
        Cursor cursor = mReadDb.rawQuery(queryString, new String[]{});
        cursor.moveToNext();
        return cursor;
    }

    public String convertListToQueryFragment(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder(list.size()*10);
        String delim = "'"; // Delimiter starts off with no comma, so we don't start the list with a comma
        for (String id: list) {
            sb.append(delim).append(id).append("'");
            delim = ",'";
        }
        return sb.toString();

    }

    // Reading from the database

    public Cursor getRowCursor() {
        String queryString = String.format("SELECT * FROM %ss WHERE _ID = %s LIMIT 1;",
                this.object_type, this.id);
        return getPrimedCursor(queryString);
    }

    public Cursor getAllRowsCursor(String all_item_type, int show_archive) {
        String archive_fragment = "";
        if (show_archive == 0 && all_item_type.equals("topic")) {
            archive_fragment = "WHERE archive = 0";
        }
        String queryString = String.format("SELECT * FROM %s %s;", all_item_type, archive_fragment);
        return getPrimedCursor(queryString);
    }

    public Cursor getRelationshipCursor(String related_type, String related_id) {
        related_type = related_type.toLowerCase().replace("s", "");
        String queryString = String.format("SELECT * FROM %s WHERE %s_ID = %s AND %s_ID = %s;",
                getRelationshipTableName(related_type), this.object_type, this.id, related_type, related_id);
        return getPrimedCursor(queryString);
    }

    public Cursor getAllRelationshipsCursor(String related_type, int show_archive) {
        related_type = related_type.toLowerCase().replace("s","");
        String archive_fragment = "";
        if (show_archive == 0 && (related_type.equals("topic") || this.object_type.equals("topic"))) {
            archive_fragment = "AND archive = 0";
        }
        String queryString = String.format("SELECT * FROM %ss WHERE _ID IN ( SELECT %s_id FROM %s WHERE %s_id = %s %s);",
                related_type, related_type, getRelationshipTableName(related_type), this.object_type, this.id, archive_fragment);
        return getPrimedCursor(queryString);
    }

    public Cursor getConditionalRowsCursor(String related_type, int show_related, int show_archive, int through_group) {
        related_type = related_type.toLowerCase().replace("s","");
        StringBuilder sb = new StringBuilder(200);
        sb.append("SELECT * FROM ").append(related_type).append("s");
        // Special - get topics through groups
        if (through_group == 1 && this.object_type.equals("contact") && related_type.equals("topic")) {
            sb.append(" WHERE _id IN (").append(getTopicsThroughGroups(show_archive)).append(")");
        } else {
            if ((show_archive == 0 && related_type.equals("topic")) || (show_related == 1)) {
                sb.append(" WHERE");
            }
            if (show_archive == 0 && related_type.equals("topic")) {
                sb.append(" archive = 0 AND");
            }
            if (show_related == 1) {
                String select_string = String.format(" _ID IN ( SELECT %s_id FROM %s WHERE %s_id = %s",
                        related_type, getRelationshipTableName(related_type), this.object_type, this.id);
                sb.append(select_string);
                if (show_archive == 0 && (related_type.equals("topic") || this.object_type.equals("topic"))) {
                    sb.append(" AND archive = 0");
                }
                sb.append(")");
            }
        }
        sb.append(";");
        return getPrimedCursor(sb.toString());
    }

    public String getTopicsThroughGroups(int show_archive) {

        // Initialize fragment string for use below.
        String archive_fragment = "";
        if (show_archive == 0) {
            archive_fragment = "AND archive = 0";
        }

        // Get list of topics via group
        ArrayList<String> group_ids = getItemInfoArray(getAllRelationshipsCursor("group", show_archive), "_id");
        String query_fragment = convertListToQueryFragment(group_ids);
        Cursor cursor = getPrimedCursor(String.format("SELECT DISTINCT topic_id FROM group_topic WHERE group_id IN (%s) %s;",
                query_fragment, archive_fragment));
        ArrayList<String> group_topic_ids = getItemInfoArray(cursor, "topic_id");

        // Get list of topics
        ArrayList<String> archived_topics = new ArrayList<String>();
        ArrayList<String> unarchived_topics = new ArrayList<String>();
        cursor = getAllRelationshipsCursor("topic", 1);  // Get archived topics so we can throw out group_topics that have an override
        cursor.moveToPosition(-1); // sometimes I wonder about my life choices
        while(cursor.moveToNext()) {
            if (cursor.getInt(cursor.getColumnIndex("archive")) == 0) {
                unarchived_topics.add(cursor.getString(cursor.getColumnIndex("_id")));
            } else {
                archived_topics.add(cursor.getString(cursor.getColumnIndex("_id")));
            }
        }

        // Convert them to sets so we can do some logics.
        Set<String> group_topic_id_set = new HashSet<String>(group_topic_ids);
        Set<String> archived_topic_set = new HashSet<String>(archived_topics);
        Set<String> unarchived_topic_set = new HashSet<String>(unarchived_topics);

        group_topic_id_set.removeAll(archived_topic_set); // remove archived contact_topics
        group_topic_id_set.addAll(unarchived_topic_set); // then add the unarchived contact_topics

        ArrayList<String> id_list = new ArrayList<String>(group_topic_id_set);
        return convertListToQueryFragment(id_list);
    }

    public Integer getRelationshipStatus(String status_type, String related_type, String related_id) {
        Cursor cursor = getRelationshipCursor(related_type, related_id);
        return cursor.getInt(cursor.getColumnIndex(status_type));
    }

    public void getAllImageStatus(String related_type, String related_id) {
        Cursor cursor = getRelationshipCursor(related_type, related_id);
        if (cursor.getCount() > 0) {
            this.mCachedStatus.star_status = cursor.getInt(cursor.getColumnIndex("star"));
            this.mCachedStatus.star_lock_status = cursor.getInt(cursor.getColumnIndex("star_lock"));
            this.mCachedStatus.archive_status = cursor.getInt(cursor.getColumnIndex("archive"));
            this.mCachedStatus.archive_lock_status = cursor.getInt(cursor.getColumnIndex("archive_lock"));
        } else {
            this.mCachedStatus.star_status = 0;
            this.mCachedStatus.star_lock_status = 0;
            this.mCachedStatus.archive_status = 0;
            this.mCachedStatus.archive_lock_status = 0;
        }
    }

    public Integer getImageResourceStatus(String image_type, String related_type, String related_id) {

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

    public void updateObject(String name, String details) {
        // Allows us to call updateObject on non-topic objects, which have no URI field to update.
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
        // Update the item itself
        ContentValues newValues = new ContentValues();
        newValues.put("star", 1 - this.star);
        mWriteDb.update(this.object_type_plural, newValues, "_id=?", new String[]{this.id});
        this.star = 1 - this.star;

        // Update related items
        String[] types = {"contact", "group"};
        for (String type: types) {
            String query = String.format("UPDATE %s_topic SET star = %s WHERE topic_id = %s AND star_lock = 0;",
                    type, this.star, this.id);
            mWriteDb.execSQL(query);
        }

        return this.star;
    }

    public Integer toggleArchiveStatus() {
        ContentValues newValues = new ContentValues();
        newValues.put("archive", 1 - this.archive);
        mWriteDb.update(this.object_type_plural, newValues, "_id=?", new String[]{this.id});
        this.archive = 1 - this.archive;

        // Update related items
        String[] types = {"contact", "group"};
        for (String type: types) {
            String query = String.format("UPDATE %s_topic SET archive = %s WHERE topic_id = %s AND archive_lock = 0;",
                    type, this.archive, this.id);
            mWriteDb.execSQL(query);
        }

        return this.archive;
    }

    public Integer toggleRelationshipStatus(String relationship_type, String related_type, String related_id) {
        // This may be a contact-topic relationship created via groups, so if it's been toggled, we may need to create it.
        if (this.object_type.equals("contact") && related_type.equals("Topics")) {
            Cursor cursor = getPrimedCursor(String.format("SELECT * FROM contact_topic WHERE contact_id = %s AND topic_id = %s;",
                    this.id, related_id));
            if (cursor.getCount() == 0) {
                // This is a bit of a hack, because it doesn't use any info about the archive status of the connecting group.
                String queryString = String.format("INSERT into contact_topic (contact_id, topic_id) VALUES (%s, %s);",
                        this.id, related_id);
                mWriteDb.execSQL(queryString);
            }
        }

        Integer old_status = getRelationshipStatus(relationship_type, related_type, related_id);
        ContentValues newValues = new ContentValues();
        newValues.put(relationship_type, 1 - old_status);
        String item_column_name = this.object_type + "_id";
        String related_column_name = related_type.toLowerCase().replace("s","") + "_id";
        mWriteDb.update(getRelationshipTableName(related_type), newValues,
                item_column_name + " = ? AND " + related_column_name + " = ?",
                new String[]{this.id, related_id});

        // Update related contact items
        if (related_type.equals("Groups")) {
            Cursor cursor = getPrimedCursor(String.format("SELECT * FROM contact_group WHERE group_id = %s;", related_id));
            String contact_ids = convertListToQueryFragment(getItemInfoArray(cursor, "contact_id"));
            String query = String.format("UPDATE contact_topic SET archive = %s WHERE contact_id IN (%s ) AND topic_id = %s AND archive_lock = 0;",
                    1 - old_status, contact_ids, related_id);
            mWriteDb.execSQL(query);
        }

        return  1 - old_status;
    }
}

package com.example.android.smalltalk.SmalltalkUtilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.android.smalltalk.DetailActivity;

/**
 * Created by shauna on 9/7/15.
 */
public class misc_utils {


    public static void goToDetailView(Context context, Cursor row_cursor, String item_type) {
        int item_id = row_cursor.getInt(row_cursor.getColumnIndexOrThrow("_id"));
        Intent intent = new Intent(context, DetailActivity.class)
                .putExtra("item_id", Integer.toString(item_id))
                .putExtra("item_type", item_type.toLowerCase());
        context.startActivity(intent);
    }

}

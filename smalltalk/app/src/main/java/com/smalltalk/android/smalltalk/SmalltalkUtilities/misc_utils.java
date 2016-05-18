package com.smalltalk.android.smalltalk.SmalltalkUtilities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;

import com.smalltalk.android.smalltalk.DetailActivity;

import java.util.HashMap;

public class misc_utils {

    public static void goToDetailView(Context context, Cursor row_cursor, String item_type) {
        int item_id = row_cursor.getInt(row_cursor.getColumnIndexOrThrow("_id"));
        Intent intent = new Intent(context, DetailActivity.class)
                .putExtra("item_id", Integer.toString(item_id))
                .putExtra("item_type", item_type.toLowerCase());
        context.startActivity(intent);
    }

    public static HashMap getScreenAdaption(Context context) {
        HashMap screenAdaptationHashMap = new HashMap();

        int size = context.getResources().getConfiguration().screenLayout;
        size &= Configuration.SCREENLAYOUT_SIZE_MASK;
        int layout = context.getResources().getConfiguration().orientation;

        switch (size) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                if (layout == Configuration.ORIENTATION_LANDSCAPE) {
                    screenAdaptationHashMap.put("extraDetail", false);
                    screenAdaptationHashMap.put("customRelationship", false);
                } else {
                    screenAdaptationHashMap.put("extraDetail", false);
                    screenAdaptationHashMap.put("customRelationship", false);
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                if (layout == Configuration.ORIENTATION_LANDSCAPE) {
                    screenAdaptationHashMap.put("extraDetail", true);
                    screenAdaptationHashMap.put("customRelationship", true);
                } else {
                    screenAdaptationHashMap.put("extraDetail", false);
                    screenAdaptationHashMap.put("customRelationship", true);
                }
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                if (layout == Configuration.ORIENTATION_LANDSCAPE) {
                    screenAdaptationHashMap.put("extraDetail", true);
                    screenAdaptationHashMap.put("customRelationship", true);
                } else {
                    screenAdaptationHashMap.put("extraDetail", true);
                    screenAdaptationHashMap.put("customRelationship", true);
                }
                break;
            default:  // Normal
                if (layout == Configuration.ORIENTATION_LANDSCAPE) {
                    screenAdaptationHashMap.put("extraDetail", false);
                    screenAdaptationHashMap.put("customRelationship", true);
                } else {
                    screenAdaptationHashMap.put("extraDetail", false);
                    screenAdaptationHashMap.put("customRelationship", false);
                }
        }

        return screenAdaptationHashMap;
    }


}

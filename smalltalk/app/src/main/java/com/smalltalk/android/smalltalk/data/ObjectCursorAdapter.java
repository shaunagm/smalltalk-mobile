package com.smalltalk.android.smalltalk.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.smalltalk.android.smalltalk.R;
import com.smalltalk.android.smalltalk.SmalltalkUtilities.misc_utils;

import java.util.HashMap;

public class ObjectCursorAdapter extends CursorAdapter {
    HashMap screenAdaptation;


    public ObjectCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        screenAdaptation = misc_utils.getScreenAdaption(context);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_object, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView objectName = (TextView) view.findViewById(R.id.list_item_name_textview);
        TextView objectDetails = (TextView) view.findViewById(R.id.list_item_details_textview);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String details = cursor.getString(cursor.getColumnIndexOrThrow("details"));

        objectName.setText(name);
        objectDetails.setText(details);

        if (details.isEmpty()) {
            objectDetails.setVisibility(View.GONE);
        } else {
            objectDetails.setVisibility(View.VISIBLE);
        }

    }

}
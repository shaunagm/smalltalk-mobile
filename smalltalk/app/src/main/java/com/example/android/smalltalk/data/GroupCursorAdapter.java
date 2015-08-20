package com.example.android.smalltalk.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.smalltalk.R;
import com.example.android.smalltalk.data.SmalltalkContract.GroupEntry;

public class GroupCursorAdapter extends CursorAdapter {
    public GroupCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_group, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView groupName = (TextView) view.findViewById(R.id.list_item_group_textview);
        TextView groupDetails = (TextView) view.findViewById(R.id.list_item_details_textview);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(GroupEntry.COLUMN_GROUP_NAME));
        String details = cursor.getString(cursor.getColumnIndexOrThrow(GroupEntry.COLUMN_GROUP_DETAILS));

        groupName.setText(name);
        groupDetails.setText(details);
    }

}
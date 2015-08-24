package com.example.android.smalltalk.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CursorAdapter;

import com.example.android.smalltalk.data.SmalltalkContract.ContactEntry;
import com.example.android.smalltalk.R;

public class ContactCursorAdapter extends CursorAdapter {
    public ContactCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView contactName = (TextView) view.findViewById(R.id.list_item_contact_textview);
        TextView contactDetails = (TextView) view.findViewById(R.id.list_item_contact_details_textview);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_CONTACT_NAME));
        String details = cursor.getString(cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_CONTACT_DETAILS));

        contactName.setText(name);
        contactDetails.setText(details);
    }

}
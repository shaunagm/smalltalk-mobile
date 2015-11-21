package com.example.android.smalltalk.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smalltalk.R;

import java.util.Arrays;

public class ImportContactCursorAdapter extends CursorAdapter {

    ListView mListView;
    Context mContext;
    Cursor mCursor;
    String mNameField;
    String mID;
    public String[] names;
    public Boolean[] itemChecked;
    public String[] itemID;


    public ImportContactCursorAdapter(Context context, Cursor cursor, String type, ListView listView) {
        super(context, cursor, 0);
        this.mContext = context;
        this.mCursor = cursor;
        this.mListView = listView;
        if (type.equals("contact")) {
            this.mNameField = ContactsContract.Contacts.DISPLAY_NAME;
        } else {
            this.mNameField = ContactsContract.Groups.TITLE;
            this.mID = ContactsContract.Groups._ID;
            itemID = new String[this.getCount()];
        }
        names = new String[this.getCount()];
        itemChecked = new Boolean[this.getCount()];
        Arrays.fill(itemChecked, false);
    }

    // The newView method is used to inflate a new view and return it,
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.checkbox_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final int position = cursor.getPosition();

        TextView name_view = (TextView) view.findViewById(R.id.import_contact_name);
        String name = cursor.getString(cursor.getColumnIndex(mNameField));
        name_view.setText(name);
        names[position] = name;

        // Only add ID info for groups.
        if (!(this.mID == null)) {
            TextView id_view = (TextView) view.findViewById(R.id.import_contact_hidden_id);
            String id = cursor.getString(cursor.getColumnIndex(mID));
            id_view.setText(id);
            itemID[position] = id;
        }

        CheckBox checkbox = (CheckBox) view.findViewById(R.id.import_contact_checkbox);
        checkbox.setOnCheckedChangeListener(null);  // View recycling will cause checked state weirdness without this.

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.import_contact_checkbox);
                itemChecked[position] = checkbox.isChecked();
            }
        });
        checkbox.setChecked(itemChecked[position]);
    }

    public void selectAllToggle() {

        Boolean newValue;
        newValue = !itemChecked[0];

        for ( int j = 0; j < mCursor.getCount(); j++ ) {
            itemChecked[j] = newValue;
            if (j < mListView.getChildCount()) {
            View childView = mListView.getChildAt(j);
            CheckBox checkbox = (CheckBox) childView.findViewById(R.id.import_contact_checkbox);
            checkbox.setChecked(newValue);
            }
        }
    }

}
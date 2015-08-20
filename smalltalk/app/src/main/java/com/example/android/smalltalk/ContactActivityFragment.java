package com.example.android.smalltalk;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.smalltalk.data.ContactCursorAdapter;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.SmalltalkContract.ContactEntry;
import com.example.android.smalltalk.SmalltalkUtilities;
import com.example.android.smalltalk.data.SmalltalkContract.TopicEntry;
import com.example.android.smalltalk.data.SmalltalkContract.GroupEntry;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ContactActivityFragment extends Fragment {

    ContactCursorAdapter mContactAdapter;
    SmalltalkDBHelper mdbHelper;

    public ContactActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mdbHelper = new SmalltalkDBHelper(this.getActivity());
        SmalltalkUtilities.addContacts(mdbHelper.getWritableDatabase());  // Uncomment to repopulate with fake data

        // Get data from database

        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        Cursor cursor = readDb.rawQuery("SELECT * FROM contacts ORDER BY contact_name ASC", new String[] {});

        // Get views
        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        ListView contact_list = (ListView) rootView.findViewById(R.id.listview_contacts);

        // Setup cursor adapter
        mContactAdapter = new ContactCursorAdapter(this.getActivity(), cursor, 0);
        contact_list.setAdapter(mContactAdapter);

        final ContactCursorAdapter newContactAdapter = mContactAdapter; // Make new final

        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor row_cursor = (Cursor) newContactAdapter.getItem(position);
                String name_item = row_cursor.getString(row_cursor.getColumnIndexOrThrow(ContactEntry.COLUMN_CONTACT_NAME));
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, name_item);
                startActivity(intent);
            }
        });

        return rootView;

    }
}

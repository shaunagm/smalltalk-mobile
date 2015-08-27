package com.example.android.smalltalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.smalltalk.data.GroupCursorAdapter;
import com.example.android.smalltalk.data.GroupCursorAdapter;
import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupActivityFragment extends Fragment {

    GroupCursorAdapter mGroupAdapter;
    SmalltalkDBHelper mdbHelper;

    public GroupActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get data from database
        mdbHelper = SmalltalkDBHelper.getInstance(getActivity());
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        Cursor cursor = readDb.rawQuery("SELECT * FROM groups ORDER BY name ASC", new String[] {});

        // Get views
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        ListView group_list = (ListView) rootView.findViewById(R.id.listview_groups);

        // Setup cursor adapter
        mGroupAdapter = new GroupCursorAdapter(this.getActivity(), cursor, 0);
        group_list.setAdapter(mGroupAdapter);

        final GroupCursorAdapter newGroupAdapter = mGroupAdapter; // Make new final

        group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor row_cursor = (Cursor) newGroupAdapter.getItem(position);
                SmalltalkUtilities.goToDetailView(view.getContext(), row_cursor, "group");
            }
        });

        return rootView;

    }
}

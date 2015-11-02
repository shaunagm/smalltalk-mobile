package com.example.android.smalltalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.SmalltalkUtilities.misc_utils;
import com.example.android.smalltalk.data.ObjectCursorAdapter;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListActivityFragment extends Fragment {

    public ListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get type from intent
        Intent intent = getActivity().getIntent();
        final String list_type = intent.getStringExtra("list_type");
        final int show_archived = intent.getIntExtra("show_archived", 0);

        // Get data from database
        SmalltalkDBHelper mdbHelper = SmalltalkDBHelper.getInstance(getActivity());
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        Cursor cursor = db_utils.getListCursorGivenType(getActivity(), list_type, show_archived);

        // Views!
        View rootView = inflater.inflate(R.layout.list_activity_layout, container, false);

        // Show archived button
        Button show_archived_button = (Button) rootView.findViewById(R.id.listview_show_archived);
        if (list_type.equals("topics")) {
            if (show_archived == 1) {
                show_archived_button.setText("Hide archived topics");
            }
            show_archived_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ListActivity.class)
                            .putExtra("list_type", list_type)
                            .putExtra("show_archived", 1 - show_archived);
                    startActivity(intent);
                }
            });
        } else {
            show_archived_button.setVisibility(View.GONE);
        }

        ListView object_list = (ListView) rootView.findViewById(R.id.listview_objects);
        final ObjectCursorAdapter adapter = new ObjectCursorAdapter(this.getActivity(), cursor, 0);
        object_list.setAdapter(adapter);
        object_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor row_cursor = (Cursor) adapter.getItem(position);
                misc_utils.goToDetailView(view.getContext(), row_cursor, list_type.replace("s", ""));
            }
        });

        return rootView;
    }
}

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

import com.example.android.smalltalk.data.TopicCursorAdapter;
import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopicActivityFragment extends Fragment {

    TopicCursorAdapter mTopicAdapter;
    SmalltalkDBHelper mdbHelper;

    public TopicActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get data from database
        mdbHelper = SmalltalkDBHelper.getInstance(getActivity());
        SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
        Cursor cursor = readDb.rawQuery("SELECT * FROM topics ORDER BY name ASC", new String[] {});

        // Get views
        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        ListView topic_list = (ListView) rootView.findViewById(R.id.listview_topics);

        // Setup cursor adapter
        mTopicAdapter = new TopicCursorAdapter(this.getActivity(), cursor, 0);
        topic_list.setAdapter(mTopicAdapter);

        final TopicCursorAdapter newTopicAdapter = mTopicAdapter; // Make new final

        topic_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor row_cursor = (Cursor) newTopicAdapter.getItem(position);
                String item_type = "topics";
                Integer item_id = row_cursor.getInt(row_cursor.getColumnIndexOrThrow(SmalltalkContract.TopicEntry._ID));
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("item_id", item_id)
                        .putExtra("item_type", item_type);
                startActivity(intent);
            }
        });

        return rootView;

    }
}

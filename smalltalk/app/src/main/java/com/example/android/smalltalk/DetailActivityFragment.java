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
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smalltalk.data.ContactCursorAdapter;
import com.example.android.smalltalk.data.GroupCursorAdapter;
import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.TopicCursorAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    SmalltalkDBHelper mdbHelper;
    ContactCursorAdapter mContactAdapter;
    TopicCursorAdapter mTopicAdapter;
    GroupCursorAdapter mGroupAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mdbHelper = SmalltalkDBHelper.getInstance(this.getActivity());

        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("item_type") && intent.hasExtra("item_id")) {
            String item_type = intent.getStringExtra("item_type");
            Integer item_id = intent.getIntExtra("item_id", 99);
            String item_id_as_string = item_id.toString();

            SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
            String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                    item_type, item_id_as_string);
            Cursor cursor = readDb.rawQuery(queryString, new String[]{});
            cursor.moveToNext();

            ((TextView) rootView.findViewById(R.id.detail_item_name))
                    .setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            ((TextView) rootView.findViewById(R.id.detail_item_details))
                    .setText(cursor.getString(cursor.getColumnIndexOrThrow("details")));
            ((TextView) rootView.findViewById(R.id.detail_item_type))
                    .setText(item_type);
            ((TextView) rootView.findViewById(R.id.detail_item_id_secret))
                    .setText(item_id_as_string);


            if (!item_type.equals("contacts")) {

                ((TextView) rootView.findViewById(R.id.detail_item_contact_title)).setVisibility(View.VISIBLE);

                String ContactQueryString;
                if (item_type.equals("topics")) {
                    ContactQueryString = String.format("SELECT * FROM contacts WHERE _ID IN ( SELECT " +
                            "contact_id FROM topic_contact WHERE topic_id = %s);", item_id_as_string);
                } else {
                    ContactQueryString = String.format("SELECT * FROM contacts WHERE _ID IN ( SELECT " +
                            "contact_id FROM contact_group WHERE group_id = %s);", item_id_as_string);
                };
                Cursor contact_cursor = readDb.rawQuery(ContactQueryString, new String[]{});

                ListView contact_list = (ListView) rootView.findViewById(R.id.listview_contacts);
                mContactAdapter = new ContactCursorAdapter(this.getActivity(), contact_cursor, 0);
                contact_list.setAdapter(mContactAdapter);

                final ContactCursorAdapter newContactAdapter = mContactAdapter; // Make new final
                contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Cursor row_cursor = (Cursor) newContactAdapter.getItem(position);
                        String item_type = "contacts";
                        Integer item_id = row_cursor.getInt(row_cursor.getColumnIndexOrThrow(SmalltalkContract.ContactEntry._ID));
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra("item_id", item_id)
                                .putExtra("item_type", item_type);
                        startActivity(intent);
                    }
                });
            };

            if (!item_type.equals("groups")) {

                ((TextView) rootView.findViewById(R.id.detail_item_group_title)).setVisibility(View.VISIBLE);

                String GroupQueryString;
                if (item_type.equals("topics")) {
                    GroupQueryString = String.format("SELECT * FROM groups WHERE _ID IN ( SELECT " +
                            "group_id FROM topic_group WHERE topic_id = %s);", item_id_as_string);
                } else {
                    GroupQueryString = String.format("SELECT * FROM groups WHERE _ID IN ( SELECT " +
                            "group_id FROM contact_group WHERE contact_id = %s);", item_id_as_string);
                };
                Cursor group_cursor = readDb.rawQuery(GroupQueryString, new String[]{});

                ListView group_list = (ListView) rootView.findViewById(R.id.listview_groups);
                mGroupAdapter = new GroupCursorAdapter(this.getActivity(), group_cursor, 0);
                group_list.setAdapter(mGroupAdapter);

                final GroupCursorAdapter newGroupAdapter = mGroupAdapter; // Make new final
                group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Cursor row_cursor = (Cursor) newGroupAdapter.getItem(position);
                        String item_type = "groups";
                        Integer item_id = row_cursor.getInt(row_cursor.getColumnIndexOrThrow(SmalltalkContract.GroupEntry._ID));
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra("item_id", item_id)
                                .putExtra("item_type", item_type);
                        startActivity(intent);
                    }
                });
            };

            if (!item_type.equals("topics")) {

                ((TextView) rootView.findViewById(R.id.detail_item_topic_title)).setVisibility(View.VISIBLE);

                String TopicQueryString;
                if (item_type.equals("contacts")) {
                    TopicQueryString = String.format("SELECT * FROM topics WHERE _ID IN ( SELECT " +
                            "topic_id FROM topic_contact WHERE contact_id = %s);", item_id_as_string);
                } else {
                    TopicQueryString = String.format("SELECT * FROM topics WHERE _ID IN ( SELECT " +
                            "topic_id FROM topic_group WHERE group_id = %s);", item_id_as_string);
                };
                Cursor topic_cursor = readDb.rawQuery(TopicQueryString, new String[]{});

                ListView topic_list = (ListView) rootView.findViewById(R.id.listview_topics);
                mTopicAdapter = new TopicCursorAdapter(this.getActivity(), topic_cursor, 0);
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
            };


        };

        return rootView;
    }
}

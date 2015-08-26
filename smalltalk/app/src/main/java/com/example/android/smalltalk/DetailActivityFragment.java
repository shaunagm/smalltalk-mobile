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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

import com.example.android.smalltalk.data.ContactCursorAdapter;
import com.example.android.smalltalk.data.ExpandableCheckboxAdapter;
import com.example.android.smalltalk.data.GroupCursorAdapter;
import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.TopicCursorAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    SmalltalkDBHelper mdbHelper;
    ContactCursorAdapter mContactAdapter;
    TopicCursorAdapter mTopicAdapter;
    GroupCursorAdapter mGroupAdapter;
    ExpandableListView mExpandableListView;
    ExpandableCheckboxAdapter mExpandableCheckboxAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mdbHelper = SmalltalkDBHelper.getInstance(this.getActivity());

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("item_type") && intent.hasExtra("item_id")) {

            String item_type = intent.getStringExtra("item_type");
            String item_id = intent.getStringExtra("item_id");

            SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
            String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                    item_type, item_id);
            Cursor cursor = readDb.rawQuery(queryString, new String[]{});
            cursor.moveToNext();

            ((TextView) rootView.findViewById(R.id.detail_item_name))
                    .setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            ((TextView) rootView.findViewById(R.id.detail_item_details))
                    .setText(cursor.getString(cursor.getColumnIndexOrThrow("details")));
            ((TextView) rootView.findViewById(R.id.detail_item_type))
                    .setText(item_type);
            ((TextView) rootView.findViewById(R.id.detail_item_id_secret))
                    .setText(item_id);

            mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view);
            ArrayList<String> list_headers = new ArrayList<String>();
            HashMap<String, List<String>> list_items = new HashMap();

            if (!item_type.equals("contacts")) {

                String ContactQueryString;
                if (item_type.equals("topics")) {
                    ContactQueryString = String.format("SELECT * FROM contacts WHERE _ID IN ( SELECT " +
                            "contact_id FROM topic_contact WHERE topic_id = %s);", item_id);
                } else {
                    ContactQueryString = String.format("SELECT * FROM contacts WHERE _ID IN ( SELECT " +
                            "contact_id FROM contact_group WHERE group_id = %s);", item_id);
                };
                Cursor contact_cursor = readDb.rawQuery(ContactQueryString, new String[]{});

                ArrayList<String> contact_items = new ArrayList<String>();
                int columnIndex=contact_cursor.getColumnIndex("name");
                while(contact_cursor.moveToNext()) {
                    contact_items.add(contact_cursor.getString(columnIndex)); //add the item
                }

                list_headers.add("Contacts");
                list_items.put("Contacts", contact_items);
            };

            if (!item_type.equals("groups")) {

                String GroupQueryString;
                if (item_type.equals("topics")) {
                    GroupQueryString = String.format("SELECT * FROM groups WHERE _ID IN ( SELECT " +
                            "group_id FROM topic_group WHERE topic_id = %s);", item_id);
                } else {
                    GroupQueryString = String.format("SELECT * FROM groups WHERE _ID IN ( SELECT " +
                            "group_id FROM contact_group WHERE contact_id = %s);", item_id);
                };
                Cursor group_cursor = readDb.rawQuery(GroupQueryString, new String[]{});

                ArrayList<String> group_items = new ArrayList<String>();
                int columnIndex=group_cursor.getColumnIndex("name");
                while(group_cursor.moveToNext()) {
                    group_items.add(group_cursor.getString(columnIndex)); //add the item
                }

                list_headers.add("Groups");
                list_items.put("Groups", group_items);

            };

            if (!item_type.equals("topics")) {

                String TopicQueryString;
                if (item_type.equals("contacts")) {
                    TopicQueryString = String.format("SELECT * FROM topics WHERE _ID IN ( SELECT " +
                            "topic_id FROM topic_contact WHERE contact_id = %s);", item_id);
                } else {
                    TopicQueryString = String.format("SELECT * FROM topics WHERE _ID IN ( SELECT " +
                            "topic_id FROM topic_group WHERE group_id = %s);", item_id);
                };
                Cursor topic_cursor = readDb.rawQuery(TopicQueryString, new String[]{});

                ArrayList<String> topic_items = new ArrayList<String>();
                int columnIndex=topic_cursor.getColumnIndex("name");
                while(topic_cursor.moveToNext()) {
                    topic_items.add(topic_cursor.getString(columnIndex)); //add the item
                }

                list_headers.add("Topics");
                list_items.put("Topics", topic_items);
            };
            mExpandableCheckboxAdapter = new ExpandableCheckboxAdapter(this.getActivity(), list_headers, list_items);
            mExpandableListView.setAdapter(mExpandableCheckboxAdapter);
            final ArrayList<String> new_list_headers = list_headers;
            final HashMap<String, List<String>> new_list_items = list_items;

            mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View view,
                                            int groupPosition, int childPosition, long id) {
                    String item_type = new_list_headers.get(groupPosition);
                    String item_name = new_list_items.get(item_type).get(childPosition);
                    SmalltalkUtilities.goToDetailViewGivenNameAndType(view.getContext(),
                           item_name, item_type);
                    return false;
                }
            });

        };
        return rootView;
    }
}

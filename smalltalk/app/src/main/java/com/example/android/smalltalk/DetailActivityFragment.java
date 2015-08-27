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
import com.example.android.smalltalk.data.SmalltalkObject;
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
            SmalltalkObject current_object = new SmalltalkObject(getActivity(), item_id, item_type);
            Cursor cursor = current_object.getRowCursor(getActivity());
            cursor.moveToNext();

            ((TextView) rootView.findViewById(R.id.detail_item_name))
                    .setText(current_object.getName());
            ((TextView) rootView.findViewById(R.id.detail_item_details))
                    .setText(current_object.getDetails());
            ((TextView) rootView.findViewById(R.id.detail_item_type))
                    .setText(item_type);
            ((TextView) rootView.findViewById(R.id.detail_item_id_secret))
                    .setText(item_id);

            mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view);
            ArrayList<String> list_headers = new ArrayList<String>();
            HashMap<String, List<String>> list_items = new HashMap();

            if (!item_type.equals("contact")) {
                ArrayList<String> contact_items = current_object.getRelatedNames(getActivity(), "contacts");
                list_headers.add("Contacts");
                list_items.put("Contacts", contact_items);
            };

            if (!item_type.equals("group")) {
                ArrayList<String> group_items = current_object.getRelatedNames(getActivity(), "groups");
                list_headers.add("Groups");
                list_items.put("Groups", group_items);
            };

            if (!item_type.equals("topic")) {
                ArrayList<String> topic_items = current_object.getRelatedNames(getActivity(), "topics");
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

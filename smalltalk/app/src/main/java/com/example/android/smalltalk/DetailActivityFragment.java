package com.example.android.smalltalk;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
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
            Cursor cursor = current_object.getRowCursor();
            cursor.moveToNext();

            ((TextView) rootView.findViewById(R.id.detail_item_name))
                    .setText(current_object.getName());
            ((TextView) rootView.findViewById(R.id.detail_item_details))
                    .setText(current_object.getDetails());
            ((TextView) rootView.findViewById(R.id.detail_item_type))
                    .setText(item_type);
            ((TextView) rootView.findViewById(R.id.detail_item_id_secret))
                    .setText(item_id);

            if (item_type.equals("topic")) {
                ImageButton star_button = (ImageButton) rootView.findViewById(R.id.star_button);
                star_button.setVisibility(View.VISIBLE);
                if (current_object.getStarStatus() == 1) {
                    star_button.setImageResource(R.drawable.star_on);
                }
                ImageButton archive_button = (ImageButton) rootView.findViewById(R.id.archive_button);
                archive_button.setVisibility(View.VISIBLE);
                if (current_object.getArchiveStatus() == 1) {
                    archive_button.setImageResource(R.drawable.archive_on);
                }

                TextView URIField = (TextView) rootView.findViewById(R.id.detail_item_uri);
                URIField.setText(current_object.getURI());
                URIField.setVisibility(View.VISIBLE);

                URIField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView URIField = (TextView) v.findViewById(R.id.detail_item_uri);
                        Uri webpage = Uri.parse(URIField.getText().toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(intent);
                    }
                });
            }

            mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandable_list_view);
            mExpandableCheckboxAdapter = new ExpandableCheckboxAdapter(this.getActivity(), current_object, false);
            mExpandableListView.setAdapter(mExpandableCheckboxAdapter);

            mExpandableListView.setOnChildClickListener(new OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View view,
                                            int groupPosition, int childPosition, long id) {
                    String item_type = mExpandableCheckboxAdapter.getGroup(groupPosition).toLowerCase().replace("s","");
                    String item_id = mExpandableCheckboxAdapter.getChildDBId(groupPosition, childPosition);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("item_id", item_id)
                            .putExtra("item_type", item_type);
                    startActivity(intent);
                    return false;
                }
            });

        };
        return rootView;
    }
}

package com.example.android.smalltalk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.SmalltalkUtilities.misc_utils;
import com.example.android.smalltalk.data.ObjectCursorAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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
        final int show_starred = intent.getIntExtra("show_starred", 0);

        // Get data from database
        Cursor cursor = db_utils.getListCursorGivenType(getActivity(), list_type, show_archived, show_starred);

        // Views!
        View rootView = inflater.inflate(R.layout.list_activity_layout, container, false);

        TextView header = (TextView) rootView.findViewById(R.id.listview_header);
        if (list_type.equals("groups")) {
            header.setText("tags");
        } else  {
            header.setText(list_type);
        }

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        // Show archived button
        Button show_archived_button = (Button) rootView.findViewById(R.id.listview_show_archived);
        if (list_type.equals("topics")) {
            if (show_archived == 1) {
                show_archived_button.setText("Hide archived");
            }
            show_archived_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ListActivity.class)
                            .putExtra("list_type", list_type)
                            .putExtra("show_starred", show_starred)
                            .putExtra("show_archived", 1 - show_archived);
                    startActivity(intent);
                }
            });
        } else {
            show_archived_button.setVisibility(View.GONE);
        }

        // Show starred button
        // Show archived button
        Button show_starred_button = (Button) rootView.findViewById(R.id.listview_show_starred_only);
        if (list_type.equals("topics")) {
            if (show_starred == 1) {
                show_starred_button.setText("Show un-starred");
            }
            show_starred_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ListActivity.class)
                            .putExtra("list_type", list_type)
                            .putExtra("show_starred", 1 - show_starred)
                            .putExtra("show_archived", show_archived);
                    startActivity(intent);
                }
            });
        } else {
            show_starred_button.setVisibility(View.GONE);
        }

        ListView object_list = (ListView) rootView.findViewById(R.id.listview_objects);
        final ObjectCursorAdapter adapter = new ObjectCursorAdapter(this.getActivity(), cursor);
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

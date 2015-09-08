package com.example.android.smalltalk;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.example.android.smalltalk.data.ExpandableCheckboxAdapter;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.SmalltalkObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    SmalltalkDBHelper mdbHelper;
    ExpandableListView mExpandableListView;
    ExpandableCheckboxAdapter mExpandableCheckboxAdapter;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.detail_layout, container, false);
        mdbHelper = SmalltalkDBHelper.getInstance(this.getActivity());

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("item_type") && intent.hasExtra("item_id")) {

            final int show_archived = intent.getIntExtra("show_archived", 0);
            final int show_through = intent.getIntExtra("show_through", 0);

            final String item_type = intent.getStringExtra("item_type");
            final String item_id = intent.getStringExtra("item_id");
            SmalltalkObject current_object = new SmalltalkObject(getActivity(), item_id, item_type, show_archived);
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

            Button show_archive_button = (Button) rootView.findViewById(R.id.show_archived_relationships);
            if (show_archived == 1) {
                show_archive_button.setText("Hide archived relationships");
            }
            show_archive_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("item_type", item_type)
                            .putExtra("item_id", item_id)
                            .putExtra("show_archived", 1 - show_archived)
                            .putExtra("show_through", show_through);
                    startActivity(intent);
                }
            });

            if (item_type.equals("contact")) {
                Button show_through_button = (Button) rootView.findViewById(R.id.show_through_relationships);
                show_through_button.setVisibility(View.VISIBLE);
                if (show_through == 1) {
                    show_through_button.setText("Exclude topics through groups");
                }
                show_through_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra("item_type", item_type)
                                .putExtra("item_id", item_id)
                                .putExtra("show_archived", show_archived)
                                .putExtra("show_through", 1 - show_through);
                        startActivity(intent);
                    }
                });
            }

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
            mExpandableCheckboxAdapter = new ExpandableCheckboxAdapter(this.getActivity(), current_object, false, show_archived, show_through);
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

package com.example.android.smalltalk;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.smalltalk.data.RelationshipAdapter;
import com.example.android.smalltalk.data.SmalltalkObject;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DetailActivityFragment extends Fragment {

    SmalltalkObject current_object;
    String first_type;
    String second_type;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true); // Make sure we use our detailactivity-specific actionbar

        View rootView = inflater.inflate(R.layout.detail_layout, container, false);

        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("item_type") && intent.hasExtra("item_id")) {

            final boolean show_archived = intent.getBooleanExtra("show_archived", false);
            final boolean show_through = intent.getBooleanExtra("show_through", false);

            final String item_type = intent.getStringExtra("item_type");
            final String item_id = intent.getStringExtra("item_id");
            current_object = new SmalltalkObject(getActivity(), item_id, item_type, show_archived);
            Cursor cursor = current_object.getRowCursor();
            cursor.moveToNext();

            ((TextView) rootView.findViewById(R.id.detail_item_name))
                    .setText(current_object.getName());
            ((TextView) rootView.findViewById(R.id.detail_item_details))
                    .setText(current_object.getDetails());
            ((TextView) rootView.findViewById(R.id.detail_item_type_secret))
                    .setText(item_type);
            ((TextView) rootView.findViewById(R.id.detail_item_id_secret))
                    .setText(item_id);

            // If type is topic, add extra fields.
            if (item_type.equals("topic")) {
                TextView URIField = (TextView) rootView.findViewById(R.id.detail_item_uri);
                URIField.setText(current_object.getURI());
                URIField.setVisibility(View.VISIBLE);
                URIField.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView URIField = (TextView) v.findViewById(R.id.detail_item_uri);
                        Uri webpage = Uri.parse(URIField.getText().toString());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        try
                        { startActivity(intent); }
                        catch (ActivityNotFoundException e)
                        {
                            Toast toast = Toast.makeText(getActivity(),"There was a problem with your URL.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }

                    }
                });
            }

            ExpandableListView list = (ExpandableListView) rootView.findViewById(R.id.relationship_list);
            RelationshipAdapter adapter = new RelationshipAdapter(this.getActivity(), current_object,
                    show_archived, show_through, list);
            list.setAdapter(adapter);

            // Isn't it fun putting design stuff in the business logic?
            list.setGroupIndicator(null);
            list.setDividerHeight(5);

        };
        return rootView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Get the base menu
        super.onCreateOptionsMenu(menu, inflater);

        if (current_object == null ) {
            getActivity().invalidateOptionsMenu();
//            return; // If current_object hasn't been set yet, wait for createView to finish.
        }

        // Go through all current menu items and change them as needed:
        MenuItem current_item;
        for(int i = 0; i < menu.size(); i++) {
            current_item = menu.getItem(i);
            switch (current_item.getItemId()) {
                case R.id.edit_action:
                    current_item.setVisible(true);
                    break;
                case R.id.star_action:
                    if (current_object.getType().equals("topic")) {
                        if (current_object.getStarStatus() == 1) {
                            current_item.setIcon(R.drawable.star_white_full);
                            current_item.setTitle("un-star");
                        }
                        current_item.setVisible(true);
                    }
                    break;
                case R.id.archive_action:
                    if (current_object.getType().equals("topic")) {
                        if (current_object.getArchiveStatus() == 1) {
                            current_item.setIcon(R.drawable.hide_white);
                            current_item.setTitle("restore");
                        }
                        current_item.setVisible(true);
                    }
                    break;
                case R.id.search:
                    current_item.setVisible(false);
                default:
                    current_item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            // Main Actions
            case R.id.edit_action:
                Intent intent = new Intent(this.getActivity(), EditActivity.class)
                        .putExtra("item_id", current_object.getID())
                        .putExtra("item_type", current_object.getType());
                startActivity(intent);
                return true;
            case R.id.archive_action:
                current_object.toggleArchiveStatus();
                getActivity().invalidateOptionsMenu(); // Need to redraw options menu to show toggled archive icon/text
                return true;
            case R.id.star_action:
                current_object.toggleStarStatus();
                getActivity().invalidateOptionsMenu(); // Need to redraw options menu to show toggled archive icon/text
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

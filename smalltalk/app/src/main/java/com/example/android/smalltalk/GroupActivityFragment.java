package com.example.android.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupActivityFragment extends Fragment {

    ArrayAdapter<String> mGroupAdapter;

    public GroupActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] fake_group_data = new String[]{
                "Scoobies",
                "Slayers",
                "Vampires",
                "Everybody!"
        };

        List<String> listOfTopics = new ArrayList<String>(Arrays.asList(fake_group_data));

        mGroupAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_group, // The name of the layout ID.
                R.id.list_item_group_textview, // The ID of the textview to populate.
                listOfTopics);

        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_groups);
        listView.setAdapter(mGroupAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String topic_item = mGroupAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, topic_item);
                startActivity(intent);
            }
        });

        return rootView;

    }
}

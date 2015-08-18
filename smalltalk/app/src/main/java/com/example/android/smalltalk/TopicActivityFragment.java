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
public class TopicActivityFragment extends Fragment {

    ArrayAdapter<String> mTopicAdapter;

    public TopicActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] fake_topic_data = new String[]{
                "Stakes!  Why did it have to be stakes?",
                "Who run the world?  Vampires!",
                "Treeeeees"
        };

        List<String> listOfTopics = new ArrayList<String>(Arrays.asList(fake_topic_data));

        mTopicAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_topic, // The name of the layout ID.
                R.id.list_item_topic_textview, // The ID of the textview to populate.
                listOfTopics);

        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_topics);
        listView.setAdapter(mTopicAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String topic_item = mTopicAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, topic_item);
                startActivity(intent);
            }
        });

        return rootView;

    }
}

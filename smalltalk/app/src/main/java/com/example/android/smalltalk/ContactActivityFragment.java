package com.example.android.smalltalk;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
public class ContactActivityFragment extends Fragment {

    ArrayAdapter<String> mNameAdapter;

    public ContactActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] fake_name_data = new String[]{
                "Buffy Summers",
                "Willow Rosenberg",
                "Xander Harris",
                "Rupert Giles"
        };

        List<String> listOfNames = new ArrayList<String>(Arrays.asList(fake_name_data));

        mNameAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_contact, // The name of the layout ID.
                R.id.list_item_name_textview, // The ID of the textview to populate.
                listOfNames);

        View rootView = inflater.inflate(R.layout.fragment_contact, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_contacts);
        listView.setAdapter(mNameAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String name_item = mNameAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, name_item);
                startActivity(intent);
            }
        });

        return rootView;

    }
}

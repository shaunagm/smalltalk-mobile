package com.example.android.smalltalk.data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.android.smalltalk.ImportContactsActivity;
import com.example.android.smalltalk.R;
import com.example.android.smalltalk.SmalltalkUtilities.import_utils;

import java.util.Arrays;
import java.util.List;


public class ContactOptionsAdapter extends ArrayAdapter<String> {
    List<String> mOptionLabels;
    List<String> mOptionShortnames;

    public ContactOptionsAdapter(Context context) {

        super(context, 0);

        String all_count = import_utils.getAndroidContactsCount(context, "all");
        String phone_count = import_utils.getAndroidContactsCount(context, "phone");
        String star_count = import_utils.getAndroidContactsCount(context, "star");
        String year_count = import_utils.getAndroidContactsCount(context, "year");
        String five_count = import_utils.getAndroidContactsCount(context, "five");

        mOptionLabels = Arrays.asList( "All contacts (" + all_count + ")",
                "With phone numbers (" + phone_count + ")",
                "Starred (" + star_count + ")",
                "Contacted within last year (" + year_count + ")",
                "Contacted five or more times (" + five_count + ")");
        mOptionShortnames = Arrays.asList("all", "phone", "star", "year", "five");
    }

    @Override
    public int getCount()
    {
        return mOptionShortnames.size();  // Since we're not passing in a list, the adapter needs
        // to know how many views to create.
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflate View
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.import_contact_list_options,
                parent, false);

        // Add description
        Button label = (Button) convertView.findViewById(R.id.import_contact_option_label);
        label.setText(mOptionLabels.get(position));

        // Add invisible shortname
        TextView shortname = (TextView) convertView.findViewById(R.id.import_contact_invisible_shortname);
        shortname.setText(mOptionShortnames.get(position));

        // Add onclick function to move to import contacts (check if it should stay in fragment)
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get info from view and send intent
                CheckBox checkbox = (CheckBox) view.getRootView().findViewById(R.id.contact_options_group_option_checkbox);
                TextView shortname = (TextView) view.findViewById(R.id.import_contact_invisible_shortname);
                Intent intent = new Intent(getContext(), ImportContactsActivity.class)
                        .putExtra("groups", checkbox.isChecked())
                        .putExtra("type", shortname.getText().toString());
                getContext().startActivity(intent);

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}
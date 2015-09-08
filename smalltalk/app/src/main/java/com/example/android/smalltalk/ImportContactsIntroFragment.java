package com.example.android.smalltalk;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.smalltalk.SmalltalkUtilities.import_utils;
import com.example.android.smalltalk.data.ImportContactCursorAdapter;

/**
 * Created by shauna on 9/8/15.
 */
public class ImportContactsIntroFragment extends android.support.v4.app.Fragment {

    String[] mContactOptions;

    public ImportContactsIntroFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.import_contacts_intro, container, false);
        Context context = container.getContext();

        String all_count = import_utils.getAndroidContactsCount(context, "all");
        String phone_count = import_utils.getAndroidContactsCount(context, "phone");
        String star_count = import_utils.getAndroidContactsCount(context, "star");
        String year_count = import_utils.getAndroidContactsCount(context, "year");
        String five_count = import_utils.getAndroidContactsCount(context, "five");

        mContactOptions = new String[] { "All (" + all_count + " contacts)",
                "Only contacts with phone #s (" + phone_count + " contacts)",
                "Only starred contacts (" + star_count + " contacts)",
                "Contacts contacted within last year (" + year_count + " contacts)",
                "Contacts contacted five or more times (" + five_count + " contacts)"};

        ListView listView = (ListView) rootView.findViewById(R.id.contact_options_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(),
                R.layout.import_contact_intro_list_item, R.id.import_contact_option_label, mContactOptions);
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get info from view and add to Bundle
                CheckBox checkbox = (CheckBox) view.getRootView().findViewById(R.id.contact_options_group_option_checkbox);
                Bundle bundle = new Bundle();
                String[] contact_options = {"all", "phone", "star", "year", "five"};
                bundle.putString("type", contact_options[position]);
                bundle.putBoolean("groups", checkbox.isChecked());

                // Create fragment and bundle selected option with it
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                ImportContactsFragment import_contacts = new ImportContactsFragment();
                import_contacts.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_layout_content, import_contacts)
                        .commit();
            }
        });

        return rootView;
    }



}


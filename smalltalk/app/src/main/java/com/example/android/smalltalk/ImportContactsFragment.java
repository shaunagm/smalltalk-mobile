package com.example.android.smalltalk;

import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.SmalltalkUtilities.import_utils;
import com.example.android.smalltalk.data.ContactOptionsAdapter;
import com.example.android.smalltalk.data.ImportContactCursorAdapter;


public class ImportContactsFragment extends android.support.v4.app.Fragment {

    public ImportContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();

        // If no intent, do intro
        if (!(intent.hasExtra("type"))) {
            View rootView = inflater.inflate(R.layout.import_contacts_intro, container, false);
            Context context = container.getContext();
            ListView listView = (ListView) rootView.findViewById(R.id.contact_options_list);
            ContactOptionsAdapter optionsAdapter = new ContactOptionsAdapter(this.getActivity());
            listView.setAdapter(optionsAdapter);
            return rootView;
        }

        String type = intent.getStringExtra("type");
        Boolean include_groups = intent.getBooleanExtra("groups", false);

        View rootView = inflater.inflate(R.layout.import_contacts_layout, container, false);

        Cursor cursor = import_utils.getAndroidContacts(this.getActivity(), type);
        final ImportContactCursorAdapter contact_adapter = new ImportContactCursorAdapter(this.getActivity(), cursor, 0, "contact");
        ListView contacts = (ListView) rootView.findViewById(R.id.listview_import_contacts);
        contacts.setAdapter(contact_adapter);

        final ImportContactCursorAdapter group_adapter;
        if (include_groups) {
            cursor = import_utils.getPhoneGroups(this.getActivity());
            group_adapter = new ImportContactCursorAdapter(this.getActivity(), cursor, 0, "group");
            ListView groups = (ListView) rootView.findViewById(R.id.listview_import_groups);
            groups.setVisibility(View.VISIBLE);
            groups.setAdapter(group_adapter);
            rootView.findViewById(R.id.import_groups_header).setVisibility(View.VISIBLE);
        } else {
            group_adapter = null; // need to initialize this so we can pass it to save_imports
        }

        // Save button!
        Button save_button = (Button) rootView.findViewById(R.id.import_contacts_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save_imports(contact_adapter, group_adapter);
            }
        });

        return rootView;

    }

    public void save_imports(ImportContactCursorAdapter contact_adapter, ImportContactCursorAdapter group_adapter) {

        // Goes through list of contacts.  If checked, create the contact and add smalltalk ID
        // to smalltalk_contact_ids.  Otherwise, add a "" to smalltalk_contact_ids.
        String[] smalltalk_contact_names = new String[contact_adapter.names.length];
        String[] smalltalk_contact_ids = new String[contact_adapter.names.length];
        for (int i = 0; i < contact_adapter.names.length; i++) {
            if (contact_adapter.itemChecked[i] == true) {
                String id = db_utils.getOrCreate(this.getActivity(), "contacts", contact_adapter.names[i]);
                smalltalk_contact_ids[i] = id;
                smalltalk_contact_names[i] = contact_adapter.names[i].toLowerCase();
            } else {
                smalltalk_contact_names[i] = "";
            }
        }

        if (!(group_adapter == null)) {
            // Goes through groups.  If checked, create the group, temporarily storing the smalltalk ID
            // for the group.  Then get list of contact ids in the group.  If any match contact_ids, check
            // smalltalk_contact_ids for their ID.  If the number is not 0, create a relationship.
            for (int j = 0; j < group_adapter.names.length; j++) {
                if (group_adapter.itemChecked[j] == true) {
                    String smalltalk_group_id = db_utils.getOrCreate(this.getActivity(), "groups", group_adapter.names[j]);
                    String[] contact_names = import_utils.getContactNamesGivenGroup(this.getActivity(), group_adapter.itemID[j]);
                    for (int k = 0; k < contact_names.length; k++) {
                        if (Arrays.asList(smalltalk_contact_names).contains(contact_names[k])) {
                            int index = Arrays.asList(smalltalk_contact_names).indexOf(contact_names[k]);
                            db_utils.createContactGroupRelationship(this.getActivity(), smalltalk_contact_ids[index], smalltalk_group_id);
                        }
                    }
                }
            }
        }
    }
}
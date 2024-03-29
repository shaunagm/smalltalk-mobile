package com.smalltalk.android.smalltalk;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.smalltalk.android.smalltalk.SmalltalkUtilities.db_utils;
import com.smalltalk.android.smalltalk.SmalltalkUtilities.import_utils;
import com.smalltalk.android.smalltalk.data.ContactOptionsAdapter;
import com.smalltalk.android.smalltalk.data.ImportContactCursorAdapter;

import java.util.Arrays;


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
            ListView listView = (ListView) rootView.findViewById(R.id.contact_options_list);
            ContactOptionsAdapter optionsAdapter = new ContactOptionsAdapter(this.getActivity());
            listView.setAdapter(optionsAdapter);
            return rootView;
        }

        // Otherwise, show import screen
        String type = intent.getStringExtra("type");
        Boolean include_groups = intent.getBooleanExtra("groups", false);
        final View rootView = inflater.inflate(R.layout.import_contacts_layout, container, false);

        // Make contacts listview, assign adapter
        ListView contacts = (ListView) rootView.findViewById(R.id.listview_import_contacts);
        Cursor cursor = import_utils.getAndroidContacts(this.getActivity(), type);
        final ImportContactCursorAdapter contact_adapter = new ImportContactCursorAdapter(this.getActivity(), cursor, "contact", contacts);
        contacts.setAdapter(contact_adapter);

        // Make groups listview if necessary

        final ImportContactCursorAdapter group_adapter;
        if (include_groups) {
            // Make groups listview, set header, assign adapter
            ListView groups = (ListView) rootView.findViewById(R.id.listview_import_groups);
            cursor = import_utils.getPhoneGroups(this.getActivity());
            group_adapter = new ImportContactCursorAdapter(this.getActivity(), cursor, "group", groups);
            groups.setAdapter(group_adapter);
        } else {
            group_adapter = null; // need to initialize this so we can pass it to save_imports
        }

        // Toggle group-type buttons
        Button toggle_type = (Button) rootView.findViewById(R.id.import_contacts_toggle_type);
        toggle_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleType();
            }
        });

        // Select button
        final Button toggle_select = (Button) rootView.findViewById(R.id.import_contacts_toggle_select);
        toggle_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSelect();
            }
        });

        // Save button
        Button save_button = (Button) rootView.findViewById(R.id.import_contacts_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_imports(contact_adapter, group_adapter);
            }
        });

        return rootView;

    }

    // Allows users to toggle between viewing groups and contacts
    public void toggleType() {

        ListView contacts = (ListView) this.getActivity().findViewById(R.id.listview_import_contacts);
        ListView groups = (ListView) this.getActivity().findViewById(R.id.listview_import_groups);
        TextView header = (TextView) this.getActivity().findViewById(R.id.listview_header);
        Button toggle_type = (Button) this.getActivity().findViewById(R.id.import_contacts_toggle_type);

        if (contacts.getVisibility() == View.VISIBLE) {
            contacts.setVisibility(View.GONE);
            groups.setVisibility(View.VISIBLE);
            header.setText("Groups/Tags");
            toggle_type.setText("view contacts");
        } else {
            contacts.setVisibility(View.VISIBLE);
            groups.setVisibility(View.GONE);
            header.setText("Contacts");
            toggle_type.setText("view groups/tags");
        }
    }

    public void toggleSelect() {

        Button toggle_type = (Button) this.getActivity().findViewById(R.id.import_contacts_toggle_type);
        ListView listView;
        if (toggle_type.getText().equals("view contacts")) {
            listView = (ListView) this.getActivity().findViewById(R.id.listview_import_groups);
        } else {
            listView = (ListView) this.getActivity().findViewById(R.id.listview_import_contacts);
        }

        ImportContactCursorAdapter listAdapter = (ImportContactCursorAdapter) listView.getAdapter();
        listAdapter.selectAllToggle();
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

        Intent intent = new Intent(getActivity(), ListActivity.class)
                .putExtra("list_type", "contacts")
                .putExtra("show_archived", 0);
        startActivity(intent);

    }

}


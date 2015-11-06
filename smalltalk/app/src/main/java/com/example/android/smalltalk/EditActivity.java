package com.example.android.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.SmalltalkObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class EditActivity extends BaseActivity {

    SmalltalkDBHelper mdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_form);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("item_type") && intent.hasExtra("item_id")) {
                // edit item
                String item_type = intent.getStringExtra("item_type");
                String item_id = intent.getStringExtra("item_id");
                SmalltalkObject current_object = new SmalltalkObject(this, item_id, item_type);
                populate_edit_view(current_object);
            } else if (!(intent.getType() == null) && intent.getType().equals("text/plain")) {
                // add item from share
                String name = intent.getStringExtra(Intent.EXTRA_TITLE);
                String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                URL url;
                try { url = new URL(text); }
                catch (MalformedURLException e) { url = null; }
                populate_share_view(name, text, url);
            }
            // If neither share nor add matches, or if in fact intent is null, will show a regular
            // "Add new data" form by default.
        }
    }

    public void populate_share_view(String name, String details, URL url) {

        // shared items are always topics
        RadioButton item_type = (RadioButton) findViewById(R.id.edit_item_type_topic);
        item_type.setChecked(true);
        item_type.setVisibility(View.GONE);

        EditText nameField = (EditText) findViewById(R.id.edit_item_name);
        nameField.setText(name);

        if (!(url == null)) {
            EditText URIField = (EditText) findViewById(R.id.edit_item_uri);
            URIField.setText(url.toString());
        }

        EditText detailsField = (EditText) findViewById(R.id.edit_item_details);
        detailsField.setText(details);

    }

    // Go through and adapt to edit_data_form and the default of
    public void populate_edit_view(SmalltalkObject current_object) {

        TextView secret_id_view = (TextView) findViewById(R.id.edit_item_id);
        secret_id_view.setText(current_object.getID());

        RadioButton item_type;
        if (current_object.getType().equals("contact")) {
            item_type = (RadioButton) findViewById(R.id.edit_item_type_contact);
        } else if (current_object.getType().equals("group")) {
            item_type = (RadioButton) findViewById(R.id.edit_item_type_group);
        } else {
            item_type = (RadioButton) findViewById(R.id.edit_item_type_topic);
        }
        item_type.setChecked(true);
        RadioGroup typeSet = (RadioGroup) findViewById(R.id.edit_item_type);
        typeSet.setVisibility(View.GONE);

        EditText nameField = (EditText) findViewById(R.id.edit_item_name);
        nameField.setText(current_object.getName());

        EditText URIField = (EditText) findViewById(R.id.edit_item_uri);
        if (current_object.getType().equals("topic")) {
            URIField.setText(current_object.getURI());
        } else {
            URIField.setVisibility(View.INVISIBLE);
        }

        EditText detailsField = (EditText) findViewById(R.id.edit_item_details);
        detailsField.setText(current_object.getDetails());

        Button edit_button = (Button) findViewById(R.id.edit_save_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_item(v);
            }
        });

    }


    public void add_item(View view) {

        final EditText nameField = (EditText) findViewById(R.id.edit_item_name);
        String item_name = nameField.getText().toString();

        final EditText detailsField = (EditText) findViewById(R.id.edit_item_details);
        String item_details = detailsField.getText().toString();

        final EditText uriField = (EditText) findViewById(R.id.edit_item_uri);
        String item_uri = uriField.getText().toString();

        RadioGroup item_type_options = (RadioGroup) findViewById(R.id.edit_item_type);
        RadioButton selected_item_view = (RadioButton) item_type_options.findViewById(item_type_options.getCheckedRadioButtonId());
        String item_type = (String) selected_item_view.getText();

        long id = db_utils.createObject(this, item_type, item_name, item_details, item_uri);

        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", Long.toString(id))
                .putExtra("item_type", item_type);
        startActivity(intent);
    }

    public void update_item(View view) {

        final EditText nameField = (EditText) findViewById(R.id.edit_item_name);
        String item_name = nameField.getText().toString();

        final EditText detailsField = (EditText) findViewById(R.id.edit_item_details);
        String item_details = detailsField.getText().toString();

        RadioGroup item_type_options = (RadioGroup) view.findViewById(R.id.edit_item_type);
        RadioButton selected_item_view = (RadioButton) item_type_options.findViewById(item_type_options.getCheckedRadioButtonId());
        String item_type = (String) selected_item_view.getText();

        final TextView idField = (TextView) findViewById(R.id.edit_item_id);
        String item_id = idField.getText().toString();

        SmalltalkObject object_to_update = new SmalltalkObject(this, item_id, item_type);

        if (item_type.equals("topic")) {
            final EditText uriField = (EditText) findViewById(R.id.edit_item_uri);
            String item_uri = uriField.getText().toString();
            object_to_update.updateObject(item_name, item_details, item_uri);
        } else {
            object_to_update.updateObject(item_name, item_details);
        }

        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", item_id)
                .putExtra("item_type", item_type);
        startActivity(intent);

    }
}

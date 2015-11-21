package com.example.android.smalltalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.data.SmalltalkObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class EditActivity extends BaseActivity {

    SmalltalkObject current_object;
    Boolean[] errors;
    String[] warnings;

    // There are three situations in which this fragment may run:
    //      1) to add new items
    //      2) to create new items from the share intent
    //      3) to edit an existing item


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_data_form);

        errors = new Boolean[]{false, false, false};
        warnings = new String[]{"Name is a required field. ",
                "You already have an item by that name. ",
                "That is not a valid URI. "};

        // shared items are usually topics, so select by default
        RadioButton default_type = (RadioButton) findViewById(R.id.edit_item_type_topic);
        default_type.setChecked(true);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("item_type") && intent.hasExtra("item_id")) {
                // edit item
                String item_type = intent.getStringExtra("item_type");
                String item_id = intent.getStringExtra("item_id");
                current_object = new SmalltalkObject(this, item_id, item_type);
                populate_edit_view();
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

        // Add listener+validator to name & uri fields
        EditText nameField = (EditText) findViewById(R.id.edit_item_name);
        nameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!(hasFocus)) {
                    textValidator(view, "name"); // Check text once user is done typing.
                }
            }
        });

        EditText uriField = (EditText) findViewById(R.id.edit_item_uri);
        uriField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!(hasFocus)) {
                    // Check text once user is done typing.
                    textValidator(view, "uri");
                }
            }
        });

        // For views where you can choose type, hide/show URI when topic is deselected/selected.
        RadioGroup item_type = (RadioGroup) findViewById(R.id.edit_item_type);
        if (item_type.getVisibility() == View.VISIBLE) {
            item_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    EditText uriField = (EditText) findViewById(R.id.edit_item_uri);
                    if (checkedId == R.id.edit_item_type_topic) {
                        uriField.setVisibility(View.VISIBLE);
                    } else {
                        uriField.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    public void populate_share_view(String name, String details, URL url) {

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
    public void populate_edit_view() {

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

        Button delete_button = (Button) findViewById(R.id.edit_delete_button);
        delete_button.setVisibility(View.VISIBLE);

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

        if (item_type.equals(("tag"))) {
            item_type = "group";  // Change from user-visible name to database name.
        }

        long id = db_utils.createObject(this, item_type, item_name, item_details, item_uri);

        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", Long.toString(id))
                .putExtra("item_type", item_type);
        startActivity(intent);
    }

    public void delete_item(View view) {
        EditText name = (EditText) view.getRootView().findViewById(R.id.edit_item_name);
        String nameText = name.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);

        builder.setTitle("Delete " + nameText + "?");

        builder.setPositiveButton("Yes, delete this", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Boolean success = current_object.removeSelf();

                if (!success) {
                    Toast toast = Toast.makeText(EditActivity.this, "There was a problem deleting this item. Please contact the app developer.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Intent list_contact_intent = new Intent(getApplicationContext(), ListActivity.class)
                            .putExtra("list_type", current_object.getType() + "s");
                    startActivity(list_contact_intent);
                }
            }
        });
        builder.setNegativeButton("No, go back", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void update_item(View view) {

        final EditText nameField = (EditText) findViewById(R.id.edit_item_name);
        String item_name = nameField.getText().toString();

        final EditText detailsField = (EditText) findViewById(R.id.edit_item_details);
        String item_details = detailsField.getText().toString();

        RadioGroup item_type_options = (RadioGroup) findViewById(R.id.edit_item_type);
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

    public void textValidator(View view, String text_type) {

        if (text_type.equals("name")) {

            EditText nameField = (EditText) findViewById(R.id.edit_item_name);
            String nameText = nameField.getText().toString();

            if (nameText.length() == 0) {
                errors[0] = true;
                errors[1] = false;
            } else {
                errors[0] = false;

                // Find the item type selected
                RadioGroup item_type_options = (RadioGroup) findViewById(R.id.edit_item_type);
                RadioButton selected_item_view = (RadioButton) item_type_options.findViewById(item_type_options.getCheckedRadioButtonId());
                String item_type = (String) selected_item_view.getText();
                // Query to see if there's a duplicate.  If yes, warn user.
                String id = db_utils.checkExists(getApplicationContext(), nameText, item_type + "s");

                if (id.equals("0")) {
                    // if there's no duplicate, yay!
                    errors[1] = false;
                } else {
                    // Check that it's not an edited item matching its own name
                    TextView secret_id_view = (TextView) findViewById(R.id.edit_item_id);
                    errors[1] = !id.equals(secret_id_view.getText().toString());
                }
            }

        } else {

            EditText uriField = (EditText) findViewById(R.id.edit_item_uri);
            String uri = uriField.getText().toString();

            if (uri.toString().length() == 0) {
                errors[2] = false;
            } else {

                // Check for scheme & add it if missing
                String newUrl = uri;
                Pattern scheme = Pattern.compile("((?:(http|https|Http|Https|HTTP|HTTPS):\\/{1,2}?)).+?");
                if (!(scheme.matcher(uri).matches())) {
                    newUrl = "http://".concat(uri);
                }

                // If URI now passes, update field and set errors to none.
                if (Patterns.WEB_URL.matcher(newUrl).matches()) {
                    uriField.setText(newUrl, TextView.BufferType.EDITABLE);
                    errors[2] = false;
                } else {
                    errors[2] = true; // Something else wrong with the URI :(
                }
            }
        }

        // Allow user to press "save" button only if all fields are valid.
        Button saveButton = (Button) findViewById(R.id.edit_save_button);
        TextView warningField = (TextView) findViewById(R.id.edit_warnings);
        if (validationWarnings().length() == 0) {
            saveButton.setClickable(true);
            warningField.setText(validationWarnings());
            warningField.setVisibility(View.GONE);
        } else {
            saveButton.setClickable(false);
            warningField.setText(validationWarnings());
            warningField.setVisibility(View.VISIBLE);
        }

    }


    public String validationWarnings() {

        // Checks to see if form is ready to be submitted & returns warnings if not.
        String warningString = "";
        if (errors[0]) {
            warningString = warningString.concat(warnings[0]);
        }
        if (errors[1]) {
            warningString = warningString.concat(warnings[1]);
        }
        EditText uriField = (EditText) findViewById(R.id.edit_item_uri);
        if ((errors[2]) && (uriField.getVisibility() == View.VISIBLE)) {
            warningString = warningString.concat(warnings[2]);
        }

        return warningString;

    }

}

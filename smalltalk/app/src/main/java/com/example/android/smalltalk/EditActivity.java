package com.example.android.smalltalk;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

import java.util.Arrays;

public class EditActivity extends BaseActivity {

    SmalltalkDBHelper mdbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        super.onCreateDrawer();

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content_frame);
        viewGroup.addView(View.inflate(this, R.layout.edit_data_form, null));

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("item_type") && intent.hasExtra("item_id")) {
            String item_type = intent.getStringExtra("item_type");
            String item_id_as_string = intent.getStringExtra("item_id");

            mdbHelper = SmalltalkDBHelper.getInstance(this);
            SQLiteDatabase readDb = mdbHelper.getReadableDatabase();
            String queryString = String.format("SELECT * FROM %s WHERE _ID = %s LIMIT 1;",
                    item_type, item_id_as_string);
            Cursor cursor = readDb.rawQuery(queryString, new String[]{});
            cursor.moveToNext();

            String[] typeOptions = {"contacts", "groups", "topics"};
            Spinner typeSpinner = (Spinner) findViewById(R.id.new_item_type_spinner);
            typeSpinner.setSelection(Arrays.asList(typeOptions).indexOf(item_type));

            EditText nameField = (EditText) findViewById(R.id.new_item_form_name);
            nameField.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));

            EditText detailsField = (EditText) findViewById(R.id.new_item_form_details);
            detailsField.setText(cursor.getString(cursor.getColumnIndexOrThrow("details")));

            TextView secret_id_view = (TextView) findViewById(R.id.detail_item_id_secret);
            secret_id_view.setText(item_id_as_string);
        }
    }

    public void update_item(View view) {

        mdbHelper = SmalltalkDBHelper.getInstance(this);

        final Spinner typeSpinner = (Spinner) findViewById(R.id.new_item_type_spinner);
        String item_type = typeSpinner.getSelectedItem().toString();

        final EditText nameField = (EditText) findViewById(R.id.new_item_form_name);
        String item_name = nameField.getText().toString();

        final EditText detailsField = (EditText) findViewById(R.id.new_item_form_details);
        String item_details = detailsField.getText().toString();

        final TextView idField = (TextView) findViewById(R.id.detail_item_id_secret);
        String item_id = idField.getText().toString();

        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long newID = 0;
        String table_name = "Null";
        String selection = "_ID LIKE ?";
        String[] selectionArgs = { item_id };


        switch (item_type) {
            case "Contact":
                values.put(SmalltalkContract.ContactEntry.COLUMN_CONTACT_NAME, item_name);
                values.put(SmalltalkContract.ContactEntry.COLUMN_CONTACT_DETAILS, item_details);
                table_name = SmalltalkContract.ContactEntry.TABLE_NAME;
                break;
            case "Group":
                values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_NAME, item_name);
                values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_DETAILS, item_details);
                table_name = SmalltalkContract.GroupEntry.TABLE_NAME;
                break;
            case "Topic":
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_NAME, item_name);
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_DETAILS, item_details);
                table_name = SmalltalkContract.TopicEntry.TABLE_NAME;
                break;
        }

        db.update(table_name, values, selection, selectionArgs);
        db.close();

        String item_type_reformatted = item_type.toLowerCase().concat("s");
        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", Integer.parseInt(item_id))
                .putExtra("item_type", item_type_reformatted);
        startActivity(intent);


    }
}

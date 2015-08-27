package com.example.android.smalltalk;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.smalltalk.data.ExpandableCheckboxAdapter;
import com.example.android.smalltalk.data.RelatedObjectMap;
import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.SmalltalkObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
            String item_id = intent.getStringExtra("item_id");
            SmalltalkObject current_object = new SmalltalkObject(this, item_id, item_type);
            Cursor cursor = current_object.getRowCursor(this);
            cursor.moveToNext();

            EditText nameField = (EditText) findViewById(R.id.new_item_form_name);
            nameField.setText(current_object.getName());

            EditText detailsField = (EditText) findViewById(R.id.new_item_form_details);
            detailsField.setText(current_object.getDetails());

            TextView secret_type_view = (TextView) findViewById(R.id.detail_item_type_secret);
            secret_type_view.setText(item_type);

            TextView secret_id_view = (TextView) findViewById(R.id.detail_item_id_secret);
            secret_id_view.setText(item_id);

            ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.expandable_list_view);

            RelatedObjectMap object_map = new RelatedObjectMap();
            
            if (!item_type.equals("contact")) {

                object_map.addHeaderNames("Contacts");
                object_map.addNamesAndIDs("Contacts", current_object.getAllItemsNamesAndIDs(this, "contacts"));
                object_map.addPrecheckedList("Contacts", current_object.getRelatedNames(this, "contacts"));
                
            };

            if (!item_type.equals("group")) {

                object_map.addHeaderNames("Groups");
                object_map.addNamesAndIDs("Groups", current_object.getAllItemsNamesAndIDs(this, "groups"));
                object_map.addPrecheckedList("Groups", current_object.getRelatedNames(this, "groups"));
            };

            if (!item_type.equals("topic")) {

                object_map.addHeaderNames("Topics");
                object_map.addNamesAndIDs("Topics", current_object.getAllItemsNamesAndIDs(this, "topics"));
                object_map.addPrecheckedList("Topics", current_object.getRelatedNames(this, "topics"));
                
            };

            ExpandableCheckboxAdapter eCA = new ExpandableCheckboxAdapter(this, object_map, current_object);
            expandableListView.setAdapter(eCA);
        }
    }

    public void update_item(View view) {

        mdbHelper = SmalltalkDBHelper.getInstance(this);

        final EditText nameField = (EditText) findViewById(R.id.new_item_form_name);
        String item_name = nameField.getText().toString();

        final EditText detailsField = (EditText) findViewById(R.id.new_item_form_details);
        String item_details = detailsField.getText().toString();

        final TextView typeField = (TextView) findViewById(R.id.detail_item_type_secret);
        String item_type = typeField.getText().toString();

        final TextView idField = (TextView) findViewById(R.id.detail_item_id_secret);
        String item_id = idField.getText().toString();

        SmalltalkObject object_to_update = new SmalltalkObject(this, item_id, item_type);
        object_to_update.updateObject(this, item_name, item_details);

//        // Now take care of relationships
//        final ExpandableListView headerField = (ExpandableListView) findViewById(R.id.expandable_list_view);
//
//        for (int i = 0; i < headerField.getChildCount(); i++)
//        {
//            View current_view = headerField.getChildAt(i);
//            int spacewaste = 1;
//        }
//

        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", item_id)
                .putExtra("item_type", item_type);
        startActivity(intent);

    }
}

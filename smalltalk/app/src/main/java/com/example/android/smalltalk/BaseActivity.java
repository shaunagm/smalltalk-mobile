package com.example.android.smalltalk;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.SmalltalkObject;

import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerOptions;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent new_intent = new Intent(this.getApplicationContext(), EditActivity.class);
                this.startActivity(new_intent);
                return true;
            case R.id.list_topics:
                Intent list_topic_intent = new Intent(this.getApplicationContext(), ListActivity.class)
                        .putExtra("list_type", "topics");
                this.startActivity(list_topic_intent);
                return true;
            case R.id.list_contacts:
                Intent list_contact_intent = new Intent(this.getApplicationContext(), ListActivity.class)
                        .putExtra("list_type", "contacts");
                this.startActivity(list_contact_intent);
                return true;
            case R.id.list_groups:
                Intent list_group_intent = new Intent(this.getApplicationContext(), ListActivity.class)
                        .putExtra("list_type", "groups");
                this.startActivity(list_group_intent);
                return true;
            case R.id.import_contacts:
                Intent import_intent = new Intent(this.getApplicationContext(), ImportContactsActivity.class);
                this.startActivity(import_intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public SmalltalkObject getObjectFromView(View view) {
        TextView item_id_view = (TextView) findViewById(R.id.detail_item_id_secret);
        String item_id = item_id_view.getText().toString();

        TextView item_type_view = (TextView) findViewById(R.id.detail_item_type);
        String item_type = item_type_view.getText().toString();

        return new SmalltalkObject(this, item_id, item_type);
    }

    public void toggle(View view, String type) {

        SmalltalkObject current_object = getObjectFromView(view);
        int status = current_object.toggleStatus(type);
        switch (type) {
            case "star":
                ImageButton sButton = (ImageButton) view.findViewById(R.id.star_button);
                if (status == 1) {
                    sButton.setImageResource(R.drawable.star_on);
                } else {
                    sButton.setImageResource(R.drawable.star_off);
                }
                break;
            case "archive":
                ImageButton aButton = (ImageButton) view.findViewById(R.id.archive_button);
                if (status == 1) {
                    aButton.setImageResource(R.drawable.archive_on);
                } else {
                    aButton.setImageResource(R.drawable.archive_off);
                }
                break;
        }
    }

    public void toggle_star(View view) {
        toggle(view, "star");
    }

    public void toggle_archive(View view) {
        toggle(view, "archive");
    }

}

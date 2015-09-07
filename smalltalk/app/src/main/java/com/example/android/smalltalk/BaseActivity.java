package com.example.android.smalltalk;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class BaseActivity extends AppCompatActivity {

    SmalltalkDBHelper mdbHelper;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerOptions;

    protected void onCreateDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_content_copy_white_18dp,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerOptions = new String[] {"Topics", "Contacts", "Groups", "Add New", "Import Contacts"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int position) {

        String list_type = "";
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case 0:
                list_type = "topics";
                break;
            case 1:
                list_type = "contacts";
                break;
            case 2:
                list_type = "groups";
                break;
            case 3:
                AddDataFragment add_data = new AddDataFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, add_data)
                        .commit();
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerList);
                return;
            case 4:
                ImportContactsFragment import_contacts = new ImportContactsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, import_contacts)
                        .commit();
                mDrawerList.setItemChecked(position, true);
                mDrawerLayout.closeDrawer(mDrawerList);
                return;
        }

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);

        Intent intent = new Intent(this.getApplicationContext(), ListActivity.class)
                .putExtra("list_type", list_type);
        this.startActivity(intent);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    public void add_new_item(View view) {

        mdbHelper = SmalltalkDBHelper.getInstance(this);

        final Spinner typeSpinner = (Spinner) findViewById(R.id.new_item_type_spinner);
        String item_type = typeSpinner.getSelectedItem().toString();

        final EditText nameField = (EditText) findViewById(R.id.new_item_form_name);
        String item_name = nameField.getText().toString();

        final EditText detailsField = (EditText) findViewById(R.id.new_item_form_details);
        String item_details = detailsField.getText().toString();

        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        long newID = 0;

        switch (item_type) {
            case "Contact":
                values.put(SmalltalkContract.ContactEntry.COLUMN_CONTACT_NAME, item_name);
                values.put(SmalltalkContract.ContactEntry.COLUMN_CONTACT_DETAILS, item_details);
                newID = db.insert(SmalltalkContract.ContactEntry.TABLE_NAME, null, values);
                db.close();
                break;
            case "Group":
                values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_NAME, item_name);
                values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_DETAILS, item_details);
                newID = db.insert(SmalltalkContract.GroupEntry.TABLE_NAME, null, values);
                db.close();
                break;
            case "Topic":
                final EditText uriField= (EditText) findViewById(R.id.new_item_form_uri);
                String item_uri = uriField.getText().toString();
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_URI, item_uri);
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_NAME, item_name);
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_DETAILS, item_details);
                newID = db.insert(SmalltalkContract.TopicEntry.TABLE_NAME, null, values);
                db.close();
                break;
        }

        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", Long.toString(newID))
                .putExtra("item_type", item_type.toLowerCase());
        startActivity(intent);

    }

    public void import_contacts(View view) {
        Toast.makeText(getApplicationContext(), "IMPORT ME", Toast.LENGTH_SHORT).show();
    }

}

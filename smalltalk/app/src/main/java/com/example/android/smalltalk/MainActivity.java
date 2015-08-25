package com.example.android.smalltalk;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.widget.Spinner;

import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

public class MainActivity extends AppCompatActivity {

    SmalltalkDBHelper mdbHelper;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        SmalltalkUtilities.exportDB(this); // For debug purposes, because adb doesn't let you view databases with unrooted phones! >:(
//        this.deleteDatabase("smalltalk.db");
//        SmalltalkUtilities.populateDB(this);
        mdbHelper = SmalltalkDBHelper.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerOptions = new String[] {"Topics", "Contacts", "Groups", "Add New"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

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

        if (savedInstanceState == null) {
            selectItem(0);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (position) {
            case 0:
                TopicActivityFragment topicFragment = new TopicActivityFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, topicFragment)
                        .commit();
                break;
            case 1:
                ContactActivityFragment contactFragment = new ContactActivityFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, contactFragment)
                        .commit();
                break;
            case 2:
                GroupActivityFragment groupFragment = new GroupActivityFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, groupFragment)
                        .commit();
                break;
            case 3:
                AddDataFragment dataFragment = new AddDataFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, dataFragment)
                        .commit();
                break;
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void add_new_item(View view) {

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
            case "Topic":
                values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_NAME, item_name);
                values.put(SmalltalkContract.GroupEntry.COLUMN_GROUP_DETAILS, item_details);
                newID = db.insert(SmalltalkContract.GroupEntry.TABLE_NAME, null, values);
                db.close();
                break;
            case "Group":
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_NAME, item_name);
                values.put(SmalltalkContract.TopicEntry.COLUMN_TOPIC_DETAILS, item_details);
                newID = db.insert(SmalltalkContract.TopicEntry.TABLE_NAME, null, values);
                db.close();
                break;
        }

        String item_type_reformatted = item_type.toLowerCase().concat("s");
        Intent intent = new Intent(this, DetailActivity.class)
                .putExtra("item_id", (int) newID)
                .putExtra("item_type", item_type_reformatted);
        startActivity(intent);

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

}

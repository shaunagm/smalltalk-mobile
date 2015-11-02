package com.example.android.smalltalk;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smalltalk.data.SmalltalkObject;

public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerOptions;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        ComponentName cn = new ComponentName(this, SearchActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        return super.onCreateOptionsMenu(menu);
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
                break;
        }
        return false; // If an item selection is not handled by the main options, passes it on to fragment

    }

    public static int getContentViewCompat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ?
                android.R.id.content : R.id.action_bar_activity_content;
    }

}

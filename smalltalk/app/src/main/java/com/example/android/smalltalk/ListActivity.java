package com.example.android.smalltalk;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity_layout);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        ListActivityFragment listFragment = new ListActivityFragment();
        fragmentManager.beginTransaction()
                .add(R.id.list_activity, listFragment)
                .commit();
    }

}
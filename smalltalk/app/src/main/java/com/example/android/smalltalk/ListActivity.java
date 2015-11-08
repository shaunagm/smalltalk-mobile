package com.example.android.smalltalk;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class ListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        ListActivityFragment listFragment;
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            listFragment = new ListActivityFragment();
            ft.add(android.R.id.content, listFragment).commit();
        } else {
            listFragment = (ListActivityFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }

    }

}

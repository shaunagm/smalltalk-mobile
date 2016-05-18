package com.smalltalk.android.smalltalk;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class ImportContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        ImportContactsFragment introFragment;
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            introFragment = new ImportContactsFragment();
            ft.add(android.R.id.content, introFragment).commit();
        } else {
            introFragment = (ImportContactsFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }

    }

}


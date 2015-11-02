package com.example.android.smalltalk;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ImportContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ImportContactsFragment introFragment = new ImportContactsFragment();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, introFragment).commit();
    }

}


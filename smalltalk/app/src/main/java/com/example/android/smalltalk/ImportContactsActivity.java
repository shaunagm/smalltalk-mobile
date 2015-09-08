package com.example.android.smalltalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ImportContactsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        ImportContactsIntroFragment introFragment = new ImportContactsIntroFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_layout_content, introFragment)
                .commit();
    }

}


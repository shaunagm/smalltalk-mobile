package com.smalltalk.android.smalltalk;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        DetailActivityFragment detailFragment;
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            detailFragment = new DetailActivityFragment();
            ft.add(android.R.id.content, detailFragment).commit();
        } else {
            detailFragment = (DetailActivityFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }

    }


}

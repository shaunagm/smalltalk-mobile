package com.example.android.smalltalk;

import android.os.Bundle;

public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivityFragment detailFragment = new DetailActivityFragment();
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, detailFragment).commit();
    }


}

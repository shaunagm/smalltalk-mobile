package com.example.android.smalltalk;

import android.os.Bundle;
import com.example.android.smalltalk.SmalltalkUtilities.fixture_utils;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        super.onCreateDrawer();

//        fixture_utils.exportDB(this);
//        this.deleteDatabase("smalltalk.db");
//        fixture_utils.populateDB(this);

        // If no previous state, start with Topic View
        if (savedInstanceState == null) {
            super.selectItem(0);
        }

    }

}

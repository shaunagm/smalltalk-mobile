package com.example.android.smalltalk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

//        fixture_utils.exportDB(this);
//        this.deleteDatabase("smalltalk.db");
//        fixture_utils.populateDB(this);

        // If no previous state, start with Topic View
        if (savedInstanceState == null) {
            // Select Topic View via action bar?
        }

    }

}

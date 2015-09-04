package com.example.android.smalltalk;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.smalltalk.data.SmalltalkContract;
import com.example.android.smalltalk.data.SmalltalkDBHelper;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        super.onCreateDrawer();

//        SmalltalkUtilities.exportDB(this); // For debug purposes, because adb doesn't let you view databases with unrooted phones! >:(
//        this.deleteDatabase("smalltalk.db");
//        SmalltalkUtilities.populateDB(this);

        // If no previous state, start with Topic View
        if (savedInstanceState == null) {
            super.selectItem(0);
        }

    }

}

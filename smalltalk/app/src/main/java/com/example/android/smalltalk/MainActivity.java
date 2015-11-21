package com.example.android.smalltalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.android.smalltalk.SmalltalkUtilities.fixture_utils;
import com.example.android.smalltalk.SmalltalkUtilities.misc_utils;

import java.util.HashMap;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        HashMap screenAdaptations = misc_utils.getScreenAdaption(getApplicationContext());
        if ( screenAdaptations.get("dualPane") == true) {
            setContentView(R.layout.dual_pane_layout);
        } else {
            setContentView(R.layout.base_layout);
        }

        fixture_utils.exportDB(this);
        this.deleteDatabase("smalltalk.db");
        fixture_utils.populateDB(this);


        // If no previous state, start with Topic View
        if (savedInstanceState == null) {

            // If running app for the first time, created sharedpreferences file
            SharedPreferences settings = getSharedPreferences("SmalltalkPreferences", 0);
            if(!settings.contains("showHelp")) {
                SharedPreferences.Editor spEditor = settings.edit();
                spEditor.putBoolean("showHelp", true);
                spEditor.commit();
                Intent import_intent = new Intent(this.getApplicationContext(), LandingActivity.class);
                this.startActivity(import_intent);
            } else {
                boolean showHelp = settings.getBoolean("showHelp", true);
                if (showHelp) {
                    Intent import_intent = new Intent(this.getApplicationContext(), LandingActivity.class);
                    this.startActivity(import_intent);
                } else {
                    Intent import_intent = new Intent(this.getApplicationContext(), ListActivity.class)
                            .putExtra("list_type", "topics");
                    this.startActivity(import_intent);
                }

            }

        }

    }

}

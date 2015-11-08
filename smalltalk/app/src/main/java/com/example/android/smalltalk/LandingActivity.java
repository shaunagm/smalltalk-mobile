package com.example.android.smalltalk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.data.SmalltalkDBHelper;
import com.example.android.smalltalk.data.SmalltalkObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class LandingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_layout);

    }

    public void hide_help(View view) {
        SharedPreferences settings = getSharedPreferences("SmalltalkPreferences", 0);
        SharedPreferences.Editor spEditor = settings.edit();
        spEditor.putBoolean("showHelp", false);
        spEditor.commit();

        Toast toast = Toast.makeText(LandingActivity.this, "Great!  Your new default page is the list of topics.", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        Intent import_intent = new Intent(this.getApplicationContext(), ListActivity.class)
                .putExtra("list_type", "topics");
        this.startActivity(import_intent);

    }
}

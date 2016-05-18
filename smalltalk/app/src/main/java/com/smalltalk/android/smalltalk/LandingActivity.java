package com.smalltalk.android.smalltalk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

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

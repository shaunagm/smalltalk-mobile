package com.example.android.smalltalk;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.smalltalk.data.SmalltalkDBHelper;

public class DetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        DetailActivityFragment detailFragment = new DetailActivityFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.main_layout_content, detailFragment)
                .commit();

    }

    public void edit_item(View view) {

        final TextView secret_id_view = (TextView) findViewById(R.id.detail_item_id_secret);
        final String item_id = secret_id_view.getText().toString();

        final TextView type_view = (TextView) findViewById(R.id.detail_item_type);
        final String item_type = type_view.getText().toString();

        Intent intent = new Intent(this, EditActivity.class)
                .putExtra("item_id", item_id)
                .putExtra("item_type", item_type);
        startActivity(intent);

    }

}

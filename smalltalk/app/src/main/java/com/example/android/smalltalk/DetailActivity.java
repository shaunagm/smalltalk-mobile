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
        setContentView(R.layout.activity_detail);
        super.onCreateDrawer();

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        DetailActivityFragment detailFragment = new DetailActivityFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, detailFragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

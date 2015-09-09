package com.example.android.smalltalk;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.SmalltalkUtilities.misc_utils;
import com.example.android.smalltalk.data.ObjectCursorAdapter;

public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);
            String[] query_type = new String[]{"topics", "contacts", "groups"};

            for (String type: query_type) {
                final String final_type = type;
                Cursor cursor = db_utils.searchQuery(this, type, query);
                if (cursor.getCount() > 0) {
                    int header_id = 0;
                    int objects_id = 0;
                    switch (type) {
                        case "topics":
                            header_id = R.id.search_contacts_header;
                            objects_id = R.id.search_contact_objects;
                            break;
                        case "contacts":
                            header_id = R.id.search_topics_header;
                            objects_id = R.id.search_topic_objects;
                            break;
                        case "groups":
                            header_id = R.id.search_groups_header;
                            objects_id = R.id.search_group_objects;
                            break;
                    }

                    TextView header = (TextView) findViewById(header_id);
                    header.setVisibility(View.VISIBLE);

                    ListView object_list = (ListView) findViewById(objects_id);
                    object_list.setVisibility(View.VISIBLE);

                    final ObjectCursorAdapter adapter = new ObjectCursorAdapter(this, cursor, 0);
                    object_list.setAdapter(adapter);
                    object_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Cursor row_cursor = (Cursor) adapter.getItem(position);
                            misc_utils.goToDetailView(view.getContext(), row_cursor, final_type);
                        }
                    });
                }
            }
        }
    }

}

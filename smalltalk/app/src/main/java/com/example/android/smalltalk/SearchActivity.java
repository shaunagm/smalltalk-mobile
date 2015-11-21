package com.example.android.smalltalk;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.smalltalk.SmalltalkUtilities.db_utils;
import com.example.android.smalltalk.SmalltalkUtilities.misc_utils;
import com.example.android.smalltalk.data.ObjectCursorAdapter;

public class SearchActivity extends BaseActivity {

    String[] query_type = new String[]{"topics", "contacts", "groups"};
    int[] type_ids = new int[]{ R.id.search_topic_objects,  R.id.search_contact_objects,
            R.id.search_group_objects};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY);

            for(int l=0; l<=2; l++) {  // Loop through three query types

                final String type = query_type[l];
                Cursor cursor = db_utils.searchQuery(this, type, query);
                ListView object_list = (ListView) findViewById(type_ids[l]);

                if (cursor.getCount() > 0) {

                    final ObjectCursorAdapter adapter = new ObjectCursorAdapter(this, cursor);
                    object_list.setAdapter(adapter);
                    object_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            Cursor row_cursor = (Cursor) adapter.getItem(position);
                            misc_utils.goToDetailView(view.getContext(), row_cursor, type.replace("s",""));
                        }
                    });

                }

            }

            // Buttons!
            Button show_topics = (Button) findViewById(R.id.search_show_topics);
            show_topics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showType(0);
                }
            });
            Button show_contacts = (Button) findViewById(R.id.search_show_contacts);
            show_contacts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showType(1);
                }
            });
            Button show_groups = (Button) findViewById(R.id.search_show_groups);
            show_groups.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showType(2);
                }
            });



        }
    }

    public void showType(int position) {

        TextView header = (TextView) findViewById(R.id.searchview_header);
        header.setText(query_type[position]);

        for (int id:type_ids) {
            ListView object_list = (ListView) findViewById(id);
            if (id == type_ids[position]) {
                object_list.setVisibility(View.VISIBLE);
            } else {
                object_list.setVisibility(View.GONE);
            }
        }

    }

}

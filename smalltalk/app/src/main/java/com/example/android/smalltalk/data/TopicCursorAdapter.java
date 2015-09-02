package com.example.android.smalltalk.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.smalltalk.R;
import com.example.android.smalltalk.data.SmalltalkContract.TopicEntry;

import org.w3c.dom.Text;

public class TopicCursorAdapter extends CursorAdapter {
    public TopicCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_topic, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView topicName = (TextView) view.findViewById(R.id.list_item_topic_textview);
        TextView topicURI = (TextView) view.findViewById(R.id.list_item_topic_URI_textview);
        TextView topicDetails = (TextView) view.findViewById(R.id.list_item_topic_details_textview);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(TopicEntry.COLUMN_TOPIC_NAME));
        String URI = cursor.getString(cursor.getColumnIndexOrThrow(TopicEntry.COLUMN_TOPIC_URI));
        String details = cursor.getString(cursor.getColumnIndexOrThrow(TopicEntry.COLUMN_TOPIC_DETAILS));

        topicName.setText(name);
        topicURI.setText(URI);
        topicDetails.setText(details);
    }

}
package com.example.android.smalltalk.data;

import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by shauna on 8/19/15.
 */
public class SmalltalkContract {

        // To make it easy to query for the exact date, we normalize all dates that go into
        // the database to the start of the the Julian day at UTC.
        public static long normalizeDate(long startDate) {
            // normalize the start date to the beginning of the (UTC) day
            Time time = new Time();
            time.set(startDate);
            int julianDay = Time.getJulianDay(startDate, time.gmtoff);
            return time.setJulianDay(julianDay);
        }

        /* Inner class that defines the table contents of the contacts table */
        public static final class ContactEntry implements BaseColumns {

            public static final String TABLE_NAME = "contacts";

            // Name of contact
            public static final String COLUMN_CONTACT_NAME = "contact_name";
            // Details about contact
            public static final String COLUMN_CONTACT_DETAILS = "contact_details";

        }

        /* Inner class that defines the table contents of the topics table */
        public static final class TopicEntry implements BaseColumns {

            public static final String TABLE_NAME = "topics";


            // Name of contact
            public static final String COLUMN_TOPIC_NAME = "topic_name";
            // Details about contact
            public static final String COLUMN_TOPIC_DETAILS = "topic_details";

        }

        /* Inner class that defines the table contents of the topics table */
        public static final class GroupEntry implements BaseColumns {

            public static final String TABLE_NAME = "groups";


            // Name of contact
            public static final String COLUMN_GROUP_NAME = "group_name";
            // Details about contact
            public static final String COLUMN_GROUP_DETAILS = "group_details";

        }

}

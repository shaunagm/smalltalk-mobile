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

        public static final class ContactEntry implements BaseColumns {

            public static final String TABLE_NAME = "contacts";

            // Name of contact
            public static final String COLUMN_CONTACT_NAME = "name";
            // Details about contact
            public static final String COLUMN_CONTACT_DETAILS = "details";

        }

        public static final class TopicEntry implements BaseColumns {

            public static final String TABLE_NAME = "topics";

            // Name of contact
            public static final String COLUMN_TOPIC_NAME = "name";
            // Details about contact
            public static final String COLUMN_TOPIC_DETAILS = "details";

        }

        public static final class GroupEntry implements BaseColumns {

            public static final String TABLE_NAME = "groups";

            // Name of contact
            public static final String COLUMN_GROUP_NAME = "name";
            // Details about contact
            public static final String COLUMN_GROUP_DETAILS = "details";

        }

        public static final class ContactGroupJunction implements BaseColumns {

            public static final String TABLE_NAME = "contact_group";
            public static final String COLUMN_CONTACT_KEY = "contact_id";
            public static final String COLUMN_GROUP_KEY = "group_id";

        }

        public static final class TopicContactJunction implements BaseColumns {

            public static final String TABLE_NAME = "contact_topic";
            public static final String COLUMN_TOPIC_KEY = "topic_id";
            public static final String COLUMN_CONTACT_KEY = "contact_id";

        }

        public static final class TopicGroupJunction implements BaseColumns {

            public static final String TABLE_NAME = "group_topic";
            public static final String COLUMN_TOPIC_KEY = "topic_id";
            public static final String COLUMN_GROUP_KEY = "group_id";

        }

}

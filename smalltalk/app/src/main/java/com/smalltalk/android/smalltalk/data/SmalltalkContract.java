package com.smalltalk.android.smalltalk.data;

import android.provider.BaseColumns;

public class SmalltalkContract {

        public static final class ContactEntry implements BaseColumns {

            public static final String TABLE_NAME = "contacts";
            public static final String COLUMN_CONTACT_NAME = "name";
            public static final String COLUMN_CONTACT_DETAILS = "details";

        }

        public static final class TopicEntry implements BaseColumns {

            public static final String TABLE_NAME = "topics";
            public static final String COLUMN_TOPIC_NAME = "name";
            public static final String COLUMN_TOPIC_DETAILS = "details";
            public static final String COLUMN_TOPIC_URI = "URI";
            public static final String COLUMN_STAR = "star";
            public static final String COLUMN_ARCHIVE = "archive";

        }

        public static final class GroupEntry implements BaseColumns {

            public static final String TABLE_NAME = "groups";
            public static final String COLUMN_GROUP_NAME = "name";
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
            public static final String COLUMN_STAR = "star";
            public static final String COLUMN_ARCHIVE = "archive";
            public static final String COLUMN_STAR_LOCK = "star_lock";
            public static final String COLUMN_ARCHIVE_LOCK = "archive_lock";

        }

        public static final class TopicGroupJunction implements BaseColumns {

            public static final String TABLE_NAME = "group_topic";
            public static final String COLUMN_TOPIC_KEY = "topic_id";
            public static final String COLUMN_GROUP_KEY = "group_id";
            public static final String COLUMN_STAR = "star";
            public static final String COLUMN_ARCHIVE = "archive";
            public static final String COLUMN_STAR_LOCK = "star_lock";
            public static final String COLUMN_ARCHIVE_LOCK = "archive_lock";

        }

}

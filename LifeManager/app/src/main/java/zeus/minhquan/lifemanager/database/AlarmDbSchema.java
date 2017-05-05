package zeus.minhquan.lifemanager.database;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmDbSchema {
    public static final class AlarmTable {
        public static final String NAME = "alarms";

        public static final class Columns {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String HOUR = "hour";
            public static final String MINUTE = "minute";
            public static final String DAYS = "days";
            public static final String TONE = "tone";
            public static final String ENABLED = "enabled";
            public static final String VIBRATE = "vibrate";
            public static final String CATCH_A_SNOOZE = "catch_a_snooze";
            public static final String FREAKING_MATH = "freaking_math";
            public static final String NEW = "new";
            public static final String SNOOZED = "snoozed";
            public static final String SNOOZED_HOUR = "snoozed_hour";
            public static final String SNOOZED_MINUTE = "snoozed_minute";
            public static final String SNOOZED_SECONDS = "snoozed_seconds";
        }
    }
}

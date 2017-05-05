package zeus.minhquan.lifemanager.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import java.util.UUID;
import zeus.minhquan.lifemanager.database.AlarmDbSchema.AlarmTable;
import zeus.minhquan.lifemanager.models.Alarm;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmCursorWrapper extends CursorWrapper {
    public AlarmCursorWrapper(Cursor cursor) { super(cursor); }

    public Alarm getAlarm() {
        String uuidString = getString(getColumnIndex(AlarmTable.Columns.UUID));
        String title = getString(getColumnIndex(AlarmTable.Columns.TITLE));
        boolean isEnabled = (getInt(getColumnIndex(AlarmTable.Columns.ENABLED)) != 0);
        int timeHour = getInt(getColumnIndex(AlarmTable.Columns.HOUR));
        int timeMinute = getInt(getColumnIndex(AlarmTable.Columns.MINUTE));
        String alarmToneString = getString(getColumnIndex(AlarmTable.Columns.TONE));
        Uri alarmTone = null;
        if (!alarmToneString.isEmpty()) {
            alarmTone = Uri.parse(alarmToneString);
        }
        String[] repeatingDays = getString(getColumnIndex(AlarmTable.Columns.DAYS)).split(",");
        boolean vibrate = (getInt(getColumnIndex(AlarmTable.Columns.VIBRATE)) != 0);
        boolean tongueTwister = (getInt(getColumnIndex(AlarmTable.Columns.CATCH_A_SNOOZE)) != 0);
        boolean colorCapture = (getInt(getColumnIndex(AlarmTable.Columns.FREAKING_MATH)) != 0);
        boolean isNew = (getInt(getColumnIndex(AlarmTable.Columns.NEW)) != 0);
        boolean isSnoozed = (getInt(getColumnIndex(AlarmTable.Columns.SNOOZED)) != 0);
        int snoozedHour = getInt(getColumnIndex(AlarmTable.Columns.SNOOZED_HOUR));
        int snoozedMinute = getInt(getColumnIndex(AlarmTable.Columns.SNOOZED_MINUTE));
        int snoozedSeconds = getInt(getColumnIndex(AlarmTable.Columns.SNOOZED_SECONDS));

        Alarm alarm = new Alarm(UUID.fromString(uuidString));
        alarm.setTitle(title);
        alarm.setIsEnabled(isEnabled);
        alarm.setTimeHour(timeHour);
        alarm.setTimeMinute(timeMinute);
        alarm.setAlarmTone(alarmTone);
        for (int i = 0; i < repeatingDays.length; i++) {
            alarm.setRepeatingDay(i, !repeatingDays[i].equals("false"));
        }
        alarm.setVibrate(vibrate);
        alarm.setCatchASnoozeEnabled(tongueTwister);
        alarm.setFreakingMathEnabled(colorCapture);
        alarm.setNew(isNew);
        alarm.setSnoozed(isSnoozed);
        alarm.setSnoozeHour(snoozedHour);
        alarm.setSnoozeMinute(snoozedMinute);
        alarm.setSnoozeSeconds(snoozedSeconds);

        return alarm;
    }
}

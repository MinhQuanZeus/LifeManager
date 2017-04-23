package zeus.minhquan.lifemanager.scheduling;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.AlarmMainActivity;
import zeus.minhquan.lifemanager.models.Alarm;
import zeus.minhquan.lifemanager.models.AlarmList;
import zeus.minhquan.lifemanager.ringing.AlarmRingingActivity;
import zeus.minhquan.lifemanager.ringing.AlarmRingingService;
import zeus.minhquan.lifemanager.utils.DateTimeUtils;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmNotificationManager {
    public final static int NOTIFICATION_ID = 60653426;
    public static final String NOTIFICATION_NEXT_ALARM = "next_alarm";
    public static final String NOTIFICATION_ALARM_RUNNING = "alarm_running";

    private static final String TAG = "AlarmNotificationMgr";
    private static AlarmNotificationManager sManager;

    private Context mContext;
    private UUID mCurrentAlarmId;
    private long mCurrentAlarmTime;
    private boolean mNotificationsActive;
    private boolean mWakeLockEnable;

    private AlarmNotificationManager(Context context) {
        mContext = context;
        resetState();
    }

    public static AlarmNotificationManager get(Context context) {
        if (sManager == null) {
            sManager = new AlarmNotificationManager(context);
            Log.d(TAG, "Initialized!");
        }
        return sManager;
    }

    public static Notification createNextAlarmNotification(Context context, UUID alarmId, long alarmTime) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.alarm_clock_notification)
                .setLargeIcon(icon)
                .setContentTitle(context.getString(R.string.notification_next_alarm_content_title))
                .setSubText(AlarmList.get(context).getAlarm(alarmId).getTitle())
                .setContentText(DateTimeUtils.getDayAndTimeAlarmDisplayString(context, alarmTime))
                .setPriority(Notification.PRIORITY_LOW)
                .setVisibility(Notification.VISIBILITY_PRIVATE);

        Intent startIntent = new Intent(context, AlarmMainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startIntent.putExtra(AlarmRingingService.ALARM_ID, alarmId);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int) Math.abs(alarmId.getLeastSignificantBits()), startIntent, 0);
        builder.setContentIntent(contentIntent);
        return builder.build();
    }

    public static Notification createAlarmRunningNotification(Context context, UUID alarmId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.alarm_clock_notification);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        builder.setLargeIcon(icon);

        builder.setContentTitle(context.getString(R.string.notification_alarm_ringing_content_title));
        String title = AlarmList.get(context).getAlarm(alarmId).getTitle();
        builder.setContentText(title);

        Intent ringingIntent = new Intent(context, AlarmRingingActivity.class);
        ringingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ringingIntent.putExtra(AlarmRingingService.ALARM_ID, alarmId);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                (int) Math.abs(alarmId.getLeastSignificantBits()), ringingIntent, 0);
        builder.setContentIntent(contentIntent);
        return builder.build();
    }

    public void handleNextAlarmNotificationStatus() {
        // Check if notifications are enabled
        if (!shouldEnableNotifications()) return;

        // Find the alarm that will fire next
        List<Alarm> alarms = AlarmList.get(mContext).getAlarms();
        Calendar now = Calendar.getInstance();
        SortedMap<Long, UUID> alarmValues = new TreeMap<>();
        for (Alarm alarm : alarms) {
            if (alarm.isEnabled()) {
                alarmValues.put(AlarmScheduler.getAlarmTimeIncludeSnoozed(now, alarm), alarm.getId());
            }
        }

        //  Decide whether we need to enable, update or remove the notification, or do nothing
        if (!alarmValues.isEmpty()) {
            Long alarmTime = alarmValues.firstKey();
            UUID alarmId = alarmValues.get(alarmTime);
            boolean wakelockEnable = shouldEnableWakeLock();
            if (!doesCurrentStateMatchAlarmDetails(alarmId, alarmTime, wakelockEnable)) {
                updateStateWithAlarmDetails(alarmId, alarmTime, wakelockEnable);
                AlarmRingingService.startForegroundService(mContext,
                        mCurrentAlarmId,
                        mCurrentAlarmTime,
                        NOTIFICATION_NEXT_ALARM);
                AlarmRingingService.toggleWakeLock(mContext, wakelockEnable);
            }
        } else {
            disableNotifications();
        }
    }

    public void handleAlarmRunningNotificationStatus(UUID alarmId) {
        // Check if notifications are enabled
        if (!shouldEnableNotifications()) return;

        updateStateWithAlarmDetails(alarmId, 0, false);
        AlarmRingingService.startForegroundService(mContext,
                alarmId,
                0,
                NOTIFICATION_ALARM_RUNNING);

    }

    public void disableNotifications() {
        // We only attempt to disable the notification if it is already active
        if (mNotificationsActive) {
            AlarmRingingService.stopForegroundService(mContext);
            resetState();
        }
    }

    public void toggleWakeLock(boolean wakelockEnable) {
        if (mNotificationsActive) {
            mWakeLockEnable = wakelockEnable;
            AlarmRingingService.toggleWakeLock(mContext, mWakeLockEnable);
        }
    }

    private void updateStateWithAlarmDetails(UUID alarmId, long alarmTime, boolean wakelockEnable) {
        mNotificationsActive = true;
        mCurrentAlarmId = alarmId;
        mCurrentAlarmTime = alarmTime;
        mWakeLockEnable = wakelockEnable;
    }

    private boolean doesCurrentStateMatchAlarmDetails(UUID alarmId, long alarmTime, boolean wakelockEnable) {
        return (mCurrentAlarmTime == alarmTime &&
                mCurrentAlarmId.equals(alarmId) &&
                mWakeLockEnable == wakelockEnable);
    }

    private void resetState() {
        mNotificationsActive = false;
        mCurrentAlarmId = new UUID(0, 0);
        mCurrentAlarmTime = 0;
        mWakeLockEnable = false;
    }

    private boolean shouldEnableNotifications() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(mContext.getString(R.string.pref_enable_notifications_key), false);
    }

    private boolean shouldEnableWakeLock() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return preferences.getBoolean(mContext.getString(R.string.pref_enable_reliability_key), false);
    }
}


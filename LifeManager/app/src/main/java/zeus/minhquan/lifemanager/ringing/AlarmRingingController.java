package zeus.minhquan.lifemanager.ringing;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.UUID;

import zeus.minhquan.lifemanager.models.Alarm;
import zeus.minhquan.lifemanager.models.AlarmList;
import zeus.minhquan.lifemanager.scheduling.AlarmNotificationManager;
import zeus.minhquan.lifemanager.scheduling.AlarmScheduler;
import zeus.minhquan.lifemanager.utils.SharedWakeLock;

/**
 * Created by QuanT on 4/22/2017.
 */

public final class AlarmRingingController extends AlarmRingingSessionDispatcher {
    private Context mContext;
    private AlarmRingtonePlayer mRingtonePlayer;
    private AlarmVibrator mVibrator;
    private Alarm mCurrentAlarm;
    private boolean mAllowDismissRequested;

    public AlarmRingingController(Context context) {
        mContext = context;
        mRingtonePlayer = new AlarmRingtonePlayer(mContext);
        mVibrator = new AlarmVibrator(mContext);
    }

    public static AlarmRingingController newInstance(Context context) {
        return new AlarmRingingController(context);
    }

    @Override
    public void beforeDispatchFirstAlarmRingingSession() {
        mRingtonePlayer.initialize();
        mVibrator.initialize();
        SharedWakeLock.get(mContext).acquireFullWakeLock();
    }

    @Override
    protected void alarmRingingSessionCompleted() {
        silenceAlarmRinging();
        mCurrentAlarm = null;
        super.alarmRingingSessionCompleted();
    }

    @Override
    public void allAlarmRingingSessionsComplete() {
        mVibrator.cleanup();
        mRingtonePlayer.cleanup();

        SharedWakeLock.get(mContext).releaseFullWakeLock();
        AlarmNotificationManager.get(mContext).handleNextAlarmNotificationStatus();
    }

    @Override
    public void dispatchAlarmRingingSession(Intent intent) {
        if (intent != null) {
            UUID alarmId = (UUID) intent.getExtras().getSerializable(AlarmScheduler.ARGS_ALARM_ID);
            mCurrentAlarm = AlarmList.get(mContext).getAlarm(alarmId);
            startAlarmRinging();
            launchRingingUserExperience(alarmId);
            AlarmNotificationManager.get(mContext).handleAlarmRunningNotificationStatus(alarmId);
        }
    }

    public void silenceAlarmRinging() {
        mVibrator.stop();
        mRingtonePlayer.stop();
    }

    public void startAlarmRinging() {
        if (mCurrentAlarm.shouldVibrate()) {
            mVibrator.vibrate();
        }
        Uri ringtone = mCurrentAlarm.getAlarmTone();
        if (ringtone != null) {
            mRingtonePlayer.play(ringtone);
        }
    }

    public void requestAllowDismiss() {
        mAllowDismissRequested = true;
    }

    public void alarmRingingSessionDismissed() {
        if (mAllowDismissRequested) {
            mAllowDismissRequested = false;
        } else {
            if (mCurrentAlarm != null) {
                launchRingingUserExperience(mCurrentAlarm.getId());
            }
        }
    }

    private void launchRingingUserExperience(UUID alarmId) {
        Intent ringingIntent = new Intent(mContext, AlarmRingingActivity.class);
        ringingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ringingIntent.putExtra(AlarmRingingService.ALARM_ID, alarmId);
        mContext.startActivity(ringingIntent);
    }
}

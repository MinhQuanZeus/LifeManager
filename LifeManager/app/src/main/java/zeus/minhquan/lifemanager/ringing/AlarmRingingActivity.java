package zeus.minhquan.lifemanager.ringing;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.UUID;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.games.GameFactory;
import zeus.minhquan.lifemanager.games.GameCatchSnoozeFragment;
import zeus.minhquan.lifemanager.models.Alarm;
import zeus.minhquan.lifemanager.models.AlarmList;
import zeus.minhquan.lifemanager.scheduling.AlarmScheduler;
import zeus.minhquan.lifemanager.settings.AlarmSettingsFragment;
import zeus.minhquan.lifemanager.settings.GamesPreference;
import zeus.minhquan.lifemanager.settings.GamesSettingsFragment;
import zeus.minhquan.lifemanager.utils.GeneralUtils;
import zeus.minhquan.lifemanager.utils.SettingsUtils;
import zeus.minhquan.lifemanager.utils.SharedWakeLock;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmRingingActivity extends AppCompatActivity
        implements GameFactory.GameResultListener,
//        ShareFragment.ShareResultListener,
        AlarmRingingFragment.RingingResultListener,
        AlarmSnoozeFragment.SnoozeResultListener,
        AlarmNoGamesFragment.NoGameResultListener,
        AlarmSettingsFragment.AlarmSettingsListener,
        GamesSettingsFragment.GamesSettingsListener {


    private static final int ALARM_DURATION_INTEGER = (2 * 60 * 60) * 1000;
    public final String TAG = this.getClass().getSimpleName();
    private Alarm mAlarm;
    private Fragment mAlarmRingingFragment;
    private Handler mHandler;
    private Runnable mAlarmCancelTask;
    private boolean mAlarmTimedOut;
    private AlarmRingingService mRingingService;
    private boolean mIsServiceBound;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to an explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mRingingService = ((AlarmRingingService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mRingingService = null;
        }
    };

    private BroadcastReceiver mScreenReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // If the alarm is ringing, stop playing the sound and vibrating. The vibrator may
                // already have already been cancelled by the screen turning off. We do this
                // so that we can have a clean restart of vibration and sound playing after turning
                // on the screen again.
                if (isAlarmRinging()) {
                    notifyControllerSilenceAlarmRinging();
                }

                // We release and reacquire the wakelock so that we can turn the screen back on
                SharedWakeLock.get(getApplicationContext()).releaseFullWakeLock();
                SharedWakeLock.get(getApplicationContext()).acquireFullWakeLock();

                // Restart the alarm and vibrator playing if they were both turned off
                if (isAlarmRinging()) {
                    notifyControllerStartAlarmRinging();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UUID alarmId = (UUID) getIntent().getSerializableExtra(AlarmScheduler.ARGS_ALARM_ID);
        mAlarm = AlarmList.get(this).getAlarm(alarmId);

        Log.d(TAG, "Creating activity!");

        // This call must be made before setContentView to avoid the view being refreshed
        GeneralUtils.setLockScreenFlags(getWindow());

        setContentView(R.layout.activity_fragment);

        mAlarmRingingFragment = AlarmRingingFragment.newInstance(mAlarm.getId().toString());

        // We do not want any animations when the ringing fragment is launched for the first time
        GeneralUtils.showFragment(getSupportFragmentManager(),
                mAlarmRingingFragment,
                AlarmRingingFragment.RINGING_FRAGMENT_TAG);

        mAlarmCancelTask = new Runnable() {
            @Override
            public void run() {
                mAlarmTimedOut = true;
                if (!isGameRunning()) {
                    finishActivity();
                }
            }
        };
        mHandler = new Handler();
        int ringingDuration = getAlarmRingingDuration();
        if (ringingDuration > 0) {
            mHandler.postDelayed(mAlarmCancelTask, ringingDuration);
        }

        registerReceiver(mScreenReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        bindRingingService();
    }

    @Override
    public void onGameSuccess(String shareable) {
        mAlarm.onDismiss();
        cancelAlarmTimeout();
//        if (shareable != null && shareable.length() > 0) {
//            GeneralUtilities.showFragmentFromRight(getSupportFragmentManager(),
//                    ShareFragment.newInstance(shareable),
//                    ShareFragment.SHARE_FRAGMENT_TAG);
//        } else {
        finishActivity();
//        }
    }

    @Override
    public void onGameFailure() {
        if (mAlarmTimedOut) {
            finishActivity();
        } else {
            transitionBackToRingingScreen();
        }
    }

    @Override
    public void onGameError() {
        Toast.makeText(this, getString(R.string.game_error_toast), Toast.LENGTH_SHORT).show();
        GeneralUtils.showFragmentFromRight(getSupportFragmentManager(),
                GameFactory.getNoNetworkGame(this),
                GameCatchSnoozeFragment.NO_NETWORK_FRAGMENT_TAG);
    }

//    @Override
//    public void onShareCompleted() {
//        finishActivity();
//    }
//
//    @Override
//    public void onRequestLaunchShareAction() {
//        notifyControllerAllowDismiss();
//    }

    @Override
    public void onRingingDismiss() {
        notifyControllerSilenceAlarmRinging();
        Fragment gameFragment = GameFactory.getGameFragment(this, mAlarm.getId());
        if (gameFragment != null) {
            GeneralUtils.showFragmentFromRight(getSupportFragmentManager(),
                    gameFragment, GameFactory.GAME_FRAGMENT_TAG);
        } else {
            mAlarm.onDismiss();
            cancelAlarmTimeout();
            GeneralUtils.showFragmentFromRight(getSupportFragmentManager(),
                    AlarmNoGamesFragment.newInstance(mAlarm.getId().toString()),
                    AlarmNoGamesFragment.NO_GAMES_FRAGMENT_TAG);
        }
    }

    @Override
    public void onRingingSnooze() {
        notifyControllerSilenceAlarmRinging();
        cancelAlarmTimeout();
        mAlarm.snooze();
        // Show the snooze user interface
        GeneralUtils.showFragmentFromLeft(getSupportFragmentManager(),
                new AlarmSnoozeFragment(),
                AlarmSnoozeFragment.SNOOZE_FRAGMENT_TAG);
    }

    @Override
    public void onSnoozeDismiss() {
        finishActivity();
    }

    @Override
    public void onNoGameDismiss(boolean launchSettings) {
        if (launchSettings) {
            GeneralUtils.showFragmentFromRight(getSupportFragmentManager(),
                    GamesSettingsFragment.newInstance(
                            GamesPreference.getEnabledGames(this, mAlarm)),
                    GamesSettingsFragment.GAMES_SETTINGS_FRAGMENT_TAG);
        } else {
            finishActivity();
        }
    }

    @Override
    public void onSettingsSaveOrIgnoreChanges() {
        finishActivity();
    }

    @Override
    public void onSettingsDeleteOrNewCancel() {
        finishActivity();
    }

    @Override
    public void onGamesSettingsDismiss(ArrayList<String> enabledGames) {
        // If Games settings was launched from Alarm settings just update Alarm settings,
        // otherwise we need to launch Alarm settings
        AlarmSettingsFragment settingsFragment = SettingsUtils
                .getAlarmSettingsFragment(getSupportFragmentManager());
        if (settingsFragment != null){
            settingsFragment.updateGamesPreference(enabledGames);
        } else {
            GeneralUtils.showFragmentFromLeft(getSupportFragmentManager(),
                    AlarmSettingsFragment.newInstance(mAlarm.getId().toString(), enabledGames),
                    AlarmSettingsFragment.SETTINGS_FRAGMENT_TAG);
        }
    }

    @Override
    public void onShowGamesSettings(ArrayList<String> enabledGames) {
        SettingsUtils.transitionFromAlarmToGamesSettings(getSupportFragmentManager(),
                enabledGames);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Entered onResume!");

     //   GeneralUtils.registerCrashReport(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Entered onPause!");
        notifyControllerRingingDismissed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Entered onDestroy!");
        unregisterReceiver(mScreenReceiver);
        unbindRingingService();
    }

    @Override
    public void onBackPressed() {
        if (isGameRunning()) {
            transitionBackToRingingScreen();
        } else if (SettingsUtils.areEditingSettings(getSupportFragmentManager())) {
            if (SettingsUtils.areEditingAlarmSettingsExclusive(getSupportFragmentManager())) {
                // We always need to handle the case where there are non-persisted settings
                SettingsUtils.getAlarmSettingsFragment(getSupportFragmentManager()).onCancel();
            } else if (SettingsUtils.areEditingGamesSettingsExclusive(getSupportFragmentManager())) {
                // This is the scenario where we were launched from the NoGames fragment
                SettingsUtils.getGamesSettingsFragment(getSupportFragmentManager()).onBack();
            } else {
                // This implies we are in the Games settings and we were launched from Alarm
                // settings.  In this case we just pop the stack.
                super.onBackPressed();
            }
        } else if (!isAlarmRinging()) {
            finishActivity();
        }
    }

    private void finishActivity() {
        // We only want to report that ringing completed as a result of correct user action
        notifyControllerRingingCompleted();
        finish();
    }

    private void transitionBackToRingingScreen() {
        GeneralUtils.showFragmentFromLeft(getSupportFragmentManager(),
                mAlarmRingingFragment,
                AlarmRingingFragment.RINGING_FRAGMENT_TAG);
        notifyControllerStartAlarmRinging();
    }

    private boolean isAlarmRinging() {
        return (getSupportFragmentManager()
                .findFragmentByTag(AlarmRingingFragment.RINGING_FRAGMENT_TAG) != null);
    }

    private boolean isGameRunning() {
        return (getSupportFragmentManager()
                .findFragmentByTag(GameFactory.GAME_FRAGMENT_TAG) != null);
    }

    private int getAlarmRingingDuration() {
        return GeneralUtils.getDurationSetting(R.string.pref_ring_duration_key,
                R.string.pref_default_ring_duration_value,
                ALARM_DURATION_INTEGER);
    }

    private void bindRingingService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(AlarmRingingActivity.this,
                AlarmRingingService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
        mIsServiceBound = true;
    }

    private void unbindRingingService() {
        if (mIsServiceBound) {
            // Detach our existing connection.
            unbindService(mServiceConnection);
            mIsServiceBound = false;
        }
    }

    private void notifyControllerRingingCompleted() {
        if (mRingingService != null) {
            mRingingService.reportAlarmUXCompleted();
        }
    }

    private void notifyControllerSilenceAlarmRinging() {
        if (mRingingService != null) {
            mRingingService.silenceAlarmRinging();
        }
    }

    private void notifyControllerStartAlarmRinging() {
        if (mRingingService != null) {
            mRingingService.startAlarmRinging();
        }
    }

    private void notifyControllerRingingDismissed() {
        if (mRingingService != null) {
            mRingingService.reportAlarmUXDismissed();
        }
    }

    private void notifyControllerAllowDismiss() {
        if (mRingingService != null) {
            mRingingService.requestAllowUXDismiss();
        }
    }

    private void cancelAlarmTimeout () {
        mHandler.removeCallbacks(mAlarmCancelTask);
    }
}


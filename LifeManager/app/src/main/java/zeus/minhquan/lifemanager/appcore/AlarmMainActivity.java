package zeus.minhquan.lifemanager.appcore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;

import net.hockeyapp.android.FeedbackManager;

import java.util.ArrayList;
import java.util.UUID;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.models.Alarm;
import zeus.minhquan.lifemanager.scheduling.AlarmNotificationManager;
import zeus.minhquan.lifemanager.scheduling.AlarmScheduler;
import zeus.minhquan.lifemanager.settings.AlarmSettingsFragment;
import zeus.minhquan.lifemanager.settings.GamesSettingsFragment;
import zeus.minhquan.lifemanager.utils.GeneralUtils;
import zeus.minhquan.lifemanager.utils.Logger;
import zeus.minhquan.lifemanager.utils.SettingsUtils;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmMainActivity extends AppCompatActivity
        implements AlarmListFragment.AlarmListListener,
        AlarmSettingsFragment.AlarmSettingsListener,
        GamesSettingsFragment.GamesSettingsListener {

    private SharedPreferences mPreferences = null;
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        String packageName = getApplicationContext().getPackageName();
        mPreferences = getSharedPreferences(packageName, MODE_PRIVATE);
        PreferenceManager.setDefaultValues(this, R.xml.pref_global, false);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AlarmNotificationManager.get(this).handleNextAlarmNotificationStatus();

        UUID alarmId = (UUID) getIntent().getSerializableExtra(AlarmScheduler.ARGS_ALARM_ID);
        if (alarmId != null) {
            showAlarmSettingsFragment(alarmId.toString());
        }

        Logger.init(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        UUID alarmId = (UUID) intent.getSerializableExtra(AlarmScheduler.ARGS_ALARM_ID);
        if (alarmId != null) {
            showAlarmSettingsFragment(alarmId.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SettingsUtils.areEditingSettings(getSupportFragmentManager())) {
            GeneralUtils.showFragment(getSupportFragmentManager(),
                    new AlarmListFragment(),
                    AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedbackManager.unregister();
        Logger.flush();
    }


    @Override
    public void onBackPressed() {
        if (SettingsUtils.areEditingAlarmSettingsExclusive(getSupportFragmentManager())) {
            SettingsUtils.getAlarmSettingsFragment(getSupportFragmentManager()).onCancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                    AudioManager.ADJUST_LOWER,
                    AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM,
                    AudioManager.ADJUST_RAISE,
                    AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    public void onSettingsSaveOrIgnoreChanges() {
        GeneralUtils.showFragmentFromLeft(getSupportFragmentManager(),
                new AlarmListFragment(),
                AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
        onAlarmChanged();
    }

    @Override
    public void onSettingsDeleteOrNewCancel() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, R.anim.slide_down);
        transaction.replace(R.id.fragment_container, new AlarmListFragment());
        transaction.commit();
        onAlarmChanged();
    }

    @Override
    public void onGamesSettingsDismiss(ArrayList<String> enabledGames) {
        AlarmSettingsFragment settingsFragment = SettingsUtils.
                getAlarmSettingsFragment(getSupportFragmentManager());
        if (settingsFragment != null){
            settingsFragment.updateGamesPreference(enabledGames);
        }
    }

    @Override
    public void onShowGamesSettings(ArrayList<String> enabledGames) {
        SettingsUtils.transitionFromAlarmToGamesSettings(getSupportFragmentManager(), enabledGames);
    }

    @Override
    public void onAlarmSelected(Alarm alarm) {
        showAlarmSettingsFragment(alarm.getId().toString());
    }

    @Override
    public void onAlarmChanged() {
        AlarmNotificationManager.get(this).handleNextAlarmNotificationStatus();
    }

    private void showAlarmSettingsFragment(String alarmId) {
        SettingsUtils.transitionFromAlarmListToSettings(getSupportFragmentManager(), alarmId);
    }
}

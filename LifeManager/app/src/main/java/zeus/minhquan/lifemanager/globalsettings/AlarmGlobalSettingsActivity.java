package zeus.minhquan.lifemanager.globalsettings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.DividerItemDecoration;
import zeus.minhquan.lifemanager.scheduling.AlarmNotificationManager;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmGlobalSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new AlarmGlobalSettingsFragment())
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) ||
                (keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            ((AlarmGlobalSettingsFragment) fragments.get(0)).onKeyDown(keyCode);
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static class AlarmGlobalSettingsFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ListPreference mSnoozeDuration;
        private ListPreference mRingDuration;
        private VolumeSliderPreference mAlarmVolume;
        private SwitchPreferenceCompat mEnableNotifications;
        private SwitchPreferenceCompat mEnableReliability;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.pref_global);
            mSnoozeDuration = (ListPreference) findPreference(getString(R.string.pref_snooze_duration_key));
            mRingDuration = (ListPreference) findPreference(getString(R.string.pref_ring_duration_key));
            mAlarmVolume = (VolumeSliderPreference) findPreference(getString(R.string.pref_ring_volume_key));
            mEnableNotifications = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_enable_notifications_key));
            mEnableReliability = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_enable_reliability_key));
            mEnableReliability.setEnabled(mEnableNotifications.isChecked());
            setDefaultSummaryValues();
        }

        @Override
        public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            LinearLayout rootLayout = (LinearLayout) parent.getParent();
            AppBarLayout appBarLayout = (AppBarLayout) LayoutInflater.from(getContext()).inflate(R.layout.settings_toolbar, rootLayout, false);
            rootLayout.addView(appBarLayout, 0); // insert at top
            Toolbar bar = (Toolbar) appBarLayout.findViewById(R.id.settings_toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(bar);
            RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL_LIST));
            return recyclerView;
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(getString(R.string.pref_snooze_duration_key))) {
                mSnoozeDuration.setSummary(mSnoozeDuration.getEntry());
                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(getString(R.string.pref_snooze_duration_display_key), mSnoozeDuration.getEntry().toString())
                        .apply();
            } else if (key.equals(getString(R.string.pref_ring_duration_key))) {
                mRingDuration.setSummary(mRingDuration.getEntry());
            } else if (key.equals(getString(R.string.pref_enable_notifications_key))) {
                boolean notificationsEnabled = mEnableNotifications.isChecked();
                // As the reliability setting is dependant on notifications, we enable or
                // disable appropriately
                mEnableReliability.setEnabled(notificationsEnabled);
                if (notificationsEnabled) {
                    AlarmNotificationManager.get(getContext()).handleNextAlarmNotificationStatus();
                } else {
                    AlarmNotificationManager.get(getContext()).disableNotifications();
                }
            } else if (key.equals(getString(R.string.pref_enable_reliability_key))) {
                AlarmNotificationManager.get(getContext())
                        .toggleWakeLock(mEnableReliability.isChecked());
            }
        }

        public void onKeyDown(int keyCode) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                mAlarmVolume.decreaseVolume();
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mAlarmVolume.increaseVolume();
            }
        }

        private void setDefaultSummaryValues() {
            mSnoozeDuration.setSummary(mSnoozeDuration.getEntry());
            mRingDuration.setSummary(mRingDuration.getEntry());
        }
    }
}

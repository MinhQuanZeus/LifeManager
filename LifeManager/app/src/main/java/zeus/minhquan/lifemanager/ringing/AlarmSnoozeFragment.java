package zeus.minhquan.lifemanager.ringing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import zeus.minhquan.lifemanager.R;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmSnoozeFragment extends Fragment {
    public static final String SNOOZE_FRAGMENT_TAG = "snooze_fragment";
    private static final int SNOOZE_SCREEN_TIMEOUT_DURATION = 3 * 1000;
    SnoozeResultListener mCallback;
    private Handler mHandler;
    private Runnable mAutoDismissTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snooze, container, false);
        TextView snoozeDuration = (TextView) view.findViewById(R.id.alarm_snoozed_duration);
        snoozeDuration.setText(getAlarmSnoozeDuration());
        mAutoDismissTask = new Runnable() {
            @Override
            public void run() {
                mCallback.onSnoozeDismiss();
            }
        };
        mHandler = new Handler();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (SnoozeResultListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mAutoDismissTask);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.postDelayed(mAutoDismissTask, SNOOZE_SCREEN_TIMEOUT_DURATION);

    }

    private String getAlarmSnoozeDuration() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getString(getString(R.string.pref_snooze_duration_display_key),
                getString(R.string.pref_default_snooze_duration_label));
    }

    public interface SnoozeResultListener {
        void onSnoozeDismiss();
    }
}


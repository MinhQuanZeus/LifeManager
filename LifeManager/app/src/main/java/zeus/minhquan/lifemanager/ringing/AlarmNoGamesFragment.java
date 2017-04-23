package zeus.minhquan.lifemanager.ringing;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.models.Alarm;
import zeus.minhquan.lifemanager.models.AlarmList;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmNoGamesFragment extends Fragment {
    public static final String NO_GAMES_FRAGMENT_TAG = "no_games_fragment";
    private static final String ARGS_ALARM_ID = "alarm_id";
    private static final int NO_GAME_SCREEN_TIMEOUT_DURATION = 5 * 1000;
    NoGameResultListener mCallback;
    private Handler mHandler;
    private Runnable mAutoDismissTask;

    public static AlarmNoGamesFragment newInstance(String alarmId) {
        AlarmNoGamesFragment fragment = new AlarmNoGamesFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARGS_ALARM_ID, alarmId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_no_game, container, false);

        Bundle args = getArguments();
        UUID alarmId = UUID.fromString(args.getString(ARGS_ALARM_ID));
        Alarm alarm = AlarmList.get(getContext()).getAlarm(alarmId);

        String name = alarm.getTitle();
        if (name != null && !name.isEmpty()) {
            TextView alarmTitle = (TextView) view.findViewById(R.id.alarm_no_games_label);
            alarmTitle.setText(name);
            alarmTitle.setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.alarm_no_games_tap_to_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mAutoDismissTask);
                mCallback.onNoGameDismiss(true);
            }
        });

        mAutoDismissTask = new Runnable() {
            @Override
            public void run() {
                mCallback.onNoGameDismiss(false);
            }
        };
        mHandler = new Handler();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (NoGameResultListener) context;
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
        mHandler.postDelayed(mAutoDismissTask, NO_GAME_SCREEN_TIMEOUT_DURATION);
    }

    public interface NoGameResultListener {
        void onNoGameDismiss(boolean launchSettings);
    }
}


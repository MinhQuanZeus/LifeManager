package zeus.minhquan.lifemanager.settings;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;

import java.util.ArrayList;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.models.Alarm;

/**
 * Created by QuanT on 4/22/2017.
 */

public class GamesPreference extends Preference {
    private String[] mGameLabels;
    private String[] mGameValues;
    ArrayList<String> mInitialValues;
    ArrayList<String> mEnabledValues;

    public static ArrayList<String> getEnabledGames(Context context, Alarm alarm) {
        ArrayList<String> enabledGames = new ArrayList<>();
        if (alarm.isFreakingMathEnabled()) {
            enabledGames.add(context.getString(R.string.pref_game_freaking_math_id));
        }
        if(alarm.isCatchASnoozeEnabled()){
            enabledGames.add(context.getString(R.string.pref_game_catch_a_snooze_id));
        }
        return enabledGames;
    }

    public GamesPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean hasChanged() {
        return !mInitialValues.equals(mEnabledValues);
    }

    public boolean isCatchASnoozeEnabled() {
        return mEnabledValues.contains(getContext().getString(R.string.pref_game_catch_a_snooze_id));
    }

    public boolean isFreakingMathEnabled() {
        return mEnabledValues.contains(getContext().getString(R.string.pref_game_freaking_math_id));
    }


    public void setGameValuesAndSummary(ArrayList<String> enabledGames) {
        mEnabledValues = enabledGames;
        setSummaryValues(mEnabledValues);
    }

    public void setInitialValues(Alarm alarm) {
        mGameValues = getContext().getResources().getStringArray(R.array.pref_game_values);
        mGameLabels = getContext().getResources().getStringArray(R.array.pref_game_labels);
        mEnabledValues = getEnabledGames(getContext(), alarm);

        // Save the initial state so we can check for changes later
        mInitialValues = new ArrayList<>(mEnabledValues);
    }

    public void setInitialSummary() {
        setSummaryValues(mInitialValues);
    }

    public ArrayList<String> getEnabledGameValues() {
        return mEnabledValues;
    }

    private void setSummaryValues(ArrayList<String> values) {
        String summaryString = "";
        for (int i = 0; i < mGameValues.length; i++) {
            if (values.contains(mGameValues[i])) {
                String displayString = mGameLabels[i];
                if (summaryString.isEmpty()) {
                    summaryString = displayString;
                } else {
                    summaryString += ", " + displayString;
                }
            }
        }
        if (summaryString.isEmpty()) {
            summaryString = getContext().getString(R.string.pref_no_games);
        }
        setSummary(summaryString);
    }
}


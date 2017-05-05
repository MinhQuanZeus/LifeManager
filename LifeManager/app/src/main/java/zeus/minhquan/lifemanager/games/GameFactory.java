package zeus.minhquan.lifemanager.games;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import zeus.minhquan.lifemanager.models.Alarm;
import zeus.minhquan.lifemanager.models.AlarmList;
import zeus.minhquan.lifemanager.utils.Logger;

/**
 * Created by QuanT on 4/22/2017.
 */

public final class GameFactory {

    public static final String GAME_FRAGMENT_TAG = "game_fragment";
    private static final String TAG = "GameFactory";

    public static Fragment getGameFragment(Activity caller, UUID alarmId) {
        Alarm alarm = AlarmList.get(caller).getAlarm(alarmId);
        List<Class> games = new ArrayList<>();
        if(alarm.isCatchASnoozeEnabled()){
            games.add(GameCatchSnoozeFragment.class);
        }
        if(alarm.isFreakingMathEnabled()){
            games.add(GameFreakingMathFragment.class);
        }

        Class game = null;
        if(games.size()>0){
            int rand = new Random().nextInt(games.size());
            game = games.get(rand);
        }

        Fragment fragment = null;
        if (game != null) {
            try {
                fragment = (Fragment) game.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Couldn't create fragment:", e);
                Logger.trackException(e);
            }
        }
        return fragment;
    }


    public static Fragment getNoNetworkGame(Activity caller) {
        Fragment fragment = null;
        try {
            fragment = GameFreakingMathFragment.class.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Couldn't create fragment:", e);
            Logger.trackException(e);
        }
        return fragment;
    }

    public interface GameResultListener {
        void onGameSuccess(String shareable);
        void onGameFailure();
        void onGameError();
    }
}


package zeus.minhquan.lifemanager.utils;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.settings.AlarmSettingsFragment;
import zeus.minhquan.lifemanager.settings.GamesSettingsFragment;

/**
 * Created by QuanT on 4/22/2017.
 */

public class SettingsUtils {
    private SettingsUtils() {}

    public static AlarmSettingsFragment getAlarmSettingsFragment(FragmentManager fragmentManager) {
        return (AlarmSettingsFragment)fragmentManager
                .findFragmentByTag(AlarmSettingsFragment.SETTINGS_FRAGMENT_TAG);
    }

    public static GamesSettingsFragment getGamesSettingsFragment(FragmentManager fragmentManager) {
        return (GamesSettingsFragment)fragmentManager
                .findFragmentByTag(GamesSettingsFragment.GAMES_SETTINGS_FRAGMENT_TAG);
    }

    public static boolean areEditingAlarmSettingsExclusive(FragmentManager fragmentManager) {
        return (getAlarmSettingsFragment(fragmentManager) != null) &&
                (getGamesSettingsFragment(fragmentManager) == null);
    }

    public static boolean areEditingGamesSettingsExclusive(FragmentManager fragmentManager) {
        return (getAlarmSettingsFragment(fragmentManager) == null) &&
                (getGamesSettingsFragment(fragmentManager) != null);
    }

    public static boolean areEditingSettings(FragmentManager fragmentManager) {
        return (getAlarmSettingsFragment(fragmentManager) != null) ||
                (getGamesSettingsFragment(fragmentManager) != null);
    }

    public static void transitionFromAlarmToGamesSettings(FragmentManager fragmentManager,
                                                          ArrayList<String> enabledGames) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment_container, GamesSettingsFragment.newInstance(enabledGames),
                GamesSettingsFragment.GAMES_SETTINGS_FRAGMENT_TAG);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void transitionFromAlarmListToSettings(FragmentManager fragmentManager,
                                                         String alarmId) {
        GeneralUtils.showFragmentFromRight(fragmentManager,
                AlarmSettingsFragment.newInstance(alarmId),
                AlarmSettingsFragment.SETTINGS_FRAGMENT_TAG);
    }
}

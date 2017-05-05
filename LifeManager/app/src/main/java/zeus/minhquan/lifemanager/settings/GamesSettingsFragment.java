package zeus.minhquan.lifemanager.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.DividerItemDecoration;
import zeus.minhquan.lifemanager.utils.GeneralUtils;
import zeus.minhquan.lifemanager.utils.Logger;
import zeus.minhquan.lifemanager.utils.SettingsUtils;

/**
 * Created by QuanT on 4/22/2017.
 */

public class GamesSettingsFragment extends PreferenceFragmentCompat {
    public static final String GAMES_SETTINGS_FRAGMENT_TAG = "games_settings_fragment";
    private static final String ARGS_ENABLED_GAMES = "enabled_games";
    GamesSettingsListener mCallback;

    public static GamesSettingsFragment newInstance(ArrayList<String> enabledGames) {
        GamesSettingsFragment fragment = new GamesSettingsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putStringArrayList(ARGS_ENABLED_GAMES, enabledGames);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (GamesSettingsListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        Logger.flush();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (launchedFromAlarmSettings()) {
            onBack();
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, final String s) {
        addPreferencesFromResource(R.xml.pref_games);
        setDefaultEnabledState();

        Bundle args = getArguments();
        ArrayList<String> enabledGames = args.getStringArrayList(ARGS_ENABLED_GAMES);
        for (String gameId : enabledGames) {
            ((CheckBoxPreference)findPreference(gameId)).setChecked(true);
        }
    }

    @Override
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
                                             Bundle savedInstanceState) {
        LinearLayout rootLayout = (LinearLayout) parent.getParent();
        AppBarLayout appBarLayout =
                (AppBarLayout) LayoutInflater.from(getContext()).inflate(R.layout.settings_toolbar,
                        rootLayout,
                        false);
        rootLayout.addView(appBarLayout, 0); // insert at top
        Toolbar bar = (Toolbar) appBarLayout.findViewById(R.id.settings_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(bar);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If Alarm settings is already in the backstack just pop, otherwise callback
                if (launchedFromAlarmSettings()) {
                    getFragmentManager().popBackStack();
                } else {
                    onBack();
                }

            }
        });
        bar.setTitle(R.string.pref_title_games);

        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, savedInstanceState);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));
        return recyclerView;
    }

    public void onBack() {
        mCallback.onGamesSettingsDismiss(getEnabledGames());
    }

    private void setDefaultEnabledState() {
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            ((CheckBoxPreference)preferenceScreen.getPreference(i)).setChecked(false);
        }

    }

    private ArrayList<String> getEnabledGames() {
        ArrayList<String> enabledGames = new ArrayList<>();
        PreferenceScreen preferenceScreen = getPreferenceManager().getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            CheckBoxPreference preference = (CheckBoxPreference)preferenceScreen.getPreference(i);
            if (preference.isChecked()) {
                enabledGames.add(preference.getKey());
            }
        }
        return enabledGames;
    }

    private boolean launchedFromAlarmSettings() {
        return (SettingsUtils.getAlarmSettingsFragment(getFragmentManager()) != null);
    }

    public interface GamesSettingsListener {
        void onGamesSettingsDismiss(ArrayList<String> enabledGames);
    }
}



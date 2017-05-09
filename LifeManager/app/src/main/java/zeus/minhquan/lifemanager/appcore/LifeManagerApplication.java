package zeus.minhquan.lifemanager.appcore;

import android.app.Application;
import android.content.Context;

import zeus.minhquan.lifemanager.database.ToDoCB;
import zeus.minhquan.lifemanager.games.SoundManager;

/**
 * Created by QuanT on 4/22/2017.
 */

public class LifeManagerApplication extends Application {
    public static final String TAG = "ToDo";
    private static LifeManagerApplication instance;
    private static Context sContext;
    private ToDoCB toDoCB;

    public static Context getAppContext() {
        return LifeManagerApplication.sContext;
    }

    public static LifeManagerApplication getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        toDoCB = new ToDoCB(getApplicationContext());
        LifeManagerApplication.sContext = getApplicationContext();
        SoundManager.loadSoundIntoList(this);
    }

    public ToDoCB getToDoCB() {
        return toDoCB;
    }
}

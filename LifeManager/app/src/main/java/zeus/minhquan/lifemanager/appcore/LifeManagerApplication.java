package zeus.minhquan.lifemanager.appcore;

import android.app.Application;
import android.content.Context;

import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.ToDoCB;

/**
 * Created by QuanT on 4/22/2017.
 */

public class LifeManagerApplication extends Application {
    public static final String TAG = "ToDo";
    private ToDoCB toDoCB;
    private RemindDatabase remindDatabase;
    private static LifeManagerApplication instance;


    private static Context sContext;
    public static Context getAppContext() {
        return LifeManagerApplication.sContext;
    }

    public RemindDatabase getRemindDatabase() {
        return remindDatabase;
    }

    public static LifeManagerApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        remindDatabase = new RemindDatabase(this);
        toDoCB = new ToDoCB(getApplicationContext());
        LifeManagerApplication.sContext = getApplicationContext();
    }
    public ToDoCB getToDoCB() {
        return toDoCB;
    }
}

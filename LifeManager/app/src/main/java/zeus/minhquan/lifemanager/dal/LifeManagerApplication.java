package zeus.minhquan.lifemanager.dal;

import android.app.Application;

/**
 * Created by EDGY on 4/21/2017.
 */

public class LifeManagerApplication extends Application {
    private static LifeManagerApplication instance;
    private DatabaseContext databaseContext;

    @Override
    public void onCreate() {
        instance = this;
        databaseContext = new DatabaseContext(this);
        super.onCreate();
    }

    public static LifeManagerApplication getInstance() {
        return instance;
    }

    public DatabaseContext getDatabaseContext() {
        return databaseContext;
    }
}

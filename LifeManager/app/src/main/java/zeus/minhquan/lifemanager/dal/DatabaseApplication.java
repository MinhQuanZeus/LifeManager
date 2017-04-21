package zeus.minhquan.lifemanager.dal;

import android.app.Application;

/**
 * Created by EDGY on 4/21/2017.
 */

public class DatabaseApplication extends Application {
    private static DatabaseApplication instance;
    private DatabaseContext databaseContext;

    @Override
    public void onCreate() {
        instance = this;
        databaseContext = new DatabaseContext(this);
        super.onCreate();
    }

    public static DatabaseApplication getInstance() {
        return instance;
    }

    public DatabaseContext getDatabaseContext() {
        return databaseContext;
    }
}

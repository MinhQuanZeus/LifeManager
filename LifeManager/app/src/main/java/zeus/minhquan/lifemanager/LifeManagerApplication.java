package zeus.minhquan.lifemanager;

import android.app.Application;
import android.util.Log;

import zeus.minhquan.lifemanager.databases.RemindDatabase;


/**
 * Created by anh82 on 4/18/2017.
 */

public class LifeManagerApplication extends Application {
    private RemindDatabase remindDatabase;
    private static LifeManagerApplication instance;

    @Override
    public void onCreate() {
        instance = this;
        remindDatabase = new RemindDatabase(this);
        super.onCreate();
        Log.d("Hello" , "oncreate");
    }

    public RemindDatabase getStoryDatabase(){
        return remindDatabase;
    }

    public static LifeManagerApplication getInstance() {
        return instance;
    }
}

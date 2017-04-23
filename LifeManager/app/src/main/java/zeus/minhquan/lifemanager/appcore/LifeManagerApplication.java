package zeus.minhquan.lifemanager.appcore;

import android.app.Application;
import android.content.Context;

import com.uservoice.uservoicesdk.UserVoice;

/**
 * Created by QuanT on 4/22/2017.
 */

public class LifeManagerApplication extends Application {
    private static Context sContext;
    public static Context getAppContext() {
        return LifeManagerApplication.sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LifeManagerApplication.sContext = getApplicationContext();
    }
}

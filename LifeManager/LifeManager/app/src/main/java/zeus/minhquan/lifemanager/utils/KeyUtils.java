package zeus.minhquan.lifemanager.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by QuanT on 4/22/2017.
 */

public class KeyUtils {
    public static String getToken(Context caller, String resource) {
        String token = null;
        try {
            ApplicationInfo ai = caller.getPackageManager().getApplicationInfo(caller.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            token = bundle.getString("zeus.minhquan.lifemanager.token." + resource);
        } catch (Exception ex) {
            Logger.trackException(ex);
        }
        return token;
    }
}

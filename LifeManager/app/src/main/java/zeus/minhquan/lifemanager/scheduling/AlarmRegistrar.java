package zeus.minhquan.lifemanager.scheduling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by QuanT on 4/22/2017.
 */

public class AlarmRegistrar extends BroadcastReceiver {

    private static void refreshAlarms(Context context) {
        AlarmScheduler.cancelAlarms(context);
        if (AlarmScheduler.scheduleAlarms(context)) {
            AlarmNotificationManager.get(context).handleNextAlarmNotificationStatus();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        refreshAlarms(context);
    }
}
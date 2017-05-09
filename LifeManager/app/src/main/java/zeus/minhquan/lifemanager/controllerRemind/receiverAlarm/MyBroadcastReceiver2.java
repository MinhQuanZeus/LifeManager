package zeus.minhquan.lifemanager.controllerRemind.receiverAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import zeus.minhquan.lifemanager.controllerRemind.NotifyMonitor;
import zeus.minhquan.lifemanager.database.models.Remind;

/**
 * Created by anh82 on 5/8/2017.
 */

public class MyBroadcastReceiver2 extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Remind remind = (Remind) intent.getSerializableExtra("remind");

        intent = new Intent(context, NotifyMonitor.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("remind1", remind);
        context.startActivity(intent);


    }

}

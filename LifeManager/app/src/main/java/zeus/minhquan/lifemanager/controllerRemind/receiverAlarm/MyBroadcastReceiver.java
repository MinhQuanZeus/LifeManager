package zeus.minhquan.lifemanager.controllerRemind.receiverAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.RecordActivity;
import zeus.minhquan.lifemanager.controllerRemind.NotifyMonitor;
import zeus.minhquan.lifemanager.database.models.Remind;

public class MyBroadcastReceiver extends BroadcastReceiver {
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

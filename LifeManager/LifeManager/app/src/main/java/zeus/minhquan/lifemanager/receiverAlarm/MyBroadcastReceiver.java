package zeus.minhquan.lifemanager.receiverAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import zeus.minhquan.lifemanager.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.lactroi);
        mp.start();
        Toast.makeText(context, "Đã đến giờ rồi dậy nào !!!!", Toast.LENGTH_LONG).show();
    }

}

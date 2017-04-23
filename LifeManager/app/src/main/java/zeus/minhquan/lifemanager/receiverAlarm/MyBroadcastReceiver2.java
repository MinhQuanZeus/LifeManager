package zeus.minhquan.lifemanager.receiverAlarm;

/**
 * Created by anh82 on 4/23/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.Toast;

import zeus.minhquan.lifemanager.R;

public class MyBroadcastReceiver2 extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.lactroi);
        mp.start();
        Toast.makeText(context, "Dậy đi , ngủ như con lợn  !!!!", Toast.LENGTH_LONG).show();
    }

}

package zeus.minhquan.lifemanager.receiverAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import zeus.minhquan.lifemanager.R;

public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bd = intent.getExtras();
        String  title = "";
        if(bd != null){
            title = bd.getString("title");
        }
        mp=MediaPlayer.create(context, R.raw.lactroi);
        mp.start();
        Toast.makeText(context, "Đã đến giờ của "+title+" !!!!", Toast.LENGTH_LONG).show();
    }

}

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

public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bd = intent.getExtras();
        String  title = "";
        String record = "";
        title = intent.getStringExtra("title");
        record = intent.getStringExtra("record");
        Toast.makeText(context,title+" !!!! "+ record, Toast.LENGTH_LONG).show();
//        if(bd != null){
//            title = bd.getString("title");
//            record = bd.getString("record");
//        }
        if(record != "" && record != null){
            playRecord(record);
        } else {
            mp=MediaPlayer.create(context, R.raw.lactroi);
            mp.start();
        }
        //  R.string.Alarm_Remind_rung
        Toast.makeText(context,"Đã đến giờ của " + title+" !!!!", Toast.LENGTH_LONG).show();
    }


    public void playRecord(String recordPath){
        mp = new MediaPlayer();
        try {
            mp.setDataSource(recordPath);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();

    }

}

package zeus.minhquan.lifemanager.controllerRemind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.controllerRemind.receiverAlarm.MyBroadcastReceiver;
import zeus.minhquan.lifemanager.database.models.Remind;

public class NotifyMonitor extends AppCompatActivity {
    MediaPlayer mp;
    TextView title;
    TextView description;
    TextView time;
    ImageView clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_monitor);
        //Bundle bd = getIntent().getExtras();
        Remind remind;
        if(getIntent().getSerializableExtra("remind1") != null){
            remind = (Remind) getIntent().getSerializableExtra("remind1");
        } else {
           remind  = (Remind) getIntent().getSerializableExtra("remind2");
           // remind  = (Remind) bd.getSerializable("remind2");
        }

        cancelAlarm(remind.getId());
        setDefault(remind);
        if(remind.getRecord_name() != null && remind.getRecord_name()!= ""){
           playRecord(remind.getRecord_name());
        } else {
            mp=MediaPlayer.create(NotifyMonitor.this, R.raw.lactroi);
            mp.setLooping(true);
            mp.start();
        }

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp != null){
                    mp.stop();
                }
                NotifyMonitor.this.finish();
                System.exit(0);
            }
        });
        TranslateAnimation animation = new TranslateAnimation(0,40, 0, 0);
        animation.setDuration(1000);
        //animation.setFillEnabled(true);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        clock.setAnimation(animation);
    }

    private void setDefault(Remind remind) {
        title = (TextView) findViewById(R.id.titleNotify);
        description = (TextView) findViewById(R.id.descriptionNotify);
        time = (TextView) findViewById(R.id.timeNotify);
        clock = (ImageView) findViewById(R.id.clock);
        title.setText(remind.getTitle());
        description.setText(remind.getDescription());
        time.setText(remind.getTime() + "  " + remind.getDate());
        clock.setImageResource(R.drawable.alarm_ringing_clock);
    }

    public void playRecord(String recordPath){
        mp = new MediaPlayer();
        try {
            mp.setDataSource(recordPath);
            mp.setLooping(true);
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.start();
    }
    public  void cancelAlarm(int id) {
        Intent intent = new Intent(NotifyMonitor.this, MyBroadcastReceiver.class);
        //intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotifyMonitor.this.getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) NotifyMonitor.this.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}

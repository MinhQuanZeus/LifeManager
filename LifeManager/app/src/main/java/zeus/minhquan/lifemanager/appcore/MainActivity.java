package zeus.minhquan.lifemanager.appcore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import zeus.minhquan.lifemanager.R;

public class MainActivity extends AppCompatActivity {
    ImageView iv_alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_alarm = (ImageView)findViewById(R.id.iv_alarm_clock);
        iv_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AlarmMainActivity.class);
                startActivity(intent);
            }
        });
    }
}

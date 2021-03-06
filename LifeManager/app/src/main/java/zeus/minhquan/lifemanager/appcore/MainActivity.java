package zeus.minhquan.lifemanager.appcore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.controllerRemind.RemindActivity;
import zeus.minhquan.lifemanager.todolist.ListActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView iv_alarm, iv_todo;
    private ImageView iv_remind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_alarm = (ImageView)findViewById(R.id.iv_alarm_clock);
        iv_remind = (ImageView) findViewById(R.id.iv_remind);
        iv_todo = (ImageView) findViewById(R.id.iv_todo);
        iv_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AlarmMainActivity.class);
                startActivity(intent);
            }
        });
        iv_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RemindActivity.class);
                startActivity(intent);
            }
        });
        iv_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ListActivity.class);
                startActivity(intent);
            }
        });
    }
}

package zeus.minhquan.lifemanager.appcore;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.controllerRemind.RemindActivity;

public class BaseActivityBottonNavigation extends AppCompatActivity {

    protected FrameLayout frameLayout;
    private static boolean isLaunch = true;
    private static boolean isAlarmStart,isRemindSrart,isTodoStart;
    BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_alarm:
                    if(!isAlarmStart) {
//                        Intent myIntent = new Intent(BaseActivityBottonNavigation.this, AlarmMainActivity.class);
//                        ActivityOptions options =
//                                ActivityOptions.makeCustomAnimation(BaseActivityBottonNavigation.this, android.R.anim.fade_in, android.R.anim.fade_out);
//                        startActivity(myIntent, options.toBundle());
                        startActivity(new Intent(BaseActivityBottonNavigation.this, AlarmMainActivity.class));
                    }
                    isAlarmStart = true;
                    isRemindSrart = false;
                    isTodoStart = false;
                    return true;
                case R.id.navigation_remind:
                    if(!isRemindSrart) {
                        startActivity(new Intent(BaseActivityBottonNavigation.this, RemindActivity.class));
//                        Intent myIntent = new Intent(BaseActivityBottonNavigation.this, RemindActivity.class);
//                        ActivityOptions options =
//                                ActivityOptions.makeCustomAnimation(BaseActivityBottonNavigation.this, android.R.anim., android.R.anim.fade_out);
//                        startActivity(myIntent, options.toBundle());
                    }
                    isAlarmStart = false;
                    isRemindSrart = true;
                    isTodoStart = false;
                    return true;
                case R.id.navigation_todo:
                    if(!isTodoStart) {
                        startActivity(new Intent(BaseActivityBottonNavigation.this, ToDoMainActivity.class));
//                        Intent myIntent = new Intent(BaseActivityBottonNavigation.this, ToDoMainActivity.class);
//                        ActivityOptions options =
//                                ActivityOptions.makeCustomAnimation(BaseActivityBottonNavigation.this, android.R.anim.fade_in, android.R.anim.fade_out);
//                        startActivity(myIntent, options.toBundle());
                    }
                    isAlarmStart = false;
                    isRemindSrart = false;
                    isTodoStart = true;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_botton_navigation);

        frameLayout = (FrameLayout) findViewById(R.id.content);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (isLaunch) {
            isLaunch = false;
            startActivity(new Intent(BaseActivityBottonNavigation.this, AlarmMainActivity.class));
            isAlarmStart = true;
            isRemindSrart = false;
            isTodoStart = false;
        }
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }
}

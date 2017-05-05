package zeus.minhquan.lifemanager.appcore;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.couchbase.lite.Document;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.todolist.ToDoListFragment;
import zeus.minhquan.lifemanager.utils.GeneralUtils;

public class ToDoMainActivity extends AppCompatActivity implements ToDoListFragment.TaskListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_main);
    }

    @Override
    public void onTaskSelected(Document list) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        GeneralUtils.showFragment(getSupportFragmentManager(),
                new ToDoListFragment(),
                AlarmListFragment.ALARM_LIST_FRAGMENT_TAG);
    }
}

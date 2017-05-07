package zeus.minhquan.lifemanager.appcore;

import android.os.Bundle;

import com.couchbase.lite.Document;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.utils.GeneralUtils;

public class ToDoMainActivity extends BaseActivity implements ToDoListFragment.TaskListListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //   setContentView(R.layout.activity_to_do_main);
        getLayoutInflater().inflate(R.layout.activity_to_do_main, frameLayout);
        mDrawerList.setItemChecked(position, true);
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

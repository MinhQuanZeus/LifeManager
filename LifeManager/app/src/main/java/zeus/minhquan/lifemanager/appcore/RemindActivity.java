package zeus.minhquan.lifemanager.appcore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import java.util.ArrayList;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.RemindEmptyInterface;
import zeus.minhquan.lifemanager.controllerRemind.AddRemindActivity;
import zeus.minhquan.lifemanager.controllerRemind.MyArrayAdapter;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.Remind;


public class RemindActivity extends BaseActivityBottonNavigation implements RemindEmptyInterface {

    //public static HashMap<String ,PendingIntent> listPendingAlarm = new HashMap<>();
    private FloatingActionButton ivAdd;
    ArrayList<Remind> arrRemind;
    MyArrayAdapter adapter=null;
    ExpandableLayoutListView expandableLayoutListView;
    private RelativeLayout mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_remind);
        getLayoutInflater().inflate(R.layout.activity_remind, frameLayout);
        navigation.getMenu().getItem(1).setChecked(true);

        setDefault();
        mEmptyView = (RelativeLayout) findViewById(R.id.empty_view);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindActivity.this, AddRemindActivity.class);
                intent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK);
                updateUI();
                startActivity(intent);
            }
        });
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefault();
    }
    public void updateUI() {
        if (arrRemind.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    private void setDefault() {
        ivAdd = (FloatingActionButton) findViewById(R.id.add_remind);
        expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview1);
        RemindDatabase remindDatabase = LifeManagerApplication.getInstance().getRemindDatabase();
        arrRemind = new ArrayList<>();
        arrRemind = (ArrayList<Remind>) remindDatabase.loadAllReminds();


        adapter=new MyArrayAdapter(this, R.layout.view_row, arrRemind,expandableLayoutListView);
        expandableLayoutListView.setAdapter(adapter);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setEmptyRemind() {
        updateUI();
    }
}

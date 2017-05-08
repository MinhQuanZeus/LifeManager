package zeus.minhquan.lifemanager.controllerRemind;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import java.util.ArrayList;
import java.util.HashMap;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.Remind;


public class RemindActivity extends AppCompatActivity {

    //public static HashMap<String ,PendingIntent> listPendingAlarm = new HashMap<>();
    private FloatingActionButton ivAdd;
    ArrayList<Remind> arrRemind;
    MyArrayAdapter adapter=null;
    ExpandableLayoutListView expandableLayoutListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_remind);

        setDefault();
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindActivity.this, AddRemindActivity.class);
                intent.setFlags(  Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDefault();
    }

    private void setDefault() {
        ivAdd = (FloatingActionButton) findViewById(R.id.add_remind);
        expandableLayoutListView = (ExpandableLayoutListView) findViewById(R.id.listview1);

        RemindDatabase remindDatabase = LifeManagerApplication.getInstance().getRemindDatabase();
        arrRemind = new ArrayList<>();
        arrRemind = (ArrayList<Remind>) remindDatabase.loadAllReminds();

        for(Remind remind : arrRemind){

        }

        adapter=new MyArrayAdapter(this,R.layout.view_row, arrRemind,expandableLayoutListView);
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
}

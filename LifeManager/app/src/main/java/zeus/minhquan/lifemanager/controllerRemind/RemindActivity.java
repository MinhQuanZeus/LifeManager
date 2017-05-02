package zeus.minhquan.lifemanager.controllerRemind;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import java.util.ArrayList;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.Remind;


public class RemindActivity extends AppCompatActivity {


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
        Log.d("Size is", arrRemind.size() + "");
        for(Remind remind : arrRemind){
            Log.d("Content is", remind.getTitle() + "");
        }
        adapter=new MyArrayAdapter(this,R.layout.view_row, arrRemind);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

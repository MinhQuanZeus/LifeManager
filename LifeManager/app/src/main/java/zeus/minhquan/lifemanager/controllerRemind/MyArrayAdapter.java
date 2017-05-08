package zeus.minhquan.lifemanager.controllerRemind;

/**
 * Created by anh82 on 4/21/2017.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.Remind;
import zeus.minhquan.lifemanager.controllerRemind.receiverAlarm.MyBroadcastReceiver;

import static android.content.Context.ALARM_SERVICE;

public class MyArrayAdapter extends ArrayAdapter<Remind>
{
    Activity context=null;
    ArrayList<Remind>myArray=null;
    LayoutInflater inflater;
    int layoutId;
    ViewHolder holder;
    SharedPreferences.Editor editor;
    RemindDatabase rd;
    ExpandableLayoutListView expandableLayoutListView;




    public MyArrayAdapter(Activity context, int layoutId, ArrayList<Remind>arr , View listView){
        super(context, layoutId, arr);
        this.context=context;
        this.layoutId=layoutId;
        this.myArray=arr;
        rd = new RemindDatabase(context);
        inflater=  context.getLayoutInflater();
        expandableLayoutListView = (ExpandableLayoutListView)listView;

    }
    public int getCount() {
        return myArray.size();

    }

    public Remind getItem(int position) {
        return myArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position,  View convertView,  ViewGroup parent) {

        final Remind emp = myArray.get(position);
        if (convertView == null) {
            convertView=inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.title1 = (TextView) convertView.findViewById(R.id.contentRemind1);
            holder.txtTimeFull1 = (TextView) convertView.findViewById(R.id.timeRemind1);


            holder.description = (TextView) convertView.findViewById(R.id.description);

            holder.txtRecord = (TextView) convertView.findViewById(R.id.et_sd_record);
            holder.btnDelete = (ImageView) convertView.findViewById(R.id.btn_sd_delete);
            holder.btnUpdate = (Button) convertView.findViewById(R.id.btnUpdate);

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rd.delete(emp.getId());
                    cancelAlarm(emp.getId());
                    myArray.remove(emp);
                    MyArrayAdapter adapter=new MyArrayAdapter(context,layoutId, myArray,expandableLayoutListView);
                    expandableLayoutListView.setAdapter(adapter);
                }
            });

            holder.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UpdateRemind.class);
                    intent.putExtra("id",emp.getId());
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        setValues(emp);

        // setValues(emp);





        return convertView;

    }

    public void setValues(Remind emp){
        holder.title1.setText(emp.getTitle());
        holder.txtTimeFull1.setText(emp.getTime() + "    " + emp.getDate());

        holder.description.setText(emp.getDescription());

        String record = emp.getRecord_name();
        if(record != null && record != ""){
            for(int i=record.length()-1 ; i >= 0 ; i-- ){
                if(record.charAt(i)=='/'){
                    record = record.substring(i+1, record.length());
                    break;
                }
            }

        }
        holder.txtRecord.setText(record);


    }
    public  void cancelAlarm(int id) {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        //intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }





    // doan code tao su kien dem
    public void startEvent(int second ,String title, int id){
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +  second * 1000 , pendingIntent);
        Toast.makeText(context, "Alarm set in "  +second+ " seconds",Toast.LENGTH_LONG).show();





    }









    static class ViewHolder {
        TextView title1;
        TextView txtTimeFull1;
        TextView description;
        TextView txtRecord;
        ImageView btnDelete;
        Button btnUpdate;

    }
}

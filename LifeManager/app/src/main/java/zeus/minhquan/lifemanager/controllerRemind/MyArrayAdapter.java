package zeus.minhquan.lifemanager.controllerRemind;

/**
 * Created by anh82 on 4/21/2017.
 */

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;

import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.RecordActivity;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.MyDate;
import zeus.minhquan.lifemanager.database.models.MyTime;
import zeus.minhquan.lifemanager.database.models.Remind;
import zeus.minhquan.lifemanager.database.models.TimeType;
import zeus.minhquan.lifemanager.receiverAlarm.MyBroadcastReceiver;

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

    // bien xet time
    private MyDate myDatePicker;
    private MyTime myTimePicker;
    int yearChoose;
    int monthChoose;
    int dayChoose;
    int houseChoose;
    int minuteChoose;

    public MyArrayAdapter(Activity context, int layoutId, ArrayList<Remind>arr , View listView){
        super(context, layoutId, arr);
        this.context=context;
        this.layoutId=layoutId;
        this.myArray=arr;
        rd = new RemindDatabase(context);
        inflater=  context.getLayoutInflater();
        expandableLayoutListView = (ExpandableLayoutListView)listView;

        Calendar now = Calendar.getInstance();
        myDatePicker = new MyDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DATE));
        myTimePicker = new MyTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
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

            holder.title2 = (EditText) convertView.findViewById(R.id.title2);
            holder.description2 = (EditText) convertView.findViewById(R.id.descriptionRemind1);
            holder.date2 = (TextView) convertView.findViewById(R.id.date2);
            holder.time2 = (TextView) convertView.findViewById(R.id.time2);
            holder.txtRecord = (TextView) convertView.findViewById(R.id.record1);
            holder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            holder.btnUpdate = (Button) convertView.findViewById(R.id.btnEdit);

            holder.imgDate = (ImageView) convertView.findViewById(R.id.iv_Calendar) ;
            holder.imgTime = (ImageView) convertView.findViewById(R.id.iv_Clock) ;
            holder.imgRecord = (ImageView) convertView.findViewById(R.id.iv_record) ;



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
            holder.imgDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDatePickerDialog();
                }
            });
            holder.imgTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePickerDialog();
                }
            });


            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title1.setText(emp.getTitle());
        holder.txtTimeFull1.setText(emp.getTime() + "    " + emp.getDate());
        holder.title2.setText(emp.getTitle());
        holder.description2.setText(emp.getDescription());
        holder.date2.setText( emp.getDate());
        holder.time2.setText(emp.getTime());
        holder.txtRecord.setText(emp.getRecord_name());




        Log.d("Content" , emp.getTitle());

        return convertView;

    }
    public  void cancelAlarm(int id) {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        //intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public String getCurrentDate(TimeType timeType){
        Calendar now = Calendar.getInstance();
        switch (timeType){
            case DATE:
              //  Log.d(TAG,now.get(Calendar.YEAR) + "YEAR");
                myDatePicker = new MyDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DATE));
                return myDatePicker.getDate();

            case TIME:
                myTimePicker = new MyTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
                return myTimePicker.getTime();
        }
        return null;
    }
    public void showRecordActivity(){
        Intent intent = new Intent(context, RecordActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // cancel alarm
    public  void cancelAlarm() {
        Intent intent = new Intent(context, MyBroadcastReceiver.class);
        //intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), rd.getIDMax(), intent, 0);
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
        RemindActivity.listPendingAlarm.put(id + "",pendingIntent);

        Log.d("size after Adding" , RemindActivity.listPendingAlarm.size() + "");


    }


    public void showDatePickerDialog(){
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                myDatePicker = new MyDate(year, month , date);
                holder.date2.setText(myDatePicker.getDate());
                // lay ngay thang dc chon de chut nua tinh thoi gian hen gio
                yearChoose = year;
                monthChoose = month;
                dayChoose = date;

                 Log.d("Nam thang ngay" , yearChoose+" , "+monthChoose+" , "+dayChoose );
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(context, callback, myDatePicker.getYear(), myDatePicker.getMonth()-1, myDatePicker.getMyDate());
        datePicker.setTitle("Choose your date");
        datePicker.show();
    }









    public void showTimePickerDialog() {
        final TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
               // Log.d(TAG,minute +" abc");
                myTimePicker = new MyTime(hour, minute);
                holder.time2.setText(myTimePicker.getTime());

                // lay thoi gian hien tai chut nua tinh thoi gian con lai de dem
                houseChoose = hour;
                minuteChoose = minute;
                 Log.d("gio , phut" , houseChoose+" , "+minuteChoose);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, callback, myTimePicker.getHour(), myTimePicker.getMin(), true);
        timePickerDialog.setTitle("Choose your time");
        timePickerDialog.show();
    }



    static class ViewHolder {
        TextView title1;
        EditText Description1;
        TextView txtTimeFull1;
        EditText title2;
        EditText description2;
        TextView date2;
        TextView time2;
        TextView txtRecord;
        ImageView imgRecord;
        ImageView imgDate;
        ImageView imgTime;
        Button btnDelete;
        Button btnUpdate;

    }
}

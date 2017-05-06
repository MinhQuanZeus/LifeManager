package zeus.minhquan.lifemanager.controllerRemind;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import zeus.minhquan.lifemanager.RecordActivity;
import zeus.minhquan.lifemanager.database.models.MyDate;
import zeus.minhquan.lifemanager.database.models.MyTime;
import zeus.minhquan.lifemanager.database.models.TimeType;
import zeus.minhquan.lifemanager.receiverAlarm.MyBroadcastReceiver;
import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.Remind;


public class AddRemindActivity extends AppCompatActivity {

    private static final String TAG = "AddRemindActivity";

    RemindDatabase db = LifeManagerApplication.getInstance().getRemindDatabase();

    private EditText etTitle;
    private EditText etDescription;
    private TextView txtDate;
    private TextView txtTime;
    private MyDate myDatePicker;
    private MyTime myTimePicker;
    private ImageView ivSave;
    private ImageView ivRecord;
    private TextView tvRecord;
    int yearChoose;
    int monthChoose;
    int dayChoose;
    int houseChoose;
    int minuteChoose;



    public void setDefault(){
        etTitle = (EditText) findViewById(R.id.et_title);
        etDescription = (EditText) findViewById(R.id.description1);
        txtDate = (TextView) findViewById(R.id.et_date);
        txtTime = (TextView) findViewById(R.id.et_time);
        ivSave = (ImageView) findViewById(R.id.iv_save);
        ivRecord = (ImageView) findViewById(R.id.iv_record);
        tvRecord = (TextView) findViewById(R.id.et_record);
        //current date time

        txtDate.setText(getCurrentDate(TimeType.DATE));
        txtTime.setText(getCurrentDate(TimeType.TIME));

        Calendar c = Calendar.getInstance();
         yearChoose = c.get(Calendar.YEAR);
         monthChoose = c.get(Calendar.MONTH);
         dayChoose = c.get(Calendar.DAY_OF_MONTH);
         houseChoose = c.get(Calendar.HOUR_OF_DAY);
         minuteChoose = c.get(Calendar.MINUTE);
    }



    public String getCurrentDate(TimeType timeType){
        Calendar now = Calendar.getInstance();
        switch (timeType){
            case DATE:
                Log.d(TAG,now.get(Calendar.YEAR) + "YEAR");
                myDatePicker = new MyDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DATE));
                return myDatePicker.getDate();

            case TIME:
                myTimePicker = new MyTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
                return myTimePicker.getTime();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent().hasExtra("record")) {
            tvRecord.setText(getIntent().getStringExtra("record"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        setDefault();
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.d("hello" , "ABC");
                //TODO : valid input
                Date dateNow = new Date();
                Date dateFuture = new GregorianCalendar(yearChoose,monthChoose,dayChoose,houseChoose,minuteChoose).getTime();
                Log.d("Date Now " , dateNow.toString());
                Log.d( " thoi gian" , yearChoose + "," + monthChoose + "," + dayChoose + " , " +minuteChoose);
                Log.d("date Future" , dateFuture.toString() );

                int second = (int)((dateFuture.getTime() - dateNow.getTime())/1000) ;

                Log.d("Second" , second + "");
                if(second > 0){
                    Log.d("hello" , "ABC");

                    db.add(new Remind(etTitle.getText().toString(), etDescription.getText().toString()
                            ,txtDate.getText().toString(), txtTime.getText().toString()));

                    Log.d("so giay hen là ", " " + second);
                    //Log.d("  ID MAX " , db.getIDMax() + "");
                 startEvent(second ,etTitle.getText().toString(), db.getIDMax());
                }
                else {
                  //  cancelAlarm();

                }



            }
        });

        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordActivity();
            }
        });

    }

    public void showRecordActivity(){
        Intent intent = new Intent(AddRemindActivity.this, RecordActivity.class);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // cancel alarm
    public  void cancelAlarm() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        //intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), db.getIDMax(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // doan code tao su kien dem
    public void startEvent(int second ,String title, int id){
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("title",title );
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +  second * 1000 , pendingIntent);
        Toast.makeText(this, "Alarm set in "  +second+ " seconds",Toast.LENGTH_LONG).show();
        RemindActivity.listPendingAlarm.put(id + "",pendingIntent);

        Log.d("size after Adding" , RemindActivity.listPendingAlarm.size() + "");


    }


    public void showDatePickerDialog(){
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                myDatePicker = new MyDate(year, month , date);
                txtDate.setText(myDatePicker.getDate());
                // lay ngay thang dc chon de chut nua tinh thoi gian hen gio
                yearChoose = year;
                monthChoose = month;
                dayChoose = date;

               // Log.d("Nam thang ngay" , yearChoose+" , "+monthChoose+" , "+dayChoose );
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(AddRemindActivity.this, callback, myDatePicker.getYear(), myDatePicker.getMonth()-1, myDatePicker.getMyDate());

        datePicker.setTitle("Choose your date");
        datePicker.show();
    }









    public void showTimePickerDialog() {
        final TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                Log.d(TAG,minute +" abc");
                myTimePicker = new MyTime(hour, minute);
                txtTime.setText(myTimePicker.getTime());

                // lay thoi gian hien tai chut nua tinh thoi gian con lai de dem
                houseChoose = hour;
                minuteChoose = minute;
               // Log.d("gio , phut" , houseChoose+" , "+minuteChoose);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddRemindActivity.this, callback, myTimePicker.getHour(), myTimePicker.getMin(), true);
        timePickerDialog.setTitle("Choose your time");
        timePickerDialog.show();
    }


}

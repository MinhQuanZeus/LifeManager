package zeus.minhquan.lifemanager.controllerRemind;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import zeus.minhquan.lifemanager.RecordActivity;
import zeus.minhquan.lifemanager.database.models.MyDate;
import zeus.minhquan.lifemanager.database.models.MyTime;
import zeus.minhquan.lifemanager.database.models.TimeType;
import zeus.minhquan.lifemanager.controllerRemind.receiverAlarm.MyBroadcastReceiver;
import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.appcore.LifeManagerApplication;
import zeus.minhquan.lifemanager.database.RemindDatabase;
import zeus.minhquan.lifemanager.database.models.Remind;
import zeus.minhquan.lifemanager.utils.CountDownAlarm;


public class AddRemindActivity extends AppCompatActivity {

    private static final String TAG = "AddRemindActivity";

    RemindDatabase db = LifeManagerApplication.getInstance().getRemindDatabase();

    private EditText etTitle;
    private EditText etDescription;
    private TextView txtDate;
    private TextView txtTime;
    private MyDate myDatePicker;
    private MyTime myTimePicker;
    private Button ivSave;
    private ImageView ivRecord;
    private TextView tvRecord;
    private boolean isSave;
    private int yearChoose;
    private int monthChoose;
    private int dayChoose;
    private int houseChoose;
    private int minuteChoose;
    private String mainRecordPath;
    private ImageView ivBack;
    private ImageView ivShowRecord;
    private Button btnCancel;

    public void setDefault(){
        etTitle = (EditText) findViewById(R.id.et_title1);
        etDescription = (EditText) findViewById(R.id.et_description1);
        txtDate = (TextView) findViewById(R.id.et_date1);
        txtTime = (TextView) findViewById(R.id.et_time1);
        ivSave = (Button) findViewById(R.id.iv_save);
        ivRecord = (ImageView) findViewById(R.id.iv_record1);
        tvRecord = (TextView) findViewById(R.id.et_record);
        ivBack = (ImageView) findViewById(R.id.iv_back_list_remind);
        ivShowRecord = (ImageView) findViewById(R.id.view_record);
        btnCancel = (Button) findViewById(R.id.iv_cancel);
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

        etTitle.setText(getDataToResume("title"));
        etDescription.setText(getDataToResume("description"));
        String date = getDataToResume("date");
        String time = getDataToResume("time");
        if(date.equals("")){
            txtDate.setText(getCurrentDate(TimeType.DATE));
        } else {
            txtDate.setText(getDataToResume("date"));
        }
        if(time.equals("")){
            txtTime.setText(getCurrentDate(TimeType.TIME));
        } else {
            txtTime.setText(getDataToResume("time"));
        }
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            cal.setTime(sdf.parse(txtDate.getText().toString()));
        } catch (ParseException e) {

        }
        yearChoose = cal.get(Calendar.YEAR);
        monthChoose = cal.get(Calendar.MONTH);
        dayChoose = cal.get(Calendar.DAY_OF_MONTH);

        //convert string to house and minute

        String time2 = txtTime.getText().toString();
        if(time2.substring(8,10).equals("AM")){
            houseChoose = Integer.parseInt(time2.substring(0,2));
            minuteChoose = Integer.parseInt(time2.substring(5,7));
        } else {
            houseChoose = Integer.parseInt(time2.substring(0,2))+12;
            minuteChoose = Integer.parseInt(time2.substring(5,7));
        }
        tvRecord.setText(getDataToResume("record_name"));
        mainRecordPath = getDataToResume("record_path");
        tvRecord.setText(getDataToResume("record_name"));
        mainRecordPath = getDataToResume("record_path");
    }

    public String getDataToResume(String data){
        if(getIntent().hasExtra(data)){
            return getIntent().getStringExtra(data);
        } else return "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remind);
        Log.d(TAG,"onResume gaygaygay");
        setDefault();
        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etTitle, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        etTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etTitle, InputMethodManager.SHOW_IMPLICIT);
            }
        });
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

                isSave = true;
                if(etTitle.getText().toString().equals("") || etTitle == null){
                    Toast.makeText(AddRemindActivity.this, R.string.no_title, Toast.LENGTH_SHORT).show();
                    isSave = false;
                }
                if(isSave) {
                    Date dateNow = new Date();
                    Date dateFuture = new GregorianCalendar(yearChoose, monthChoose, dayChoose, houseChoose, minuteChoose).getTime();
                    int second = (int) ((dateFuture.getTime() - dateNow.getTime()) / 1000);
                    if (second > 0) {
                        //TODO: import record path to database
                        //mainRecordPath
                        Remind remindTemp ;
                        if(mainRecordPath != null && mainRecordPath != ""){
                            remindTemp = new Remind(etTitle.getText().toString(), etDescription.getText().toString()
                                    , txtDate.getText().toString(), txtTime.getText().toString(), mainRecordPath);

                        } else {
                            remindTemp = new Remind(etTitle.getText().toString(), etDescription.getText().toString()
                                    , txtDate.getText().toString(), txtTime.getText().toString());
                        }
                        db.add(remindTemp);
                        startEvent(second, remindTemp, db.getIDMax());
                        Intent intent = new Intent(AddRemindActivity.this, RemindActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AddRemindActivity.this,R.string.past_input_time, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Want to save?

                //back to List remind
                AddRemindActivity.super.onBackPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRemindActivity.super.onBackPressed();
            }
        });
        ivShowRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecordActivity();
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
        String title = getData(etTitle);
        String description = getData(etDescription);
        String date = getData(txtDate);
        String time = getData(txtTime);
        intent.putExtra("title",title);
        intent.putExtra("description",description);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public String getData(TextView evText){
        if(evText == null){
            return "";
        } else {
            return evText.getText().toString();
        }
    }

    // doan code tao su kien dem
    public void startEvent(int second ,Remind emp, int id){
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        intent.putExtra("remind", emp);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +  second * 1000 , pendingIntent);
        Toast.makeText(this, "Alarm set in "  + CountDownAlarm.getTimeCountDown(second),Toast.LENGTH_LONG).show();
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
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(AddRemindActivity.this, callback,
                myDatePicker.getYear(), myDatePicker.getMonth()-1, myDatePicker.getMyDate());
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

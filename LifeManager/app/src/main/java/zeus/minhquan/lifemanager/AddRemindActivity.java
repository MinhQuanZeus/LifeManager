package zeus.minhquan.lifemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import zeus.minhquan.lifemanager.dal.DatabaseApplication;
import zeus.minhquan.lifemanager.dal.DatabaseContext;
import zeus.minhquan.lifemanager.model.RemindInfo;

public class AddRemindActivity extends AppCompatActivity {

    private static final String TAG = "AddRemindActivity";
    private static final int HOUR_OF_TIME = 12;
    private EditText etTitle;
    private EditText etDescription;
    private TextView txtDate;
    private TextView txtTime;
    private MyDate myDatePicker;
    private MyTime myTimePicker;
    private ImageView ivSave;
    private ImageView ivRecord;
    private TextView tvRecord;

    public enum HalfTime{
        AM,
        PM
    }

    public class MyTime{
        private int hour;
        private int min;
        private HalfTime halfTime;

        public MyTime(int hour, int min) {
            this.hour = hour;
            this.min = min;
        }

        public int getHour() {
            return hour;
        }

        public int getMin() {
            return min;
        }

        public String getTime(){
            String hourFormat = "";
            String minFormat = "";
            halfTime = getHalfTime(hour);
            if(hour < 10){
                hourFormat = "0" + hour;
            } else {
                hourFormat = "" + hour;
            }
            if(min < 10){
                minFormat = "0" + min;
            } else {
                minFormat = "" + min;
            }
            return hourFormat + " : " + minFormat + " " + halfTime;
        }

        public HalfTime getHalfTime(int hour){
            if(hour >= HOUR_OF_TIME){
                this.hour -= HOUR_OF_TIME;
                return HalfTime.PM;
            } else return HalfTime.AM;
        }
    }


    public class MyDate{
        private int year;
        private int month;
        private int myDate;
        private static final int COUNT_MONTH_START = 1;

        public MyDate(int year, int month, int date) {
            this.year = year;
            this.month = month + COUNT_MONTH_START;
            this.myDate = date;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getMyDate() {
            return myDate;
        }

        public String getDate(){
            return myDate + "/" + month + "/" + year;
        }
    }

    public void setDefault(){
        etTitle = (EditText) findViewById(R.id.et_title);
        etDescription = (EditText) findViewById(R.id.et_description);
        txtDate = (TextView) findViewById(R.id.et_date);
        txtTime = (TextView) findViewById(R.id.et_time);
        ivSave = (ImageView) findViewById(R.id.et_save);
        ivRecord = (ImageView) findViewById(R.id.iv_record);
        tvRecord = (TextView) findViewById(R.id.et_record);
        //current date time

        txtDate.setText(getCurrentDate(TimeType.DATE));
        txtTime.setText(getCurrentDate(TimeType.TIME));
    }

    public enum TimeType{
        DATE,
        TIME
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
                //TODO : valid input

                //add remind to sqlite
                DatabaseContext db = DatabaseApplication.getInstance().getDatabaseContext();
                db.add(new RemindInfo(etTitle.getText().toString(), etDescription.getText().toString(),
                        txtDate.getText().toString(), txtTime.getText().toString()));
            }
        });
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordActivity();
            }
        });
        tvRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecordActivity();
            }
        });

    }

    public void showRecordActivity(){
        Intent intent = new Intent(AddRemindActivity.this, RecordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void showDatePickerDialog(){
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                myDatePicker = new MyDate(year, month , date);
                txtDate.setText(myDatePicker.getDate());
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(AddRemindActivity.this, callback, myDatePicker.getYear(), myDatePicker.getMonth(), myDatePicker.getMyDate());
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
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddRemindActivity.this, callback, myTimePicker.getHour(), myTimePicker.getMin(), true);
        timePickerDialog.setTitle("Choose your time");
        timePickerDialog.show();
    }


}

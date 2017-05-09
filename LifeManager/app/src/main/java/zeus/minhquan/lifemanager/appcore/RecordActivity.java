package zeus.minhquan.lifemanager.appcore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import zeus.minhquan.lifemanager.MyListener;
import zeus.minhquan.lifemanager.R;
import zeus.minhquan.lifemanager.adapters.RecordAdapter;
import zeus.minhquan.lifemanager.animation.GifImageView;
import zeus.minhquan.lifemanager.controllerRemind.AddRemindActivity;
import zeus.minhquan.lifemanager.controllerRemind.UpdateRemind;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordActivity extends AppCompatActivity implements MyListener {

    private static final String TAG = "RecordActivity";
    private static final String RANDOM_CHARACTER = "ABCDEFGHIKLMNOPQRSTUVWXYZ";
    private static final int REQUEST_PERMISSION_CODE = 1;
    private static final int MAX_OF_2_NUMBER = 99;
    private static final int MAX_OF_SECOND_IN_MIN = 59;
    private static final int MAX_TIME_RECORD = 5;//min
    private static final int WAIT_TO_LOAD = 200;
    private ImageView ivRecord;
    private MediaRecorder mediaRecorder;
    private String outputFile = null;
    private Random random;
    private boolean isStartRecording;
    private boolean isRecording;
    private boolean isPlayRecord;
    private boolean isPlaying;
    private ArrayList<FileRecord> fileRecords;
    private String outputPath;
    private String outputName;
    private TextView tvTimeRecord;
    private Timer timer;
    private ListView records;
    private MediaPlayer mediaRecordPlayer;
    private boolean isChooseRecord;
    private ImageView ivShowRecord;
    private ConstraintLayout constraintLayout;
    private ConstraintSet applyConstraintSet;
    private ConstraintSet resetConstraintSet;
    private boolean isSlideUp;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private Timer timerShow;
    private ImageView ivPlay;
    private ImageView ivStop;
    private FileRecord playRecord;
    private ImageView ivSave;
    private boolean isSave;
    private RecordAdapter recordAdapter;
    private ImageView ivRecordDisk;
    private boolean isRotate;
    private Timer animationDisk;
    private TextView tvText;
    private ImageView ivGuide;
    private GifImageView ivAnim;
    private ImageView ivBack;
    private TextView tvRecordHide;
    private EditText tvInputRecord;
    private boolean isDuplicate;

    public void setDefault(){
        ivRecord = (ImageView) findViewById(R.id.iv_start_record);
        tvTimeRecord = (TextView) findViewById(R.id.tv_time_record);
        records = (ListView) findViewById(R.id.lv_record);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivShowRecord = (ImageView) findViewById(R.id.iv_show_record);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_main);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivStop = (ImageView) findViewById(R.id.iv_stop);
        ivSave = (ImageView) findViewById(R.id.iv_save);
        tvText = (TextView) findViewById(R.id.tv_text);
        tvInputRecord = (EditText) findViewById(R.id.et_input_rc);
        ivGuide = (ImageView) findViewById(R.id.iv_guide);
        ivAnim = (GifImageView) findViewById(R.id.iv_anim);
        tvRecordHide = (TextView) findViewById(R.id.tv_text);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        SCREEN_WIDTH = displayMetrics.widthPixels;
        SCREEN_HEIGHT = displayMetrics.heightPixels;
        random = new Random();
        applyConstraintSet = new ConstraintSet();
        resetConstraintSet = new ConstraintSet();
        applyConstraintSet.clone(constraintLayout);
        resetConstraintSet.clone(constraintLayout);
        isSlideUp = false;
        isStartRecording = true;
        isRecording = true;
        isPlayRecord = true;
        isPlaying = false;
        isChooseRecord = false;
        isSave = false;
        isRotate = true;
        isDuplicate = false;
    }

    @Override
    public void emptyClick() {
        emptyRecord();
    }

    enum TypeSlide{
        UP,
        DOWN
    }

    public void onSlide(int height, TypeSlide typeSlide) {
        TransitionManager.beginDelayedTransition(constraintLayout);
        switch (typeSlide){
            case UP:
                applyConstraintSet.constrainHeight(R.id.lv_record, height);
                applyConstraintSet.constrainHeight(R.id.iv_start_record, 0);
                applyConstraintSet.constrainWidth(R.id.iv_start_record, 0);
                applyConstraintSet.constrainHeight(R.id.tv_time_record, 0);
                applyConstraintSet.constrainWidth(R.id.tv_time_record, 0);
                applyConstraintSet.constrainHeight(R.id.et_input_rc, 0);
                applyConstraintSet.constrainWidth(R.id.et_input_rc, 0);
                applyConstraintSet.constrainHeight(R.id.iv_guide, 0);
                applyConstraintSet.constrainWidth(R.id.iv_guide, 0);
                applyConstraintSet.constrainHeight(R.id.tv_text, 0);
                applyConstraintSet.constrainWidth(R.id.tv_text, 0);
                applyConstraintSet.constrainHeight(R.id.iv_play, 120);
                applyConstraintSet.constrainWidth(R.id.iv_play, 120);
                applyConstraintSet.constrainHeight(R.id.iv_stop, 80);
                applyConstraintSet.constrainWidth(R.id.iv_stop, 80);
                InputMethodManager inputMethodManager =
                        (InputMethodManager) RecordActivity.this.getSystemService(
                                Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(
                        RecordActivity.this.getCurrentFocus().getWindowToken(), 0);
                break;
            case DOWN:
                if(height <= 50){
                    height = 0;
                    ivStop.setImageResource(R.drawable.pre_stop);
                    ivPlay.setImageResource(R.drawable.play_record);
                    ivStop.setEnabled(false);
                    ivPlay.setEnabled(true);
                }
                applyConstraintSet.constrainHeight(R.id.lv_record, height);
                applyConstraintSet.constrainHeight(R.id.iv_start_record, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainWidth(R.id.iv_start_record, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainHeight(R.id.tv_time_record, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainWidth(R.id.et_input_rc, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainHeight(R.id.et_input_rc, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainWidth(R.id.tv_time_record, 0);
                applyConstraintSet.constrainWidth(R.id.iv_guide, 37);
                applyConstraintSet.constrainHeight(R.id.tv_text, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainHeight(R.id.iv_play, 0);
                applyConstraintSet.constrainWidth(R.id.iv_play, 0);
                applyConstraintSet.constrainHeight(R.id.iv_stop, 0);
                applyConstraintSet.constrainWidth(R.id.iv_stop, 0);
                break;
        }
        applyConstraintSet.applyTo(constraintLayout);
    }

    public void onResetClick(View view) {

    }

    public void enableSlide(TypeSlide typeSlide){
        switch (typeSlide){
            case DOWN:
                ivPlay.setImageResource(R.drawable.pre_play);
                ivStop.setImageResource(R.drawable.stop_record);
                ivPlay.setEnabled(false);
                ivStop.setEnabled(true);
                break;

            case UP:
                ivPlay.setImageResource(R.drawable.play_record);
                ivStop.setImageResource(R.drawable.pre_stop);
                ivPlay.setEnabled(true);
                ivStop.setEnabled(false);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
//        getLayoutInflater().inflate(R.layout.activity_record, frameLayout);
//        navigation.getMenu().getItem(1).setChecked(true);
        setDefault();
        ivSave.setEnabled(false);
        ivStop.setEnabled(false);
        ivShowRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStartRecording) {
                    if (ivShowRecord.getY() >= SCREEN_HEIGHT / 2) {
                        timerShow = new Timer();
                        timerShow.schedule(new TimerTask() {
                            private int count = 0;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (count < SCREEN_HEIGHT / 2) {
                                            count += 50;
                                            onSlide(count, TypeSlide.UP);
                                        } else {
                                            timerShow.cancel();
                                            timerShow.purge();
                                            ivShowRecord.setImageResource(R.drawable.slide_down);
                                        }
                                    }
                                });
                            }
                        }, 0, 5);
                    } else {
                        timerShow = new Timer();
                        timerShow.schedule(new TimerTask() {
                            private int count = SCREEN_HEIGHT / 2;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (count > 0) {
                                            count -= 50;
                                            onSlide(count, TypeSlide.DOWN);
                                        } else {
                                            ivSave.setImageResource(R.drawable.ic_pre_black_24dp);
                                            isSave = true;
                                            timerShow.cancel();
                                            timerShow.purge();
                                            ivShowRecord.setImageResource(R.drawable.slide_up);
                                            ivSave.setEnabled(false);
                                        }
                                    }
                                });
                            }
                        }, 0, 5);
                    }
                } else {
                    Toast.makeText(RecordActivity.this, "You must stop the record to show list records",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(checkPermission()) {
            fileRecords = getFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
            loadAllRecord();
        }
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvInputRecord.getText().toString().equals("")) {
                    Toast.makeText(RecordActivity.this, R.string.no_name_record,Toast.LENGTH_SHORT).show();
                }else if(tvInputRecord.getText().toString().length() < 4){
                    Toast.makeText(RecordActivity.this, R.string.too_short_name_record,Toast.LENGTH_SHORT).show();
                } else if(tvInputRecord.getText().toString().length() > 16){
                    Toast.makeText(RecordActivity.this, R.string.too_long_name_record,Toast.LENGTH_SHORT).show();
                } else {
                    isDuplicate = false;
                    if (isStartRecording) {
                        if(fileRecords != null) {
                            for (FileRecord fileRecord : fileRecords) {
                                if (fileRecord.getFileName().equals(tvInputRecord.getText().toString() + "BNN.3gp")) {
                                    isDuplicate = true;
                                    break;
                                }
                            }
                        }
                        if(isDuplicate){
                            Toast.makeText(RecordActivity.this, R.string.duplicate_record_name,Toast.LENGTH_SHORT).show();
                        } else {
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager) RecordActivity.this.getSystemService(
                                            Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(
                                    RecordActivity.this.getCurrentFocus().getWindowToken(), 0);
                            tvRecordHide.setText(R.string.click_to_stop);
                            startRecord();
                            tvInputRecord.setEnabled(false);
                        }
                    } else {
                        if (mediaRecorder != null) {
                            ivRecord.setImageResource(R.drawable.start_recording);
                            ivAnim.setWillNotDraw(true);
                            isStartRecording = true;
                            isRecording = false;
                            try {
                                mediaRecorder.stop();
                                tvInputRecord.setEnabled(true);
                                tvRecordHide.setText(R.string.click_to_record);
                                Log.d(TAG,"Debug output : "+outputPath+" ||| " + outputName);
                                fileRecords = getFiles(outputPath);
                                timer.cancel();
                                loadAllRecord();
                            } catch (Exception e) {

                            }
                            Toast.makeText(RecordActivity.this, R.string.record_complete,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                ivSave.setImageResource(R.drawable.ic_done_black_24dp);
                ivSave.setEnabled(true);
                isSave = true;
                playRecord = (FileRecord) (parent.getItemAtPosition(position));
                if(records.getAdapter().getCount() == 0){
                    Toast.makeText(RecordActivity.this, R.string.no_record,Toast.LENGTH_SHORT).show();
                    applyConstraintSet.constrainWidth(R.id.iv_no_record,ConstraintSet.WRAP_CONTENT);
                    applyConstraintSet.constrainHeight(R.id.iv_no_record, ConstraintSet.WRAP_CONTENT);
                }
                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ivStop.setEnabled(true);
                        ivPlay.setImageResource(R.drawable.pre_play);
                        ivPlay.setEnabled(false);
                        ivStop.setImageResource(R.drawable.stop_record);
                        Log.d(TAG,"fking record "+playRecord.getFilePath());
                        Log.d(TAG,"position "+position);
                        playRecord(playRecord.getFilePath());
//                        animationDisk = new Timer();
//                        animationDisk.schedule(new TimerTask() {
//                            @Override
//                            public void run() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        RotateAnimation rotate = new RotateAnimation(0, 360,
//                                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
//                                                0.5f);
//                                        rotate.setDuration(4000);
//                                        rotate.setRepeatCount(Animation.INFINITE);
//                                        if(isRotate) {
//                                            ivRecordDisk.setAnimation(rotate);
//                                        } else {
//                                            animationDisk.cancel();
//                                            animationDisk.purge();
//                                            isRotate = true;
//                                        }
//                                    }
//                                });
//                            }
//                        },0
                    }
                });
                ivStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopRecord();
                    }
                });
            }
        });
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSave){
                    Toast.makeText(RecordActivity.this,"Please choose record", Toast.LENGTH_SHORT).show();
                } else {
                    if(isPlayRecord) stopRecord();
                    Log.d("Page" , sendDataToResume("page"));
                    if(sendDataToResume("page")!= null && sendDataToResume("page") != "" ) {
                        Log.d("Check click" , "da click roi nhe");
                        Intent intent = new Intent(RecordActivity.this, UpdateRemind.class);
                        intent.putExtra("title",sendDataToResume("title"));
                        intent.putExtra("description",sendDataToResume("description"));
                        intent.putExtra("date",sendDataToResume("date"));
                        intent.putExtra("time",sendDataToResume("time"));
                        intent.putExtra("record_path", playRecord.getFilePath());
                        intent.putExtra("record_name", playRecord.getFileName());
                        intent.putExtra("idFromRecord", sendDataToResume("idFromRecord"));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(RecordActivity.this, AddRemindActivity.class);
                        intent.putExtra("title",sendDataToResume("title"));
                        intent.putExtra("description",sendDataToResume("description"));
                        intent.putExtra("date",sendDataToResume("date"));
                        intent.putExtra("time",sendDataToResume("time"));
                        intent.putExtra("record_path", playRecord.getFilePath());
                        intent.putExtra("record_name", playRecord.getFileName());
                        startActivity(intent);
                    }

                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sendDataToResume("title").equals("") && sendDataToResume("description").equals("")){

                    RecordActivity.super.onBackPressed();
                } else if(sendDataToResume("page")!= null && sendDataToResume("page") != "" ) {
                    Intent intent = new Intent(RecordActivity.this, UpdateRemind.class);
                    intent.putExtra("title",sendDataToResume("title"));
                    intent.putExtra("description",sendDataToResume("description"));
                    intent.putExtra("date",sendDataToResume("date"));
                    intent.putExtra("time",sendDataToResume("time"));
                    intent.putExtra("record_path", "");
                    intent.putExtra("record_name", "");
                    intent.putExtra("idFromRecord", sendDataToResume("idFromRecord"));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(RecordActivity.this, AddRemindActivity.class);
                    intent.putExtra("title",sendDataToResume("title"));
                    intent.putExtra("description",sendDataToResume("description"));
                    intent.putExtra("date",sendDataToResume("date"));
                    intent.putExtra("time",sendDataToResume("time"));
                    intent.putExtra("record_path", "");
                    intent.putExtra("record_name", "");
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(sendDataToResume("title").equals("") && sendDataToResume("description").equals("")){

            RecordActivity.super.onBackPressed();
        } else if(sendDataToResume("page")!= null && sendDataToResume("page") != "" ) {
            Intent intent = new Intent(RecordActivity.this, UpdateRemind.class);
            intent.putExtra("title",sendDataToResume("title"));
            intent.putExtra("description",sendDataToResume("description"));
            intent.putExtra("date",sendDataToResume("date"));
            intent.putExtra("time",sendDataToResume("time"));
            intent.putExtra("record_path", "");
            intent.putExtra("record_name", "");
            intent.putExtra("idFromRecord", sendDataToResume("idFromRecord"));
            startActivity(intent);
        } else {
            Intent intent = new Intent(RecordActivity.this, AddRemindActivity.class);
            intent.putExtra("title",sendDataToResume("title"));
            intent.putExtra("description",sendDataToResume("description"));
            intent.putExtra("date",sendDataToResume("date"));
            intent.putExtra("time",sendDataToResume("time"));
            intent.putExtra("record_path", "");
            intent.putExtra("record_name", "");
            startActivity(intent);
        }
    }

    public void stopRecord(){
        if(mediaRecordPlayer != null){
            ivPlay.setEnabled(true);
            ivStop.setEnabled(false);
            ivPlay.setImageResource(R.drawable.play_record);
            ivStop.setImageResource(R.drawable.pre_stop);
            isRotate = false;
            isPlayRecord = false;
            mediaRecordPlayer.stop();
            mediaRecordPlayer.release();
        } else {
            return;
        }
    }

    public String sendDataToResume(String data){
        if(getIntent().hasExtra(data)){
            return getIntent().getStringExtra(data);
        } else return "";
    }

    public void startRecord() {
        if (checkPermission()) {
            outputPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            outputName = tvInputRecord.getText().toString() + "BNN.3gp";
            outputFile = outputPath + "/" + outputName;
            readyToRecord();
            isStartRecording = false;
            isRecording = true;
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                ivAnim.setWillNotDraw(false);
                ivAnim.setGifImageResource(R.drawable.anim_record);
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    private int count = 0;
                    private int second = 0;
                    private int minute = 0;
                    private String ticks;
                    private String seconds;
                    private String minutes;

                    public String defaultDisplay(int number) {
                        if (number < 10) {
                            return "0" + number;
                        }
                        else return "" + number;
                    }

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    count++;
                                    ticks = defaultDisplay(count);
                                    if (count >= MAX_OF_2_NUMBER) {
                                        count = 0;
                                        second++;
                                        if (second > MAX_OF_SECOND_IN_MIN) {
                                            second = 0;
                                            minute++;
                                            if (minute == MAX_TIME_RECORD) {
                                                cancel();
                                            }
                                        }
                                    }
                                    seconds = defaultDisplay(second);
                                    minutes = defaultDisplay(minute);
                                    tvTimeRecord.setText(minutes + " : " + seconds + " : " + ticks);
                                }
                            });
                        }
                    }, 0, 10);
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(RecordActivity.this, "Recording error",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(RecordActivity.this, "Recording error",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception ex){
                    ex.printStackTrace();
            }
                Toast.makeText(RecordActivity.this, R.string.start_record,
                        Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
    }

    public void emptyRecord(){
        applyConstraintSet.constrainWidth(R.id.iv_no_record,ConstraintSet.WRAP_CONTENT);
        applyConstraintSet.constrainHeight(R.id.iv_no_record, ConstraintSet.WRAP_CONTENT);
    }

    public void loadAllRecord(){
        if(fileRecords != null && fileRecords.size() != 0) {
            applyConstraintSet.constrainWidth(R.id.iv_no_record,0);
            applyConstraintSet.constrainHeight(R.id.iv_no_record, 0);
            RecordAdapter recordAdapter = new RecordAdapter(RecordActivity.this, fileRecords, RecordActivity.this);
            records.setAdapter(recordAdapter);
            records.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        } else if(fileRecords.size() == 0){
            Toast.makeText(RecordActivity.this, R.string.no_record,Toast.LENGTH_SHORT).show();
            applyConstraintSet.constrainWidth(R.id.iv_no_record,ConstraintSet.WRAP_CONTENT);
            applyConstraintSet.constrainHeight(R.id.iv_no_record, ConstraintSet.WRAP_CONTENT);
        }
    }

    public void playRecord(String recordPath){
//        if (!isRecording) {
//            Toast.makeText(RecordActivity.this, "Please stop recorder to play",
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
            isPlayRecord = true;
            AudioManager amanager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = amanager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            amanager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
            mediaRecordPlayer = new MediaPlayer();
            mediaRecordPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            try {
                mediaRecordPlayer.setDataSource(recordPath);
                mediaRecordPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaRecordPlayer.start();
            Toast.makeText(RecordActivity.this, "Recording Playing",
                    Toast.LENGTH_LONG).show();
            if(mediaRecordPlayer != null) {
            mediaRecordPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    enableSlide(TypeSlide.UP);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(RecordActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RecordActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(RecordActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }
    
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RANDOM_CHARACTER.
                    charAt(random.nextInt(RANDOM_CHARACTER.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    public void readyToRecord(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);
    }

    public class FileRecord{
        private String filePath;
        private String fileName;

        public FileRecord(String filePath, String fileName) {
            this.filePath = filePath;
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public ArrayList<FileRecord> getFiles(String direct){
        ArrayList<FileRecord> myRecords = new ArrayList<>();
        File f = new File(direct);
        f.mkdirs();
        File[] files = f.listFiles();
        if (files.length == 0) {
            return null;
        } else {
            for (File file : files) {
                if(file.getAbsolutePath().endsWith("BNN.3gp")) {
                    myRecords.add(new FileRecord(file.getAbsolutePath(), file.getName()));
                }
            }
        }
        return myRecords;
    }
}

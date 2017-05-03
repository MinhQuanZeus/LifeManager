package zeus.minhquan.lifemanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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

import zeus.minhquan.lifemanager.adapters.RecordAdapter;
import zeus.minhquan.lifemanager.controllerRemind.AddRemindActivity;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordActivity extends AppCompatActivity {

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

    public void setDefault(){
        ivRecord = (ImageView) findViewById(R.id.iv_start_record);
        tvTimeRecord = (TextView) findViewById(R.id.tv_time_record);
        records = (ListView) findViewById(R.id.lv_record);
        ivShowRecord = (ImageView) findViewById(R.id.iv_show_record);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraint_main);
        ivPlay = (ImageView) findViewById(R.id.iv_play);
        ivStop = (ImageView) findViewById(R.id.iv_stop);
        ivSave = (ImageView) findViewById(R.id.iv_save);
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
                applyConstraintSet.constrainHeight(R.id.iv_play, 120);
                applyConstraintSet.constrainWidth(R.id.iv_play, 120);
                applyConstraintSet.constrainHeight(R.id.iv_stop, 80);
                applyConstraintSet.constrainWidth(R.id.iv_stop, 80);
                break;

            case DOWN:
                if(height <= 50){
                    height = 0;
                }
                applyConstraintSet.constrainHeight(R.id.lv_record, height);
                applyConstraintSet.constrainHeight(R.id.iv_start_record, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainWidth(R.id.iv_start_record, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainHeight(R.id.tv_time_record, ConstraintSet.WRAP_CONTENT);
                applyConstraintSet.constrainWidth(R.id.tv_time_record, ConstraintSet.WRAP_CONTENT);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setDefault();
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
                                            Log.d(TAG, "FkingCount" + count);
                                        } else {
                                            ivSave.setImageResource(R.drawable.save_record_pre);
                                            isSave = true;
                                            timerShow.cancel();
                                            timerShow.purge();
                                            ivShowRecord.setImageResource(R.drawable.slide_up);
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
                if (isStartRecording) {
                    startRecord();
                } else {
                    if (mediaRecorder != null) {
                        ivRecord.setImageResource(R.drawable.start_recording);
                        isStartRecording = true;
                        isRecording = false;
                        try {
                            mediaRecorder.stop();
                            fileRecords.add(new FileRecord(outputPath, outputName));
                            timer.cancel();
                            loadAllRecord();
                        }catch (Exception e){

                        }
                        Toast.makeText(RecordActivity.this, "Recording Completed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ivSave.setImageResource(R.drawable.save_record);
                isSave = true;
                playRecord = (FileRecord) (parent.getItemAtPosition(position));
                ivPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playRecord(playRecord.getFilePath());
                    }
                });
                ivStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(mediaRecordPlayer != null){
                            mediaRecordPlayer.stop();
                            mediaRecordPlayer.release();
                        } else {
                            return;
                        }
                    }
                });
//                    FileRecord fileRecord = (FileRecord) (parent.getItemAtPosition(position));
//                    playRecord(fileRecord.getFilePath());
            }
        });
        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isSave){
                    Toast.makeText(RecordActivity.this,"Please choose record", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RecordActivity.this, AddRemindActivity.class);
                    intent.putExtra("title",sendDataToResume("title"));
                    intent.putExtra("description",sendDataToResume("description"));
                    String date = sendDataToResume("date");
                    Log.d(TAG,""+date);
                    intent.putExtra("date",sendDataToResume("date"));
                    intent.putExtra("time",sendDataToResume("time"));
                    intent.putExtra("record", playRecord.getFilePath());
                    startActivity(intent);
                }
            }
        });
    }

    public String sendDataToResume(String data){
        if(getIntent().hasExtra(data)){
            return getIntent().getStringExtra(data);
        } else return "";
    }

    public void startRecord() {
        if (checkPermission()) {
            outputPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            outputName = CreateRandomAudioFileName(5) + "Banana.3gp";
            outputFile = outputPath + "/" + outputName;
            Log.d(TAG, "file location : " + Environment.getExternalStorageDirectory().getAbsolutePath());
            readyToRecord();
            isStartRecording = false;
            isRecording = true;
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
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
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(RecordActivity.this, "Recording error",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                Toast.makeText(RecordActivity.this, "Recording started",
                        Toast.LENGTH_LONG).show();
        } else {
            requestPermission();
        }
    }

    public void loadAllRecord(){
        if(fileRecords != null) {
            RecordAdapter recordAdapter = new RecordAdapter(RecordActivity.this, fileRecords);
            records.setAdapter(recordAdapter);
            records.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    public void playRecord(String recordPath){
//        if (!isRecording) {
//            Toast.makeText(RecordActivity.this, "Please stop recorder to play",
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
        if (isPlayRecord) {
            mediaRecordPlayer = new MediaPlayer();
            try {

                mediaRecordPlayer.setDataSource(recordPath);
                mediaRecordPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlayRecord = false;
            mediaRecordPlayer.start();
            Toast.makeText(RecordActivity.this, "Recording Playing",
                    Toast.LENGTH_LONG).show();
        } else {
            if (mediaRecordPlayer != null) {
                isPlayRecord = true;
                mediaRecordPlayer.stop();
                mediaRecordPlayer.release();
                readyToRecord();
            }
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
                Log.d(TAG, "FKING FILE : " + file.getAbsolutePath());
                if(file.getAbsolutePath().endsWith(".3gp")) {
                    myRecords.add(new FileRecord(file.getAbsolutePath(), file.getName()));
                }
            }
        }
        return myRecords;
    }
}

package zeus.minhquan.lifemanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import zeus.minhquan.lifemanager.model.RemindInfo;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RecordActivity extends AppCompatActivity {

    private static final String TAG = "RecordActivity";
    private static final String RANDOM_CHARACTER = "ABCDEFGHIKLMNOPQRSTUVWXYZ";
    private static final int REQUEST_PERMISSION_CODE = 1;
    private ImageView ivRecord;
    private MediaRecorder mediaRecorder;
    private String outputFile = null;
    private Random random;
    private boolean isStartRecording;
    private boolean isRecording;
    private ArrayList<FileRecord> fileRecords;
    private String outputPath;
    private String outputName;

    public void setDefault(){
        ivRecord = (ImageView) findViewById(R.id.iv_start_record);
        random = new Random();
        isStartRecording = true;
        isRecording = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        setDefault();
        fileRecords = getFiles(Environment.getExternalStorageDirectory().getAbsolutePath());
        ivRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStartRecording) {
                    if (checkPermission()) {
                        outputPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        outputName = CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                        outputFile = outputPath + "/" + outputName;
                        Log.d(TAG,"file location : " + Environment.getExternalStorageDirectory().getAbsolutePath());
                        readyToRecord();
                        isStartRecording = false;
                        isRecording = true;

                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();

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
                        ivRecord.setImageResource(R.drawable.stop_recording);
                        Toast.makeText(RecordActivity.this, "Recording started",
                                Toast.LENGTH_LONG).show();
                    } else {
                        requestPermission();
                    }
                } else {
                    if (mediaRecorder != null) {
                        ivRecord.setImageResource(R.drawable.start_recording);
                        isStartRecording = true;
                        isRecording = false;
                        try {
                            mediaRecorder.stop();
                            fileRecords.add(new FileRecord(outputPath, outputName));
                        }catch (Exception e){

                        }
                        Toast.makeText(RecordActivity.this, "Recording Completed",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
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

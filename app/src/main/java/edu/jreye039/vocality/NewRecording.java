package edu.jreye039.vocality;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewRecording extends AppCompatActivity implements MediaPlayer.OnPreparedListener,AsyncResponse {

    Button startRecordBtn, stopRecordBtn, startPlayBtn, stopPlayBtn, uploadBtn;
    //the audio file's path
    String pathSave = "";
    MediaRecorder mediaRecorder;
    //to check whether the recorder is in its recording state
    boolean recording = false;
    MediaPlayer mediaPlayerRecording;
    MediaPlayer mediaPlayerAccompaniment;
    //to check whether the two MediaPlayers are both ready
    boolean bothPrepared = false;
    boolean flag = true;

    //the AWS S3 link where the backing track is stored
    String s3_key;

    //temp file/directory to store the S3 object
    File outputDir;
    File tempFile;

    private LyricProcess mLrcProcess;
    private List<LyricInfo> lrcList = new ArrayList<>();
    private int index = 0;

    final int REQUEST_PERMISSION_CODE = 1000;
    private LyricView lyricView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recording);

        //get the S3 link
        s3_key = getIntent().getStringExtra("AWS_S3_KEY");

        //create the temp file
        outputDir = this.getCacheDir();
        try {
            tempFile = File.createTempFile("temp", ".3gp", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //download the file
        credentialsProvider();

        setTransferUtility();

        downloadFileFromS3();

        //check if the user has allowed Vocality to access their storage/mic
        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

        lyricView = (LyricView) findViewById(R.id.lrcShowView);
        startPlayBtn = (Button) findViewById(R.id.startPlayBtn);
        stopPlayBtn = (Button) findViewById(R.id.stopPlayBtn);
        startRecordBtn = (Button) findViewById(R.id.startRecordBtn);
        stopRecordBtn = (Button) findViewById(R.id.stopRecordBtn);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);

        stopRecordBtn.setEnabled(false);
        startPlayBtn.setEnabled(false);
        stopPlayBtn.setEnabled(false);
        uploadBtn.setEnabled(false);

        startRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if device has given read/write permission to Vocality
                if (checkPermissionFromDevice()) {
                    //AWS S3 key
                    String fileID = UUID.randomUUID().toString();
                    pathSave = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileID + "_vocal_recording.3gp";

                    stopRecordBtn.setEnabled(true);
                    startPlayBtn.setEnabled(false);
                    startRecordBtn.setEnabled(false);
                    stopPlayBtn.setEnabled(false);
                    uploadBtn.setEnabled(false);

                    //setup the media recorder
                    setupMediaRecorder();
                    try {
                        mediaRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //setup the media player
                    mediaPlayerAccompaniment = new MediaPlayer();
                    try {
                        mediaPlayerAccompaniment.setDataSource(tempFile.toString());
                        mediaPlayerAccompaniment.prepareAsync();//Mayhaps async?
                        mediaPlayerAccompaniment.start();
                        mediaPlayerAccompaniment.pause();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayerAccompaniment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mediaPlayerAccompaniment.start();
                            initLrc(s3_key.substring(0,s3_key.length()-4));
                            handler.post(mRunnable);
                            //to set up a delay so that the audio correctly syncs up
                            final Handler handler5 = new Handler();
                            handler5.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recording = true;
                                    mediaRecorder.start();
                                }
                            }, 125); //a larger number will position the voice in front of the accompaniment 125
                        }
                    });

                    //set the newly recorded file to be uploaded
                    s3Upload = new File(pathSave);
                    fileKey = fileID + "_vocal_recording.3gp";

                    Toast.makeText(NewRecording.this, "Recording...", Toast.LENGTH_SHORT).show();
                }
                //ask the user for permission for read/write and mic access
                else {
                    requestPermission();
                }
            }
        });

        stopRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) {
                    mediaRecorder.stop();
                    recording = false;
                }
                handler.removeCallbacks(mRunnable);
                if (mediaPlayerAccompaniment != null) {
                    mediaPlayerAccompaniment.stop();
                    mediaPlayerAccompaniment.release();
                    setupMediaRecorder();
                }
                stopRecordBtn.setEnabled(false);
                startPlayBtn.setEnabled(true);
                startRecordBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                uploadBtn.setEnabled(true);
            }
        });

        startPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecordBtn.setEnabled(false);
                stopRecordBtn.setEnabled(false);
                startPlayBtn.setEnabled(false);
                stopPlayBtn.setEnabled(true);
                uploadBtn.setEnabled(false);

                //set up the user's audio
                mediaPlayerRecording = new MediaPlayer();
                try {
                    mediaPlayerRecording.setDataSource(pathSave);
                    mediaPlayerRecording.prepareAsync();

                    mediaPlayerRecording.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (!bothPrepared) {
                                bothPrepared = true;
                            } else if (bothPrepared) {
                                mediaPlayerAccompaniment.start();
                                mediaPlayerRecording.start();
                                bothPrepared = false;
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //set up the accompaniment's audio
                mediaPlayerAccompaniment = new MediaPlayer();
                try {
                    mediaPlayerAccompaniment.setDataSource(tempFile.toString());
                    mediaPlayerAccompaniment.prepareAsync();

                    mediaPlayerAccompaniment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (!bothPrepared) {
                                bothPrepared = true;
                            } else if (bothPrepared) {
                                mediaPlayerRecording.start();
                                mediaPlayerAccompaniment.start();
                                bothPrepared = false;
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(NewRecording.this, "Playing...", Toast.LENGTH_SHORT).show();
            }
        });

        stopPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecordBtn.setEnabled(false);
                startRecordBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                startPlayBtn.setEnabled(true);
                uploadBtn.setEnabled(true);

                if (mediaPlayerRecording != null) {
                    mediaPlayerRecording.stop();
                    mediaPlayerRecording.release();
                    setupMediaRecorder();
                }

                if (mediaPlayerAccompaniment != null) {
                    mediaPlayerAccompaniment.stop();
                    mediaPlayerAccompaniment.release();
                    setupMediaRecorder();
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s3Upload == null || !s3Upload.exists()) {
                    Log.d("AWS S3 UPLOAD", "FILE NOT FOUND");
                } else {
                    //upload the file to AWS S3
                    credentialsProvider();
                    setTransferUtility();
                    uploadFileToS3();

                    //get the user's username
                    SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    String username = userInfo.getString("username", "");

                    //attach the recording to the user
                    String title = s3_key.substring(0, s3_key.length() - 4);
                    NewRecordingBackgroundWorker backgroundWorker = new NewRecordingBackgroundWorker(getApplicationContext());
                    backgroundWorker.execute(username, title, fileKey, s3_key);

                    //take the user to the main feed
                    finish();
                }
            }
        });
//        new Thread(new runable()).start();
    }

//    class runable implements Runnable{
//        @Override
//        public void run() {
//            while(true){
//                try{
//                    Thread.sleep(100);
//                    if(mediaPlayerAccompaniment.isPlaying()){
//                        lyricView.setText(lrcIndex());
//                        mhandler.post(mUpdateResults);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    Handler mhandler = new Handler();
//    Runnable mUpdateResults = new Runnable() {
//        @Override
//        public void run() {
//            lyricView.invalidate();
//        }
//    };

    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(pathSave);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    private void requestPermission() {
        //TODO might not need to read external storage, maybe take out
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission Denied: Please allow Vocality to access the device's microphone/storage", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    //AWS S3
    AmazonS3Client s3;
    TransferUtility transferUtility;
    File s3Upload;
    String fileKey;

    public void credentialsProvider() {
        //initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-west-2:af04cf4e-54ef-4854-97aa-8613ba65e9a9", //identity pool ID
                Regions.US_WEST_2 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider) {
        //create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        //set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));

    }

    public void setTransferUtility() {

        transferUtility = new TransferUtility(s3, getApplicationContext());
    }

    public void uploadFileToS3() {

        TransferObserver transferObserver = transferUtility.upload(
                "vocality", /* The bucket to upload to */
                fileKey,           /* The file's name on S3 */
                s3Upload           /* The file to be uploaded's local location */
        );
    }

    public void downloadFileFromS3() {

        TransferObserver transferObserver = transferUtility.download(
                "vocality", /* The bucket to download from */
                s3_key,            /* The key for the object to download */
                tempFile           /* The file to download the object to */
        );
    }

    public void initLrc(String songname) {
        //读取歌词文件
        InitLyricBackgroundWorker lyricBackgroundWorker = new InitLyricBackgroundWorker(getApplicationContext());
        lyricBackgroundWorker.delegate = this;
        lyricBackgroundWorker.execute(songname);
        //传回处理后的歌词文件
    }

    Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            lyricView.setIndex(lrcIndex());
            lyricView.invalidate();
            handler.postDelayed(mRunnable, 100);
        }
    };

    public int lrcIndex() {
        int currentTime = 0;
        int duration = 0;
        int index = 0;
        int tempIndex = 0;
        if(mediaPlayerAccompaniment.isPlaying()) {
            currentTime = mediaPlayerAccompaniment.getCurrentPosition();
            duration = mediaPlayerAccompaniment.getDuration();
        }
        if(currentTime < duration) {
            for (int i = 0; i < lrcList.size(); i++) {
                LyricInfo temp = lrcList.get(i);
                if(temp.getLrcTime()<currentTime){
                    ++index;
                }
            }
            tempIndex = index -1;
            if(tempIndex < 0) tempIndex = 0;
        }
        return tempIndex;
    }

    @Override
    public void processFinish(List<LyricInfo> LrcList) {
        lyricView.setLrcList(LrcList);
        //切换带动画显示歌词
//        lyricView.setAnimation(AnimationUtils.loadAnimation(this,R.anim.alpha_z));
    }

}
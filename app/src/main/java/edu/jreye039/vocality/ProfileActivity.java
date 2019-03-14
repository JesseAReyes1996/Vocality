package edu.jreye039.vocality;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RESULT_LOAD_IMAGE = 1;

    ImageView imageToUpload, downloadedImage;
    Button bUploadImage, bDownloadImage;
    TextView nameTextView;
    private RecyclerView RecordRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        //downloadedImage = (ImageView) findViewById(R.id.downloadedImage);

        bUploadImage = (Button) findViewById(R.id.bUploadImage);
        //bDownloadImage = (Button) findViewById(R.id.bDownloadImage);
        nameTextView = (TextView) findViewById(R.id.nameTextView);

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
        //bDownloadImage.setOnClickListener(this);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        nameTextView.setText(username);

        RecordRecyclerView = (RecyclerView) findViewById(R.id.RecordRecyclerView);
        RecordRecyclerView.setHasFixedSize(true);

        RecordRecyclerView.setLayoutManager(new LinearLayoutManager(this));//getActivity().getApplicationContext()


        //display the recordings in the RecyclerView
        ProfileBackgroundWorker backgroundWorker = new ProfileBackgroundWorker(this, RecordRecyclerView);
        backgroundWorker.execute(username);

        s3_key = getIntent().getStringExtra("AWS_S3_KEY");

        //create the temp file
        outputDir = this.getFilesDir();
        try {
            tempFile = File.createTempFile("temp", ".jpg", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //download the file
        credentialsProvider();

        setTransferUtility();

        //downloadFileFromS3();

        //check if the user has allowed Vocality to access their storage/mic
        if (!checkPermissionFromDevice()) {
            requestPermission();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.bUploadImage:
                if(s3Upload == null || !s3Upload.exists()){
                    Log.d("AWS S3 UPLOAD", "FILE NOT FOUND");
                }
                else{
                    //get the user's username
                    SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    String username = userInfo.getString("username", "");

                    String fileID = UUID.randomUUID().toString();
                    fileKey = fileID + "_image.jpg";

                    //upload the file to AWS S3
                    credentialsProvider();
                    setTransferUtility();
                    uploadFileToS3();

                    //attach the image to the user
                    ImageBackgroundWorker backgroundWorker = new ImageBackgroundWorker(getApplicationContext());
                    backgroundWorker.execute(username, fileKey);
                    
                }
                //Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();

                break;
            //case R.id.bDownloadImage:

            //break;

            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            //set the image on the profile on the app
            imageToUpload.setImageURI(selectedImage);

            //send the picture to be uploaded to AWS S3
            String picturePath = getPath(this, selectedImage);
            s3Upload = new File(picturePath);
        }
    }

    public void onLogOut(View view) {
        //clear SharedPreferences
        SharedPreferences userInfo = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = userInfo.edit();
        edit.putString("username", "");
        edit.apply();
        finish();
    }

    public static String getPath(Context context, Uri uri){
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if(cursor.moveToFirst()){
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if(result == null){
            result = "not found";
        }
        return result;
    }

    //the AWS S3 link where the backing track is stored
    String s3_key;

    //temp file/directory to store the S3 object
    File outputDir;
    File tempFile;

    final int REQUEST_PERMISSION_CODE = 1000;

    private void requestPermission(){
        //TODO might not need to read external storage, maybe take out
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){}
                else{
                    Toast.makeText(this, "Permission Denied: Please allow Vocality to access the device's microphone/storage", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    //AWS S3
    AmazonS3Client s3;
    TransferUtility transferUtility;
    File s3Upload;
    String fileKey;

    public void credentialsProvider(){
        //initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-west-2:af04cf4e-54ef-4854-97aa-8613ba65e9a9", //identity pool ID
                Regions.US_WEST_2 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
        //create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        //set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));

    }

    public void setTransferUtility(){

        transferUtility = new TransferUtility(s3, getApplicationContext());
    }

    public void uploadFileToS3(){

        TransferObserver transferObserver = transferUtility.upload(
                "vocality", /* The bucket to upload to */
                fileKey,           /* The file's name on S3 */
                s3Upload           /* The file to be uploaded's local location */
        );
    }

    public void downloadFileFromS3(){

        TransferObserver transferObserver = transferUtility.download(
                "vocality", /* The bucket to download from */
                s3_key,            /* The key for the object to download */
                tempFile           /* The file to download the object to */
        );
    }

}


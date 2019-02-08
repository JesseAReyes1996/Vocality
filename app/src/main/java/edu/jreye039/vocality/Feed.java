package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //check if the is the user is logged in
        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        //if they aren't logged in, take them to the registration/login activity
        if(username.equals("")){
            Intent startIntent = new Intent(getApplicationContext(), NotLoggedIn.class);
            startActivity(startIntent);
        }

        //
        if(fileToUpload == null || !fileToUpload.exists()){
            Log.d("FILE TO UPLOAD", "NOT FOUND");
        }
        else{
            Log.d("FILE TO UPLOAD", "FOUND");
        }

        credentialsProvider();

        setTransferUtility();

        uploadFileToS3();
        //
    }

    AmazonS3Client s3;
    TransferUtility transferUtility;
    File fileToUpload = new File("/storage/emulated/0/DCIM/Camera/IMG_20190208_055926.jpg");

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
                "photos.png", /* The key for the uploaded object */
                fileToUpload       /* The file where the data to upload exists */
        );
    }
}

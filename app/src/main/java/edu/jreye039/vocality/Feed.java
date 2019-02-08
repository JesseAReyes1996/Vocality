package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

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
    }

    // Initialize the Amazon Cognito credentials provider
    CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(),
            "us-west-2:af04cf4e-54ef-4854-97aa-8613ba65e9a9", // Identity pool ID
            Regions.US_WEST_2 // Region
    );
}

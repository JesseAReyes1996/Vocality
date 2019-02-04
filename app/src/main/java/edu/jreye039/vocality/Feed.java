package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Feed extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //check if there is the user is logged in
        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        //if they aren't logged in, take them to the login/sign up activity
        if(username.equals("")){
            Intent startIntent = new Intent(getApplicationContext(), NotLoggedIn.class);
            startActivity(startIntent);
        }
    }
}

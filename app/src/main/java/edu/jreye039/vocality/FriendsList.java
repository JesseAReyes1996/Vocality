package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class FriendsList extends AppCompatActivity {

    ListView friendsList;
    Button addFriendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        friendsList = findViewById(R.id.friendsList);
        addFriendBtn = findViewById(R.id.addFriendsBtn);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        FriendsListBackgroundWorker backgroundWorker = new FriendsListBackgroundWorker(this);
        backgroundWorker.execute(username);

        ReceiveRequestListBackgroundWorker requestBackgroundWorker = new ReceiveRequestListBackgroundWorker(this);
        requestBackgroundWorker.execute(username);

    }

    public void onRequestFriends (View view) {
        Intent startIntent = new Intent(getApplicationContext(), SearchFriends.class);
        startActivity(startIntent);
    }
}

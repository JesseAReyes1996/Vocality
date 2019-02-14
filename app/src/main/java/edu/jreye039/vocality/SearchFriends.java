package edu.jreye039.vocality;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SearchFriends extends AppCompatActivity {

    EditText searchFriendsEditText;
    Button searchFriendsBtn;

    TextView resultFriendsTextView;
    Button addFriendsBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        searchFriendsEditText = (EditText) findViewById(R.id.searchFriendsEditText);
        searchFriendsBtn = (Button) findViewById(R.id.searchFriendsBtn);

        resultFriendsTextView = (TextView)findViewById(R.id.resultFriendsTextView);
        addFriendsBtn = (Button) findViewById(R.id.addFriendsBtn);
    }

    public void onSearchFriends(View v){
        String username = searchFriendsEditText.getText().toString().toLowerCase();

        SearchFriendsBackgroundWorker backgroundWorker = new SearchFriendsBackgroundWorker(this);
        backgroundWorker.execute(username);
    }

    public void onAddFriends(View v){
        //addFriendB

    }
}

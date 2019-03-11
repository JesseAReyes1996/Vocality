package edu.jreye039.vocality;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;

    EditText commentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //close the keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        commentEditText = (EditText) findViewById(R.id.commentEditText);

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentsRecyclerView.setHasFixedSize(true);

        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //display the post's comments
        DisplayCommentsBackgroundWorker backgroundWorker = new DisplayCommentsBackgroundWorker(this, commentsRecyclerView);
        backgroundWorker.execute(getIntent().getStringExtra("recording_id"));
    }

    public void onPost(View view) {
        //get the user's comment
        String comment = commentEditText.getText().toString();

        //the text field is empty
        if(comment.equals("")){}

        //post the comment
        else{
            //clear the EditText
            commentEditText.getText().clear();

            //get username of user commenting
            SharedPreferences userInfo = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            String userCommenting = userInfo.getString("username", "");

            //get the id of the recording the user is commenting on
            String recordingID = getIntent().getStringExtra("recording_id");

            AddCommentBackgroundWorker backgroundWorker = new AddCommentBackgroundWorker(this);
            backgroundWorker.execute(userCommenting, recordingID, comment);
        }
    }
}

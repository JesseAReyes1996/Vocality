package edu.jreye039.vocality;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Button createAccountBtn = (Button) findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get new user info
                EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                EditText confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);

                //cast the info to strings
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                //if the passwords don't match, notify the user
                TextView passwordMatch = (TextView) findViewById(R.id.passwordMatchTextView);

                if(password != confirmPassword){
                    passwordMatch.setVisibility(View.VISIBLE);
                }
                //connect to the database
                //else{
                //    BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                //    backgroundWorker.execute("create", username, password);
                //}

                //TODO check if username already exists
            }
        });
    }
}

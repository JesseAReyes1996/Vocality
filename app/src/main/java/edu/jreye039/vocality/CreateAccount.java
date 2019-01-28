package edu.jreye039.vocality;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateAccount extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    EditText confirmPasswordEditText;

    TextView passwordMatchTextView;
    TextView usernameExistsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //for retrieving new user info
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);

        //for validation of new user info
        passwordMatchTextView = (TextView) findViewById(R.id.passwordMatchTextView);
        usernameExistsTextView = (TextView) findViewById(R.id.usernameExistsTextView);

    }

    public void onCreateAccount(View view) {
        //get login info
        String username = usernameEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();

        //confirm user's password
        String confirmPassword = confirmPasswordEditText.getText().toString();

        //if the passwords don't match, notify the user
        if(!password.equals(confirmPassword)){
            passwordMatchTextView.setVisibility(View.VISIBLE);
        }
        //attempt to create a new account
        else{
            passwordMatchTextView.setVisibility(View.INVISIBLE);
            BackgroundWorker backgroundWorker = new BackgroundWorker(this);
            backgroundWorker.execute("createAccount", username, password);

            //TODO check if there exists an account with the given username and set a
            //TODO ViewText to reflect that
        }
    }
}

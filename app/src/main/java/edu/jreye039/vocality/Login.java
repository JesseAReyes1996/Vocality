package edu.jreye039.vocality;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Login extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
    }

    public void onLogin(View view) {
        //get login info
        String username = usernameEditText.getText().toString().toLowerCase();
        String password = passwordEditText.getText().toString();

        LoginBackgroundWorker backgroundWorker = new LoginBackgroundWorker(this);
        backgroundWorker.execute(username, password);
    }
}

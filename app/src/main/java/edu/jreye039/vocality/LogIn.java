package edu.jreye039.vocality;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get log in info
                EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);
                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);

                //Cast the info to strings
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                BackgroundWorker backgroundWorker = new BackgroundWorker(this);
                backgroundWorker.execute("login", username, password);
            }
        });
    }
}

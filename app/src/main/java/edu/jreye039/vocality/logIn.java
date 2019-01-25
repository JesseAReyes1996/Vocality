package edu.jreye039.vocality;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class logIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Button signInBtn = (Button) findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get log in info
                EditText usernameText = (EditText) findViewById(R.id.usernameText);
                EditText passwordText = (EditText) findViewById(R.id.passwordText);

                //Cast the info to strings
                String username = usernameText.getText().toString();
                String password = passwordText.getText().toString();
            }
        });
    }
}

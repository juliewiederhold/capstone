package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Created by NR on 3/4/15.
 */
public class WelcomeActivity extends ActionBarActivity {

    private final static String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Log.e(TAG, "WelcomeActivity fired");

        // Log in button click handler
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            }
        });

        // Sign up button click handler
        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
            }
        });
    }
}
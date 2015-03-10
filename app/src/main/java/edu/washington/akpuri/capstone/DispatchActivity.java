package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.parse.ParseUser;

/**
 * Created by NR on 3/4/15.
 * Main entry point for So-So.
 * Checks for a valid ParseUser, then routes the user to MainActivity.
 * Keeps the user logged-in by caching ParseUser object when a user logs in.
 */
public class DispatchActivity extends ActionBarActivity {
    public DispatchActivity() {
    }

    private final static String TAG = "DispatchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "DispatchActivity fired");
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Start an intent for the logged out activity
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }
}

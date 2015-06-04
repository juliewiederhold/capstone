package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Activity that displays the settings screen.
 */
public class SettingsActivity extends ActionBarActivity {

    private final static String TAG = "SettingsActivity";

    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView firstnameTextView;
    private TextView lastnameTextView;
    private TextView passwordTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private static SingletonUser userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        userInstance = SingletonUser.getInstance();

        ImageView profilePicture = (ImageView) findViewById(R.id.image_icon);
        Drawable picture = userInstance.getProfilePicture();
        if(picture != null) {
            Log.e(TAG, "Picture from local storage loaded.");
            profilePicture.setImageDrawable(picture);
        }

        Log.e(TAG, "SettingsActivity fired");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            String firstname = currentUser.getString("firstname");
            String lastname = currentUser.getString("lastname");
            String password = currentUser.getString("password");
            String email = currentUser.getString("email");
            String phone = currentUser.getString("phone");

            firstnameEditText = (EditText) findViewById(R.id.firstname_edit_text);
            lastnameEditText = (EditText) findViewById(R.id.lastname_edit_text);
//            passwordEditText = (TextView) findViewById(R.id.password_edit_text);
            emailEditText = (EditText) findViewById(R.id.email_edit_text);
            phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

            firstnameTextView = (TextView) findViewById(R.id.firstname_textview);
            lastnameTextView = (TextView) findViewById(R.id.lastname_textview);
//            passwordTextView = (TextView) findViewById(R.id.password_textview);
            emailTextView = (TextView) findViewById(R.id.email_textview);
            phoneTextView = (TextView) findViewById(R.id.phone_textview);

            firstnameEditText.setText(firstname);
            lastnameEditText.setText(lastname);
//            passwordEditText.setText(password);
            emailEditText.setText(email);
            phoneEditText.setText(phone);

            firstnameEditText.setKeyListener(null);
            lastnameEditText.setKeyListener(null);
            emailEditText.setKeyListener(null);
            phoneEditText.setKeyListener(null);

//            Drawable picture2 = userInstance.getProfilePictureFromParse();
//            if (picture2 != null) {
//                Log.e(TAG, "Setting profile picture");
//                profilePicture.setImageDrawable(picture2);
//            }

            Button editSettingsButton = (Button) findViewById(R.id.edit_settings_button);
            editSettingsButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity2.class);
                    startActivity(intent);
                }
            });
        } else {
            //
        }

        // Set up the log out button click handler
     /*   Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Call the Parse log out method
                ParseUser.logOut();
                // Start and intent for the dispatch activity
                // Below will start invalidate user's session and redirect to WelcomeActivity
                Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_logout:
                logout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void logout() {
        // Call the Parse log out method
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser(); // this will now be null
        userInstance.setCurrentUser(currentUser);
        // Start and intent for the dispatch activity
        // Below will start invalidate user's session and redirect to WelcomeActivity
        Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

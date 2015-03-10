package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
            passwordEditText = (EditText) findViewById(R.id.password_edit_text);
            emailEditText = (EditText) findViewById(R.id.email_edit_text);
            phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

            firstnameTextView = (TextView) findViewById(R.id.firstname_textview);
            lastnameTextView = (TextView) findViewById(R.id.lastname_textview);
            passwordTextView = (TextView) findViewById(R.id.password_textview);
            emailTextView = (TextView) findViewById(R.id.email_textview);
            phoneTextView = (TextView) findViewById(R.id.phone_textview);

            firstnameEditText.setText(firstname);
            lastnameEditText.setText(lastname);
            passwordEditText.setText(password);
            emailEditText.setText(email);
            // Will probably have to put a button here to change password
            phoneEditText.setText(phone);

            Button saveChangesButton = (Button) findViewById(R.id.save_changes_button);
            saveChangesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    saveChanges();
                }
            });
        } else {
            //
        }

        // Set up the log out button click handler
        Button logoutButton = (Button) findViewById(R.id.logout_button);
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
        });
    }

    private void saveChanges() {
        String firstname = firstnameEditText.getText().toString().trim();
        String lastname = lastnameEditText.getText().toString().trim();
//        String password = passwordEditText.getText().toString().trim();
//        String passwordAgain = passwordAgainEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
//        String emailAgain = emailAgainEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Validate the sign up data
        boolean validationError = false;
        StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
        if (firstname.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_firstname));
        }
        if (lastname.length() == 0) {
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_lastname));
        }
//        if (password.length() == 0) {
//            if (validationError) {
//                validationErrorMessage.append(getString(R.string.error_join));
//            }
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_blank_password));
//        }
//        if (!password.equals(passwordAgain)) {
//            if (validationError) {
//                validationErrorMessage.append(getString(R.string.error_join));
//            }
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
//        }
        if (email.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_email));
        }
//        if (!email.equals(emailAgain)) {
//            if (validationError) {
//                validationErrorMessage.append(getString(R.string.error_join));
//            }
//            validationError = true;
//            validationErrorMessage.append(getString(R.string.error_mismatched_emails));
//        }
        if (phone.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_phone));
        }
        validationErrorMessage.append(getString(R.string.error_end));

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SettingsActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);
        dialog.setMessage(getString(R.string.progress_saving));
        dialog.show();

        // ******* NICOLE: Should probably check which data changed and then just save those

        // Set up a new Parse user
        ParseUser user = ParseUser.getCurrentUser();
        user.setUsername(email);
//        user.setPassword(password);
        user.setEmail(email);
        // other fields
        user.put("firstname", firstname);
        user.put("lastname", lastname);
        user.put("phone", phone);
        user.saveInBackground();

        new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        }.start();
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
        // Start and intent for the dispatch activity
        // Below will start invalidate user's session and redirect to WelcomeActivity
        Intent intent = new Intent(SettingsActivity.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

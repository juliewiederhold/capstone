package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * Activity that displays the editable settings screen.
 */
public class SettingsActivity2 extends ActionBarActivity {

    private final static String TAG = "SettingsActivity";
    private static SingletonUser userInstance;

    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText oldPasswordEditText;
    private EditText newPassword1EditText;
    private EditText newPassword2EditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private TextView firstnameTextView;
    private TextView lastnameTextView;
    private TextView oldPasswordTextView;
    private TextView newPassword1TextView;
    private TextView newPassword2TextView;
    private TextView emailTextView;
    private TextView phoneTextView;

    private boolean correctOldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);
        userInstance = SingletonUser.getInstance();
//        incorrectOldPassword = true;

      //  Intent intent = getIntent();
     //   String previousActivity = intent.getStringExtra("activitySent");


        ImageView profilePicture = (ImageView) findViewById(R.id.image_icon);
        Drawable picture = userInstance.getProfilePicture();
        if(picture != null)
            profilePicture.setImageDrawable(picture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                startActivityForResult(chooserIntent, 1);
            }
        });

        Log.e(TAG, "SettingsActivity2 fired");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            String firstname = currentUser.getString("firstname");
            String lastname = currentUser.getString("lastname");
            String password = currentUser.getString("password");
            String email = currentUser.getString("email");
            String phone = currentUser.getString("phone");

            firstnameEditText = (EditText) findViewById(R.id.firstname_edit_text);
            lastnameEditText = (EditText) findViewById(R.id.lastname_edit_text);
            oldPasswordEditText = (EditText) findViewById(R.id.old_password_edit_text);
            newPassword1EditText = (EditText) findViewById(R.id.new_password1_edit_text);
            newPassword2EditText = (EditText) findViewById(R.id.new_password2_edit_text);
            emailEditText = (EditText) findViewById(R.id.email_edit_text);
            phoneEditText = (EditText) findViewById(R.id.phone_edit_text);

            firstnameTextView = (TextView) findViewById(R.id.firstname_textview);
            lastnameTextView = (TextView) findViewById(R.id.lastname_textview);
            oldPasswordTextView = (TextView) findViewById(R.id.old_password_textview);
            newPassword1TextView = (TextView) findViewById(R.id.new_password1_textview);
            newPassword2TextView = (TextView) findViewById(R.id.new_password2_textview);
            emailTextView = (TextView) findViewById(R.id.email_textview);
            phoneTextView = (TextView) findViewById(R.id.phone_textview);

            firstnameEditText.setText(firstname);
            lastnameEditText.setText(lastname);
            // oldPasswordEditText.setText(password);
            // blank for old and new password fields
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
//        Button logoutButton = (Button) findViewById(R.id.logout_button);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Call the Parse log out method
//                ParseUser.logOut();
//                // Start and intent for the dispatch activity
//                // Below will start invalidate user's session and redirect to WelcomeActivity
//                Intent intent = new Intent(SettingsActivity2.this, DispatchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Uri imageUri = data.getData();
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            ImageView imageView = (ImageView) findViewById(R.id.image_icon);
            imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
            userInstance.setProfilePicture(imageView.getDrawable());
        } catch (FileNotFoundException e) {
            // Handle the error
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    // Ignore the exception
                }
            }
        }
    }

    private void saveChanges() {
        String firstname = firstnameEditText.getText().toString().trim();
        String lastname = lastnameEditText.getText().toString().trim();
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword1 = newPassword1EditText.getText().toString().trim();
        String newPassword2 = newPassword2EditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
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
        if (oldPassword.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }

        // Check if old password entered is correct
        boolean isCorrect = checkOldPassword(oldPassword);
        if (isCorrect) {
            // Only throw error for new passwords if user enters value for them
            Log.e(TAG, "LENGTH: 1 " + newPassword1.length() + " 2 " + newPassword2.length());
            if ((newPassword1.length() > 0 || newPassword2.length() > 0) &&
                    (!newPassword1.equals(newPassword2))) {
                if (validationError) {
                    validationErrorMessage.append(getString(R.string.error_join));
                }
                validationError = true;
                validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
            }
            if (email.length() == 0) {
                if (validationError) {
                    validationErrorMessage.append(getString(R.string.error_join));
                }
                validationError = true;
                validationErrorMessage.append(getString(R.string.error_blank_email));
            }
            if (phone.length() == 0) {
                if (validationError) {
                    validationErrorMessage.append(getString(R.string.error_join));
                }
                validationError = true;
                validationErrorMessage.append(getString(R.string.error_blank_phone));
            }
            validationErrorMessage.append(getString(R.string.error_end));
        } else {
            // Do nothing
            Log.e(TAG, "isCorrect: " + isCorrect);
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_incorrect_passwords));
        }

        // If there is a validation error, display the error
        if (validationError) {
            Toast.makeText(SettingsActivity2.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        } else {
            // Set up a progress dialog
            final ProgressDialog dialog = new ProgressDialog(SettingsActivity2.this);
            dialog.setMessage(getString(R.string.progress_saving));
            dialog.show();

            // Set up a new Parse user
            ParseUser user = ParseUser.getCurrentUser();
            user.setUsername(email);
            if (newPassword2.length() > 0) {
                user.setPassword(newPassword2);
            }
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

            Intent intent = new Intent(SettingsActivity2.this, EditDefaultSettings.class);
            startActivity(intent);
        }

    }

    private boolean checkOldPassword(String oldPassword){
        correctOldPassword = false;
        try {
            ParseUser.logIn(ParseUser.getCurrentUser().getUsername(), oldPassword);
            correctOldPassword = true;
        } catch (Exception e) {

        }
//        ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), oldPassword,
//                new LogInCallback() {
//                    @Override
//                    public void done(ParseUser parseUser, ParseException e) {
//                        if (parseUser != null) {
//                            // old password is correct
//                            correctOldPassword = true;
//                            Log.e(TAG, "correctOldPassword TRUE: " + correctOldPassword);
//                        } else {
//                            // old password is incorrect
//                            correctOldPassword = true;
//                            Log.e(TAG, "correctOldPassword FALSE: " + correctOldPassword);
//                        }
//                    }
//                });
        Log.e(TAG, "correctOldPassword: " + correctOldPassword + " " + oldPassword);
        // put code below in a method that will be called once done above
        return correctOldPassword;
    }

//    private void continueSavingChanges(boolean isCorrect, String newPassword1, String newPassword2, String oldPassword,
//                                       boolean validationError, StringBuilder validationErrorMessage, String email, String phone){
//
//    }
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
        Intent intent = new Intent(SettingsActivity2.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

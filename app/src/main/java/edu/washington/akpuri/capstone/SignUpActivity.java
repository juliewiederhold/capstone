package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SignUpActivity extends ActionBarActivity {

    private final static String TAG = "SignUpActivity";
    private static SingletonUser instance;

    private EditText firstnameEditText;
    private EditText lastnameEditText;
    private EditText passwordEditText;
    private EditText passwordAgainEditText;
    private EditText emailEditText;
    private EditText emailAgainEditText;
    private EditText phoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Log.e(TAG, "SigUpActivity fired");

        instance = SingletonUser.getInstance();

        // Set up the signup form.
        firstnameEditText = (EditText) findViewById(R.id.firstname_edit_text);
        lastnameEditText = (EditText) findViewById(R.id.lastname_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
        passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        emailAgainEditText = (EditText) findViewById(R.id.email_again_edit_text);
        phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
        phoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_signup ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    signup();
                    return true;
                }
                return false;
            }
        });

        ImageView image = (ImageView) findViewById(R.id.image_icon);
        image.setImageResource(R.drawable.picholdericon);
        image.setOnClickListener(new View.OnClickListener() {
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

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Uri imageUri = data.getData();
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
            ImageView imageView = (ImageView) findViewById(R.id.image_icon);
            imageView.setImageBitmap(BitmapFactory.decodeStream(imageStream));
            // instance.setProfilePicture(BitmapFactory.decodeStream(imageStream));
            instance.setProfilePicture(imageView.getDrawable());
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

    private void signup() {
        String firstname = firstnameEditText.getText().toString().trim();
        String lastname = lastnameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordAgain = passwordAgainEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String emailAgain = emailAgainEditText.getText().toString().trim();
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
        if (password.length() == 0) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_blank_password));
        }
        if (!password.equals(passwordAgain)) {
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
        if (!email.equals(emailAgain)) {
            if (validationError) {
                validationErrorMessage.append(getString(R.string.error_join));
            }
            validationError = true;
            validationErrorMessage.append(getString(R.string.error_mismatched_emails));
        }
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
            Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
        dialog.setMessage(getString(R.string.progress_signup));
        dialog.show();

        // Set up a new Parse user
        final ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.setEmail(email);
        // other fields
        user.put("firstname", firstname);
        user.put("lastname", lastname);
        user.put("phone", phone.replaceAll("[^\\d]",""));
        user.put("setupdone", false);                       // if setupdone == true, go to MainActivity.class
                                                            // if setupdone == false, go to Welcome.class
        user.put("contacts", new ArrayList<>());
        user.put("importContacts", false);

        final ParseObject contacts = new ParseObject("ContactsObject");
        contacts.put("user", email); // contacts.put("parent", email);
        contacts.put("contacts", new ArrayList<Contact>());
        contacts.saveInBackground( new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully

                } else {
                    Log.e(TAG, "Error saving contactsId: " + e);
                }
            }
        });

        final ParseObject safetyzones = new ParseObject("SafetyZonesObject");
        safetyzones.put("user", email);
        safetyzones.put("safetyzones", new ArrayList<SafetyZone>());
        safetyzones.saveInBackground( new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully
                } else {
                    Log.e(TAG, "Error saving contactsId: " + e);
                }
            }
        });

        // Call the Parse signup method
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Handle the response
                dialog.dismiss();
                if (e != null) {
                    // Show error message
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for dispatch activity
                    Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

}

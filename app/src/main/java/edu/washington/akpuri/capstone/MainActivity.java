package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by NR on 3/4/15.
 */
public class MainActivity extends ActionBarActivity {
    /*
   * Define a request code to send to Google Play services This code is returned in
   * Activity.onActivityResult
   */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static String TAG = "MainActivity";

    private SingletonContacts instance;
    private static ArrayList<Contact> pendingContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "MainActivity fired");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            String name = currentUser.getString("firstname");
//            Log.e(TAG, name);
            TextView welcomeTextView = (TextView) findViewById(R.id.welcome);
            welcomeTextView.setText("Welcome, " + name + "!");
        } else {
            //
        }

        Button editDefaultSettings = (Button) findViewById(R.id.edit_default_settings);

        editDefaultSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(MainActivity.this, EditDefaultSettings.class);
                startActivity(next);
            }
        });

        // Set-up user's information
        instance = SingletonContacts.getInstance();
        pendingContacts = new ArrayList<>();

        if (ParseUser.getCurrentUser().get("contacts") != null) {
            JSONArray contacts = ParseUser.getCurrentUser().getJSONArray("contacts");
            Log.e(TAG, " contacts[] 1: " + contacts.toString());
            for (int i = 0; i < contacts.length(); i++) {
                String id = null;
                try {
                    id = contacts.get(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.e(TAG, id);
                if (id != null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("contact");
                    query.whereEqualTo("objectId", id);
                    // Save current contact object ids
                    if (!instance.getCurrentContacts().contains(id)) {
                        instance.getCurrentContacts().add(id);
                    }

                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject parseObject, ParseException e) {
                            String name = parseObject.getString("name");
                            String phone = parseObject.getString("phone");
                            Log.e(TAG, "Adding " + name + " " + phone + " to pendingFriends");
                            Contact currentFriend = new Contact(name, phone, 0);
                            pendingContacts.add(currentFriend);
                        }
                    });
                }
            }
            instance.setPendingFriends(pendingContacts);
            // Should be same as contacts[] 1
            Log.e(TAG, " contacts[] 2: " + instance.getCurrentContacts().toString());
            // Probably will be empty here
            Log.e(TAG, " pending friends: " + instance.getPendingFriends().toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                break;
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_safetyzones:
                Intent intent2 = new Intent(this, SafetyZonePage.class);
                this.startActivity(intent2);
                break;
            case R.id.action_contacts:
                Intent intent3 = new Intent(this, Contacts.class);
                this.startActivity(intent3);
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
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.parse.ParseUser;


public class EditDefaultSettings extends ActionBarActivity {
    private static SingletonUser userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_default_settings);

        userInstance = SingletonUser.getInstance();

        ImageButton edit_profile = (ImageButton) findViewById(R.id.edit_profile_button);
        ImageButton edit_friends = (ImageButton) findViewById(R.id.edit_friends_button);
        ImageButton edit_safety_zones = (ImageButton) findViewById(R.id.edit_safety_zones_button);
        ImageButton edit_app_num_blocking = (ImageButton) findViewById(R.id.edit_app_number_blocking);
        ImageButton edit_quick_texts = (ImageButton) findViewById(R.id.edit_quick_texts_button);

        edit_profile.setImageResource(R.drawable.ic_person_black_48dp);
        edit_friends.setImageResource(R.drawable.ic_people_black_48dp);
        edit_safety_zones.setImageResource(R.drawable.ic_home_black_48dp);
        edit_app_num_blocking.setImageResource(R.drawable.ic_do_not_disturb_alt_black_48dp);
        edit_quick_texts.setImageResource(R.drawable.ic_sms_black_48dp);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, SettingsActivity2.class);
                startActivity(next);
            }
        });

        edit_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, Contacts.class);
                startActivity(next);
            }
        });

        edit_safety_zones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, SafetyZonePage.class);
                startActivity(next);
            }
        });

        edit_app_num_blocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, AppNumberBlocking.class);
                startActivity(next);
            }
        });

        edit_quick_texts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, QuickText.class);
                startActivity(next);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_default_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        Intent intent = new Intent(this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

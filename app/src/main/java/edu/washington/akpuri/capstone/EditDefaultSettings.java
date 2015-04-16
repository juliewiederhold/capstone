package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class EditDefaultSettings extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_default_settings);

        ImageButton edit_profile = (ImageButton) findViewById(R.id.edit_profile_button);
        ImageButton edit_friends = (ImageButton) findViewById(R.id.edit_friends_button);
        ImageButton edit_safety_zones = (ImageButton) findViewById(R.id.edit_safety_zones_button);
        ImageButton edit_app_num_blocking = (ImageButton) findViewById(R.id.edit_app_number_blocking);
        ImageButton edit_quick_texts = (ImageButton) findViewById(R.id.edit_quick_texts_button);

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, SettingsActivity.class);
                next.putExtra("activitySent","EditDefaultSettings");
                startActivity(next);
            }
        });

        edit_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, Contacts.class);
                next.putExtra("activitySent","EditDefaultSettings");
                startActivity(next);
            }
        });

        edit_safety_zones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, SafetyZonePage.class);
                next.putExtra("activitySent","EditDefaultSettings");
                startActivity(next);
            }
        });

        edit_app_num_blocking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, AppNumberBlocking.class);
                next.putExtra("activitySent","EditDefaultSettings");
                startActivity(next);
            }
        });

        edit_quick_texts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(EditDefaultSettings.this, QuickText.class);
                next.putExtra("activitySent","EditDefaultSettings");
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

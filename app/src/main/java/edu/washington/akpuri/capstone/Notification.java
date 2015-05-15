package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by iguest on 5/14/15.
 */
public class Notification extends ActionBarActivity {

    @Override
    public void onBackPressed() {
        Intent myIntent = new Intent(Notification.this, MainActivity.class);
        startActivity(myIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.notification);

//        TextView notification_title = (TextView) findViewById(R.id.notification_title);
//        TextView notification_message = (TextView) findViewById(R.id.notification_message);

        ParseAnalytics.trackAppOpened(getIntent());

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");

        try{
            JSONObject notification = new JSONObject(jsonData);
            String title = notification.getString("title");
            String message = notification.getString("alert");

//            notification_title.setText(title);
//            notification_message.setText(message);
        }
        catch(JSONException e){
            Toast.makeText(getApplicationContext(), "Something went wrong with the notification", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}

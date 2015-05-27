package edu.washington.akpuri.capstone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Create night out group
 */
public class NightOutGroup extends ActionBarActivity {

    private final static String TAG = "NightOutGroup";
    private static SingletonContacts contactsInstance;
    private static SingletonUser userInstance;
    private static SingletonNightOutGroup groupInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_out_group);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String jsonData = extras.getString("com.parse.Data");
            try {
                // TODO CONTINUE
                JSONObject jsonObject = new JSONObject(jsonData);
//                Log.e(TAG, jsonObject.get("alert") + "");
//                Log.e(TAG, "long msg: " + data.getString(0));
            } catch (Exception err) {
                err.printStackTrace();
            }

        }

        contactsInstance = SingletonContacts.getInstance();
        userInstance = SingletonUser.getInstance();
        groupInstance = SingletonNightOutGroup.getInstance();

        Log.e(TAG, contactsInstance.getSosoFriends().toString());

        ListView contactListView = (ListView) findViewById(R.id.addFriendsToNighOutGroupList);
        ListAdapter adapter = new NightOutGroupAdapter(this, R.id.friendListItem, contactsInstance.getSosoFriends());
        contactListView.setAdapter(adapter);

        // Send Night Out Group request to individuals
        Button sendRequest = (Button) findViewById(R.id.sendNightOutGroupRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send requests
                Log.e(TAG, "Listing group members: " +groupInstance.getMembers().toString());
                sendRequests();
                Toast mes = Toast.makeText(getApplicationContext(), "Night Out Group Request Sent", Toast.LENGTH_LONG);
                mes.show();
                finish();
            }
        });
    }

    // TODO need: groupname
    private void sendRequests(){
        String message = userInstance.getName() + " (" + userInstance.getPhone() + ") sent you a night out request.";
        String longmessage = "Group members: " + groupInstance.getMembersAsString();
        Iterator<Map.Entry<String, Contact>> iterator = groupInstance.getGroupContact().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Contact> entry = iterator.next();
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("recipientId", entry.getValue().getObjectId());
            params.put("recipientEmail", entry.getValue().getEmail());
            params.put("message", message);
            params.put("longmessage", longmessage);
            params.put("uri", "app://host/nightoutgroup");
            ParseCloud.callFunctionInBackground("sendPushToGroup", params, new FunctionCallback<String>() {
                public void done(String success, ParseException e) {
                    if (e == null) {
                        // Push sent successfully
                        Log.e(TAG, success);
                    } else {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

    // TODO: Code to be used once a person accepts the group request:
    // ParsePush.subscribeInBackground(userInstance.getUsername());

    @Override
    public void onResume() {
        super.onResume();
        ListView contactListView = (ListView) findViewById(R.id.addFriendsToNighOutGroupList);
        ListAdapter adapter = new NightOutGroupAdapter(this, R.id.friendListItem, contactsInstance.getSosoFriends());
        contactListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_add_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

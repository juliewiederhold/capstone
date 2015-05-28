package edu.washington.akpuri.capstone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Create night out group
 */
public class NightOutGroup extends ActionBarActivity
                            implements NighOutGroupDialogFragment.NighOutGroupDialogListener {

    private final static String TAG = "NightOutGroup";
    private static SingletonContacts contactsInstance;
    private static SingletonUser userInstance;
    private static SingletonNightOutGroup groupInstance;
    private String members = "", membernames = "", memberphones = "", groupname = "", creator = "";
    private ArrayList<String> theMembers, groupMemberNames, groupMemberPhones;
    private static Contact theCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_out_group);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String jsonData = extras.getString("com.parse.Data");
        if (jsonData != null) {
            try {
                // TODO CONTINUE
                JSONObject jsonObject = new JSONObject(jsonData);
                creator = jsonObject.getString("groupcreator");          // Group creator's phone number
                Log.e(TAG, "Creator: " + creator);
                members = jsonObject.getString("members");
                theMembers = new ArrayList<>(Arrays.asList(members.split(",")));
//                membernames = jsonObject.getString("membernames");  // Group members' names
//                groupMemberNames = membernames.split(",");
//                memberphones = jsonObject.getString("memberphones");  // Group members' phone numbers
//                groupMemberPhones = memberphones.split(",");
                groupname = jsonObject.getString("groupname");      // Group name
                Log.e(TAG, "Members: " + members.toString());
                createDialog();

            } catch (JSONException err) {
                Log.e(TAG, "JSONException: " + err.getMessage());
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

    /**
     * PUSH NOTIFICATIONS WILL ONLY BE SENT TO USERS WHO ARE LOGGED IN IF WE DO DIALOG ALERTS.
     */
    // TODO need: groupname
    private void sendRequests(){
        // Create Group As Creator
        String message = userInstance.getName() + " (" + userInstance.getPhone() + ") sent you a night out request.";
//        String membernames = groupInstance.getMembersName();
//        String memberphones = groupInstance.getMembersPhone();
//        Iterator iterator = groupInstance.getGroupContact().entrySet().iterator();
        Iterator<Map.Entry<String, Contact>> iterator = groupInstance.getGroupContact().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, Contact> entry = (Map.Entry) iterator.next();
            Log.e(TAG, entry.getValue().getPhone());
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("groupcreator", userInstance.getPhone());
            params.put("recipientId", entry.getValue().getObjectId());
            params.put("recipientEmail", entry.getValue().getEmail());
            params.put("message", message);
            params.put("groupname", groupInstance.getGroupName());    // Group name temporarily the creator's phone number
            params.put("members", groupInstance.getMembersAsString());
//            params.put("membernames", membernames);
//            params.put("memberphones", memberphones);
            params.put("uri", "app://host/nightoutgroup");
//            params.put("uri", "app://host/mainactivity");
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
            iterator.remove();
        }
    }


    private void createDialog(){
//        DialogFragment newFragment = new NighOutGroupDialogFragment();
        ArrayList<String> groupMembers = new ArrayList<>();
        groupMemberNames = new ArrayList<>();
        groupMemberPhones = new ArrayList<>();
        Log.e(TAG, theMembers.size() + "");
        for (int i=0; i < theMembers.size(); i++) {
            groupMembers.add(theMembers.get(i));
            // split groupMembers to get name and phone
            String[] aMember = theMembers.get(i).split(":");
            groupMemberNames.add(aMember[0]);
            groupMemberPhones.add(aMember[1]);
            Log.e(TAG, groupMemberNames.get(i) + " " + groupMemberPhones.get(i));
        }
        DialogFragment newFragment = NighOutGroupDialogFragment.newInstance(groupMembers);
        newFragment.show(getFragmentManager(), "createDialog");
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

    public void showNightOutGroupDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NighOutGroupDialogFragment();
        dialog.show(getFragmentManager(), "NightOutDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched +
        // Subscribe to channel, which is the group name, which is the group creator's phone number
        Log.e(TAG, "Group name: " + groupname);
        ParsePush.subscribeInBackground(groupname);
        // Create group
        createGroup();
    }

    // TODO: Remove from group for every member!!
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched - button
        // Send notification to group creator that user rejected request
        String message = userInstance.getName() + " (" + userInstance.getPhone() + ") rejected the invite.";
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("recipientId", theCreator.getObjectId());
        params.put("recipientEmail", theCreator.getEmail());
        params.put("message", message);
        params.put("uri", "app://host/mainactivity");
        ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
            public void done(String success, ParseException e) {
                if (e == null) {
                    // Push sent successfully
                    Log.e(TAG, success);
                }
                else {
                    Log.e(TAG, e.toString());
                }
            }
        });
    }

    // Creates the group and adds members to it
    public void createGroup() {
        ParseQuery<ParseUser> query1 = ParseUser.getQuery();
        query1.whereContains("phone", creator);
        query1.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                try {
                    theCreator = new Contact(parseUser.get("firstname").toString() + " " + parseUser.get("lastname").toString(),
                            parseUser.get("phone").toString(), 0);
//                            Integer.parseInt(parseUser.getObjectId()));
                    theCreator.setEmail(parseUser.getUsername());
                    // Create group object
                    groupInstance.createGroup(groupname, theCreator);
                    for (String phone : groupMemberPhones) {
                        addMember(phone);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    // Destroys group and returns true if successfully destroyed, or false if not
    private boolean destroyGroup() {
        try {
            groupInstance.clearGroup();
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    // Adds a member to the group
    private void addMember(String phone) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContains("phone", phone);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    // Create Contact object
                    Contact person = new Contact(parseUser.get("firstname").toString() + " " + parseUser.get("lastname").toString(),
                            parseUser.get("phone").toString(), 0);
//                            Integer.parseInt(parseUser.getObjectId()));
                    person.setEmail(parseUser.getUsername());
                    // Add to group
                    groupInstance.addMember(person, parseUser);
                }
            }
        });

    }

    private void subscribe() {
        ParsePush.subscribeInBackground(groupname);
    }

    private void unsubscribe(){
        ParsePush.unsubscribeInBackground(groupname);
    }
}



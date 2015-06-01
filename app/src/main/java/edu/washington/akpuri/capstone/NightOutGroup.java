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
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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
 * TODO: save location for each member on Parse (MainMap.java)
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

        contactsInstance = SingletonContacts.getInstance();
        userInstance = SingletonUser.getInstance();
        groupInstance = SingletonNightOutGroup.getInstance();

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
                groupname = jsonObject.getString("groupname");      // Group name
                Log.e(TAG, "groupname: " + groupname);
                Log.e(TAG, "Members: " + members.toString());
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

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                createDialog();

            } catch (JSONException err) {
                Log.e(TAG, "JSONException: " + err.getMessage());
            }

        }

//        Log.e(TAG, contactsInstance.getSosoFriends().toString());

        ListView contactListView = (ListView) findViewById(R.id.addFriendsToNighOutGroupList);
        ListAdapter adapter = new NightOutGroupAdapter(this, R.id.friendListItem, contactsInstance.getSosoFriends());
        contactListView.setAdapter(adapter);

        // Send Night Out Group request to individuals
        Button sendRequest = (Button) findViewById(R.id.sendNightOutGroupRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Listing group members: " +groupInstance.getMembers().toString());
                // Create group locally for group creator and on Parse for everyone
                // Send requests
                sendRequests();
                Toast mes = Toast.makeText(getApplicationContext(), "Night Out Group Request Sent", Toast.LENGTH_LONG);
                mes.show();
                finish();

                Intent nightOutSetUp = new Intent(NightOutGroup.this, StartNightOutSettingConfirmation.class);
                startActivity(nightOutSetUp);
            }
        });
    }

    /**
     * IMPORTANT: PUSH NOTIFICATIONS WILL ONLY BE SENT TO USERS WHO ARE LOGGED IN IF WE DO DIALOG ALERTS.
     */
    // Will only be used if creating a group and sending requests
    private void sendRequests(){
        // Create Group As Creator
        createGroupAsCreator();
        createGroupOnParse();
        Log.e(TAG, "Members: " + groupInstance.getMembersAsString());
        String message = userInstance.getName() + " (" + userInstance.getPhone() + ") sent you a night out request.";
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
            Log.e(TAG, groupInstance.getMembersAsString());
            params.put("members", groupInstance.getMembersAsString());
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

    // Creates the group as a creator
    public void createGroupAsCreator() {
        // Create group name
        String groupname = userInstance.getName().substring(0, 3) + userInstance.getPhone();
        // Create NightOutGroup
        groupInstance.createGroup(groupname, userInstance.getContactObject());
        // Todo Add self as member BELOW DOESN'T SEEM TO WORK
        groupInstance.addMember(userInstance.getContactObject(), userInstance.getCurrentUser());
        Log.e(TAG, "Added owner, members: " + groupInstance.getMembersAsString());
    }

    private void createGroupOnParse() {
        // Create NightOutGroup object on Parse.com
        final ParseObject groupObject = new ParseObject("NightOutGroup");
        groupObject.put("groupName", groupInstance.getGroupName());
        groupObject.put("groupCreator",userInstance.getCurrentUser());
        groupObject.put("groupMemberIds", groupInstance.getMemberIds()); // Group member IDs except for creator
        groupObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.e(TAG, "Group should've been created");
                } else {

                    Log.e(TAG, "Error creating the group. Code: " + e.getCode());
                }
            }
        });
    }

    // Creates the group as a member and adds members to it
    public void createGroup(Contact theCreator) {
        groupInstance.createGroup(groupname, theCreator);
        for (String phone : groupMemberPhones) {
            addMember(phone);
        }
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

    // Should be used when night out is over (from group creator's account)
    private void deleteGroupFromParse(){
        // Delete NightOutGroup Object from Parse.com
        ParseQuery<ParseObject> query = ParseQuery.getQuery("NightOutGroup");
        query.whereEqualTo("groupName", groupInstance.getGroupName());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    Log.e(TAG, parseObject.get("groupName") + " found and will be deleted.");
                    parseObject.deleteInBackground();
                } else {
                    Log.e(TAG, "Group could not be found on Parse.com.");
                }
            }
        });
    }

    private void createDialog(){
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
        // User touched Accept
        // Subscribe to Parse channel, which is the group name, which is the group creator's phone number
        // to receive push notifications for night out group
        Log.e(TAG, "Group name: " + groupname);
        ParsePush.subscribeInBackground(groupname);
        // Create group as a member
        createGroup(theCreator);
    }

    // TODO: Remove from group for every member!!
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched - button
        // Send notification to group creator that user rejected request
//        String message = userInstance.getName() + " (" + userInstance.getPhone() + ") rejected the invite.";
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("recipientId", theCreator.getObjectId());
//        params.put("recipientEmail", theCreator.getEmail());
//        params.put("message", message);
//        params.put("uri", "app://host/mainactivity");
//        ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
//            public void done(String success, ParseException e) {
//                if (e == null) {
//                    // Push sent successfully
//                    Log.e(TAG, success);
//                }
//                else {
//                    Log.e(TAG, e.toString());
//                }
//            }
//        });
        deleteMemberFromGroup(userInstance.getCurrentUser().getObjectId());
    }

    private void deleteMemberFromGroup(final String memberId){
        Log.e(TAG, "Deleting " + memberId + " from the group " + groupInstance.getGroupName());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("NightOutGroup");
        query.whereEqualTo("groupName", groupInstance.getGroupName());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject != null) {
                    Log.e(TAG, parseObject.get("groupName") + " found and " + userInstance.getName() + " will be deleted.");
                    ArrayList<String> newMemberIds = groupInstance.getMemberIds();
                    newMemberIds.remove(memberId);
                    parseObject.put("groupMemberIds", newMemberIds);
                    parseObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.e(TAG, "New group member IDs saved.");
                            }
                        }
                    });
                } else {
                    Log.e(TAG, "Group could not be found on Parse.com.");
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



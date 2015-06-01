package edu.washington.akpuri.capstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by iguest on 5/20/15.
 */
public class AlertService extends BroadcastReceiver{

    private static ArrayList<Contact> friendsInNightOutGroup = new ArrayList<Contact>();
    private static SingletonUser userInstance;
    private static SingletonNightOutGroup groupInstance;
    public static final String TAG = AlertService.class.getSimpleName();


   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        Contact temp = new Contact("Julie", "4082096381", 1);
        temp.setEmail("f@f.com");
        friendsInNightOutGroup.add(temp);

        userInstance = SingletonUser.getInstance();

    }*/

    // TODO: send to all people in night out group

    @Override
    public void onReceive(Context context, Intent intent) {
        Contact temp = new Contact("Julie", "4082096381", 1);
        temp.setEmail("f@f.com");
        if(friendsInNightOutGroup.size() < 1)
            friendsInNightOutGroup.add(temp);
        userInstance = SingletonUser.getInstance();
        groupInstance = SingletonNightOutGroup.getInstance();

        final String user = ParseUser.getCurrentUser().getString("email");
        for(int i=0; i < friendsInNightOutGroup.size(); i++) {
            final Contact person = friendsInNightOutGroup.get(i);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("ContactsObject");
            query.whereEqualTo("user", user);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject parseObject, ParseException e) {
                    try {
                        if (parseObject != null) {

                            // Send push notifications
                            ParseQuery pushQuery = userInstance.getCurrentInstallation().getQuery();
                            pushQuery.whereEqualTo("user", person.getEmail());
                            ParsePush push = new ParsePush();
                            push.setQuery(pushQuery);
                            push.setMessage(userInstance.getName() + " would like you to find her. Please go assist her NOW.");

                            push.sendInBackground();
                            Log.e(TAG, "sent to: " + person.getEmail());

                        } else {
                            // Something went wrong
                            Log.e("Contacts", "Failed to retrieve contactsObject: " + e);
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }
            });
        }
    }

    private void sendAlerts(){

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
            params.put("uri", "app://host/nightoutgroup");              // Go to MainMap.java
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

}

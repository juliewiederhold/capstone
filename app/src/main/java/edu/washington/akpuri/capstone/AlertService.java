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


//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        Contact temp = new Contact("Julie", "4082096381", 1);
//        temp.setEmail("f@f.com");
//        friendsInNightOutGroup.add(temp);
//
//        userInstance = SingletonUser.getInstance();
//
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        userInstance = SingletonUser.getInstance();
        groupInstance = SingletonNightOutGroup.getInstance();
        sendAlerts();
    }

    // TODO don't send to self
    private void sendAlerts(){

        Log.e(TAG, "Members: " + groupInstance.getMembersAsString() + " " + groupInstance.getGroupContact().size() + " " + groupInstance.getMembers().size());
        String message = userInstance.getName() + " would like you to find her. Please go assist her NOW.";
        Log.e(TAG, "Message: " + message);
        ArrayList<Contact> contactObjects = groupInstance.getGroupContacts();
//        Iterator<Map.Entry<String, Contact>> iterator = groupInstance.getGroupContact().entrySet().iterator();
//        while(iterator.hasNext()) {
//            Map.Entry<String, Contact> entry = (Map.Entry) iterator.next();
        for (int i = 0; i < contactObjects.size(); i++) {
            String phoneNo = contactObjects.get(i).getPhone();
            Log.e(TAG, phoneNo);
            if (!phoneNo.equals(userInstance.getPhone())) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                Log.e(TAG, "Sending to: " + contactObjects.get(i).getObjectId() + " " + contactObjects.get(i).getEmail());
                params.put("recipientId", contactObjects.get(i).getObjectId());
                params.put("recipientEmail", contactObjects.get(i).getEmail());
                params.put("message", message);
                params.put("uri", "app://host/mainmap");              // Go to MainMap.java
                ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {
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
//            iterator.remove();
//        }
        }
    }

}

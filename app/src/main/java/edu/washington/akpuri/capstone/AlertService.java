package edu.washington.akpuri.capstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by iguest on 5/20/15.
 */
public class AlertService extends BroadcastReceiver{

    private static ArrayList<Contact> friendsInNightOutGroup = new ArrayList<Contact>();
    private static SingletonUser userInstance;
    public static final String TAG = AlertService.class.getSimpleName();


   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        Contact temp = new Contact("Julie", "4082096381", 1);
        temp.setEmail("f@f.com");
        friendsInNightOutGroup.add(temp);

        userInstance = SingletonUser.getInstance();

    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        Contact temp = new Contact("Julie", "4082096381", 1);
        temp.setEmail("f@f.com");
        if(friendsInNightOutGroup.size() < 1)
            friendsInNightOutGroup.add(temp);
        userInstance = SingletonUser.getInstance();

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

}

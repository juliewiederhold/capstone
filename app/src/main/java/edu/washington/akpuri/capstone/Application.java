package edu.washington.akpuri.capstone;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by NR on 3/4/15.
 */
public class Application extends android.app.Application {
    // Debugging switch
    public static final boolean APPDEBUG = false;

    // Debugging tag for the app
    public static final String TAG = "SoSo";

    private static SharedPreferences preferences;

    private static ConfigHelper configHelper;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e(TAG, "Application fired");

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Initialize the Parse SDK
        Parse.initialize(this, "Ip7MwttReawlDBFXZqAtfCu0AxI1H73kxF49aBW9", "8iLDRvHPSehyMHUrew3NGISpY4XVOk9CLEJzesh7");

//        ParseObject testObject = new ParseObject("TestObject");
//        testObject.put("foo", "bar");
//        testObject.saveInBackground();

        preferences = getSharedPreferences("edu.washington.akpuri.capstone", Context.MODE_PRIVATE);

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        // TODO
        // https://www.parse.com/questions/push-notification-is-not-fully-displayed-on-android-devices
        PushService.setDefaultPushCallback(this, Contacts.class);

        // Testing push notifications
        push();

        // Specify Activity to handle all pushes by default
//        PushService.setDefaultPushCallback(this, PushReceiver.class);


    }

    private void push() {
        // Do something
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }

}

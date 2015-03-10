package edu.washington.akpuri.capstone;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseObject;

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

        Parse.initialize(this, "Ip7MwttReawlDBFXZqAtfCu0AxI1H73kxF49aBW9", "8iLDRvHPSehyMHUrew3NGISpY4XVOk9CLEJzesh7");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

        preferences = getSharedPreferences("hello.jcw27.washington.edu.so_so", Context.MODE_PRIVATE);

        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }

}

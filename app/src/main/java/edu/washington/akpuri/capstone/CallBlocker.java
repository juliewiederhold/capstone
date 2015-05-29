package edu.washington.akpuri.capstone;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by iguest on 5/29/15.
 */
public class CallBlocker extends IntentService {
    public static final String TAG = AlertService.class.getSimpleName();

    public CallBlocker(String name){
        super(name);
        Log.e(TAG, "In CallBlocker");
    }

    @Override
    protected void onHandleIntent(Intent workIntent){
        //Gets data from the incoming Intent

        // Do work here based on contents from Intent
    }
}

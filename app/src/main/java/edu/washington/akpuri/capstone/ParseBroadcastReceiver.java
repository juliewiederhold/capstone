package edu.washington.akpuri.capstone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

/**
 * Created by NR on 5/19/15.
 */
public class ParseBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION = "edu.washington.akpuri.capstone.MESSAGE";
    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final String PARSE_JSON_CHANNEL_KEY = "com.parse.Channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String channel = intent.getExtras().getString(PARSE_JSON_CHANNEL_KEY);
        try {
            JSONObject json = new JSONObject(intent.getExtras().getString(PARSE_EXTRA_DATA_KEY));
        } catch (Exception e) {
            // error
        }
    }
}

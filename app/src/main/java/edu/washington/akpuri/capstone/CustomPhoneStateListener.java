package edu.washington.akpuri.capstone;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;

import edu.washington.akpuri.capstone.com.android.internal.telephony.ITelephony;

/**
 * Created by iguest on 5/29/15.
 */
public class CustomPhoneStateListener extends BroadcastReceiver{
    //private static final String TAG = "PhoneStateChanged";
    Context context;
    public CustomPhoneStateListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            Bundle bundle = intent.getExtras();
            String phoneNumber = bundle.getString("incoming_number");
            Log.d("INCOMING", phoneNumber);
            if ((isBlocked(phoneNumber))) {
                telephonyService.endCall();
                Log.d("HANG UP", phoneNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isBlocked(String outGoingNumber){
        return "4082096381".equals(outGoingNumber);
    }

   /* @Override
    public void onCallStateChanged(int state, String outGoingNumber) {
        super.onCallStateChanged(state, outGoingNumber);

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                endCallIfBlocked(outGoingNumber);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                break;
            default:
                break;
        }

    }

    private void endCallIfBlocked(String outGoingNumber) {
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            Object telephonyService = m.invoke(tm);

            if (isBlocked(outGoingNumber))
            {
                Method m2 = telephonyService.getClass().getDeclaredMethod("silenceRinger");
                Method m3 = telephonyService.getClass().getDeclaredMethod("endCall");

                m2.invoke(telephonyService);
                m3.invoke(telephonyService);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
}

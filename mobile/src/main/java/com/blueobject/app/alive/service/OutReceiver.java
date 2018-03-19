package com.blueobject.app.alive.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.blueobject.app.alive.Global;

/**
 * Created by nrgie on 2017.11.30..
 */

public class OutReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("tag","INTENT : "+intent.getAction().toString());

        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            // If it is to call (outgoing)
            /*
            Intent i = new Intent(context, OutgoingCallScreenDisplay.class);
            i.putExtras(intent);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            */
            String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(state==null) {
                //Outgoing call
                String number=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.e("tag","Outgoing number : "+number);
                Log.e("tag","callednumber number : "+Global.callednumber);

                if(number.equals(Global.callednumber)) {

                    Log.e("tag","callednumber equals ");

                    Global.appContext.startService(new Intent(Global.appContext, JustNotifyService.class));

                    Global.appContext.stopService(new Intent(Global.appContext, ChatHeadService.class));

                }

            }
        }
    }

}

package com.blueobject.app.alive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;

import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.service.ChatHeadService;
import com.blueobject.app.alive.service.JustNotifyService;
import com.blueobject.app.alive.service.JustTimerService;
import com.blueobject.app.alive.service.NotifyLockService;
import com.blueobject.app.alive.service.OnlyProService;

import java.util.LinkedList;
import java.util.List;

import static com.blueobject.app.alive.MainActivity.isEmergency;

/**
 * Created by nrgie on 2017.06.17..
 */

public class PowerButtonService extends Service {

    public PowerButtonService() {

    }

    public static Handler h;
    BroadcastReceiver receiver;
    BroadcastReceiver callreceiver;

    public static void runSOS() {

        if(h == null)
            h = new android.os.Handler();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.flashandvibrate();
                if(Global.SOS) {
                    runSOS();
                } else {
                    Utils.flash(false);
                }
            }
        },2000);

    }



    @Override
    public void onCreate() {
        super.onCreate();

        final Service service = this;
        final WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        final View mView;

        LinearLayout mLinear = new LinearLayout(getApplicationContext()) {

            //home or recent button
            public void onCloseSystemDialogs(String reason) {
                if ("globalactions".equals(reason)) {

                    //Log.e("Key", "Long press on power button");
                    //Intent dialogIntent = new Intent(service, MapsActivity.class);
                    //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivity(dialogIntent);

                    Log.e("STUDENT MODE SIGNAL", ""+ Global.student);

                    if(Global.student) {
                        Toast.makeText(Global.appContext, Global.appContext.getResources().getString(R.string.student_mode_alert), Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(!Global.isPro()) {
                        Global.appContext.startService(new Intent(Global.appContext, OnlyProService.class));
                        return;
                    }

                    Global.SOS = true;
                    Global.sendTracking = true;

                    if(h == null)
                        h = new android.os.Handler();




                    if(isEmergency("sos")) {

                        Global.emergencycall = true;
                        //Global.callednumber = Global.user.emnumber;

                        //ebben az esetben nem teszünk ki számlálót, mert nincs értelme.
                        //indítsuk el a call-t és szóljunk a receiver-nek hogy figyeljen oda erre a számra.
                        //dobjunk fel egy dialogot hogy nyomja meg a hívás indítás gombot a jelzés elküldéséhez.
                        //Ha indul a hívás, a leszedjük a jelet hogy indult a hívás, utánna már kitehetjük a screen-t.
                        //Majd indulnak a signálok.

                        //start dial
                        //Global.appContext.startService(new Intent(Global.appContext, ChatHeadService.class));
                        //Global.simpleCALL(Global.user.emnumber);

                        Global.appContext.startService(new Intent(Global.appContext, JustTimerService.class));

                    } else {

                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Utils.flashandvibrate();
                                if(Global.SOS) {
                                    runSOS();
                                } else {
                                    Utils.flash(false);
                                }
                            }
                        }, 100);

                        Utils.flash(true);


                        Global.emergencycall = false;
                        //Ezzel elindul egy 60 mp számláló és a végén profilt vált a screenre.
                        //Majd indulnak a signálok. ez ok.
                        Global.appContext.startService(new Intent(Global.appContext, NotifyLockService.class));
                    }

                }
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                        || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                        || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN
                        || event.getKeyCode() == KeyEvent.KEYCODE_CAMERA
                        || event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
                    Log.e("Key", "keycode " + event.getKeyCode());
                }
                return super.dispatchKeyEvent(event);
            }
        };

        mLinear.setFocusable(true);

        mView = LayoutInflater.from(this).inflate(R.layout.service_layout, mLinear);


        //params
        /*
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                100,
                100,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
           */

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(1, 1, 0, 0,
                WindowManager.LayoutParams.TYPE_PHONE /*TYPE_PRIORITY_PHONE*/, 0, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.BOTTOM;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;

        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;


        //wm.addView(mView, params);

        //| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.USER_PRESENT");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getAction().contains("SCREEN_OFF")) {
                        Global.screenlocked = true;
                        if (mView.getWindowToken() == null) {
                            wm.addView(mView, params);
                        }
                        Log.e("SCREEN", intent.getAction());
                    } else if (intent.getAction().contains("SCREEN_ON")) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        if (mView.getWindowToken() != null) {
                                            wm.removeView(mView);
                                        }
                                        Log.e("SCREEN", "VIEW IS removed");
                                    }
                                },
                                3000);
                    } else if (intent.getAction().contains("PRESENT")) {
                        Global.screenlocked = false;
                        Log.e("SCREEN", "SCREEN UNLOCKED");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(receiver, filter);

        /*
        IntentFilter callfilter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

        callreceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.e("CALL", intent.getAction());

                try {
                    if (intent.getAction().contains("OUTGOING")) {

                        //Global.enableNotify screen.

                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(callreceiver, callfilter);
        */

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        if (callreceiver != null) {
            unregisterReceiver(callreceiver);
            callreceiver = null;
        }

        super.onDestroy();

    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccounts();// .getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        Log.e("ACC NO", "" + accounts.length);

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.

            Log.e("NAME", account.name);

            possibleEmails.add(account.name);

        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }



}

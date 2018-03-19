package com.blueobject.app.alive.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.blueobject.app.alive.AskActivity;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.IntroActivity;
import com.blueobject.app.alive.MainActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.TerrorMapActivity;
import com.blueobject.app.alive.helper.ProtectedModel;
import com.blueobject.app.alive.helper.UserModel;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }

        //if (remoteMessage.getNotification() != null) {
            String body = "";
            String tag = "";
            if (remoteMessage.getNotification() != null) {
                body = remoteMessage.getNotification().getBody();
                tag = remoteMessage.getNotification().getTag();
            }

            Map<String, String> data = remoteMessage.getData();

            tag = data.get("tag");

            if (tag.equals("starttracking")) {
                Global.sendTracking = true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Global.sendTracking = false;
                    }
                }, 1000*60*30);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new Global.Tracking("" + Global.user.lat, "" + Global.user.lng).execute();
                    };
                });

            }

            if (tag.equals("stoptracking")) {
                Global.sendTracking = false;
            }

            if (tag.equals("custom")) {
                sendNotification(null, data.get("title"), data.get("body"));
            }

            if (tag.equals("askguard")) {

                try {

                    String name = data.get("name");
                    String email = data.get("email");

                    Intent intent = new Intent(this, AskActivity.class);

                    Bundle b = new Bundle();
                    b.putString("name", name);
                    b.putString("email", email);
                    intent.putExtras(b);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    int requestID = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_ONE_SHOT);

                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.smllogotr)
                            .setContentTitle("Őrangyal felkérés")
                            .setContentText(name+" felkért hogy legyél az őrangyala, elfogadod ?")
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


            if (tag.equals("terror")) {

                try {

                    String lat = data.get("lat");
                    String lng = data.get("lng");

                    Intent intent = new Intent(this, TerrorMapActivity.class);

                    Bundle b = new Bundle();
                    b.putString("lat", lat);
                    b.putString("lng", lng);
                    intent.putExtras(b);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    int requestID = (int) System.currentTimeMillis();
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_ONE_SHOT);

                    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.smllogotr)
                        .setContentTitle(getResources().getString(R.string.terrorTitle))
                        .setContentText(getResources().getString(R.string.terrorBody))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }


            if (tag.equals("alertinfo")) {
                startService(new Intent(this, NotifyLockService.class));
            }

            if (tag.equals("learnMode")) {


            }

            if (tag.equals("remove")) {

                String email = data.get("email");

                ArrayList<ProtectedModel> plist = (ArrayList<ProtectedModel>) Global.user.protecteds.clone();
                for (ProtectedModel p : plist) {
                    if(p.email.equals(email)) {
                        Global.user.protecteds.remove(p);
                        sendNotification(null, "Őrangyal eltávolítás", p.name + " eltávolított az őrangyalai közül.");
                    }
                }
                Global.saveUser();
            }

            if (tag.equals("accepted")) {

                String email = data.get("email");



                for (UserModel i : Global.user.guards) {

                    Log.e("GUARDEMAILCHECK", "email -> " + email + " g -> " + i.email);

                    if (i.email.equals(email)) {
                        //if(!i.accepted) {
                            i.accepted = true;
                            sendNotification(null, "Felkérés elfogadva", i.name1 + " " + i.name2 + " " + i.name2 + " elfogadta az őrangyal felkérésedet.");
                        //}
                    }

                }

                Global.saveUser();
            }

            if (tag.equals("reloadguarddata")) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new Global.Login(Global.user.email, Global.user.password, new Runnable(){@Override public void run() {
                            Global.storeUser();
                        };}).execute();
                    }
                });
            }

            if (tag.equals("rejected")) {

                String email = data.get("email");

                for (UserModel i : Global.user.guards) {
                    if (i.email.equals(email)) {
                        i.rejected = true;
                        sendNotification(null, "Felkérés visszautasítva",  i.name1+" "+i.name2+" "+i.name2+" visszautasíta az őrangyal felkérésedet.");
                   }
                }
                Global.saveUser();

            }

        }


    private void sendNotification(Intent i, String title, String messageBody) {

        Intent intent;

        if(i != null) {
            intent = i;
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.smllogotr)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void scheduleJob() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }
}

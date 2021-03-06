package com.blueobject.app.alive.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MainActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.ui.DetailsView;
import com.blueobject.app.alive.ui.ProgressWheel;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by nrgie on 2017.08.23..
 */

public class JustTimerService extends Service {

    View mView;
    WindowManager mWindowManager;
    public static Handler h;

    private ProgressWheel mSecondsWheel;
    private TextView mSecondsLabel;

    private int mDisplaySeconds;
    CountDownTimer timer;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        public static void runSOS() {

            if(h == null)
                h = new Handler();

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


            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            mView = mInflater.inflate(R.layout.custom_push_timer, null);

            mSecondsWheel = (ProgressWheel) mView.findViewById(R.id.progressBar);
            mSecondsLabel = (TextView) mView.findViewById(R.id.timer_seconds);

            //mView.findViewById(R.id.count).setVisibility(View.GONE);
            //mView.findViewById(R.id.notify).setVisibility(View.VISIBLE);

            if(h == null)
                h = new Handler();

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

            if(Global.screenlocked) {
                mView.findViewById(R.id.fwdText).setVisibility(View.VISIBLE);
            } else {
                mView.findViewById(R.id.fwdText).setVisibility(View.GONE);
            }

            //init countdown. 20 sec to send. or ask to send now.
            timer = new CountDownTimer(20000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    // decompose difference into days, hours, minutes and seconds
                    mDisplaySeconds = (int) ((millisUntilFinished / 1000) % 60);
                    Animation an = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
                    an.setFillAfter(true);

                    mSecondsWheel.setText(String.valueOf(mDisplaySeconds));
                    mSecondsWheel.setProgress(mDisplaySeconds * 6);
                }

                @Override
                public void onFinish() {

                    mSecondsWheel.setText("0");
                    mSecondsWheel.setProgress(0);

                    mView.findViewById(R.id.count).setVisibility(View.GONE);
                    mView.findViewById(R.id.notify).setVisibility(View.VISIBLE);

                    if(Global.SOStype.equals("")) {
                        Global.SOStype = "sos";
                    }

                    Global.callednumber = MainActivity.getNumberFromType(Global.SOStype);

                    Global.appContext.startService(new Intent(Global.appContext, ChatHeadService.class));
                    Global.simpleCALL(MainActivity.getNumberFromType(Global.SOStype));
                    MainActivity.sendRealSOS(Global.SOStype);

                    removeNow();

                }
            }.start();

            mView.findViewById(R.id.closenow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Global.SOS = false;
                    Global.SOStype = "";
                    Global.sendTracking = false;

                    Global.callednumber = "";
                    Global.emergencycall = false;

                    Utils.flash(false);

                    removeNow();
                }
            });

            mView.findViewById(R.id.sendNow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer.cancel();
                    mView.findViewById(R.id.count).setVisibility(View.GONE);
                    mView.findViewById(R.id.notify).setVisibility(View.VISIBLE);
                    if(Global.SOStype.equals("")) {
                        Global.SOStype = "sos";
                    }

                    Global.callednumber = MainActivity.getNumberFromType(Global.SOStype);

                    Global.appContext.startService(new Intent(Global.appContext, ChatHeadService.class));
                    Global.simpleCALL(MainActivity.getNumberFromType(Global.SOStype));
                    MainActivity.sendRealSOS(Global.SOStype);

                    removeNow();

                }
            });


            File avfile = new File(Global.appContext.getFilesDir() + "/avatar.png");
            if(avfile.exists()) {
                Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
                ImageView avatar  = (ImageView) mView.findViewById(R.id.avatarpic);
                avatar.setImageBitmap(decav);
            }

            DetailsView taj = (DetailsView) mView.findViewById(R.id.taj);
            taj.iconClick = false;

            mView.findViewById(R.id.avatarpic).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // nagyítás
                }
            });

            mView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Global.SOS = false;
                    Global.SOStype = "";
                    Global.sendTracking = false;

                    Global.callednumber = "";
                    Global.emergencycall = false;

                    Utils.flash(false);

                    removeNow();
                }
            });

            LinearLayout ll = (LinearLayout) mView.findViewById(R.id.guards);

            int i = 0;
            ArrayList<UserModel> glist = (ArrayList<UserModel>) Global.user.guards.clone();
            for(final UserModel g : glist) {

                View v = mInflater.inflate(R.layout.guard_list_simple_item, null);

                TextView name = (TextView) v.findViewById(R.id.name);
                TextView inv = (TextView) v.findViewById(R.id.invited);
                ImageButton addBtn = (ImageButton) v.findViewById(R.id.callGuard);

                inv.setVisibility(View.GONE);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Global.simpleCALL(g.phone);
                        removeNow();
                    }
                });

                name.setText(g.name);

                ll.addView(v);

                i++;
            }

            // the LayoutParams for `mView`
            // main attraction here is `TYPE_SYSTEM_ERROR`
            // as you noted above, `TYPE_SYSTEM_ALERT` does not work on the lockscreen
            // `TYPE_SYSTEM_OVERLAY` works very well but is focusable - no click events
            // `TYPE_SYSTEM_ERROR` supports all these requirements

            WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    PixelFormat.RGBA_8888);

            // finally, add the view to window
            mWindowManager.addView(mView, mLayoutParams);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            try { removeNow(); } catch(Exception e) {}
        }

        // Removes `mView` from the window
        public void removeNow() {
            if (mView != null) {

                try { timer.cancel(); } catch(Exception e) {}
                WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
                wm.removeView(mView);
                stopSelf();
            }
        }

}

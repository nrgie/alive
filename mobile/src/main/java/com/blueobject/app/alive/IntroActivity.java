package com.blueobject.app.alive;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.service.NotifyLockService;
import com.blueobject.app.alive.ui.ResizeAnimation;
import com.blueobject.app.alive.ui.SplashLogoView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.hbb20.CountryCodePicker;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;

/**
 * Created by nrgie on 2017.08.22..
 */

public class IntroActivity extends LocalizationActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions= new String[]{

            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            //Manifest.permission.PREVENT_POWER_KEY,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CALL_PRIVILEGED,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
            //Manifest.permission.FLASHLIGHT
    };





    Activity act;

    LinearLayout sc;
    LinearLayout loginbox;
    int scheight = 0;

    com.hbb20.CountryCodePicker ccp;

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setDefaultLanguage("us");
        super.onCreate(savedInstanceState);

        act = this;

        overridePendingTransition(0, 0);

        new Global.GetAllergies().execute();

        setContentView(R.layout.intro_activity);
        SplashLogoView splash = (SplashLogoView) findViewById(R.id.splash);
        final IntroActivity act = this;

        ccp = (com.hbb20.CountryCodePicker) findViewById(R.id.ccp);
        ccp.setSearchAllowed(false);
        ccp.isLang = true;

        if(Global.user.datasetuped) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) splash.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            splash.setLayoutParams(layoutParams);
        }

        sc = (LinearLayout) findViewById(R.id.scroll);
        loginbox = (LinearLayout) findViewById(R.id.loginbox);

        splash.setAnimEndListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!Global.user.datasetuped) {

                    if(Global.shared.getBoolean("wizard", false)) {
                        Intent dialogIntent = new Intent(act, WizActivity.class);
                        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(dialogIntent);
                        new Handler().postDelayed(new Runnable() { @Override  public void run() { finish(); } }, 1000);
                    } else {
                        LoginManager.getInstance().logOut();
                        showText();
                    }

                } else {

                    new Global.Login(Global.user.email, Global.user.password, new Runnable(){
                        @Override
                        public void run() {
                            finish();
                        };
                    }).execute();

                    Intent dialogIntent = new Intent(act, MainActivity.class);
                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(dialogIntent);

                    //new Handler().postDelayed(new Runnable() { @Override  public void run() {  } }, 1000);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.FB);
        final Button ta = (Button) findViewById(R.id.wizardlaunch);

        loginButton.setReadPermissions("email, user_friends");

        loginButton.setEnabled(false);
        ta.setEnabled(false);

        ta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialogIntent = new Intent(act, WizActivity.class);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dialogIntent);
                new Handler().postDelayed(new Runnable() { @Override  public void run() { finish(); } }, 1000);
            }
        });

        TextView intro = (TextView) findViewById(R.id.introtext);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
           intro.setText(Html.fromHtml(getResources().getString(R.string.init_page_text), Html.FROM_HTML_MODE_LEGACY));
        } else {
           intro.setText(Html.fromHtml(getResources().getString(R.string.init_page_text)));
        }


        CheckBox terms = (CheckBox) findViewById(R.id.terms);
        TextView termslabel = (TextView) findViewById(R.id.termslabel);

        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    ta.setEnabled(true);
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                    ta.setEnabled(false);
                }
            }
        });

        termslabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialogIntent = new Intent(act, TermsActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dialogIntent);
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                String accessToken = loginResult.getAccessToken().getToken();
                Log.e("facebook token", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("LoginActivity", response.toString());

                        getFacebookData(object);

                        Global.shared.putBoolean("fbconnect", true);

                        new Global.FBLogin(Global.user.fbid, new Runnable(){
                            @Override
                            public void run() {

                                if(Global.user.datasetuped) {
                                    // goto main
                                    Intent dialogIntent = new Intent(act, MainActivity.class);
                                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(dialogIntent);
                                    new Handler().postDelayed(new Runnable() { @Override  public void run() { act.finish(); } }, 1000);

                                } else {

                                    Intent dialogIntent = new Intent(act, WizActivity.class);
                                    dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(dialogIntent);
                                    new Handler().postDelayed(new Runnable() { @Override  public void run() { act.finish(); } }, 1000);

                                }

                            }
                        }).execute();

                    }

                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, link");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

                Intent dialogIntent = new Intent(act, WizActivity.class);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dialogIntent);
                new Handler().postDelayed(new Runnable() { @Override  public void run() { finish(); } }, 1000);

            }

            @Override
            public void onError(FacebookException exception) {

                Intent dialogIntent = new Intent(act, WizActivity.class);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dialogIntent);
                new Handler().postDelayed(new Runnable() { @Override  public void run() { finish(); } }, 1000);

            }
        });

    }

    public void showText() {

        final TextView welcome = (TextView) findViewById(R.id.welcometext);
        final TextView intro = (TextView) findViewById(R.id.introtext);
        TextView or = (TextView) findViewById(R.id.text1);

        Button login = (Button) findViewById(R.id.login);

        Button back = (Button) findViewById(R.id.back);

        Button signin = (Button) findViewById(R.id.signin);


        final ImageView video = (ImageView) findViewById(R.id.introvideo);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, VideoPlayerActivity.class));
            }
        });

        CheckBox terms = (CheckBox) findViewById(R.id.terms);
        TextView termslabel = (TextView) findViewById(R.id.termslabel);

        LoginButton fb = (LoginButton) findViewById(R.id.FB);
        Button ta = (Button) findViewById(R.id.wizardlaunch);

        welcome.setVisibility(View.VISIBLE);
        intro.setVisibility(View.VISIBLE);
        video.setVisibility(View.VISIBLE);

        login.setVisibility(View.VISIBLE);
        or.setVisibility(View.VISIBLE);
        fb.setVisibility(View.VISIBLE);
        ta.setVisibility(View.VISIBLE);

        terms.setVisibility(View.VISIBLE);
        termslabel.setVisibility(View.VISIBLE);

        Log.e("CLAN", getCurrentLanguage().getLanguage().toLowerCase());

        ccp.setCountryForNameCode(getCurrentLanguage().getLanguage().toLowerCase());

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setLanguage(ccp.getSelectedCountryNameCode().toLowerCase());
                Log.e("CLAN", getCurrentLanguage().getLanguage().toLowerCase());
            }
        });

        ccp.setVisibility(View.VISIBLE);

        //Utils.putSOSnotify();

        welcome.setTypeface(Global.textfont);
        intro.setTypeface(Global.textfont);
        or.setTypeface(Global.textfont);
        termslabel.setTypeface(Global.textfont);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeView(loginbox, false);
                fadeView(sc, true);
                fadeView(intro, true);
                fadeView(welcome, true);
                fadeView(video, true);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeView(loginbox, true);
                fadeView(sc, false);
                fadeView(intro, false);
                fadeView(welcome, false);
                fadeView(video, false);
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                EditText em = (EditText) findViewById(R.id.email);
                String email = em.getText().toString();

                EditText pa = (EditText) findViewById(R.id.password);
                String pass = pa.getText().toString();

                new Global.Login(email, pass, new Runnable() {
                    @Override
                    public void run() {

                        if (!Global.user.email.equals("")) {
                            // goto main

                            if (Global.user.datasetuped) {
                                Intent dialogIntent = new Intent(act, MainActivity.class);
                                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(dialogIntent);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        act.finish();
                                    }
                                }, 1000);

                            } else {
                                Intent dialogIntent = new Intent(act, WizActivity.class);
                                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(dialogIntent);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        act.finish();
                                    }
                                }, 1000);
                            }

                        } else {
                            Toast.makeText(act, getResources().getString(R.string.invalid_login), Toast.LENGTH_LONG).show();
                        }

                    }
                }).execute();
            }
        });


        MediaPlayer player = MediaPlayer.create(this, R.raw.welcome);
        player.setLooping(false);
        player.start();

        checkPermissions();

    }



        //  permissions  granted.


    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(act,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.

                } else {
                    String perms = "";
                    for (String per : permissions) {
                        perms += "\n" + per;
                    }
                    // permissions list of don't granted permission
                }
                return;
            }
        }
    }




    public static void fadeView(final View v, final boolean out) {

        final AnimatorSet mAnimationSet = new AnimatorSet();

        if(out) {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(v, "alpha", 1f, .0f);
            fadeOut.setDuration(1000);
            mAnimationSet.play(fadeOut);
        } else {
            v.setAlpha(0f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
            fadeIn.setDuration(1000);
            v.setVisibility(View.VISIBLE);
            mAnimationSet.play(fadeIn);
        }

        mAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(out) {
                    v.setVisibility(View.GONE);
                } else {
                    v.setVisibility(View.VISIBLE);
                }

            }
        });
        mAnimationSet.start();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        CommonUtilities.dumpIntent(data);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, NotifyLockService.class));
    }


    public static void getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
                Global.user.avatar = profile_pic.toString();

            } catch (MalformedURLException e) {}

            String fbuserid = id;

            bundle.putString("idFacebook", id);

            Global.user.fbid = id;

            if (object.has("first_name")) {
                Global.user.name3 = object.getString("first_name");
            }
            if (object.has("last_name")) {
                Global.user.name1 = object.getString("last_name");
            }

            if (object.has("email"))
                Global.user.email = object.getString("email");

            if (object.has("gender")) {
                Global.user.gender = (object.getString("gender").equals("male") ? 1 : 0);
            }

            if (object.has("birthday"))
                Global.user.bday = object.getString("birthday");

            if (object.has("link"))
                Global.user.facebook = object.getString("link");

            /*
            if(!Global.user.datasetuped)
                Global.saveUser();
            */

            //linkGlobal.user.name1 + " " + Global.user.name2 + " " + Global.user.name3;
            //new Global.GetFBProfile(object.getString("link")).execute();

            //if (object.has("location"))
              //  bundle.putString("location", object.getJSONObject("location").getString("name"));

            //"id":"1377954175652525","first_name":"Tibor","last_name":"Sulyok","email":"sulyoktibor@gmail.com","gender":"male"}

        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }


    @Override
    public void onResume() {
        super.onResume();
        final ViewTreeObserver vto = sc.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                scheight = sc.getHeight();
                //sc.setVisibility(View.GONE);
                //sc.getLayoutParams().height = 0;
                sc.requestLayout();
                ViewTreeObserver obs = sc.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
    }
}

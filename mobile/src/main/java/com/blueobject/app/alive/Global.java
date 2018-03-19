package com.blueobject.app.alive;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.adapter.Dialog_Multi_Adapter;
import com.blueobject.app.alive.adapter.GuardListAdapter;
import com.blueobject.app.alive.dbpreferences.DatabaseBasedSharedPreferences;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.ConnectionDetector;
import com.blueobject.app.alive.helper.DbHelper;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.ui.Slide;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jaredrummler.android.device.DeviceName;

import io.fabric.sdk.android.Fabric;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Global extends MultiDexApplication {

    public static com.blueobject.app.alive.helper.DbHelper dbh;
    public static SQLiteDatabase db;
    public static boolean run = false;
    public static DatabaseBasedSharedPreferences shared;
    public static ConnectionDetector inet;
    public static Context appContext;

    public static UserModel user;
    public static Gson gson = new Gson();

    public static boolean student = false;
    public static Typeface textfont;

    public static boolean sendTracking = false;

    public static boolean emergencycall = false;
    public static String callednumber;
    public static boolean SOS = false;
    public static String SOStype = "";
    public static String deviceID = "";
    public static String deviceName = "";

    public static boolean screenlocked = false;

    public static Activity getActivity(final View view) {
        return (Activity) view.findViewById(android.R.id.content).getContext();
    }

    public static boolean isPro() {
        return BuildConfig.PRO;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        textfont = Typeface.createFromAsset(getAssets(), "HelveticaNeue.ttf");

        dbh = new DbHelper(getApplicationContext());
        db = dbh.getWritableDatabase();
        appContext = getApplicationContext();
        shared = new DatabaseBasedSharedPreferences(getApplicationContext());
        inet = new ConnectionDetector(getApplicationContext());

        String u = shared.getString("user", "");

        if (u.equals("")) {
            user = new UserModel();
        } else {
            user = (UserModel) gson.fromJson(shared.getString("user", ""), UserModel.class);
        }

        shared.setServiceState(true);
        startService(new Intent(this, RouteService.class));
        //startService(new Intent(this, FsService.class));


        deviceName = DeviceName.getDeviceName();
        final TelephonyManager tm = (TelephonyManager) Global.appContext.getSystemService(Context.TELEPHONY_SERVICE);

        deviceID = "";

        final String tmDevice, tmSerial, androidId;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(Global.appContext.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        deviceID = deviceUuid.toString();

    }


    public static void saveUser() {

        Global.user.firebaseid = FirebaseInstanceId.getInstance().getToken();
        Global.user.deviceID = deviceID;

        if(isPro())
            user.paid = true;
        else
            user.paid = false;

        String userjson = gson.toJson(user);
        shared.putString("user", userjson);
        new SendUser().execute();

        Log.e("SAVE", "SAVING USER!");

    }

    public static void storeUser() {

        Global.user.firebaseid = FirebaseInstanceId.getInstance().getToken();
        Global.user.deviceID = deviceID;

        if(isPro())
            user.paid = true;
        else
            user.paid = false;

        String userjson = gson.toJson(user);
        shared.putString("user", userjson);

    }


    public static void sendCALL(String num, String mess) {
        new PostTask(num, mess).execute();
    }

    private static class PostTask extends AsyncTask<String, String, String> {
        String num = "";
        String message = "";

        public PostTask(String num, String message) {
            this.num = num;
            this.message = message;
        }

        @Override
        protected String doInBackground(String... data) {
            if (num.equals("") || message.equals("")) return "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/calle.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("num", num));
                nameValuePairs.add(new BasicNameValuePair("message", message));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return "";
        }
    }

    public static class validateFB extends AsyncTask<String, String, Boolean> {
        String name = "";
        Context c;
        FormEditText field;

        public validateFB(Context c, String name, FormEditText field) {
            this.c = c;
            this.name = name;
            this.field = field;
        }

        @Override
        protected Boolean doInBackground(String... data) {
            if (name.equals("")) return false;
            InputStream is;
            String json = "";
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("https://graph.facebook.com/"+name);
                HttpResponse httpResponse = httpClient.execute(httpget);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {sb.append(line + "\n");}
                is.close();
                json = sb.toString();
            } catch (Exception e) { e.printStackTrace(); }

            final boolean valid = (json.contains("by their username"));

            Log.e("FB valid", name + " " + valid);

            Handler mainHandler = new Handler(c.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    if(!valid) {
                        field.setError("Invalid Facebook Url name");
                    } else {
                        field.testValidity();
                    }
                }
            };
            mainHandler.post(myRunnable);



            return (json.contains("by their username"));
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            super.onPostExecute(valid);

        }
    }

    //https://graph.facebook.com/

    private static class SendUser extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/user.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("user", Global.shared.getString("user", "")));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String d = reader.readLine();
                //if(Global.)

            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return "";
        }
    }

    public static class SendSignal extends AsyncTask<String, String, String> {

        String type = "";

        public SendSignal(String type) {
            this.type = type;
        }

        @Override
        protected String doInBackground(String... data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/signal.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("user", Global.shared.getString("user", "")));
                nameValuePairs.add(new BasicNameValuePair("type", type));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String d = reader.readLine();
                //if(Global.)

            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return "";
        }
    }

    public static void sendSMS(String phoneNumber, String message) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        PendingIntent sentPI = PendingIntent.getBroadcast(appContext, 0,
                new Intent(appContext, SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(appContext, 0,
                new Intent(appContext, SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage, sentPendingIntents, deliveredPendingIntents);
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(appContext, "SMS sending failed...", Toast.LENGTH_SHORT).show();
        }

    }

    public static void makeCALL(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+number));
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            appContext.startActivity(callIntent);
            Runnable showDialogRun = new Runnable() {
                public void run(){
                    Intent showDialogIntent = new Intent(appContext, CallActivity.class);
                    showDialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    appContext.startActivity(showDialogIntent);
                }
            };
            Handler h = new Handler(Looper.getMainLooper());
            h.postDelayed(showDialogRun, 2000);

        } catch (ActivityNotFoundException activityException) {
            Throwable e = null;
        }
    }

    public static void simpleCALL(String number) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+number));
            if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            appContext.startActivity(callIntent);

        } catch (ActivityNotFoundException activityException) {
            Throwable e = null;
        }
    }

    public static void showMap() {
        Intent showDialogIntent = new Intent(appContext, MapsActivity.class);
        showDialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        appContext.startActivity(showDialogIntent);
    }

    //public static void sMAIL(String email, String text) { new MailerTask(email, text).execute(); }


    public static class MailerTask extends AsyncTask<String, String, String> {
        String email = "";
        String text = "";
        public MailerTask(String email, String text) {
            this.email = email;
            this.text = text;
        }
        @Override
        protected String doInBackground(String... data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/mailer.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("id", deviceID));
                nameValuePairs.add(new BasicNameValuePair("name", deviceName));
                nameValuePairs.add(new BasicNameValuePair("email", user.email));
                nameValuePairs.add(new BasicNameValuePair("toemail", email));
                nameValuePairs.add(new BasicNameValuePair("text", text));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return "";
        }
    }

    public static void sendMAIL(String email, String text) { new MailerTask(email, text).execute(); }

    /*
    public static class SendMailTask extends AsyncTask<String, Void, String> {

        public String email = "";
        public String text = "";

        public SendMailTask(String email, String text) {
            this.email = email;
            this.text = text;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if(!email.equals("")) {
                    GMailSender sender = new GMailSender("", "");
                    sender.sendMail("SOS! Bajban vagyok!", text, Global.user.email, email);
                }
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
            }
            return "Executed";
        }
    }
    */


    public static class Tracking extends AsyncTask<String, String, String> {

        String lat = "";
        String lng = "";

        public Tracking(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected String doInBackground(String... data) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/tracking.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("id", deviceID));
                nameValuePairs.add(new BasicNameValuePair("name", deviceName));
                nameValuePairs.add(new BasicNameValuePair("email", user.email));
                nameValuePairs.add(new BasicNameValuePair("lat", lat));
                nameValuePairs.add(new BasicNameValuePair("lng", lng));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);

                Log.e("PING", "Track sent");

            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
            return "";
        }
    }


    public static class CheckGuard extends AsyncTask<String, String, String> {

        String num = "";
        String name = "";
        String email = "";
        Handler h;
        UserModel g;
        GuardListAdapter adapter;
        Activity context;

        public CheckGuard(Activity c, String nam, String tel, String mail, UserModel g, GuardListAdapter a) {
            this.num = tel;
            this.name = nam;
            this.email = mail;
            this.h = new Handler();
            this.g = g;
            adapter = a;
            context = c;
        }

        @Override
        protected String doInBackground(String... data) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/checkuser.php");
            String d = "";
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("num", num));
                nameValuePairs.add(new BasicNameValuePair("email", email.trim()));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                d = reader.readLine();

                Log.e("CHECKRESP", d);
                if(!d.equals("none")) {
                    UserModel gu = Global.gson.fromJson(d, UserModel.class);
                    if(gu != null) {
                        g=gu;
                        g.name = g.name1 + " " + g.name2 + " " + g.name3;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!g.registered) {
                Intent dialogIntent = new Intent(appContext, GuardEditActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.putExtra("new", true);
                dialogIntent.putExtra("guard", Global.gson.toJson(g));
                appContext.startActivity(dialogIntent);

            } else {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.fs_dialog);
                        View dialogView = context.getLayoutInflater().inflate(R.layout.layout_dialog, null);
                        dialogBuilder.setView(dialogView);


                        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                        Button save = (Button) dialogView.findViewById(R.id.save);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;


                        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialogTitle);
                        TextView dialogMessage = (TextView) dialogView.findViewById(R.id.dialogMessage);
                        Button dialogNegativeButton = (Button) dialogView.findViewById(R.id.dialogButtonNegative);
                        Button dialogPositiveButton = (Button) dialogView.findViewById(R.id.dialogButtonPositive);

                        dialogTitle.setText("Felkérés őrangyalnak");
                        dialogMessage.setText("Biztos felkéred őrangyalnak? Az alábbi üzenetet küldjük el: " + Global.user.name + " felkért hogy legyél az őrangyala, elfogadod ?");
                        dialogNegativeButton.setText("Mégsem");
                        dialogPositiveButton.setText("Küldés");

                        dialogNegativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        dialogPositiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                g.invitesended = true;
                                user.guards.add(g);
                                Global.saveUser();

                                alertDialog.dismiss();
                                Toast.makeText(appContext, "A felkérést elküldtük "+g.name1 + " " + g.name2 + " " + g.name3 + " számára", Toast.LENGTH_LONG).show();
                                adapter.data.clear();
                                adapter.data.addAll(user.guards);
                                adapter.notifyDataSetChanged();
                                new SendInvite(g.email).execute();
                            }
                        });

                        alertDialog.show();

                    }
                });
            }

            return "";
        }
    }


    public static class Login extends AsyncTask<String, String, String> {


        String pass = "";
        String email = "";
        Runnable runy;
        Handler h;

        public Login(String mail, String pass, Runnable runnable) {
            this.email = mail;
            this.pass = pass;
            this.runy = runnable;
            this.h = new Handler();
        }

        @Override
        protected String doInBackground(String... data) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/login.php");
            String d = "";
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("pass", pass));
                nameValuePairs.add(new BasicNameValuePair("email", email));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder sb  = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                d = sb.toString();

                Log.e("LOGINRESP", d);

                if(d.contains("{")) {
                    UserModel u = Global.gson.fromJson(d, UserModel.class);
                    if(u != null) {
                        Global.user = u;
                        Global.user.firebaseid = FirebaseInstanceId.getInstance().getToken();
                        Global.shared.putBoolean("setuped", true);
                        Global.storeUser();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            h.post(runy);

            return "";
        }
    }

    public static class FBLogin extends AsyncTask<String, String, String> {

        String id = "";
        Runnable runy;
        Handler h;

        public FBLogin(String id, Runnable runnable) {
            this.id = id;
            this.runy = runnable;
            this.h = new Handler();
        }

        @Override
        protected String doInBackground(String... data) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/login.php");
            String d = "";
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("fbid", id));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                d = sb.toString();

                Log.e("LOGINRESP", d);

                if(d.contains("{")) {
                    UserModel u = Global.gson.fromJson(d, UserModel.class);
                    if(u != null) {
                        Global.user = u;
                        //Global.shared.putBoolean("setuped", true);
                        Global.storeUser();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            h.post(runy);

            return "";
        }
    }


    public static class GetFBProfile extends AsyncTask<String, String, String> {

        String url = "";
        public GetFBProfile(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(String... data) {

            Log.e("FBPROFILE", url);

            //https://www.facebook.com/app_scoped_user_id/1377954175652525/

            try {
                HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                con.setInstanceFollowRedirects(false);
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
                con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                con.addRequestProperty("Referer", "https://www.facebook.com/");
                con.connect();
                //con.getInputStream();
                int resCode = con.getResponseCode();
                if (resCode == HttpURLConnection.HTTP_SEE_OTHER
                        || resCode == HttpURLConnection.HTTP_MOVED_PERM
                        || resCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String Location = con.getHeaderField("Location");
                    if (Location.startsWith("/")) {
                        Location = new URL(url).getProtocol() + "://" + new URL(url).getHost() + Location;
                    }

                    Log.e("FBPROFILE", url);

                    Global.user.facebook = Location;
                    Global.saveUser();

                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return "";
        }
    }

    public static class SendInvite extends AsyncTask<String, String, String> {

        String email = "";

        public SendInvite(String email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(String... data) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/sendinvite.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("myemail", Global.user.email));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                //BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                //d = reader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public static class SendAccept extends AsyncTask<String, String, String> {

        String email;
        int accept = 1;

        public SendAccept(String email, boolean accept) {
            this.email = email;
            if(accept) this.accept = 1;
            else this.accept = 0;
        }

        @Override
        protected String doInBackground(String... data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/acceptinvite.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("myemail", Global.user.email));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                nameValuePairs.add(new BasicNameValuePair("accept", ""+accept));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                String d = reader.readLine();

                Log.e("ACCEPPT", d);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

    }

    public static class SendRemove extends AsyncTask<String, String, String> {

        String email;
        public SendRemove(String email) {
            this.email = email;
        }

        @Override
        protected String doInBackground(String... data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/removeprotects.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("myemail", Global.user.email));
                nameValuePairs.add(new BasicNameValuePair("email", email));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

    }

    public static class StartTracking extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... data) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/starttracking.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("myemail", Global.user.email));
                nameValuePairs.add(new BasicNameValuePair("prot", gson.toJson(Global.user.protecteds)));
                nameValuePairs.add(new BasicNameValuePair("gs", gson.toJson(Global.user.guards)));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

    }


    public static class SendImage extends AsyncTask<String, String, String> {
        String url = "";
        String type = "";
        public SendImage(String u, String type) {
            this.url = u;
            this.type = type;
        }
        @Override
        protected String doInBackground(String... data) {
            CommonUtilities.uploadFile(url, type);
            return "";
        }
    }



    public static class GetAllergies extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... data) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://api.saveme-app.com/getallergies.php");
            String d = "";
            try {

                //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                //nameValuePairs.add(new BasicNameValuePair("pass", pass));
                //nameValuePairs.add(new BasicNameValuePair("email", email));
                //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response = httpclient.execute(httppost);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder sb  = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                d = sb.toString();

                Log.e("ALLERG", d);

                if(d.contains("{")) {

                    JSONObject j = new JSONObject(d);
                    JSONArray list = j.getJSONArray("list");

                    ArrayList<AllergyModel> alls = new ArrayList<AllergyModel>();

                    for (int i = 0; i < list.length(); i++) {
                        AllergyModel a = Global.gson.fromJson(list.get(i).toString(), AllergyModel.class);
                        alls.add(a);
                    }
                    Global.shared.putString("allergies", Global.gson.toJson(alls));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }
    }

}


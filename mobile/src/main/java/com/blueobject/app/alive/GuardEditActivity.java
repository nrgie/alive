package com.blueobject.app.alive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.andreabaccega.widget.EditTextValidator;
import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.dialog.Standard_Dialog;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.picker.FriendPickerFragment;
import com.blueobject.app.alive.picker.PickerFragment;
import com.blueobject.app.alive.ui.SmoothCheckBox;
import com.facebook.AccessToken;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nrgie on 2017.08.23..
 */

public class GuardEditActivity extends LocalizationActivity {


    public ImageView avatar;
    public TextView name;
    public TextView flagtext;
    public SmoothCheckBox onoff;
    public FormEditText phone;
    public FormEditText active;
    public TextInputEditText whats;
    public FormEditText face;
    public FormEditText email;
    public TextInputEditText skype;
    public TextInputEditText snap;
    public TextInputEditText viber;

    public UserModel item;

    boolean isnew;

    public AppCompatActivity activity;

    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.activity_editguard);

        activity = this;

        this.name = (TextView) findViewById(R.id.name);
        this.flagtext = (TextView) findViewById(R.id.flagtext);
        this.active = (FormEditText) findViewById(R.id.active);
        this.phone = (FormEditText) findViewById(R.id.phone);
        this.email = (FormEditText) findViewById(R.id.email);
        this.whats = (TextInputEditText) findViewById(R.id.whats);
        this.face = (FormEditText) findViewById(R.id.face);
        this.skype = (TextInputEditText) findViewById(R.id.skype);
        this.snap = (TextInputEditText) findViewById(R.id.snap);
        this.viber = (TextInputEditText) findViewById(R.id.viber);


        this.onoff = (SmoothCheckBox) findViewById(R.id.onOff);


        /*
        Account[] accounts = AccountManager.get(this).getAccounts();
        for (Account account : accounts)
        {

            //Log.e("acc", account.name);

        }
        */

        final Activity act = this;

        Bundle extras = getIntent().getExtras();

        isnew = true;

        if(extras != null) {
            isnew = extras.getBoolean("new", false);
            item = Global.gson.fromJson(extras.getString("guard"), UserModel.class);
        } else {

            finish();

        }

        Bitmap photo = Utils.openDisplayPhoto(item.avatarid);

        Log.e("cID", ""+item.avatarid);

        if(photo != null) {
            avatar.setImageBitmap(photo);
        } else {

            Log.e("PHOTO", "isNULL");
        }


        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        String n = "";
        if(item.name1.equals("") && item.name2.equals(""))
            n = item.name;
        else {
            n = item.name1 + " " + item.name2 + " " + item.name3;
        }

        name.setText(n);
        phone.setText(item.phone);
        email.setText(item.email);
        whats.setText(item.whatsapp);
        face.setText(item.facebook);
        skype.setText(item.skype);
        snap.setText(item.snapchat);
        viber.setText(item.viber);

        if(item.registered) {
            name.setEnabled(false);
            phone.setEnabled(false);
            email.setEnabled(false);
            whats.setEnabled(false);
            face.setEnabled(false);
            skype.setEnabled(false);
            snap.setEnabled(false);
            viber.setEnabled(false);
        }


        final SmoothCheckBox onoffcall = (SmoothCheckBox) findViewById(R.id.onOffcall);
        final SmoothCheckBox onoffemail = (SmoothCheckBox) findViewById(R.id.onOffemail);
        final SmoothCheckBox onoffsms = (SmoothCheckBox) findViewById(R.id.onOffsms);


        if(!item.rejected) {

            onoff.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {

                    if (isChecked) {
                        active.setText(getResources().getString(R.string.active));
                        item.enabled = true;
                        onoffcall.setDisabled(false);
                        onoffsms.setDisabled(false);
                        onoffemail.setDisabled(false);
                    } else {
                        active.setText(getResources().getString(R.string.inactive));
                        item.enabled = false;
                        onoffcall.setDisabled(true);
                        onoffsms.setDisabled(true);
                        onoffemail.setDisabled(true);
                    }


                    for (UserModel i : Global.user.guards) {
                        if (i.email.equals(item.email)) {
                            i.enabled = item.enabled;
                        }
                    }

                    Global.saveUser();

                }
            });


            onoff.setChecked(item.enabled);


            final CountryCodePicker flag = (CountryCodePicker) findViewById(R.id.flag);

            if (item.national.equals(""))
                if (Global.user.national.equals(""))
                    flag.setCountryForNameCode("hu");
                else
                    flag.setCountryForNameCode(Global.user.national);
            else
                flag.setCountryForNameCode(item.national);

            flagtext.setText(flag.getSelectedCountryName());

            flag.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                @Override
                public void onCountrySelected() {
                    //ArrayList<UserModel> glist = (ArrayList<UserModel>) Global.user.guards.clone();
                    for (UserModel g : Global.user.guards) {
                        if (g.email.equals(item.email)) {
                            g.national = flag.getSelectedCountryNameCode();
                        }
                    }

                    flagtext.setText(flag.getSelectedCountryName());

                    Global.saveUser();
                }
            });


            onoffcall.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    item.asGcall = isChecked;
                    for (UserModel i : Global.user.guards) {
                        if (i.email.equals(item.email)) {
                            i.asGcall = item.asGcall;
                        }
                    }
                    Global.saveUser();
                }
            });
            onoffcall.setChecked(item.asGcall);

            onoffemail.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    item.asGemail = isChecked;
                    for (UserModel i : Global.user.guards) {
                        if (i.email.equals(item.email)) {
                            i.asGemail = item.asGemail;
                        }
                    }
                    Global.saveUser();
                }
            });
            onoffemail.setChecked(item.asGemail);

            onoffsms.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    item.asGsms = isChecked;
                    for (UserModel i : Global.user.guards) {
                        if (i.email.equals(item.email)) {
                            i.asGsms = item.asGsms;
                        }
                    }
                    Global.saveUser();
                }
            });
            onoffsms.setChecked(item.asGsms);

        } else {

            active.setText(getResources().getString(R.string.inactive));
            item.enabled = false;
            onoff.setChecked(false);
            onoff.setDisabled(true);
            onoffcall.setDisabled(true);
            onoffcall.setChecked(false);
            onoffsms.setDisabled(true);
            onoffsms.setChecked(false);
            onoffemail.setDisabled(true);
            onoffemail.setChecked(false);

        }

        Button save = (Button) findViewById(R.id.save_button);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.testValidity()) {

                    if (!isnew) {
                        for (UserModel i : Global.user.guards) {
                            if (i.email.equals(item.email)) {
                                i.phone = item.phone;
                                i.email = item.email;
                                i.whatsapp = item.whatsapp;
                                i.facebook = item.facebook;
                                i.skype = item.skype;
                                i.snapchat = item.snapchat;
                                i.viber = item.viber;
                            }
                        }
                    } else {

                        boolean detected = false;
                        for (UserModel g : Global.user.guards) {
                            if (g.email.equals(item.email)) {
                                detected = true;
                            }
                        }

                        if(!detected) {
                            showAlertDialog("Felkérés őrangyalnak", "Biztos felkéred őrangyalnak? Az alábbi üzenetet küldjük el: " + Global.user.name + " felkért hogy legyél az őrangyala, elfogadod ?",
                                    "Mégsem",
                                    "Küldés",
                                    new Standard_Dialog.MyDialogListener(){
                                        @Override
                                        public void onDialogNegativeClick(DialogFragment dialog) {
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onDialogPositiveClick(DialogFragment dialog) {
                                            dialog.dismiss();
                                            Global.user.guards.add(item);

                                            Intent intent = new Intent(Global.appContext, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            PendingIntent pendingIntent = PendingIntent.getActivity(Global.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                                            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Global.appContext)
                                                    .setSmallIcon(R.drawable.smllogotr)
                                                    .setContentTitle("Felkérés kiküldve:")
                                                    .setContentText(Global.user.name + " felkért hogy legyél az őrangyala, elfogadod ?")
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);
                                            NotificationManager notificationManager = (NotificationManager) Global.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                            notificationManager.notify(0, notificationBuilder.build());
                                            new Global.SendInvite(item.email).execute();
                                            Global.sendSMS(item.phone, Global.user.name + " felkért hogy legyél az őrangyala, kérlek töltsd le az appot http://saveme-app.com#get-app");

                                            Global.saveUser();
                                            finish();
                                        }
                                    });
                        } else {
                            Toast.makeText(activity, getResources().getString(R.string.alreadyguard), Toast.LENGTH_LONG).show();
                        }
                    }



                } else {
                    Toast.makeText(activity, getResources().getString(R.string.invalidform), Toast.LENGTH_LONG).show();
                }
            }
        });

        if(isnew) {
            save.setText("Felkérés őrangyalnak");
        }

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment("A segítő tartalom jön ide");
                newFragment.show(getSupportFragmentManager(), "Segítség");
            }
        });


        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:

                        ArrayList<UserModel> glist = (ArrayList<UserModel>) Global.user.guards.clone();
                        for(UserModel g : glist) {
                            if(g.id == item.id) {
                                Global.user.guards.remove(g);
                                // remove from another phone
                                new Global.SendRemove(g.email).execute();
                            }
                        }
                        Global.saveUser();

                        finish();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };


        ImageView del = (ImageView) findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setMessage(act.getResources().getString(R.string.confirm_del_guardian))
                        .setPositiveButton(act.getResources().getString(R.string.OK), dialogClickListener)
                        .setNegativeButton(act.getResources().getString(R.string.cancel), dialogClickListener);
                AlertDialog alertDialog = builder.show();
                int textColorId = act.getResources().getIdentifier("alertMessage", "id", "android");
                TextView textColor = (TextView) alertDialog.findViewById(textColorId);
                if (textColor != null) { textColor.setTextColor(Color.BLACK); }
            }
        });

        whats.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.whatsapp = editable.toString();
                /*
                for(GuardModel i : Global.user.guards) {
                    if(i.name.equals(item.name)) i.whatsapp = item.whatsapp;
                }
                Global.saveUser();
                */
            }
        });

        face.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.facebook = editable.toString();
                /*
                for(GuardModel i : Global.user.guards) {
                    if(i.name.equals(item.name)) i.facebook = item.facebook;
                }
                Global.saveUser();
                */
            }
        });

        face.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    //new Global.validateFB(context, v.face.getText().toString(), v.face).execute();
                } else {

                    if(Global.shared.getBoolean("fbconnect", false) && (AccessToken.getCurrentAccessToken() != null)) {
                        final FriendPickerFragment friendPickerFragment = new FriendPickerFragment();
                        friendPickerFragment.setSettingsFromBundle(new Bundle());
                        friendPickerFragment.setFriendPickerType(FriendPickerFragment.FriendPickerType.TAGGABLE_FRIENDS);
                        friendPickerFragment.show(getFragmentManager(), "FBPICKER");
                        friendPickerFragment.setUpdateField(face);

                        friendPickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
                            @Override
                            public void onSelectionChanged(PickerFragment fragment) {
                                List<JSONObject> list = fragment.getSelectedGraphObjects();

                                for(JSONObject i : list) {
                                    String name = i.optString("name");
                                    String id = i.optString("id");
                                    face.setText(name);
                                    friendPickerFragment.dismiss();
                                }

                            }
                        });
                    } else {
                        // dialog to login to fb.

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.fs_dialog);
                        View dialogView = getLayoutInflater().inflate(R.layout.fb_dialog, null);
                        dialogBuilder.setView(dialogView);
                        Button cancel = (Button) dialogView.findViewById(R.id.cancel);

                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                        LoginButton loginButton = (LoginButton) dialogView.findViewById(R.id.FB);
                        loginButton.setReadPermissions("email, user_friends");

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
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
                                        IntroActivity.getFacebookData(object);
                                        Global.shared.putBoolean("fbconnect", true);
                                        alertDialog.dismiss();

                                        final FriendPickerFragment friendPickerFragment = new FriendPickerFragment();
                                        friendPickerFragment.setSettingsFromBundle(new Bundle());
                                        friendPickerFragment.setFriendPickerType(FriendPickerFragment.FriendPickerType.TAGGABLE_FRIENDS);
                                        friendPickerFragment.show(getFragmentManager(), "FBPICKER");
                                        friendPickerFragment.setUpdateField(face);

                                        friendPickerFragment.setOnSelectionChangedListener(new PickerFragment.OnSelectionChangedListener() {
                                            @Override
                                            public void onSelectionChanged(PickerFragment fragment) {
                                                List<JSONObject> list = fragment.getSelectedGraphObjects();
                                                for(JSONObject i : list) {
                                                    String name = i.optString("name");
                                                    String id = i.optString("id");
                                                    face.setText(name);
                                                    friendPickerFragment.dismiss();
                                                }
                                            }
                                        });
                                    }

                                });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location, link");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }

                            @Override
                            public void onCancel() { alertDialog.dismiss();}
                            @Override
                            public void onError(FacebookException exception) { alertDialog.dismiss(); }
                        });

                        alertDialog.show();
                    }
                }
            }
        });

        skype.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.skype = editable.toString();
            }
        });

        snap.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.snapchat = editable.toString();
            }
        });

        viber.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.viber = editable.toString();
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.email = editable.toString();
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) { if (!hasFocus) {email.testValidity(); /*Global.saveUser();*/ } }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                item.phone = editable.toString();
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) { if (!hasFocus) { phone.testValidity(); /* Global.saveUser(); */ } }
        });

    }

    public void showAlertDialog(String title, String message, String negativeButton, String positiveButton, Standard_Dialog.MyDialogListener myDialogListener) {
        Standard_Dialog newDialog = Standard_Dialog.newInstance(title, message, negativeButton, positiveButton, myDialogListener);
        newDialog.show(getSupportFragmentManager(), "dialog");
    }

    public void exit(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}

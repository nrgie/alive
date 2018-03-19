package com.blueobject.app.alive;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.telecom.PhoneAccount;
import android.telecom.TelecomManager;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.adapter.GuardListSimpleAdapter;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.EasyRatingDialog;
import com.blueobject.app.alive.helper.PendingGuard;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.service.ChatHeadService;
import com.blueobject.app.alive.service.JustNotifyService;
import com.blueobject.app.alive.service.JustTimerService;
import com.blueobject.app.alive.service.NotifyLockService;
import com.blueobject.app.alive.service.OnlyProService;
import com.blueobject.app.alive.ui.AccountImageView;
import com.blueobject.app.alive.ui.DetailsView;
import com.facebook.CallbackManager;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.messaging.FirebaseMessaging;

import com.blueobject.app.alive.dialog.Standard_Dialog;
import com.blueobject.app.alive.fragments.HomeFragment;


import com.blueobject.app.alive.ui.Items;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nrgie on 2017.06.17..
 */

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.view.ViewConfiguration.getLongPressTimeout;
import static com.blueobject.app.alive.Global.appContext;

public class MainActivity extends LocalizationActivity {

    public final static int REQUEST_CODE = 10101;
    public final static int REQUEST_INVITE = 3233;
    public final static int MULTIPLE_PERMISSIONS = 11111;

    public TextView username;
    public TextView pending;

    public static FragmentManager fragmentManager;

    MainActivity act;

    EasyRatingDialog easyRatingDialog;

    protected Spinner blood;
    protected Spinner bloodrh;
    protected EditText height;
    protected EditText weight;
    protected EditText allergy;

    private int LONG_PRESS_DURATION = 3000; //5 seconds
    final public static int SHOWID = 111;
    private boolean isTouching = false;

    static Handler h;

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
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        act = this;

        h = new Handler();

        fragmentManager = getSupportFragmentManager();

        setContentView(R.layout.activity_main);

        username = (TextView) findViewById(R.id.username);

        Switch student = (Switch) findViewById(R.id.studentmode);

        easyRatingDialog = new EasyRatingDialog(this);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("APP", "Key: " + key + " Value: " + value);
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news");


        student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Global.student = isChecked;
            }
        });

        if (checkDrawOverlayPermission()) {
            startService(new Intent(this, PowerButtonService.class));
        }

        setContentView(R.layout.activity_main);
        //fragmentManager.beginTransaction().replace(R.id.main_content, new HomeFragment()).addToBackStack(null).commit();


        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment newFragment = new HelpDialogFragment("A segítő tartalom jön ide");
                //newFragment.show(getSupportFragmentManager(), "Segítség");
                //startHelp(v);
                startShowCase();
            }
        });

        final ExpandableLayout expandable = (ExpandableLayout) findViewById(R.id.expandable_layout_0);
        final ImageView expand = (ImageView) findViewById(R.id.expand_button);

        final RecyclerView list = (RecyclerView) findViewById(R.id.guardslist);
        list.setAdapter(new GuardListSimpleAdapter(act, new ArrayList<UserModel>()));
        LinearLayoutManager llm = new LinearLayoutManager(act);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);


        final LocationManager lmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPS();
        }

        findViewById(R.id.medinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, MediActivity.class));
            }
        });

        findViewById(R.id.med).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, MedicineActivity.class));
            }
        });


        File avfile = new File(Global.appContext.getFilesDir() + "/avatar.png");
        final ImageView avatar = (ImageView) findViewById(R.id.user);
        if (avfile.exists()) {
            findViewById(R.id.imagemaskframe).setVisibility(View.VISIBLE);
            Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decav);
            roundDrawable.setCircular(true);
            avatar.setImageDrawable(roundDrawable);
        } else {
            findViewById(R.id.imagemaskframe).setVisibility(View.GONE);

        }

        pending = (TextView) findViewById(R.id.pending);

        if (Global.user.pending == null)
            Global.user.pending = new ArrayList<PendingGuard>();

        if (Global.user.pending.size() > 0) {
            Log.e("Pendings", Global.gson.toJson(Global.user.pending));
            pending.setVisibility(View.VISIBLE);
            pending.setText("" + Global.user.pending.size());
        } else {
            pending.setVisibility(View.GONE);
        }

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(act, v);
                for (PendingGuard pg : Global.user.pending) {
                    popupMenu.getMenu().add(pg.name);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        PendingGuard pg = Global.user.pending.get(item.getItemId());

                        Intent intent = new Intent(act, AskActivity.class);

                        Bundle b = new Bundle();
                        b.putString("name", pg.name);
                        b.putString("email", pg.email);
                        intent.putExtras(b);
                        act.startActivity(intent);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        return false;
                    }
                });
                popupMenu.show();
            }

        });


        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandable.isExpanded()) expandable.collapse();
                else {


                    ((DetailsView) findViewById(R.id.taj)).refresh();
                    ((DetailsView) findViewById(R.id.blood)).refresh();
                    ((DetailsView) findViewById(R.id.height)).refresh();
                    ((DetailsView) findViewById(R.id.weight)).refresh();
                    ((DetailsView) findViewById(R.id.allergy)).refresh();
                    ((DetailsView) findViewById(R.id.medinfo)).refresh();
                    ((DetailsView) findViewById(R.id.med)).refresh();

                    ((DetailsView) findViewById(R.id.taj)).setClicktoZoomIcon();
                    ((DetailsView) findViewById(R.id.taj)).hideNext();
                    ((DetailsView) findViewById(R.id.blood)).hideNext();
                    ((DetailsView) findViewById(R.id.height)).hideNext();
                    ((DetailsView) findViewById(R.id.weight)).hideNext();
                    ((DetailsView) findViewById(R.id.allergy)).hideNext();

                    GuardListSimpleAdapter adapter = (GuardListSimpleAdapter) list.getAdapter();

                    adapter.data.clear();
                    adapter.data.addAll(Global.user.guards);
                    adapter.notifyDataSetChanged();

                    if (Global.user.guards.size() == 0) {
                        findViewById(R.id.guardshead).setVisibility(View.GONE);
                        findViewById(R.id.guardssep).setVisibility(View.GONE);
                        findViewById(R.id.guardssep2).setVisibility(View.GONE);
                    } else {
                        findViewById(R.id.guardshead).setVisibility(View.VISIBLE);
                        findViewById(R.id.guardssep).setVisibility(View.VISIBLE);
                        findViewById(R.id.guardssep2).setVisibility(View.VISIBLE);
                    }

                    expandable.expand();
                }
            }
        });

        if (!Global.user.datasetuped) {
            Global.user.datasetuped = true;
            Global.saveUser();
        }


        /////////////// FROM MAIN FRAG

        ImageButton ta = (ImageButton) findViewById(R.id.ta);
        CircleButton police = (CircleButton) findViewById(R.id.police);
        CircleButton health = (CircleButton) findViewById(R.id.health);
        CircleButton fire = (CircleButton) findViewById(R.id.fire);
        CircleButton sml = (CircleButton) findViewById(R.id.sml);

        ta.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isTouching) sendSos("ta");
                    }
                }, duration > 0 ? duration : 0);
                return false;
            }
        });

        ta.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) isTouching = true;
                else if (event.getAction() == MotionEvent.ACTION_UP) isTouching = false;
                return false;
            }
        });


        police.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isTouching) sendSos("police");
                    }
                }, duration > 0 ? duration : 0);
                return false;
            }
        });

        police.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) isTouching = true;
                else if (event.getAction() == MotionEvent.ACTION_UP) isTouching = false;
                return false;
            }
        });

        health.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isTouching) sendSos("ambulance");
                    }
                }, duration > 0 ? duration : 0);
                return false;
            }
        });

        health.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) isTouching = true;
                else if (event.getAction() == MotionEvent.ACTION_UP) isTouching = false;
                return false;
            }
        });

        fire.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isTouching) sendSos("fire");
                    }
                }, duration > 0 ? duration : 0);
                return false;
            }
        });

        fire.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) isTouching = true;
                else if (event.getAction() == MotionEvent.ACTION_UP) isTouching = false;
                return false;
            }
        });

        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(act, v);
                popupMenu.inflate(R.menu.home_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.call:
                                Global.simpleCALL(Global.user.policenumber);
                                return true;
                            case R.id.map:
                                act.startActivity(new Intent(act, MapsNearbyActivity.class).putExtra("type", "police"));
                                return true;
                        }
                        return false;
                    }
                });
                // Force icons to show
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popupMenu);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    Log.e("POPUP", "error forcing menu icons to show", e);
                    popupMenu.show();
                    return;
                }
                popupMenu.show();
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(act, v);
                popupMenu.inflate(R.menu.home_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.call:
                                Global.simpleCALL(Global.user.ambulancenumber);
                                return true;
                            case R.id.map:
                                act.startActivity(new Intent(act, MapsNearbyActivity.class).putExtra("type", "health"));
                                return true;
                        }
                        return false;
                    }
                });
                // Force icons to show
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popupMenu);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    Log.e("POPUP", "error forcing menu icons to show", e);
                    popupMenu.show();
                    return;
                }
                popupMenu.show();
            }
        });

        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(act, v);
                popupMenu.inflate(R.menu.home_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.call:
                                Global.simpleCALL(Global.user.firenumber);
                                return true;
                            case R.id.map:
                                act.startActivity(new Intent(act, MapsNearbyActivity.class).putExtra("type", "fire"));
                                return true;
                        }
                        return false;
                    }
                });
                Object menuHelper;
                Class[] argTypes;
                try {
                    Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                    fMenuHelper.setAccessible(true);
                    menuHelper = fMenuHelper.get(popupMenu);
                    argTypes = new Class[]{boolean.class};
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    Log.e("POPUP", "error forcing menu icons to show", e);
                    popupMenu.show();
                    return;
                }
                popupMenu.show();
            }
        });

        sml.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isTouching) sendSos("sos");
                    }
                }, duration > 0 ? duration : 0);
                return false;
            }
        });

        sml.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) isTouching = true;
                else if (event.getAction() == MotionEvent.ACTION_UP) isTouching = false;
                return false;
            }
        });

        if (!Global.shared.getBoolean("showcasedmain", false)) {
            startShowCase();
        }

    }


    public void startShowCase() {


        ImageButton ta = (ImageButton) findViewById(R.id.ta);
        CircleButton police = (CircleButton) findViewById(R.id.police);
        CircleButton health = (CircleButton) findViewById(R.id.health);
        CircleButton fire = (CircleButton) findViewById(R.id.fire);
        CircleButton sml = (CircleButton) findViewById(R.id.sml);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(250); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(act, "" + SHOWID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView materialShowcaseView, int i) {
                Log.e("SHOWED", "" + i);
                if (i == 7) Global.shared.putBoolean("showcasedmain", true);
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(findViewById(R.id.help), getResources().getString(R.string.main_help_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(findViewById(R.id.settings), getResources().getString(R.string.main_settings_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(findViewById(R.id.guards), getResources().getString(R.string.main_guardstrack_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(findViewById(R.id.expand_button), getResources().getString(R.string.main_medinfo_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(sml, getResources().getString(R.string.main_sos_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(police, getResources().getString(R.string.main_police_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(health, getResources().getString(R.string.main_health_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(fire, getResources().getString(R.string.main_fire_btn), getResources().getString(R.string.understand));
        sequence.addSequenceItem(ta, getResources().getString(R.string.main_ta_btn), getResources().getString(R.string.understand));

        sequence.start();

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        /*
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS );
            return false;
        }
        */
        return true;
    }

    public void sendSos(String type) {
        sendSOS(type);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                List<String> listPermissionsNeeded = new ArrayList<>();
                for (String p : permissions) {
                    listPermissionsNeeded.add(p);
                }
                if (!listPermissionsNeeded.isEmpty()) {
                    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                }
            }
            return;
        }
    }


    public boolean checkDrawOverlayPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!Settings.canDrawOverlays(this)) {

            /*
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
            */

            Intent overLay = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(true);
            dialog.setTitle("Required");
            dialog.setMessage("please Enable");
            final AlertDialog alert = dialog.create();
            alert.show();
            startActivity(overLay);

            return false;
        } else {
            return true;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            //if (Settings.canDrawOverlays(this)) {
            //  startService(new Intent(this, PowerButtonService.class));
            //}
        }

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d("APP", "onActivityResult: sent invitation " + id);
                }
            }
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_content);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {

        final ExpandableLayout expandable = (ExpandableLayout) findViewById(R.id.expandable_layout_0);
        if (expandable.isExpanded()) {
            expandable.collapse();
            return;
        }

        showAlertDialog(getResources().getString(R.string.logout), getResources().getString(R.string.logout_askmsg),
                getResources().getString(R.string.cancel),
                getResources().getString(R.string.logout),
                new Standard_Dialog.MyDialogListener() {

                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        dialog.dismiss();
                    }
                });

    }

    public void showAlertDialog(String title, String message, String negativeButton, String positiveButton, Standard_Dialog.MyDialogListener myDialogListener) {
        Standard_Dialog newDialog = Standard_Dialog.newInstance(title, message, negativeButton, positiveButton, myDialogListener);
        newDialog.show(getSupportFragmentManager(), "dialog");
    }

    public void showGPS() {
        showAlertDialog(getResources().getString(R.string.enablegps), getResources().getString(R.string.enablegpstext),
                getResources().getString(R.string.OK),
                getResources().getString(R.string.settings), new Standard_Dialog.MyDialogListener() {
                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();

                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialog.dismiss();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        easyRatingDialog.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUsername();
        if (Global.user.pending.size() > 0) {
            Log.e("Pendings", Global.gson.toJson(Global.user.pending));
            pending.setVisibility(View.VISIBLE);
            pending.setText("" + Global.user.pending.size());
        } else {
            pending.setVisibility(View.GONE);
        }


        File avfile = new File(Global.appContext.getFilesDir() + "/avatar.png");
        final ImageView avatar = (ImageView) findViewById(R.id.user);

        if (avfile.exists()) {
            findViewById(R.id.imagemaskframe).setVisibility(View.VISIBLE);
            final Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
            RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), decav);
            roundDrawable.setCircular(true);
            avatar.setImageDrawable(roundDrawable);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder photoBuilder = new AlertDialog.Builder(act);
                    View photoView = getLayoutInflater().inflate(R.layout.dialog_photoview, null);
                    photoBuilder.setView(photoView);
                    final AlertDialog photoDialog = photoBuilder.create();
                    photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                    ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                    //photo.setImageDrawable(avatar.getDrawable());
                    photo.setImageBitmap(decav);
                    ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photoDialog.dismiss();
                        }
                    });
                    photoDialog.show();
                }
            });
        } else {
            findViewById(R.id.imagemaskframe).setVisibility(View.GONE);
        }


        easyRatingDialog.showIfNeeded();
    }

    public void setUsername() {
    }

    public static void runSOS() {

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.flashandvibrate();
                if (Global.SOS) {
                    runSOS();
                } else {
                    Utils.flash(false);
                }
            }
        }, 2000);

    }

    public static void sendSOS(String type) {

        Log.e("STUDENT MODE SIGNAL", "" + Global.student);


        if (Global.student) {
            Toast.makeText(Global.appContext, Global.appContext.getResources().getString(R.string.student_mode_alert), Toast.LENGTH_LONG).show();
            return;
        }

        if (!Global.isPro()) {
            Global.appContext.startService(new Intent(Global.appContext, OnlyProService.class));
            return;
        }


        Global.SOS = true;
        Global.SOStype = type;
        //Global.sendTracking = true;

        if (h == null)
            h = new Handler();

        //ezt csak akkor kellene elindítani, ha a tipus szerinti szám nem emergency szám.

        if(isEmergency(type)) {

            Global.emergencycall = true;
            //Global.callednumber = Global.user.emnumber;

            //ebben az esetben nem teszünk ki számlálót, mert nincs értelme.
            //indítsuk el a call-t és szóljunk a receiver-nek hogy figyeljen oda erre a számra.
            //dobjunk fel egy dialogot hogy nyomja meg a hívás indítás gombot a jelzés elküldéséhez.
            //Ha indul a hívás, a leszedjük a jelet hogy indult a hívás, utánna már kitehetjük a screen-t.
            //Majd indulnak a signálok.

            /*
            //start dial
            Global.appContext.startService(new Intent(Global.appContext, ChatHeadService.class));
            Global.simpleCALL(Global.user.emnumber);
            */

            Global.appContext.startService(new Intent(Global.appContext, JustTimerService.class));

        } else {

            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.flashandvibrate();
                    if (Global.SOS) {
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


    public void startSettings(View v) {
        Intent in = new Intent(this, SettingsActivity.class);
        //in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
    }

    public void startHelp(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.fs_dialog);
        View dialogView = getLayoutInflater().inflate(R.layout.askforhelp, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.browsefaq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                //Intent i = new Intent(v.getContext(), FaqActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //startActivity(i);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://help.saveme-app.com/" + getCurrentLanguage().getLanguage().toLowerCase()));
                startActivity(browserIntent);

            }
        });

        dialogView.findViewById(R.id.sendmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String to = "support@saveme-app.com";
                String subject = Global.user.name1 + " " + Global.user.name2 + " " + Global.user.name3 + " asks for support";
                String body = "Hi!\n I have a problem about : \n";
                String mailTo = "mailto:" + to +
                        "?&subject=" + Uri.encode(subject) +
                        "&body=" + Uri.encode(body);
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                emailIntent.setData(Uri.parse(mailTo));
                startActivity(emailIntent);

            }
        });

        alertDialog.show();
    }


    public void startGuards(View v) {
        Intent dialogIntent = new Intent(this, GuardMapsActivity.class);
        startActivity(dialogIntent);
    }


    public void callFire(View v) {
        Global.simpleCALL(Global.user.firenumber);
    }

    public void callPolice(View v) {
        Global.simpleCALL(Global.user.policenumber);
    }

    public void callAmb(View v) {
        Global.simpleCALL(Global.user.ambulancenumber);
    }


    public void callEm(View v) {
        Global.simpleCALL(Global.user.emnumber);
    }


    public static boolean isEmergency(String type) {

        if(type.equals("sos")) {
            return PhoneNumberUtils.isEmergencyNumber(Global.user.emnumber);
        }

        if(type.equals("police")) {
            return PhoneNumberUtils.isEmergencyNumber(Global.user.policenumber);
        }

        if(type.equals("fire")) {
            return PhoneNumberUtils.isEmergencyNumber(Global.user.firenumber);
        }

        if(type.equals("ambulace")) {
            return PhoneNumberUtils.isEmergencyNumber(Global.user.ambulancenumber);
        }

        if(type.equals("ta")) {
            return PhoneNumberUtils.isEmergencyNumber(Global.user.terrornumber);
        }

        return true;

    }

    public static String getNumberFromType(String type) {

        if(type.equals("sos")) {
            return Global.user.emnumber;
        }

        if(type.equals("police")) {
            return Global.user.policenumber;
        }

        if(type.equals("fire")) {
            return Global.user.firenumber;
        }

        if(type.equals("ambulace")) {
            return Global.user.ambulancenumber;
        }

        if(type.equals("ta")) {
            return Global.user.terrornumber;
        }

        return "";

    }


    public static void sendRealSOS(String type) {

        if(Global.student) {
            Toast.makeText(Global.appContext, "A gyakorló módban nem fog indulni valós vészjelzés", Toast.LENGTH_LONG).show();
            return;
        }

        if(!Global.isPro()) {
            Global.appContext.startService(new Intent(Global.appContext, OnlyProService.class));

            if(type.equals("ta"))
                new Global.SendSignal(type).execute();

            return;
        }

        new Global.SendSignal(type).execute();

        LocationManager locationManager = (LocationManager) Global.appContext.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(Global.appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Global.appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }

        Location location = locationManager.getLastKnownLocation(provider);

        String smstext = "";
        String calltext = "";

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            smstext = appContext.getResources().getString(R.string.smstext);
            smstext += " lat: " + latitude + "\nlong: " +longitude;
            smstext += "\n\nhttp://maps.google.com/maps?q="+latitude+","+longitude;

            calltext = appContext.getResources().getString(R.string.calltext);
            calltext = "latitude. " + latitude + " longitude. " +longitude;
        }

        Intent intent = new Intent(Global.appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(Global.appContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(Global.appContext)
            .setSmallIcon(R.drawable.smllogotr)
            .setContentTitle(appContext.getResources().getString(R.string.sendedsostxt))
            .setContentText(smstext)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) Global.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        if(type.equals("sos")) {

            if(Global.user.gsoscall) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGcall)
                        Global.sendCALL(g.phone, calltext);
                }
            }

            if(Global.user.gsossms) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGsms)
                        Global.sendSMS(g.phone, smstext);
                }
            }

            if(Global.user.gsosemail) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGemail)
                        Global.sendMAIL(g.email, smstext);
                }
            }

            if(Global.user.soscall) {
                if(!Global.emergencycall)
                   Global.simpleCALL(Global.user.emnumber);

                Global.sendSMS(Global.user.emnumber, smstext);
            } else {
                Global.showMap();
            }

        }

        if(type.equals("police")) {

            if(Global.user.gpolicecall) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGcall)
                        Global.sendCALL(g.phone, calltext);
                }
            }

            if(Global.user.gpolicesms){
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGsms)
                        Global.sendSMS(g.phone, smstext);
                }
            }

            if(Global.user.gpoliceemail) {
                for (UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGemail)
                        Global.sendMAIL(g.email, smstext);
                }
            }

            if(Global.user.policecall) {
                if(!Global.emergencycall)
                    Global.simpleCALL(Global.user.policenumber);

                Global.sendSMS(Global.user.policenumber, smstext);
            } else {
                Global.showMap();
            }
        }

        if(type.equals("fire")) {

            if(Global.user.gfirecall) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGcall)
                        Global.sendCALL(g.phone, calltext);
                }
            }

            if(Global.user.gfiresms) {
                for (UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGsms)
                        Global.sendSMS(g.phone, smstext);
                }
            }

            if(Global.user.gfireemail) {
                for (UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGemail)
                        Global.sendMAIL(g.email, smstext);
                }
            }

            if(Global.user.firecall) {
                if(!Global.emergencycall)
                    Global.simpleCALL(Global.user.firenumber);
                Global.sendSMS(Global.user.firenumber, smstext);
            } else {
                Global.showMap();
            }
        }

        if(type.equals("ambulace")) {

            if(Global.user.gambcall) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGcall)
                        Global.sendCALL(g.phone, calltext);
                }
            }
            if(Global.user.gambsms) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGsms)
                        Global.sendSMS(g.phone, smstext);
                }
            }

            if(Global.user.gambemail) {
                for (UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGemail)
                        Global.sendMAIL(g.email, smstext);
                }
            }

            if(Global.user.ambcall) {
                if(!Global.emergencycall)
                    Global.simpleCALL(Global.user.ambulancenumber);

                Global.sendSMS(Global.user.ambulancenumber, smstext);
            } else {
                Global.showMap();
            }
        }

        if(type.equals("ta")) {
            if(Global.user.gtacall) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGcall)
                        Global.sendCALL(g.phone, calltext);
                }
            }

            if(Global.user.gtasms) {
                for(UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGsms)
                        Global.sendSMS(g.phone, smstext);
                }
            }

            if(Global.user.gtaemail) {
                for (UserModel g : Global.user.guards) {
                    if(g.enabled && g.asGemail)
                        Global.sendMAIL(g.email, smstext);
                }
            }

            if(Global.user.tacall) {
                if(!Global.emergencycall)
                    Global.simpleCALL(Global.user.emnumber);

                Global.sendSMS(Global.user.emnumber, smstext);
            } else {
                Global.showMap();
            }
        }

    }

}

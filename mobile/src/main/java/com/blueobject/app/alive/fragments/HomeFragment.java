package com.blueobject.app.alive.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blueobject.app.alive.CircleButton;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MainActivity;
import com.blueobject.app.alive.MapsActivity;
import com.blueobject.app.alive.MapsNearbyActivity;
import com.blueobject.app.alive.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.lang.reflect.Field;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.view.ViewConfiguration.getLongPressTimeout;


public class HomeFragment extends Fragment {

    View v;

    private HomeFragment Frag;
    private int LONG_PRESS_DURATION = 3000; //5 seconds
    final public static int SHOWID = 111;
    private boolean isTouching = false;

    protected Spinner blood;
    protected Spinner bloodrh;
    protected EditText height;
    protected EditText weight;
    protected EditText allergy;
    protected EditText medinfo;
    protected EditText med;
    protected EditText doctors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Frag = this;
    }

    MainActivity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.main_content, container, false);

        ImageButton ta = (ImageButton) v.findViewById(R.id.ta);
        ta.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { if (isTouching) sendSos("ta");}
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

        CircleButton police = (CircleButton) v.findViewById(R.id.police);
        police.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { if (isTouching) sendSos("police");}
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


        CircleButton health = (CircleButton) v.findViewById(R.id.health);
        health.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { if (isTouching) sendSos("ambulance");}
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

        CircleButton fire = (CircleButton) v.findViewById(R.id.fire);
        fire.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { if (isTouching) sendSos("fire");}
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
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.inflate(R.menu.home_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.call:
                                Global.simpleCALL(Global.user.policenumber);
                                return true;
                            case R.id.map:
                                activity.startActivity(new Intent(activity, MapsNearbyActivity.class).putExtra("type", "police"));
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
                    argTypes = new Class[] { boolean.class };
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
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.inflate(R.menu.home_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.call:
                                Global.simpleCALL(Global.user.ambulancenumber);
                                return true;
                            case R.id.map:
                                activity.startActivity(new Intent(activity, MapsNearbyActivity.class).putExtra("type", "health"));
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
                    argTypes = new Class[] { boolean.class };
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
                PopupMenu popupMenu = new PopupMenu(activity, v);
                popupMenu.inflate(R.menu.home_popupmenu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.call:
                                Global.simpleCALL(Global.user.firenumber);
                                return true;
                            case R.id.map:
                                activity.startActivity(new Intent(activity, MapsNearbyActivity.class).putExtra("type", "fire"));
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
                    argTypes = new Class[] { boolean.class };
                    menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                } catch (Exception e) {
                    Log.e("POPUP", "error forcing menu icons to show", e);
                    popupMenu.show();
                    return;
                }
                popupMenu.show();
            }
        });


        CircleButton sml = (CircleButton) v.findViewById(R.id.sml);
        sml.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int duration = LONG_PRESS_DURATION - getLongPressTimeout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() { if (isTouching) sendSos("sos");}
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

        Global.shared.putBoolean("showcasedmain", false);


       //if(!Global.shared.getBoolean("showcasedmain", false)) {

            ShowcaseConfig config = new ShowcaseConfig();
            config.setDelay(250); // half second between each showcase view

            MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, ""+SHOWID);

            sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
                @Override
                public void onShow(MaterialShowcaseView materialShowcaseView, int i) {
                    Log.e("SHOWED", ""+i);
                    if(i==8) Global.shared.putBoolean("showcasedmain", true);
                }
            });

            sequence.setConfig(config);

            sequence.addSequenceItem(sml, "Ezt a gombot nyomva tartva legalább 5 másodpercig, egy általános azonnali vészjelzést küld a beállított kapcsolati csatornákra", "okay");

            /*
            sequence.addSequenceItem(activity.findViewById(R.id.help), "Ezzel a gommbal segítséget tud kérni.", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(activity.findViewById(R.id.settings), "Ezzel a gommbal a beállításokat tudja előhozni.", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(activity.findViewById(R.id.guards), "Ezzel a gommbal az őrangyalai jelenlegi helyzetét tudja térképen megnézni.", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(activity.findViewById(R.id.expand_butt), "Ezzel az orvosi adatlap összefoglalóját tudja megtekinteni.", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(sml, "Ezt a gombot nyomva tartva legalább 5 másodpercig, egy általános azonnali vészjelzést küld a beállított kapcsolati csatornákra", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(police, "Ezt a gombot nyomva tartva legalább 5 másodpercig a rendőrség vészhívó számára tud küldeni egy azonnali hangüzentetet amelyben az ön helyzete is továbbítódik", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(health, "Ezt a gombot nyomva tartva legalább 5 másodpercig a mentők vészhívó számára tud küldeni egy azonnali hangüzentetet amelyben az ön helyzete is továbbítódik", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(fire, "Ezt a gombot nyomva tartva legalább 5 másodpercig a tűzoltóság vészhívó számára tud küldeni egy azonnali hangüzentetet amelyben az ön helyzete is továbbítódik", activity.getResources().getString(R.string.understand));
            sequence.addSequenceItem(ta, "Ezt a gombot nyomva tartva legalább 5 másodpercig egy Terrorista támadás jelzést tud elküldeni az ön helyzetével a beállított csatornákra", activity.getResources().getString(R.string.understand));
            */

            sequence.start();

        //}
        return v;
    }

    public void sendSos(String type){
        MainActivity.sendSOS(type);
    }



}

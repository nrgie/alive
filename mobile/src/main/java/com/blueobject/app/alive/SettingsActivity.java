package com.blueobject.app.alive;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.ui.SettingsView;
import com.blueobject.app.alive.ui.SmoothCheckBox;
import com.facebook.login.LoginManager;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

import static com.blueobject.app.alive.MainActivity.REQUEST_INVITE;

/**
 * Created by nrgie on 2017.08.23..
 */

public class SettingsActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.settings_activity);
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(Global.user.guards.size()==0) {
            findViewById(R.id.enablings).setVisibility(View.GONE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            /*
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            */

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void generalLaunch(View v) {

        String name = "org.bitbucket.stefanodp91.ui.BasicFragment";
        String title = getResources().getString(R.string.generalsettings);

        Intent i = new Intent(this, SettingsDetailActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("fragment", name);
        i.putExtra("title", title);
        startActivity(i);

    }

    public void contactLaunch(View v) {

        String name = "org.bitbucket.stefanodp91.ui.ContactFragment";
        String title = getResources().getString(R.string.label_contact);

        Intent i = new Intent(this, SettingsDetailActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("fragment", name);
        i.putExtra("title", title);
        startActivity(i);

    }

    public void healthLaunch(View v) {

        String name = "org.bitbucket.stefanodp91.ui.HealthFragment";
        String title = getResources().getString(R.string.medilabel);

        Intent i = new Intent(this, SettingsDetailActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("fragment", name);
        i.putExtra("title", title);
        startActivity(i);

    }

    public void emergencyLaunch(View v) {

        String name = "org.bitbucket.stefanodp91.ui.ReviewFragment";
        String title = getResources().getString(R.string.signalsetup);

        Intent i = new Intent(this, SettingsDetailActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("fragment", name);
        i.putExtra("title", title);
        startActivity(i);

    }

    public void guardsLaunch(View v) {

        String name = "org.bitbucket.stefanodp91.ui.GuardsFragment";
        String title = getResources().getString(R.string.guardshandling);

        Intent i = new Intent(this, SettingsDetailActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("fragment", name);
        i.putExtra("title", title);
        startActivity(i);

    }

    public void numbersLaunch(View v) {

        String name = "org.bitbucket.stefanodp91.ui.EmergencyFragment";
        String title = getResources().getString(R.string.currentcountrysub);

        Intent i = new Intent(this, SettingsDetailActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("fragment", name);
        i.putExtra("title", title);
        startActivity(i);

    }

    public void startGuards(View v) {
        Intent dialogIntent = new Intent(this, MapsActivity.class);
        startActivity(dialogIntent);
    }

    public void termsLaunch(View v) {
        Intent i = new Intent(this, TermsActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(i);
    }

    public void helpLaunch(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.fs_dialog);
        View dialogView = getLayoutInflater().inflate(R.layout.askforhelp, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        dialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            alertDialog.dismiss();
        }});

        dialogView.findViewById(R.id.browsefaq).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            alertDialog.dismiss();
            /*
            Intent i = new Intent(v.getContext(), FaqActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);
            */
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://help.saveme-app.com/"+getCurrentLanguage().getLanguage().toLowerCase()));
            startActivity(browserIntent);
        }});

        dialogView.findViewById(R.id.sendmail).setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) {
            alertDialog.dismiss();
            String to = "support@saveme-app.com";
            String subject= Global.user.name1 + " " + Global.user.name2 + " " + Global.user.name3 + " asks for support";
            String body="Hi!\n I have a problem about : \n";
            String mailTo = "mailto:" + to +
                    "?&subject=" + Uri.encode(subject) +
                    "&body=" + Uri.encode(body);
            Intent emailIntent = new Intent(Intent.ACTION_VIEW);
            emailIntent.setData(Uri.parse(mailTo));
            startActivity(emailIntent);

        }});

        alertDialog.show();

    }

    public void appinvite(View v) {
        Intent inv = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                //.setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(inv, REQUEST_INVITE);
    }

    public void exit(View v) {
        Global.user = new UserModel();
        Global.shared.remove("user");
        Global.shared.remove("setuped");
        Global.shared.remove("pin");

        LoginManager.getInstance().logOut();

        Intent i = new Intent(this, IntroActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

        finish();
    }

    public void playSOS(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.fs_dialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_play, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ImageView cancel = (ImageView) dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { alertDialog.dismiss(); }});
        alertDialog.show();
    }

    public void chooseLang(View v) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.fs_dialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_lang, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        final CountryCodePicker ccp = (CountryCodePicker) dialogView.findViewById(R.id.ccp);
        ccp.setSearchAllowed(false);
        ccp.isLang = true;
        ccp.setCountryForNameCode(getCurrentLanguage().getLanguage().toLowerCase());
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                setLanguage(ccp.getSelectedCountryNameCode().toLowerCase());
                Log.e("CLAN", getCurrentLanguage().getLanguage().toLowerCase());
                alertDialog.dismiss();
            }
        });

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        ImageView cancel = (ImageView) dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { alertDialog.dismiss(); }});
        alertDialog.show();
    }

    public void quickEnableGuards(View v) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.fs_dialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_quickguardenable, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ImageView cancel = (ImageView) dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() { @Override public void onClick(View v) { alertDialog.dismiss(); }});

        LinearLayout ll = (LinearLayout) dialogView.findViewById(R.id.guards);

        for(final UserModel g : Global.user.guards) {
            View row = getLayoutInflater().inflate(R.layout.guard_quick_list_item, null);
            TextView name = (TextView) row.findViewById(R.id.name);
            SmoothCheckBox onoff = (SmoothCheckBox) row.findViewById(R.id.onOff);
            name.setText(g.name);
            onoff.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    g.enabled = isChecked;
                    Global.saveUser();
                }
            });
            onoff.setChecked(g.enabled);
            ll.addView(row);
        }
        alertDialog.show();


    }
}

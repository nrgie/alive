package com.blueobject.app.alive;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blueobject.app.alive.helper.WizardModel;
import com.facebook.FacebookSdk;

import org.bitbucket.stefanodp91.WizardActivity;
import org.bitbucket.stefanodp91.model.ReviewItem;

import java.util.List;

import static com.blueobject.app.alive.Global.appContext;
import static com.blueobject.app.alive.Global.shared;

/**
 * Created by nrgie on 2017.06.17..
 */

public class WizActivity extends WizardActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        initWizard(new WizardModel(this));
        Global.shared.putBoolean("wizard", true);
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
    protected void setReviewButtonAction() {

        Toast.makeText(getApplicationContext(), appContext.getResources().getString(R.string.settingssaved), Toast.LENGTH_LONG).show();

        Global.user.datasetuped = true;
        Global.saveUser();

        Global.shared.putBoolean("wizard", false);
        Global.shared.putBoolean("setuped", true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

}

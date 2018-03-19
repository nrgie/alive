package com.blueobject.app.alive.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.blueobject.app.alive.CircleButton;
import com.blueobject.app.alive.MainActivity;
import com.blueobject.app.alive.MapsActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.WizActivity;
import com.blueobject.app.alive.helper.CommonUtilities;
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

import org.json.JSONObject;

public class InitFragment extends Fragment {

    View v;
    LoginButton loginButton;
    CallbackManager callbackManager;

    private InitFragment Frag;

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
        v = inflater.inflate(R.layout.init_content, container, false);
        Button ta = (Button) v.findViewById(R.id.wizardlaunch);
        ta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialogIntent = new Intent(activity, WizActivity.class);
                dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(dialogIntent);
                activity.finish();
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
        CommonUtilities.dumpIntent(data);

    }

}

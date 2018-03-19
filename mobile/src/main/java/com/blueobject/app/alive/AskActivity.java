package com.blueobject.app.alive;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.helper.PendingGuard;
import com.blueobject.app.alive.helper.ProtectedModel;
import com.blueobject.app.alive.helper.UserModel;

import java.util.ArrayList;

/**
 * Created by nrgie on 2017.08.01..
 */

public class AskActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        overridePendingTransition(0, 0);

        Bundle e = getIntent().getExtras();
        if(e == null) finish();

        final String name = e.getString("name");

        if(name == null) finish();

        final String email = e.getString("email");

        if(email == null) finish();

        TextView ask = (TextView) findViewById(R.id.ask);

        ask.setText(name + " szeretne felkérni hogy legyél az őrangyala. Elfogadod a felkérését ?");

        Button reject = (Button) findViewById(R.id.reject);
        Button accept = (Button) findViewById(R.id.accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PendingGuard> pendings = (ArrayList<PendingGuard>) Global.user.pending.clone();
                for(PendingGuard pend : pendings) {
                    if(pend.email.equals(email)) Global.user.pending.remove(pend);
                }
                ProtectedModel p = new ProtectedModel();
                p.email = email;
                p.name = name;
                Global.user.protecteds.add(p);

                Global.saveUser();
                new Global.SendAccept(email, true).execute();
                finish();
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send back reject.
                ArrayList<PendingGuard> pendings = (ArrayList<PendingGuard>) Global.user.pending.clone();
                for(PendingGuard pend : pendings) {
                    if(pend.email.equals(email)) Global.user.pending.remove(pend);
                }
                Global.saveUser();
                new Global.SendAccept(email, false).execute();
                finish();
            }
        });

    }
}

package com.blueobject.app.alive;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.akexorcist.localizationactivity.LocalizationActivity;

/**
 * Created by nrgie on 2017.08.01..
 */

public class CallActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);

        setContentView(R.layout.call_activity);

        TextView sos = (TextView) findViewById(R.id.SOS);

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(sos,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));

        scaleDown.setInterpolator(new FastOutSlowInInterpolator());

        scaleDown.setDuration(310);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();

    }
}

package com.blueobject.app.alive;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.akexorcist.localizationactivity.LocalizationActivity;

/**
 * Created by nrgie on 2017.08.23..
 */

public class FaqActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.terms_activity);

        WebView v = (WebView) findViewById(R.id.termsview);
        v.loadUrl("http://help.saveme-app.com/"+getCurrentLanguage().getLanguage().toLowerCase());

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle(getResources().getString(R.string.termstitle));
        */
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

package com.blueobject.app.alive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.akexorcist.localizationactivity.LocalizationActivity;

/**
 * Created by nrgie on 2017.08.23..
 */

public class TermsActivity extends LocalizationActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.terms_activity);

        WebView v = (WebView) findViewById(R.id.termsview);
        Log.e("TERMSLINK", "http://saveme-app.com/terms-"+getCurrentLanguage().getLanguage().toLowerCase()+".html");
        v.loadUrl("http://saveme-app.com/terms-"+getCurrentLanguage().getLanguage().toLowerCase()+".html");

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

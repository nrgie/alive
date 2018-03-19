package com.blueobject.app.alive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.helper.UserModel;

/**
 * Created by nrgie on 2017.08.23..
 */

public class SettingsDetailActivity extends LocalizationActivity {

    Fragment fragment = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.settings_detail_activity);



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

        TextView title = (TextView) findViewById(R.id.fragmentTitle);

        Bundle extras = getIntent().getExtras();

        if(extras == null)
            finish();

        String fragmentName = extras.getString("fragment", "");
        String fragmentTitle = extras.getString("title");

        try {
            Class<?> fragmentClass = Class.forName(fragmentName);
            fragment = (Fragment)fragmentClass.newInstance();

            title.setText(fragmentTitle);

        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment, fragment).addToBackStack(null).commit();
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

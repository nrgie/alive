package com.blueobject.app.alive;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.DoctorModel;
import com.blueobject.app.alive.helper.MedicalModel;

/**
 * Created by nrgie on 2017.08.23..
 */

public class DoctorsEditActivity extends LocalizationActivity {

    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;
        setContentView(R.layout.activity_editdoctor);

        final EditText name = (EditText) findViewById(R.id.name);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText phone = (EditText) findViewById(R.id.phone);
        final EditText special = (EditText) findViewById(R.id.special);
        final EditText custom = (EditText) findViewById(R.id.custom);

        final Bundle b = getIntent().getExtras();


        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.doctoreditpageinfo));
                newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.help));
            }
        });

        String n = "";
        String p = "";
        String e = "";
        String s = "";
        String c = "";

        int id = 0;

        if(b != null && b.getString("name") != null) {
            n = b.getString("name");
            e = b.getString("email");
            p = b.getString("phone");
            s = b.getString("special");
            c = b.getString("custom");
            id = b.getInt("id");

            name.setText(n);
            email.setText(e);
            phone.setText(p);
            special.setText(s);
            custom.setText(c);

        }

        Button cancel = (Button) findViewById(R.id.cancel);
        Button save = (Button) findViewById(R.id.save);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final int finalId = id;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorModel model;

                if(name.getText().toString().trim().equals("")) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.error_field_must_not_be_empty), Toast.LENGTH_LONG).show();
                    name.requestFocus();
                    return;
                }

                if(phone.getText().toString().trim().equals("")) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.error_phone_not_valid), Toast.LENGTH_LONG).show();
                    phone.requestFocus();
                    return;
                }


                if(b != null && b.getString("name") != null) {
                    model = Global.user.doctors.get(finalId);
                    model.name = name.getText().toString().trim();
                    model.email = email.getText().toString().trim();
                    model.phone = phone.getText().toString().trim();
                    model.special = special.getText().toString().trim();
                    model.custom = custom.getText().toString().trim();
                } else {
                    model = new DoctorModel();
                    model.name = name.getText().toString().trim();
                    model.email = email.getText().toString().trim();
                    model.phone = phone.getText().toString().trim();
                    model.special = special.getText().toString().trim();
                    model.custom = custom.getText().toString().trim();
                    Global.user.doctors.add(model);
                }

                Global.saveUser();
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

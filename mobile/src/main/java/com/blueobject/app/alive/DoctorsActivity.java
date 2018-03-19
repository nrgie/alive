package com.blueobject.app.alive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.adapter.DoctorsListAdapter;
import com.blueobject.app.alive.adapter.MedicalListAdapter;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.DoctorModel;
import com.blueobject.app.alive.helper.MedicalModel;

import java.util.ArrayList;

/**
 * Created by nrgie on 2017.08.23..
 */

public class DoctorsActivity extends LocalizationActivity {

    Activity activity;
    RecyclerView doctors;
    DoctorsListAdapter dadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;

        boolean disabled = getIntent().getBooleanExtra("disabled", false);

        if(disabled)
            setContentView(R.layout.activity_doctors_disabled);
        else
            setContentView(R.layout.activity_doctors);


        doctors = (RecyclerView) findViewById(R.id.medinfo);
        doctors.setAdapter(new DoctorsListAdapter(activity, new ArrayList<DoctorModel>()));
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        doctors.setLayoutManager(llm);
        dadapter = (DoctorsListAdapter) doctors.getAdapter();

        if(disabled)
            dadapter.disabled = true;

        dadapter.data.clear();
        dadapter.data.addAll(Global.user.doctors);
        dadapter.notifyDataSetChanged();
        Button cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(!disabled) {
            Button save = (Button) findViewById(R.id.save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            Button addnew = (Button) findViewById(R.id.addnew);
            addnew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, DoctorsEditActivity.class));
                }
            });

            if(Global.user.doctors.size()==0) {
                startActivity(new Intent(activity, DoctorsEditActivity.class));
            }
        }

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.doctorpageinfo));
                newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.help));
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        dadapter.data.clear();
        dadapter.data.addAll(Global.user.doctors);
        dadapter.notifyDataSetChanged();
    }

}

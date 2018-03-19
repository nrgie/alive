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
import com.blueobject.app.alive.adapter.MedListAdapter;
import com.blueobject.app.alive.adapter.MedicalListAdapter;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.MedModel;
import com.blueobject.app.alive.helper.MedicalModel;

import java.util.ArrayList;

/**
 * Created by nrgie on 2017.08.23..
 */

public class MedicineActivity extends LocalizationActivity {

    Activity activity;
    RecyclerView med;
    MedListAdapter medadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;
        setContentView(R.layout.activity_medicines);
        med = (RecyclerView) findViewById(R.id.medinfo);
        med.setAdapter(new MedListAdapter(activity, new ArrayList<MedModel>()));
        LinearLayoutManager medllm = new LinearLayoutManager(activity);
        medllm.setOrientation(LinearLayoutManager.VERTICAL);
        med.setLayoutManager(medllm);
        medadapter = (MedListAdapter) med.getAdapter();
        medadapter.data.clear();
        medadapter.data.addAll(Global.user.med);
        medadapter.notifyDataSetChanged();
        Button cancel = (Button) findViewById(R.id.cancel);
        Button save = (Button) findViewById(R.id.save);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                startActivity(new Intent(activity, MedicineEditActivity.class));
            }
        });

        if(Global.user.med.size()==0) {
            startActivity(new Intent(activity, MedicineEditActivity.class));
        }

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.medpageinfo));
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
        medadapter.data.clear();
        medadapter.data.addAll(Global.user.med);
        medadapter.notifyDataSetChanged();
    }

}

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.adapter.AllergyListAdapter;
import com.blueobject.app.alive.adapter.AllergySetupListAdapter;
import com.blueobject.app.alive.adapter.MedListAdapter;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.MedModel;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by nrgie on 2017.08.23..
 */

public class AllergyActivity extends LocalizationActivity {

    Activity activity;
    RecyclerView med;
    MedListAdapter medadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;
        setContentView(R.layout.activity_allergy);

        Button cancel = (Button) findViewById(R.id.cancel);
        Button save = (Button) findViewById(R.id.save);

        final EditText newrow = (EditText) findViewById(R.id.newrow);
        RecyclerView list = (RecyclerView) findViewById(R.id.allist);
        final RecyclerView customlist = (RecyclerView) findViewById(R.id.customlist);

        //String[] lists = getResources().getStringArray(R.array.allergies);

        Type listType = new TypeToken<ArrayList<AllergyModel>>(){}.getType();
        ArrayList<AllergyModel> allallergy = Global.gson.fromJson(Global.shared.getString("allergies", ""), listType);
        ArrayList<AllergyModel> alls = new ArrayList<AllergyModel>();
        for(AllergyModel al : allallergy) {
            for(AllergyModel a: Global.user.allergies) {
                if(a.key.equals(al.key)) { al.check(true); }
            }

            if(Global.user.national.toLowerCase().equals("hu"))
                al.name = al.hu;

            else if(Global.user.national.toLowerCase().equals("en"))
                al.name = al.en;

            else if(Global.user.national.toLowerCase().equals("de"))
                al.name = al.de;

            else
                al.name = al.hu;

            alls.add(al);
        }

        list.setAdapter(new AllergySetupListAdapter(activity, alls));
        LinearLayoutManager listllm = new LinearLayoutManager(activity);
        listllm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(listllm);

        final AllergyListAdapter customad = new AllergyListAdapter(activity, Global.user.customallergies);

        customlist.setAdapter(customad);
        LinearLayoutManager customlistllm = new LinearLayoutManager(activity);
        customlistllm.setOrientation(LinearLayoutManager.VERTICAL);
        customlist.setLayoutManager(customlistllm);

        findViewById(R.id.add_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String row = newrow.getText().toString();
                if(row.equals("")) {
                    Toast.makeText(activity, "Nem lehet üres a sor", Toast.LENGTH_LONG).show();
                    return;
                }
                AllergyModel a = new AllergyModel();
                a.name = row;
                a.checked = true;
                Global.user.customallergies.add(a);
                Global.saveUser();
                newrow.setText("");
                customad.notifyDataSetChanged();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.saveUser();
                finish();
            }
        });

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.allergypageinfo));
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
    }

}

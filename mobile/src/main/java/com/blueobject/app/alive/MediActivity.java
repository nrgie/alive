package com.blueobject.app.alive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.adapter.MedicalListAdapter;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.MedicalModel;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.picker.FriendPickerFragment;
import com.blueobject.app.alive.picker.PickerFragment;
import com.facebook.AccessToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nrgie on 2017.08.23..
 */

public class MediActivity extends LocalizationActivity {

    Activity activity;
    RecyclerView medinfo;
    MedicalListAdapter medicaladapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;
        setContentView(R.layout.activity_medical);

        medinfo = (RecyclerView) findViewById(R.id.medinfo);
        medinfo.setAdapter(new MedicalListAdapter(this, new ArrayList<MedicalModel>()));
        LinearLayoutManager medicalllm = new LinearLayoutManager(this);
        medicalllm.setOrientation(LinearLayoutManager.VERTICAL);
        medinfo.setLayoutManager(medicalllm);
        medicaladapter = (MedicalListAdapter) medinfo.getAdapter();
        medicaladapter.data.clear();
        medicaladapter.data.addAll(Global.user.medinfo);
        medicaladapter.notifyDataSetChanged();


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

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.medicalpageinfo));
                newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.help));
            }
        });

        Button addnew = (Button) findViewById(R.id.addnew);


        if(Global.user.medinfo.size()==0) {
            startActivity(new Intent(activity, MediEditActivity.class));
        }


        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(activity, MediEditActivity.class));

                /*
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity,  R.style.fs_dialog);
                LayoutInflater inflater = activity.getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.add_medical, null);
                dialogBuilder.setView(dialogView);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);
                final EditText date = (EditText) dialogView.findViewById(R.id.date);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();

                cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    if(name.getText().toString().equals("")) {
                        Toast.makeText(activity, "A leírás nem lehet üres", Toast.LENGTH_LONG);
                        name.setFocusable(true);
                        return;
                    }

                    MedicalModel medical = new MedicalModel();
                    medical.name = name.getText().toString();
                    medical.date = date.getText().toString();
                    Global.user.medinfo.add(medical);

                    medicaladapter.data.clear();
                    medicaladapter.data.addAll(Global.user.medinfo);
                    medicaladapter.notifyDataSetChanged();
                    alertDialog.dismiss();

                    Global.saveUser();


                    }});



                alertDialog.show();

                */
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
        medicaladapter.data.clear();
        medicaladapter.data.addAll(Global.user.medinfo);
        medicaladapter.notifyDataSetChanged();
    }

}

package com.blueobject.app.alive;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.MedModel;
import com.blueobject.app.alive.helper.MedicalModel;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by nrgie on 2017.08.23..
 */

public class MedicineEditActivity extends LocalizationActivity {

    AppCompatActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;
        setContentView(R.layout.activity_editmedicine);

        final EditText name = (EditText) findViewById(R.id.name);
        final EditText qty = (EditText) findViewById(R.id.qty);
        final Bundle b = getIntent().getExtras();

        String n = "";
        String q = "";

        int id = 0;

        if(b != null && b.getString("name") != null) {
            n = b.getString("name");
            q = b.getString("qty");
            id = b.getInt("id");
            name.setText(n);
        }

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.mededitpageinfo));
                newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.help));
            }
        });

        final int finalId = id;

        final MedModel model;

        if(b != null && b.getString("name") != null) {
            model = Global.user.med.get(finalId);
        } else {
            model = new MedModel();
        }


        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name.getText().toString().trim().equals("")) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.error_field_must_not_be_empty), Toast.LENGTH_LONG).show();
                    name.requestFocus();
                    return;
                }

                if(b != null && b.getString("name") != null) {
                    model.name = name.getText().toString().trim();
                    model.qty = qty.getText().toString().trim();
                } else {
                    model.name = name.getText().toString().trim();
                    model.qty = qty.getText().toString().trim();
                    Global.user.med.add(model);
                }

                Global.saveUser();
                finish();
            }
        });

        final Button picedit = (Button) findViewById(R.id.iconedit);
        final ImageView pic = (ImageView) findViewById(R.id.icon);

        if(!model.pic.equals("")) {
            File avfile = new File(Global.appContext.getFilesDir() + model.pic);
            if (avfile.exists()) {
                Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
                pic.setImageBitmap(decav);
                picedit.setText(getResources().getString(R.string.edit));
            }
        }

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(model.pic.equals("")) return;

                AlertDialog.Builder photoBuilder = new AlertDialog.Builder(activity, R.style.fs_dialog);
                View photoView = getLayoutInflater().inflate(R.layout.dialog_photoview, null);
                photoBuilder.setView(photoView);
                final AlertDialog photoDialog = photoBuilder.create();
                photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                photo.setImageDrawable(pic.getDrawable());
                ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {photoDialog.dismiss();
                    }
                });
                photoDialog.show();
            }
        });

        picedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {
                            pic.setImageBitmap(r.getBitmap());
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(Global.appContext.getFilesDir() + model.getFileName());
                                r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                                new Global.SendImage(Global.appContext.getFilesDir() + model.getFileName(), "medicine").execute();
                                picedit.setText(getResources().getString(R.string.edit));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try { if (out != null) out.close();
                                } catch (IOException e) { e.printStackTrace();}
                            }
                        } else {
                            Log.e("APP", r.getError().getMessage());
                        }
                    }
                }).show(activity.getSupportFragmentManager());
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

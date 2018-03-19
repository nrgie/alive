package com.blueobject.app.alive.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andreabaccega.widget.EditTextValidator;
import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.DoctorsActivity;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.DoctorModel;
import com.blueobject.app.alive.helper.MedModel;
import com.blueobject.app.alive.helper.MedicalModel;
import com.blueobject.app.alive.ui.popwindow.DatePickerPopWin;

import org.bitbucket.stefanodp91.utils.CountryNum;
import org.bitbucket.stefanodp91.utils.Numbers;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by nrgie on 2017.09.12..
 */

public class DetailsView extends LinearLayout {

    LayoutInflater inflater;
    View view;

    public boolean valid = false;
    public String error = "";

    AttributeSet attrs;
    String type;
    String typeval;
    public boolean iconClick = true;

    public DetailsView(Context context) {
        super(context);
        this.attrs = null;
        setupView(context, null);
    }
    public DetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        setupView(context, attrs);
    }

    public void refresh() {
        fillValues();
    }

    public void setKey(String key) {
        TextView k = (TextView) findViewById(R.id.settingsViewKey);
        k.setText(key);
    }

    public void setValue(String value) {
        TextView v = (TextView) findViewById(R.id.settingsViewValue);
        v.setText(value);
        v.setVisibility(View.VISIBLE);
    }

    public void setIcon(Drawable icon) {
        ImageView image = (ImageView) findViewById(R.id.settingsViewIcon);
        image.setImageDrawable(icon);
    }

    public void hideNext() {
        TextView v = (TextView) findViewById(R.id.ddnext);
        v.setVisibility(View.GONE);
    }

    public void setClicktoZoomIcon() {
        final ImageView image = (ImageView) findViewById(R.id.settingsViewIcon);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                image.performClick();
            }
        });
    }

    //public CountryCodePicker getPicker() {
      //  return (CountryCodePicker) findViewById(R.id.ccp);
   // }

    public void setBitmapIcon(Bitmap icon) {
        final ImageView image = (ImageView) findViewById(R.id.settingsViewIcon);
        image.setImageBitmap(icon);
        image.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.width = (int) CommonUtilities.dp2px(40, getContext());
        params.height = (int) CommonUtilities.dp2px(40, getContext());
        image.setLayoutParams(params);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iconClick) {
                    AlertDialog.Builder photoBuilder = new AlertDialog.Builder(getContext());
                    View photoView = inflater.inflate(R.layout.dialog_photoview, null);
                    photoBuilder.setView(photoView);
                    final AlertDialog photoDialog = photoBuilder.create();
                    photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                    ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                    photo.setImageDrawable(image.getDrawable());
                    ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            photoDialog.dismiss();
                        }
                    });
                    photoDialog.show();
                }
            }
        });

    }

    public boolean testValidity() {
        final TextView v = (TextView) findViewById(R.id.settingsViewValue);
        if(v.length() < 3)
            valid = false;
        else
            valid = true;

        setValid(valid);
        return valid;
    }

    public void setError(String e) {
        this.error = e;
    }

    public void setValid(boolean v) {
        valid = v;
        if(valid) {
            ((FormEditText)view.findViewById(R.id.ddnext)).get().clearError();
            ((FormEditText)view.findViewById(R.id.ddnext)).setText(">");
        } else {
            ((FormEditText)view.findViewById(R.id.ddnext)).setText("");
            ((FormEditText)view.findViewById(R.id.ddnext)).get().showError(this.error);
        }
    }




    private void setupView(Context context, AttributeSet attrs) {


        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.detailsview, this);

        //default error;
        error = context.getString(R.string.error_field_must_not_be_empty);

        RelativeLayout block = (RelativeLayout) findViewById(R.id.settingsViewMainLayout);


        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DetailsView, 0, 0);
        try {

            Drawable icon = a.getDrawable(R.styleable.DetailsView_get_icon);
            ImageView image = (ImageView) findViewById(R.id.settingsViewIcon);
            if (icon != null) {
                image.setImageDrawable(icon);
            } else {
                image.setVisibility(View.GONE);
            }

            final TextView k = (TextView) findViewById(R.id.settingsViewKey);
            final String key = a.getString(R.styleable.DetailsView_get_key);
            if (key != null) {
                k.setText(key);
            }

            final TextView v = (TextView) findViewById(R.id.settingsViewValue);
            String value = a.getString(R.styleable.DetailsView_get_value);
            if (value != null) {
                v.setText(value);
            } else {
                v.setVisibility(View.GONE);
            }

          //  final CountryCodePicker c = (CountryCodePicker) findViewById(R.id.country);

            type = a.getString(R.styleable.DetailsView_get_type);
            typeval = a.getString(R.styleable.DetailsView_get_typevalue);

            v.setTypeface(Global.textfont);

            fillValues();

        } finally {
            a.recycle();
        }



    }

    public void fillValues() {

        if(typeval.equals("taj")) {
            File avfile = new File(Global.appContext.getFilesDir() + "/taj.png");
            if(avfile.exists()) {
                Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
                setBitmapIcon(decav);
            }
            if(Global.user.taj.equals(""))
                setValue("-");
            else
                setValue(Global.user.taj);
        }

        if(typeval.equals("blood")) {
            final String[] bloods = getResources().getStringArray(R.array.blood);
            final String[] bloodrhs = getResources().getStringArray(R.array.bloodrh);
            setValue(bloods[Global.user.blood] + " " + bloodrhs[Global.user.bloodrh]);
        }

        if(typeval.equals("height")) {
            if(Global.user.height.equals("")) {
                setValue(" - ");
            } else {
                setValue(Global.user.height + " " + getResources().getString(R.string.heightunit));
            }
        }

        if(typeval.equals("weight")) {
            if(Global.user.weight.equals("")) {
                setValue(" - ");
            } else {
                setValue(Global.user.weight + " " + getResources().getString(R.string.weightunit));
            }
        }

        if(typeval.equals("allergy")) {

            if(Global.user.allergies.size() == 0 && Global.user.customallergies.size() == 0) {
                setValue(" - ");
            } else {
                String val = "";
                int i = 1;
                int all = Global.user.allergies.size() + Global.user.customallergies.size();
                for(AllergyModel m : Global.user.allergies) {
                    if(i==all) val += m.name;
                    else val += m.name + ", ";
                    i++;
                }
                for(AllergyModel m : Global.user.customallergies) {
                    if(i==all) val += m.name;
                    else val += m.name + ", ";
                    i++;
                }
                setValue(val);
            }
        }

        if(typeval.equals("medinfo")) {
            if(Global.user.medinfo.size() == 0) {
                setValue(" - ");
            } else {
                String val = "";
                int i = 1;
                int all = Global.user.medinfo.size();
                for(MedicalModel m : Global.user.medinfo) {
                    if(i==all) val += m.date + ": " + m.name;
                    else val += m.date + ": " + m.name + ", ";
                    i++;
                }
                setValue(val);
            }
        }

        if(typeval.equals("med")) {

            if(Global.user.med.size() == 0) {
                setValue(" - ");
            } else {
                String val = "";
                int i = 1;
                int all = Global.user.med.size();
                for(MedModel m : Global.user.med) {
                    if(i==all) val += m.name;
                    else val += m.name + ", ";
                    i++;
                }
                setValue(val);
            }

        }

        if(typeval.equals("doctors")) {
            if(Global.user.doctors.size() == 0) {
                setValue(" - ");
            } else {
                String val = "";
                int i = 1;
                int all = Global.user.doctors.size();
                for(DoctorModel m : Global.user.doctors) {
                    if(i==all) val += m.name;
                    else val += m.name + " | ";
                    i++;
                }
                setValue(val);
            }

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), DoctorsActivity.class);
                    i.putExtra("disabled", true);
                    i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(i);
                }
            });

        }

    }


}

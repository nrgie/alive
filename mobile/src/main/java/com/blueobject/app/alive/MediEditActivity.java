package com.blueobject.app.alive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.adapter.MedicalListAdapter;
import com.blueobject.app.alive.fragments.HelpDialogFragment;
import com.blueobject.app.alive.helper.MedicalModel;
import com.blueobject.app.alive.ui.popwindow.DatePickerPopWin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nrgie on 2017.08.23..
 */

public class MediEditActivity extends LocalizationActivity {

    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        activity = this;
        setContentView(R.layout.activity_editmedical);

        final EditText name = (EditText) findViewById(R.id.name);
        final TextView date = (TextView) findViewById(R.id.date);

        final Bundle b = getIntent().getExtras();

        String n = "";
        String d = "";
        int id = 0;

        if(b != null && b.getString("name") != null) {
            n = b.getString("name");
            d = b.getString("date");
            id = b.getInt("id");

            name.setText(n);
            date.setText(d);
        }

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(R.string.medicaleditpageinfo));
                newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.help));
            }
        });

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
                MedicalModel model;

                if(name.getText().toString().trim().equals("")) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.error_field_must_not_be_empty), Toast.LENGTH_LONG).show();
                    name.requestFocus();
                    return;
                }

                if(date.getText().toString().trim().equals("")) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.error_field_must_not_be_empty), Toast.LENGTH_LONG).show();
                    date.requestFocus();
                    return;
                }

                if(b != null && b.getString("name") != null) {
                    model = Global.user.medinfo.get(finalId);
                    model.name = name.getText().toString().trim();
                    model.date = date.getText().toString().trim();
                } else {
                    model = new MedicalModel();
                    model.name = name.getText().toString().trim();
                    model.date = date.getText().toString().trim();
                    Global.user.medinfo.add(model);
                }

                Global.saveUser();
                finish();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


                String setdate = "2000-01-01";
                if(date.getText().toString().trim() != "") {
                    setdate = date.getText().toString().trim();
                }
                final Calendar myCalendar = Calendar.getInstance();

                DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(activity, new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month-1);
                        myCalendar.set(Calendar.DAY_OF_MONTH, day);
                        String myFormat = "yyyy-MM-dd";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        date.setText(sdf.format(myCalendar.getTime()));
                    }
                }).textConfirm(activity.getResources().getString(R.string.ok)) //text of confirm button
                        .textCancel(activity.getResources().getString(R.string.cancel)) //text of cancel button
                        .btnTextSize(20) // button text size
                        .viewTextSize(30) // pick view text size
                        .colorCancel(Color.parseColor("#999999")) //color of cancel button
                        .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                        .minYear(1900) //min year in loop
                        .maxYear(myCalendar.get(Calendar.YEAR)+1) // max year in loop
                        .showDayMonthYear(false) // shows like dd mm yyyy (default is false)
                        .dateChose(setdate) // date chose when init popwindow
                        .build();
                pickerPopWin.showPopWin((Activity) activity);

            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

}

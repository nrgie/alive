package com.blueobject.app.alive.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andreabaccega.widget.EditTextValidator;
import com.andreabaccega.widget.FormEditText;
import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.Utils;
import com.blueobject.app.alive.ui.popwindow.DatePickerPopWin;
import com.hbb20.CountryCodePicker;

import org.bitbucket.stefanodp91.utils.CountryNum;
import org.bitbucket.stefanodp91.utils.Numbers;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nrgie on 2017.09.12..
 */

public class SettingsView extends LinearLayout {

    LayoutInflater inflater;
    View view;

    private int siblingResourceId;
    private View siblingView;

    public boolean imagesetted = false;

    public boolean valid = false;
    public String error = "";

    public String id = "";

    public SettingsView(Context context) {
        super(context);
        setupView(context, null);
    }
    public SettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView(context, attrs);
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

    public CountryCodePicker getPicker() {
        return (CountryCodePicker) findViewById(R.id.ccp);
    }

    public void setBitmapIcon(Bitmap icon) {
        final ImageView image = (ImageView) findViewById(R.id.settingsViewIcon);
        image.setImageBitmap(icon);
        image.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.width = (int) CommonUtilities.dp2px(32, getContext());
        params.height = (int) CommonUtilities.dp2px(32, getContext());
        image.setLayoutParams(params);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(id.equals("fullname") && !imagesetted) return;

                AlertDialog.Builder photoBuilder = new AlertDialog.Builder(getContext());
                View photoView = inflater.inflate(R.layout.dialog_photoview, null);
                photoBuilder.setView(photoView);
                final AlertDialog photoDialog = photoBuilder.create();
                photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                photo.setImageDrawable(image.getDrawable());
                ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {photoDialog.dismiss();
                    }
                });
                photoDialog.show();
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


    private void setupView(final Context context, AttributeSet attrs) {

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.settingsview, this);

        //default error;
        error = context.getString(R.string.error_field_must_not_be_empty);

        RelativeLayout block = (RelativeLayout) findViewById(R.id.settingsViewMainLayout);


        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsView, 0, 0);
        try {

            Drawable icon = a.getDrawable(R.styleable.SettingsView_set_icon);
            ImageView image = (ImageView) findViewById(R.id.settingsViewIcon);
            if (icon != null) {
                image.setImageDrawable(icon);
            } else {
                image.setVisibility(View.GONE);
            }

            final TextView k = (TextView) findViewById(R.id.settingsViewKey);
            final String key = a.getString(R.styleable.SettingsView_set_key);
            if (key != null) {
                k.setText(key);
            }

            final TextView h = (TextView) findViewById(R.id.hint);
            final String hint = a.getString(R.styleable.SettingsView_set_hint);
            if (hint != null) {
                h.setText(hint);
                h.setVisibility(View.VISIBLE);
            }

            final TextView v = (TextView) findViewById(R.id.settingsViewValue);
            String value = a.getString(R.styleable.SettingsView_set_value);
            if (value != null) {
                v.setText(value);
            } else {
                v.setVisibility(View.GONE);
            }

            final CountryCodePicker c = (CountryCodePicker) findViewById(R.id.country);
            final String type = a.getString(R.styleable.SettingsView_set_type);
            final String typeval = a.getString(R.styleable.SettingsView_set_typevalue);

            final boolean locked = a.getBoolean(R.styleable.SettingsView_set_lock, false);

            //k.setTypeface(Global.textfont);
            v.setTypeface(Global.textfont);

            if(type != null) {

                if(type.equals("onOff")) {

                    FrameLayout frame = (FrameLayout) findViewById(R.id.settingsViewContent);
                    frame.setVisibility(View.VISIBLE);

                    final SmoothCheckBox sw = (SmoothCheckBox) findViewById(R.id.onOff);
                    sw.setVisibility(View.VISIBLE);

                    TextView dd = (TextView) findViewById(R.id.ddnext);
                    dd.setVisibility(View.GONE);

                    if(typeval.equals("learn")) {
                        sw.setChecked(Global.student);
                        sw.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                                Global.student = isChecked;
                            }
                        });
                    }

                    if(typeval.equals("trackme")) {


                        sw.setChecked(Global.user.cantrack);
                        sw.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(SmoothCheckBox checkBox, final boolean isChecked) {
                                Global.user.cantrack = isChecked;
                                Global.saveUser();
                            }
                        });

                    }

                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(!typeval.equals("trackme")) {
                                sw.performClick();
                            } else {
                                sw.disabled = true;
                                pinlock(new PinLock() {
                                    @Override
                                    public void onPositive() {
                                        sw.click();
                                        sw.disabled = false;
                                    }
                                    @Override
                                    public void onNegative() {}
                                });

                            }

                        }
                    });

                }


                if(type.equals("pin")) {

                    v.setVisibility(View.VISIBLE);

                    if(Global.shared.getString("pin", "").equals("")) {
                        v.setText(context.getResources().getString(R.string.inactive));
                    } else {
                        v.setText(context.getResources().getString(R.string.active));
                    }


                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            pinlock(new PinLock() {
                                @Override
                                public void onPositive() {

                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                                    View dialogView = inflater.inflate(R.layout.pin_input_dialog, null);
                                    dialogBuilder.setView(dialogView);
                                    final TextView title = (TextView) dialogView.findViewById(R.id.title);
                                    final FormEditText input = (FormEditText) dialogView.findViewById(R.id.input);
                                    Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                                    Button save = (Button) dialogView.findViewById(R.id.save);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                                    input.setText(Global.shared.getString("pin", ""));
                                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    input.get().setTestType(EditTextValidator.TEST_NUMERIC, context);

                                    //title.setText("PIN kód beállítása");
                                    input.setHint(getContext().getResources().getString(R.string.pindigits));

                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                    save.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if(input.getText().toString().equals("")) {
                                                Global.shared.putString("pin", "");
                                                v.setText(context.getResources().getString(R.string.inactive));
                                                alertDialog.dismiss();
                                                return;
                                            }

                                            if(input.testValidity()) {
                                                if(input.getText().toString().length() != 4) {
                                                    input.setError("4 számjegyű a pin kód");
                                                    setValid(false);
                                                    return;
                                                }
                                                setValid(true);
                                                Global.shared.putString("pin", input.getText().toString());
                                                v.setText(context.getResources().getString(R.string.active));
                                                alertDialog.dismiss();
                                            } else {
                                                setValid(false);
                                                return;
                                            }
                                        }
                                    });
                                    alertDialog.show();
                                }
                                @Override
                                public void onNegative() {}
                            });

                        }
                    });

                }


                if(type.equals("input1") && typeval != null) {

                    if(typeval.contains("user.")) {
                        final String field = typeval.replace("user.", "");
                        final String stringval = String.valueOf(Global.user.getValue(field));
                        v.setText(stringval);
                        v.setVisibility(View.VISIBLE);

                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View onclcikv) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                                View dialogView = inflater.inflate(R.layout.input_dialog, null);
                                dialogBuilder.setView(dialogView);
                                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                                final FormEditText input = (FormEditText) dialogView.findViewById(R.id.input);
                                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                                Button save = (Button) dialogView.findViewById(R.id.save);
                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                input.setText(stringval);

                                if(field.equals("email")) {
                                    input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                                    input.get().setTestType(EditTextValidator.TEST_EMAIL, context);
                                }

                                title.setText(key);
                                input.setText(stringval);
                                input.setHint(key);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        if(input.testValidity()) {
                                            setValid(true);
                                            try {
                                                Global.user.setValue(field, input.getText().toString());
                                                v.setText(input.getText().toString());
                                                Global.saveUser();
                                            } catch (NoSuchFieldException e) {
                                                e.printStackTrace();
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            }
                                            alertDialog.dismiss();
                                        } else
                                            setValid(false);
                                    }
                                });
                                alertDialog.show();
                            }
                        });

                    }

                }


                if(type.equals("birthday")) {


                    v.setText(Global.user.bday);
                    v.setVisibility(View.VISIBLE);


                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String setdate = "1970-01-01";
                            if(Global.user.bday != "") {
                                setdate = Global.user.bday;
                            }
                            final Calendar myCalendar = Calendar.getInstance();

                            DatePickerPopWin pickerPopWin = new DatePickerPopWin.Builder(getContext(), new DatePickerPopWin.OnDatePickedListener() {
                                @Override
                                public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                                    myCalendar.set(Calendar.YEAR, year);
                                    myCalendar.set(Calendar.MONTH, month-1);
                                    myCalendar.set(Calendar.DAY_OF_MONTH, day);
                                    String myFormat = "yyyy-MM-dd";
                                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                                    v.setText(sdf.format(myCalendar.getTime()));
                                    Global.user.bday = sdf.format(myCalendar.getTime());
                                    Global.saveUser();
                                }
                            }).textConfirm(getContext().getResources().getString(R.string.ok)) //text of confirm button
                                    .textCancel(getContext().getResources().getString(R.string.cancel)) //text of cancel button
                                    .btnTextSize(20) // button text size
                                    .viewTextSize(30) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .minYear(1900) //min year in loop
                                    .maxYear(myCalendar.get(Calendar.YEAR)) // max year in loop
                                    .showDayMonthYear(false) // shows like dd mm yyyy (default is false)
                                    .dateChose(setdate) // date chose when init popwindow
                                    .build();
                            pickerPopWin.showPopWin((Activity) getContext());


                        }
                    });



                }

                if(type.equals("country")) {

                    v.setVisibility(View.GONE);
                    c.setVisibility(View.VISIBLE);

                    setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            c.show();
                        }
                    });

                    if(typeval.equals("emnumbers")) {

                        if(!Global.user.emcountry.equals(""))
                            c.setCountryForNameCode(Global.user.emcountry);
                        else if(!Global.user.national.equals(""))
                            c.setCountryForNameCode(Global.user.national);


                        c.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                            @Override
                            public void onCountrySelected() {
                                String code = c.getSelectedCountryNameCode();
                                Log.e("CODE", code);

                                Global.user.emcountry = code;
                                Global.saveUser();
                                Numbers n = new Numbers();

                                try {
                                    CountryNum cc = n.get(code);

                                    Global.user.emnumber = cc.M112 ? "112" : "-";
                                    Global.user.policenumber = (cc.P.equals("")) ? cc.C : cc.P;
                                    Global.user.firenumber = (cc.F.equals("")) ? cc.C : cc.F;
                                    Global.user.ambulancenumber = (cc.A.equals("")) ? cc.C : cc.A;

                                    SettingsView emnumber = (SettingsView) getRootView().findViewById(R.id.emnumber);
                                    SettingsView policenumber = (SettingsView) getRootView().findViewById(R.id.police);
                                    SettingsView firenumber = (SettingsView) getRootView().findViewById(R.id.fire);
                                    SettingsView ambulancenumber = (SettingsView) getRootView().findViewById(R.id.amb);

                                    emnumber.setValue((Global.user.emnumber.equals("")) ? "-" : Global.user.emnumber);
                                    policenumber.setValue((Global.user.policenumber.equals("")) ? "-" : Global.user.policenumber);
                                    firenumber.setValue((Global.user.firenumber.equals("")) ? "-" : Global.user.firenumber);
                                    ambulancenumber.setValue((Global.user.ambulancenumber.equals("")) ? "-" : Global.user.ambulancenumber);

                                    Global.saveUser();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                    }

                    if(typeval.equals("national")) {

                        if(!Global.user.national.equals(""))
                            c.setCountryForNameCode(Global.user.national);

                        c.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                            @Override
                            public void onCountrySelected() {
                                String code = c.getSelectedCountryNameCode();
                                Log.e("CODE", code);
                                Global.user.national = code;
                                Global.saveUser();
                            }
                        });

                    }

                }


                if(type.equals("gender")) {

                    if(Global.user.gender == 0) {
                        v.setText(context.getResources().getString(R.string.female));
                        v.setVisibility(View.VISIBLE);
                    } else {
                        v.setVisibility(View.VISIBLE);
                        v.setText(context.getResources().getString(R.string.male));
                    }

                      setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View onclcikv) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                                View dialogView = inflater.inflate(R.layout.gender_dialog, null);
                                dialogBuilder.setView(dialogView);
                                final Slide input = (Slide) dialogView.findViewById(R.id.gender);
                                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                                Button save = (Button) dialogView.findViewById(R.id.save);
                                final AlertDialog alertDialog = dialogBuilder.create();
                                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                                if(Global.user.gender == 0)
                                    input.setChecked(false);
                                else
                                    input.setChecked(true);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                    }
                                });

                                input.setOnCheckedChangeListener(new Slide.OnCheckedChangeListener(){
                                    @Override
                                    public void onCheckedChanged(Slide checkBox, boolean isChecked) {
                                        if(isChecked) {
                                            Global.user.gender = 1;
                                            v.setText(context.getResources().getString(R.string.male));
                                        } else {
                                            Global.user.gender = 0;
                                            v.setText(context.getResources().getString(R.string.female));
                                        }
                                        Global.saveUser();
                                    }
                                });

                                alertDialog.show();
                            }
                        });

                }


            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            a.recycle();
        }


    }

    public void pinlock(final PinLock lock) {

        String stored = Global.shared.getString("pin", "");

        if(stored.equals("")) {
            lock.onPositive();
            return;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
        final View dialogView = inflater.inflate(R.layout.pin_layout, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final String TAG = "PinLockView";

        PinLockView mPinLockView;
        IndicatorDots mIndicatorDots;

        dialogView.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        PinLockListener mPinLockListener = new PinLockListener() {
            @Override
            public void onComplete(String pin) {
                String stored = Global.shared.getString("pin", "");
                if(stored.equals("")) {
                    Global.shared.putString("pin", pin);
                } else {
                    if(stored.equals(pin)) {
                        Global.saveUser();
                        alertDialog.dismiss();
                        lock.onPositive();
                    } else {
                        ((TextView) dialogView.findViewById(R.id.prompt)).setText("Invalid PIN");
                        lock.onNegative();
                    }
                }

                Log.e(TAG, "Pin complete: " + pin);
            }

            @Override
            public void onEmpty() {
                Log.d(TAG, "Pin empty");
            }

            @Override
            public void onPinChange(int pinLength, String intermediatePin) {
                Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
                ((TextView) dialogView.findViewById(R.id.prompt)).setText("Enter PIN");
            }
        };

        mPinLockView = (PinLockView) dialogView.findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) dialogView.findViewById(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
        alertDialog.show();


    }

    public interface PinLock {
        void onPositive();
        void onNegative();
    }

}

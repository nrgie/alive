package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;

import com.blueobject.app.alive.adapter.AllergySetupListAdapter;
import com.blueobject.app.alive.adapter.LangSetupListAdapter;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.LangModel;
import com.blueobject.app.alive.ui.SettingsView;
import com.blueobject.app.alive.ui.Slide;
import com.blueobject.app.alive.ui.popwindow.DatePickerPopWin;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class BasicFragment extends Fragment {
    private static final String TAG = BasicFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";

    static final int DATEPICKER_TAG = 23123123;
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    protected FormEditText name1;
    protected FormEditText name2;

    protected FormEditText email;
    protected com.hbb20.CountryCodePicker ccp;
    public com.hbb20.CountryCodePicker phoneccp;
    protected EditText bday;
    protected Slide gender;

    //protected FormEditText myphone;

    public BasicFragment frag;
    public View view;

    private String JSONType = Constants.JSON_TYPE_TEXT;
    private String JSONKey = Constants.JSON_TEXT;

    ImageView avatar;

    protected SettingsView fullname;
    protected SettingsView myphone;
    protected SettingsView mail;
    protected SettingsView home;
    protected SettingsView pass;

    LayoutInflater inflater;

    public static BasicFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        BasicFragment f = new BasicFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            mKey = args.getString(ARG_KEY);
            //mPage = mCallbacks.onGetPage(mKey);
        }
        frag = this;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_basic, container, false);

        this.inflater = inflater;

        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        this.view = v;

        fullname = (SettingsView) v.findViewById(R.id.fullname);
        fullname.id = "fullname";

        mail = (SettingsView) v.findViewById(R.id.mail);
        pass = (SettingsView) v.findViewById(R.id.pass);

        home = (SettingsView) v.findViewById(R.id.home);



        File avfile = new File(Global.appContext.getFilesDir() + "/avatar.png");
        if(avfile.exists()) {
            Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
            fullname.setBitmapIcon(decav);
            fullname.imagesetted = true;
        } else {
            Bitmap i = BitmapFactory.decodeResource(activity.getResources(), R.drawable.avatar);
            Bitmap icon = Bitmap.createScaledBitmap(i, 24, 24, false);

            fullname.imagesetted = false;
            fullname.setBitmapIcon(icon);

            fullname.getRootView().findViewById(R.id.settingsViewIcon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!fullname.imagesetted) {
                        PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                if (r.getError() == null) {

                                    //avatar.setImageBitmap(r.getBitmap());

                                    fullname.setBitmapIcon(r.getBitmap());
                                    fullname.imagesetted = true;
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(Global.appContext.getFilesDir() + "/avatar.png");
                                        r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

                                        new Global.SendImage(Global.appContext.getFilesDir() + "/avatar.png", "avatar").execute();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            if (out != null) out.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    Log.e("APP", r.getError().getMessage());
                                }
                            }
                        }).show(frag.getActivity().getSupportFragmentManager());
                    } else {
                        AlertDialog.Builder photoBuilder = new AlertDialog.Builder(getContext());
                        View photoView = inflater.inflate(R.layout.dialog_photoview, null);
                        photoBuilder.setView(photoView);
                        final AlertDialog photoDialog = photoBuilder.create();
                        photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                        ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                        photo.setImageDrawable(avatar.getDrawable());
                        ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {photoDialog.dismiss();}
                        });
                        photoDialog.show();
                    }
                }
            });
        }


        fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.dialog_fullname, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final FormEditText name1 = (FormEditText) dialogView.findViewById(R.id.name1);
                final EditText name2 = (EditText) dialogView.findViewById(R.id.name2);
                final FormEditText name3 = (FormEditText) dialogView.findViewById(R.id.name3);
                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);


                name1.setText(Global.user.name1);
                name2.setText(Global.user.name2);
                name3.setText(Global.user.name3);

                final Button iconedit = (Button) dialogView.findViewById(R.id.iconedit);

                final ImageView avatar = (ImageView) dialogView.findViewById(R.id.icon);

                File avfile = new File(Global.appContext.getFilesDir() + "/avatar.png");
                if(avfile.exists()) {
                    Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
                    avatar.setImageBitmap(decav);
                    fullname.setBitmapIcon(decav);
                    fullname.imagesetted = true;

                    avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder photoBuilder = new AlertDialog.Builder(getContext());
                            View photoView = inflater.inflate(R.layout.dialog_photoview, null);
                            photoBuilder.setView(photoView);
                            final AlertDialog photoDialog = photoBuilder.create();
                            photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                            ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                            photo.setImageDrawable(avatar.getDrawable());
                            ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {photoDialog.dismiss();
                                }
                            });
                            photoDialog.show();
                        }
                    });

                } else {
                    Bitmap i = BitmapFactory.decodeResource(activity.getResources(), R.drawable.avatar);
                    Bitmap icon = Bitmap.createScaledBitmap(i, 24, 24, false);

                    fullname.setBitmapIcon(icon);
                    fullname.imagesetted = false;
                    avatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            iconedit.performClick();
                        }
                    });
                }


                iconedit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                if (r.getError() == null) {
                                    avatar.setImageBitmap(r.getBitmap());
                                    fullname.setBitmapIcon(r.getBitmap());
                                    fullname.imagesetted = true;
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(Global.appContext.getFilesDir() + "/avatar.png");
                                        r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

                                        new Global.SendImage(Global.appContext.getFilesDir() + "/avatar.png", "avatar").execute();

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
                        }).show(frag.getActivity().getSupportFragmentManager());
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(name1.testValidity() && name3.testValidity()) {
                            fullname.setValid(true);

                            Global.user.name1 = name1.getText().toString().trim();
                            Global.user.name2 = name2.getText().toString().trim();
                            Global.user.name3 = name3.getText().toString().trim();
                            Global.user.name = Global.user.name1 + " ";
                            if(!Global.user.name2.equals(""))
                                Global.user.name += Global.user.name2 + " ";
                            Global.user.name += Global.user.name3;

                            Global.saveUser();
                            fullname.setValue(Global.user.name1 + " " + Global.user.name2 + " " + Global.user.name3);
                            alertDialog.dismiss();

                        } else {
                            fullname.setValid(false);
                        }

                    }
                });

                alertDialog.show();

            }
        });

        fullname.setValue(Global.user.name1 + " " + Global.user.name2 + " " + Global.user.name3);


        myphone = (SettingsView) v.findViewById(R.id.myphone);
        myphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.dialog_myphone, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final FormEditText phone = (FormEditText) dialogView.findViewById(R.id.myphone);


                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);

                if(Global.user.phone.equals("")) {

                    String mPhoneNumber = ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

                    if (mPhoneNumber != null) {

                        phone.setText(mPhoneNumber);
                        Global.user.phone = mPhoneNumber.trim();
                        Global.saveUser();

                    }

                } else {

                    phone.setText(Global.user.phone);

                }

                phoneccp = (com.hbb20.CountryCodePicker) dialogView.findViewById(R.id.phoneccp);

                if(!Global.user.phoneprefix.equals(""))
                    phoneccp.setCountryForNameCode(Global.user.phoneprefix);

                phoneccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
                    @Override
                    public void onCountrySelected() {
                        //String code = phoneccp.getSelectedCountryNameCode();
                        Global.user.phoneprefixcode = phoneccp.getSelectedCountryCodeWithPlus();
                        Global.user.phoneprefix = phoneccp.getSelectedCountryNameCode();
                        //Global.saveUser();
                    }
                });

                Global.user.phoneprefixcode = phoneccp.getSelectedCountryCodeWithPlus();
                Global.user.phoneprefix = phoneccp.getSelectedCountryNameCode();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(phone.testValidity()) {

                            Log.e("Check", phoneccp.getSelectedCountryCodeWithPlus() + phone.getText().toString());

                            if (isValidPhoneNumber(phoneccp.getSelectedCountryCodeWithPlus(), phone.getText().toString().trim())) {

                                myphone.setValid(true);
                                Global.user.phone = phone.getText().toString().trim();
                                Global.user.phoneprefixcode = phoneccp.getSelectedCountryCodeWithPlus();
                                Global.user.phoneprefix = phoneccp.getSelectedCountryNameCode();

                                //pre viber
                                Global.user.viber = Global.user.phoneprefixcode + Global.user.phone;

                                Global.saveUser();
                                myphone.setValue(Global.user.phoneprefixcode + " " + Global.user.phone);

                                alertDialog.dismiss();
                            } else {
                                phone.setError("Nem érvényes telefonszám");
                                myphone.setValid(false);
                            }

                        } else
                            myphone.setValid(false);
                    }
                });

                alertDialog.show();

            }
        });

        myphone.setValue(Global.user.phoneprefixcode + " " + Global.user.phone);


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.dialog_home, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);

                final FormEditText zip = (FormEditText) dialogView.findViewById(R.id.zip);
                final FormEditText city = (FormEditText) dialogView.findViewById(R.id.city);
                final FormEditText street = (FormEditText) dialogView.findViewById(R.id.street);
                final FormEditText streetno = (FormEditText) dialogView.findViewById(R.id.streetno);
                final FormEditText floordoor = (FormEditText) dialogView.findViewById(R.id.floordoor);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);

                zip.setText(Global.user.zip);
                city.setText(Global.user.city);
                street.setText(Global.user.street);
                streetno.setText(Global.user.streetno);
                floordoor.setText(Global.user.floordoor);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(zip.testValidity() && city.testValidity() && street.testValidity() && streetno.testValidity()) {
                            home.setValid(true);

                            Global.user.zip = zip.getText().toString().trim();
                            Global.user.city = city.getText().toString().trim();
                            Global.user.street = street.getText().toString().trim();
                            Global.user.streetno = streetno.getText().toString().trim();
                            Global.user.floordoor = floordoor.getText().toString().trim();

                            Global.user.home = Global.user.zip + " " + Global.user.city + " " + Global.user.street
                                    + " " + Global.user.streetno + " " + Global.user.floordoor;

                            Global.saveUser();
                            home.setValue(Global.user.home);
                            alertDialog.dismiss();

                        } else {
                            home.setValid(false);
                        }

                    }
                });

                alertDialog.show();

            }
        });

        home.setValue(Global.user.home);





        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.dialog_pass, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);

                final FormEditText pass1 = (FormEditText) dialogView.findViewById(R.id.pass1);
                final FormEditText pass2 = (FormEditText) dialogView.findViewById(R.id.pass2);


                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);

                pass1.setText(Global.user.password);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(pass1.testValidity() && pass2.testValidity()) {

                            if(!pass1.getText().toString().equals(pass2.getText().toString())) {
                                pass.setValid(false);
                                Toast.makeText(activity, activity.getResources().getString(R.string.passnotmatch), Toast.LENGTH_LONG).show();
                                return;
                            }

                            pass.setValid(true);
                            Global.user.password = pass1.getText().toString().trim();
                            Global.saveUser();
                            pass.setValue(Global.user.password);
                            alertDialog.dismiss();

                        } else {
                            pass.setValid(false);

                        }

                    }
                });

                alertDialog.show();

            }
        });

        pass.setValue(Global.user.password);




        final SettingsView langs = (SettingsView) v.findViewById(R.id.langs);

        langs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.add_langs, null);
                dialogBuilder.setView(dialogView);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);
                RecyclerView list = (RecyclerView) dialogView.findViewById(R.id.allist);
                String[] lists = getResources().getStringArray(R.array.languages);
                ArrayList<LangModel> alls = new ArrayList<LangModel>();
                for(String l : lists) {
                    boolean ch = false;
                    LangModel al = new LangModel().setName(l);
                    for(LangModel a: Global.user.langs) { if(a.name.equals(l)) { al.check(true);} }
                    alls.add(al);
                }
                list.setAdapter(new LangSetupListAdapter(activity, alls));
                LinearLayoutManager listllm = new LinearLayoutManager(activity);
                listllm.setOrientation(LinearLayoutManager.VERTICAL);
                list.setLayoutManager(listllm);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {alertDialog.dismiss();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Global.saveUser();
                        if(Global.user.langs.size() == 0) {
                            langs.setValue(" - ");
                        } else {
                            String val = "";
                            int i = 1;
                            int all = Global.user.langs.size();
                            for(LangModel m : Global.user.langs) {
                                if(i==all) val += m.name;
                                else val += m.name + ", ";
                                i++;
                            }
                            langs.setValue(val);
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        if(Global.user.langs.size() == 0) {
            langs.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.langs.size();
            for(LangModel m : Global.user.langs) {
                if(i==all) val += m.name;
                else val += m.name + ", ";
                i++;
            }
            langs.setValue(val);
        }
        


    }

    Activity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        //mCallbacks = (PageFragmentCallbacks) activity;
    }

    public boolean test() {
        return (fullname.testValidity() &&
                myphone.testValidity() &&
                mail.testValidity() &&
                pass.testValidity()
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    protected void setJSONType(String JSONType) {
        this.JSONType = JSONType;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    @Override
    public void onResume(){
        super.onResume();
        View current = activity.getCurrentFocus();
        if (current != null)
            current.clearFocus();
    }

    private static boolean isValidPhoneNumber(String code, String target) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber num = new Phonenumber.PhoneNumber();
        num.setCountryCode(Integer.parseInt(code.replace("+", "")));
        num.setNationalNumber(Long.parseLong(target));
        //num.setRawInput(target).setCountryCodeSource(Phonenumber.PhoneNumber.CountryCodeSource.FROM_NUMBER_WITH_PLUS_SIGN);
        return phoneUtil.isValidNumber(num);

        //return !(target == null || target.length() < 6 || target.length() > 13) && android.util.Patterns.PHONE.matcher(target).matches();
    }

}

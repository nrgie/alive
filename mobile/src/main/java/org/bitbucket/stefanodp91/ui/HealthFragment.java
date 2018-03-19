package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.AllergyActivity;
import com.blueobject.app.alive.DoctorsActivity;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MediActivity;
import com.blueobject.app.alive.MedicineActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.adapter.AllergyListAdapter;
import com.blueobject.app.alive.adapter.AllergySetupListAdapter;
import com.blueobject.app.alive.adapter.DoctorsListAdapter;
import com.blueobject.app.alive.adapter.MedListAdapter;
import com.blueobject.app.alive.adapter.MedicalListAdapter;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.CommonUtilities;
import com.blueobject.app.alive.helper.DoctorModel;
import com.blueobject.app.alive.helper.MedModel;
import com.blueobject.app.alive.helper.MedicalModel;
import com.blueobject.app.alive.ui.SettingsView;
import com.hbb20.CountryCodePicker;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.bitbucket.stefanodp91.model.Page;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class HealthFragment extends Fragment {
    private static final String TAG = HealthFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";

    public HealthFragment frag;

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    SettingsView taj;
    SettingsView blood;
    SettingsView height;
    SettingsView weight;
    SettingsView allergy;
    SettingsView medical;
    SettingsView meds;
    SettingsView doctors;


    /*
    protected RecyclerView alinfo;
    protected RecyclerView medinfo;
    protected RecyclerView med;
    protected RecyclerView doctors;
    */




    public static HealthFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        HealthFragment f = new HealthFragment();
        f.setArguments(args);
        return f;
    }

    public boolean test() {
        return (taj.testValidity() &&
                height.testValidity() &&
                weight.testValidity());
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

        View v = inflater.inflate(R.layout.fragment_health_text, container, false);

        if(Global.user.bloodrh == 2) {
            Global.user.bloodrh = 1;
            Global.saveUser();
        }

        taj = (SettingsView) v.findViewById(R.id.taj);

        File avfile = new File(Global.appContext.getFilesDir() + "/taj.png");
        if(avfile.exists()) {
            Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
            taj.setBitmapIcon(decav);
        }

        taj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.dialog_taj, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final FormEditText input = (FormEditText) dialogView.findViewById(R.id.input);
                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;

                final Button iconedit = (Button) dialogView.findViewById(R.id.iconedit);

                input.setText(Global.user.taj);

                final ImageView tajpic = (ImageView) dialogView.findViewById(R.id.icon);
                File avfile = new File(Global.appContext.getFilesDir() + "/taj.png");
                if(avfile.exists()) {
                    Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
                    tajpic.setImageBitmap(decav);
                    taj.setBitmapIcon(decav);
                    iconedit.setText(getContext().getResources().getString(R.string.edit));
                }

                tajpic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder photoBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                        View photoView = inflater.inflate(R.layout.dialog_photoview, null);
                        photoBuilder.setView(photoView);
                        final AlertDialog photoDialog = photoBuilder.create();
                        photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                        ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                        photo.setImageDrawable(tajpic.getDrawable());
                        ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {photoDialog.dismiss();
                            }
                        });
                        photoDialog.show();
                    }
                });

                iconedit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                if (r.getError() == null) {
                                    //avatar.setImageURI(null);
                                    //avatar.setImageURI(r.getUri());
                                    tajpic.setImageBitmap(r.getBitmap());
                                    taj.setBitmapIcon(r.getBitmap());
                                    FileOutputStream out = null;
                                    try {
                                        out = new FileOutputStream(Global.appContext.getFilesDir() + "/taj.png");
                                        r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance

                                        new Global.SendImage(Global.appContext.getFilesDir() + "/taj.png", "taj").execute();

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

                        if(input.testValidity()) {
                            taj.setValid(true);

                            Global.user.taj = input.getText().toString();
                            taj.setValue(input.getText().toString());
                            Global.saveUser();
                            alertDialog.dismiss();
                        } else {
                            taj.setValid(false);
                        }

                    }
                });

                alertDialog.show();

            }
        });



        if(Global.user.taj.equals(""))
            taj.setValue("-");
        else
            taj.setValue(Global.user.taj);

        final String[] bloods = getResources().getStringArray(R.array.blood);
        final String[] bloodrhs = getResources().getStringArray(R.array.bloodrh);

        blood = (SettingsView) v.findViewById(R.id.bl);
        blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.dialog_blood, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final EditText phone = (EditText) dialogView.findViewById(R.id.myphone);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;

                Spinner bl = (Spinner) dialogView.findViewById(R.id.blood);
                bl.setSelection((int) Global.user.blood);

                Spinner rh = (Spinner) dialogView.findViewById(R.id.bloodrh);
                rh.setSelection((int) Global.user.bloodrh);

                bl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { Global.user.blood = position; }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });

                rh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { Global.user.bloodrh = position; }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
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
                        Global.saveUser();
                        blood.setValue(bloods[Global.user.blood] + ", " + bloodrhs[Global.user.bloodrh]);
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });

        blood.setValue(bloods[Global.user.blood] + " " + bloodrhs[Global.user.bloodrh]);

        height = (SettingsView) v.findViewById(R.id.magas);
        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.inputnumber_dialog, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final FormEditText input = (FormEditText) dialogView.findViewById(R.id.input);
                title.setText(getResources().getString(R.string.height));
                input.setHint(getResources().getString(R.string.height));

                input.setText(Global.user.height);
                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);
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
                        if(input.testValidity()) {
                            Global.user.height = input.getText().toString();
                            Global.saveUser();
                            if (Global.user.height.equals("")) {
                                height.setValue(" - ");
                            } else {
                                height.setValue(Global.user.height + " " + getResources().getString(R.string.heightunit));
                            }
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
            }
        });

        if(Global.user.height.equals("")) {
            height.setValue(" - ");
        } else {
            height.setValue(Global.user.height + " " + getResources().getString(R.string.heightunit));
        }


        weight = (SettingsView) v.findViewById(R.id.suly);
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.inputnumber_dialog, null);
                dialogBuilder.setView(dialogView);
                final TextView title = (TextView) dialogView.findViewById(R.id.title);
                final FormEditText input = (FormEditText) dialogView.findViewById(R.id.input);
                title.setText(getResources().getString(R.string.weight));
                input.setHint(getResources().getString(R.string.weight));
                input.setText(Global.user.weight);
                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);
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
                        if(input.testValidity()) {
                            Global.user.weight = input.getText().toString();
                            Global.saveUser();
                            if (Global.user.weight.equals("")) {
                                weight.setValue(" - ");
                            } else {
                                weight.setValue(Global.user.weight + " " + getResources().getString(R.string.weightunit));
                            }
                            alertDialog.dismiss();
                        }
                    }
                });
                alertDialog.show();
            }
        });

        if(Global.user.weight.equals("")) {
            weight.setValue(" - ");
        } else {
            weight.setValue(Global.user.weight + " " + getResources().getString(R.string.weightunit));
        }

        allergy = (SettingsView) v.findViewById(R.id.aller);
        allergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), AllergyActivity.class));

                /*
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.fs_dialog);
                View dialogView = inflater.inflate(R.layout.add_allergy, null);
                dialogBuilder.setView(dialogView);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);
                RecyclerView list = (RecyclerView) dialogView.findViewById(R.id.allist);
                String[] lists = getResources().getStringArray(R.array.allergies);
                ArrayList<AllergyModel> alls = new ArrayList<AllergyModel>();
                for(String l : lists) {
                    boolean ch = false;
                    AllergyModel al = new AllergyModel().setName(l);
                    for(AllergyModel a: Global.user.allergies) { if(a.name.equals(l)) { al.check(true);} }
                    alls.add(al);
                }
                list.setAdapter(new AllergySetupListAdapter(activity, alls));
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
                        if(Global.user.allergies.size() == 0) {
                            allergy.setValue(" - ");
                        } else {
                            String val = "";
                            int i = 1;
                            int all = Global.user.allergies.size();
                            for(AllergyModel m : Global.user.allergies) {
                                if(i==all) val += m.name;
                                else val += m.name + ", ";
                                i++;
                            }
                            allergy.setValue(val);
                        }
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                */
            }
        });

        if(Global.user.allergies.size() == 0 && Global.user.customallergies.size() == 0) {
            allergy.setValue(" - ");
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
            allergy.setValue(val);
        }


        medical = (SettingsView) v.findViewById(R.id.mutet);
        medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, MediActivity.class));
            }
        });

        if(Global.user.medinfo.size() == 0) {
            medical.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.medinfo.size();
            for(MedicalModel m : Global.user.medinfo) {
                if(i==all) val += m.date + ": " + m.name;
                else val += m.date + ": " + m.name + ", ";
                i++;
            }
            medical.setValue(val);
        }

        meds = (SettingsView) v.findViewById(R.id.medi);

        meds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, MedicineActivity.class));
            }
        });

        if(Global.user.med.size() == 0) {
            meds.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.med.size();
            for(MedModel m : Global.user.med) {
                if(i==all) val += m.name + " (" + m.qty + ")";
                else val += m.name + " (" + m.qty + ")" + ", ";
                i++;
            }
            meds.setValue(val);
        }

        doctors = (SettingsView) v.findViewById(R.id.dokik);
        doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, DoctorsActivity.class));
            }
        });

        if(Global.user.doctors.size() == 0) {
            doctors.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.doctors.size();
            for(DoctorModel m : Global.user.doctors) {
                if(i==all) val += m.name;
                else val += m.name + " | ";
                i++;
            }
            doctors.setValue(val);
        }




        /*
        blood = (Spinner) v.findViewById(R.id.blood);
        blood.setSelection((int) Global.user.blood);

        bloodrh = (Spinner) v.findViewById(R.id.bloodrh);
        bloodrh.setSelection((int) Global.user.bloodrh);

        height = (EditText) v.findViewById(R.id.height);
        height.setText(Global.user.height);

        weight = (EditText) v.findViewById(R.id.weight);
        weight.setText(Global.user.weight);

        //taj = (FormEditText) v.findViewById(R.id.taj);
        //taj.setText(Global.user.taj);


        alinfo = (RecyclerView) v.findViewById(R.id.alinfo);
        alinfo.setAdapter(new AllergyListAdapter(activity, new ArrayList<AllergyModel>()));
        LinearLayoutManager alllm = new LinearLayoutManager(activity);
        alllm.setOrientation(LinearLayoutManager.VERTICAL);
        alinfo.setLayoutManager(alllm);
        final AllergyListAdapter aladapter = (AllergyListAdapter) alinfo.getAdapter();
        aladapter.data.clear();
        aladapter.data.addAll(Global.user.allergies);
        aladapter.notifyDataSetChanged();


        medinfo = (RecyclerView) v.findViewById(R.id.medinfo);
        medinfo.setAdapter(new MedicalListAdapter(activity, new ArrayList<MedicalModel>()));
        LinearLayoutManager medicalllm = new LinearLayoutManager(activity);
        medicalllm.setOrientation(LinearLayoutManager.VERTICAL);
        medinfo.setLayoutManager(medicalllm);
        final MedicalListAdapter medicaladapter = (MedicalListAdapter) medinfo.getAdapter();
        medicaladapter.data.clear();
        medicaladapter.data.addAll(Global.user.medinfo);
        medicaladapter.notifyDataSetChanged();


        med = (RecyclerView) v.findViewById(R.id.med);
        med.setAdapter(new MedListAdapter(activity, new ArrayList<MedModel>()));
        LinearLayoutManager medllm = new LinearLayoutManager(activity);
        medllm.setOrientation(LinearLayoutManager.VERTICAL);
        med.setLayoutManager(medllm);
        final MedListAdapter medadapter = (MedListAdapter) med.getAdapter();
        medadapter.data.clear();
        medadapter.data.addAll(Global.user.med);
        medadapter.notifyDataSetChanged();


        doctors = (RecyclerView) v.findViewById(R.id.doctors);
        doctors.setAdapter(new DoctorsListAdapter(activity, new ArrayList<DoctorModel>()));
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        doctors.setLayoutManager(llm);
        final DoctorsListAdapter dadapter = (DoctorsListAdapter) doctors.getAdapter();
        dadapter.data.clear();
        dadapter.data.addAll(Global.user.doctors);
        dadapter.notifyDataSetChanged();
        */

        /*
        taj.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.taj = editable.toString();
                Global.saveUser();
            }
        });

        taj.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    taj.testValidity();
                    Global.saveUser();
                }
            }
        });
        */
        /*
        tajpic = (ImageView) v.findViewById(R.id.tajpic);

        File tajfile = new File(Global.appContext.getFilesDir() + "/taj.png");
        if(tajfile.exists()) {
            Bitmap dectaj = BitmapFactory.decodeFile(tajfile.getAbsolutePath());
            tajpic.setImageBitmap(dectaj);
        }

        tajpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        if (r.getError() == null) {
                            //avatar.setImageURI(null);
                            //avatar.setImageURI(r.getUri());
                            tajpic.setImageBitmap(r.getBitmap());
                            FileOutputStream out = null;
                            try {
                                out = new FileOutputStream(Global.appContext.getFilesDir() + "/taj.png");
                                r.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
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
        */

        /*
        final Button addal = (Button) v.findViewById(R.id.addal);
        addal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.add_allergy, null);
                dialogBuilder.setView(dialogView);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);
                RecyclerView list = (RecyclerView) dialogView.findViewById(R.id.allist);
                String[] lists = getResources().getStringArray(R.array.allergies);
                ArrayList<AllergyModel> alls = new ArrayList<AllergyModel>();
                for(String l : lists) {
                    boolean ch = false;
                    AllergyModel al = new AllergyModel().setName(l);
                    for(AllergyModel a: Global.user.allergies) {
                        if(a.name.equals(l)) {
                            al.check(true);
                        }
                    }
                    alls.add(al);
                }

                list.setAdapter(new AllergySetupListAdapter(activity, alls));

                LinearLayoutManager listllm = new LinearLayoutManager(activity);
                listllm.setOrientation(LinearLayoutManager.VERTICAL);

                list.setLayoutManager(listllm);

                final AllergySetupListAdapter listadapter = (AllergySetupListAdapter) list.getAdapter();

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

                        aladapter.data.clear();
                        aladapter.data.addAll(Global.user.allergies);
                        aladapter.notifyDataSetChanged();

                        Global.saveUser();

                        if(Global.user.allergies.size() > 0) {
                            addal.setText(activity.getResources().getString(R.string.modify));
                        } else {
                            addal.setText(activity.getResources().getString(R.string.add));
                        }

                        alertDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    }
                });

                alertDialog.show();

            }
        });


        Button addmed = (Button) v.findViewById(R.id.addmed);
        Button addmedical = (Button) v.findViewById(R.id.addmedical);
        Button adddoc = (Button) v.findViewById(R.id.adddoctor);

        adddoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.add_doctor, null);
                dialogBuilder.setView(dialogView);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);
                final EditText phone = (EditText) dialogView.findViewById(R.id.phone);

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
                            Toast.makeText(activity, "A név nem lehet üres", Toast.LENGTH_LONG);
                            name.setFocusable(true);
                            return;
                        }

                        DoctorModel doki = new DoctorModel();
                        doki.name = name.getText().toString();
                        doki.phone = phone.getText().toString();
                        Global.user.doctors.add(doki);
                        dadapter.data.clear();
                        dadapter.data.addAll(Global.user.doctors);
                        dadapter.notifyDataSetChanged();
                        alertDialog.dismiss();

                        Global.saveUser();

                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                    }
                });

                alertDialog.show();

            }
        });

        addmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();

                View dialogView = inflater.inflate(R.layout.add_med, null);
                dialogBuilder.setView(dialogView);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);
                //final EditText phone = (EditText) dialogView.findViewById(R.id.phone);

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
                            Toast.makeText(activity, "A megnevezés nem lehet üres", Toast.LENGTH_LONG);
                            name.setFocusable(true);
                            return;
                        }

                        MedModel med = new MedModel();
                        med.name = name.getText().toString();
                        Global.user.med.add(med);

                        medadapter.data.clear();
                        medadapter.data.addAll(Global.user.med);
                        medadapter.notifyDataSetChanged();

                        Global.saveUser();

                        alertDialog.dismiss();
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    }
                });

                alertDialog.show();

            }
        });

        addmedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
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

                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    }
                });

                alertDialog.show();

            }
        });
        */



        return v;
    }

    Activity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        /*
        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }
        mCallbacks = (PageFragmentCallbacks) activity;
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*
        blood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.user.blood = position;
                Global.saveUser();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        bloodrh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Global.user.bloodrh = position;
                Global.saveUser();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        height.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.height = editable.toString();
                Global.saveUser();
            }
        });

        weight.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.weight = editable.toString();
                Global.saveUser();
            }
        });
        */
        /*
        allergy.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.allergy = editable.toString();
                Global.saveUser();
            }
        });
        */

        /*
        medinfo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.medinfo = editable.toString();
                Global.saveUser();
            }
        });

        med.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.med = editable.toString();
                Global.saveUser();
            }
        });

        doctors.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable editable) {
                Global.user.doctors = editable.toString();
                Global.saveUser();
            }
        });
        */

    }

    protected void setJSONType(String JSONType) {

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (blood != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    protected void setHint(String hint) {
        Log.d(getClass().getName(), hint);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(Global.user.medinfo.size() == 0) {
            medical.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.medinfo.size();
            for(MedicalModel m : Global.user.medinfo) {
                if(i==all) val += m.date + ": " + m.name;
                else val += m.date + ": " + m.name + ", ";
                i++;
            }
            medical.setValue(val);
        }

        if(Global.user.med.size() == 0) {
            meds.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.med.size();
            for(MedModel m : Global.user.med) {
                if(i==all) val += m.name;
                else val += m.name + ", ";
                i++;
            }
            meds.setValue(val);
        }


        if(Global.user.doctors.size() == 0) {
            doctors.setValue(" - ");
        } else {
            String val = "";
            int i = 1;
            int all = Global.user.doctors.size();
            for(DoctorModel m : Global.user.doctors) {
                if(i==all) val += m.name;
                else val += m.name + " | ";
                i++;
            }
            doctors.setValue(val);
        }

        if(Global.user.allergies.size() == 0 && Global.user.customallergies.size() == 0) {
            allergy.setValue(" - ");
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
            allergy.setValue(val);
        }

    }
}

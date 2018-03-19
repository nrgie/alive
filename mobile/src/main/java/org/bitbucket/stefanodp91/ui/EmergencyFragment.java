package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.ui.SettingsView;
import com.hbb20.CountryCodePicker;

import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.utils.Constants;
import org.bitbucket.stefanodp91.utils.CountryNum;
import org.bitbucket.stefanodp91.utils.Numbers;
import org.json.JSONException;
import org.json.JSONObject;


public class EmergencyFragment extends Fragment {
    private static final String TAG = EmergencyFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;


    protected SettingsView cc;
    protected SettingsView emnumber;
    protected SettingsView policenumber;
    protected SettingsView firenumber;
    protected SettingsView ambulancenumber;
    protected SettingsView tnumber;

    public static EmergencyFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        EmergencyFragment f = new EmergencyFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emergency_text, container, false);

        cc = (SettingsView) v.findViewById(R.id.ccountry);
        emnumber = (SettingsView) v.findViewById(R.id.emnumber);
        policenumber = (SettingsView) v.findViewById(R.id.police);
        firenumber = (SettingsView) v.findViewById(R.id.fire);
        ambulancenumber = (SettingsView) v.findViewById(R.id.amb);
        tnumber = (SettingsView) v.findViewById(R.id.terror);

        emnumber.setValue((Global.user.emnumber.equals("")) ? "-" : Global.user.emnumber);
        policenumber.setValue((Global.user.policenumber.equals("")) ? "-" : Global.user.policenumber);
        firenumber.setValue((Global.user.firenumber.equals("")) ? "-" : Global.user.firenumber);
        ambulancenumber.setValue((Global.user.ambulancenumber.equals("")) ? "-" : Global.user.ambulancenumber);

        tnumber.setValue((Global.user.terrornumber.equals("")) ? "-" : Global.user.terrornumber);

        if(!Global.user.emnumber.equals("")) {
            emnumber.setValue((Global.user.emnumber.equals("")) ? "-" : Global.user.emnumber);
            policenumber.setValue((Global.user.policenumber.equals("")) ? "-" : Global.user.policenumber);
            firenumber.setValue((Global.user.firenumber.equals("")) ? "-" : Global.user.firenumber);
            ambulancenumber.setValue((Global.user.ambulancenumber.equals("")) ? "-" : Global.user.ambulancenumber);
            tnumber.setValue((Global.user.terrornumber.equals("")) ? "-" : Global.user.terrornumber);
        } else {

            if (!Global.user.national.equals("")) {
                //ccp.setCountryForNameCode(Global.user.national);

                if (Global.user.national.equals("HU")) {

                    Global.user.emnumber = "112";
                    Global.user.policenumber = "107";
                    Global.user.firenumber = "105";
                    Global.user.ambulancenumber = "104";
                    Global.user.terrornumber = "112";

                    emnumber.setValue((Global.user.emnumber.equals("")) ? "-" : Global.user.emnumber);
                    policenumber.setValue((Global.user.policenumber.equals("")) ? "-" : Global.user.policenumber);
                    firenumber.setValue((Global.user.firenumber.equals("")) ? "-" : Global.user.firenumber);
                    ambulancenumber.setValue((Global.user.ambulancenumber.equals("")) ? "-" : Global.user.ambulancenumber);
                    tnumber.setValue((Global.user.terrornumber.equals("")) ? "-" : Global.user.terrornumber);

                    Global.saveUser();
                } else {

                    Global.user.emnumber = "112";
                    String code = cc.getPicker().getSelectedCountryNameCode();

                    Numbers n = new Numbers();

                    Global.user.emcountry = code;
                    Global.saveUser();

                    try {
                        CountryNum cc = n.get(code);

                        Global.user.emnumber = cc.M112 ? "112" : "-";
                        Global.user.policenumber = (cc.P.equals("")) ? cc.C : cc.P;
                        Global.user.firenumber = (cc.F.equals("")) ? cc.C : cc.F;
                        Global.user.ambulancenumber = (cc.A.equals("")) ? cc.C : cc.A;

                        Global.user.terrornumber = cc.M112 ? "112" : "-";

                        emnumber.setValue((Global.user.emnumber.equals("")) ? "-" : Global.user.emnumber);
                        policenumber.setValue((Global.user.policenumber.equals("")) ? "-" : Global.user.policenumber);
                        firenumber.setValue((Global.user.firenumber.equals("")) ? "-" : Global.user.firenumber);
                        ambulancenumber.setValue((Global.user.ambulancenumber.equals("")) ? "-" : Global.user.ambulancenumber);
                        tnumber.setValue((Global.user.terrornumber.equals("")) ? "-" : Global.user.terrornumber);

                        Global.saveUser();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {

                Global.user.emnumber = "112";
                String code = "";

                try {
                    TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                    String currentCountryISO = telephonyManager.getSimCountryIso();
                    code = currentCountryISO.toUpperCase();

                    Log.e(TAG, "DEFAULT COURTY CODE FROM SIM " + code);

                    if(!Global.user.national.equals("")) {
                        Global.user.national = code;
                    }

                } catch (Exception e) {
                    Log.w(TAG, "applyCustomProperty: could not set country from sim");
                }

                if(code.equals("")) {
                    code = "HU";
                }

                Numbers n = new Numbers();

                Global.user.emcountry = code;
                Global.saveUser();

                try {
                    CountryNum cc = n.get(code);

                    Global.user.emnumber = cc.M112 ? "112" : "-";
                    Global.user.policenumber = (cc.P.equals("")) ? cc.C : cc.P;
                    Global.user.firenumber = (cc.F.equals("")) ? cc.C : cc.F;
                    Global.user.ambulancenumber = (cc.A.equals("")) ? cc.C : cc.A;

                    Global.user.terrornumber = cc.M112 ? "112" : "-";

                    emnumber.setValue((Global.user.emnumber.equals("")) ? "-" : Global.user.emnumber);
                    policenumber.setValue((Global.user.policenumber.equals("")) ? "-" : Global.user.policenumber);
                    firenumber.setValue((Global.user.firenumber.equals("")) ? "-" : Global.user.firenumber);
                    ambulancenumber.setValue((Global.user.ambulancenumber.equals("")) ? "-" : Global.user.ambulancenumber);

                    tnumber.setValue((Global.user.terrornumber.equals("")) ? "-" : Global.user.terrornumber);

                    Global.saveUser();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return v;
    }

    public boolean test() {
        return (emnumber.testValidity());
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


    }

    protected void setJSONType(String JSONType) {}

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        //InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //if (!menuVisible) imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    protected void setHint(String hint) {}
}

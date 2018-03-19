package org.bitbucket.stefanodp91.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.ui.SettingsView;
import com.hbb20.CountryCodePicker;

import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.utils.Constants;
import org.bitbucket.stefanodp91.utils.Numbers;
import org.json.JSONException;
import org.json.JSONObject;


public class ContactFragment extends Fragment {
    private static final String TAG = ContactFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    protected FormEditText face;
    protected EditText whats;
    protected EditText skype;
    protected EditText snap;
    protected EditText viber;

    final static int REQUEST_READ_PHONE_STATE = 10112;

    public static ContactFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        ContactFragment f = new ContactFragment();
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

    public boolean test() {
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_text, container, false);
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

    }

    protected void setJSONType(String JSONType) {

    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (viber != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    protected void setHint(String hint) {

    }

}

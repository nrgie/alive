package org.bitbucket.stefanodp91.ui;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.utils.Constants;


/**
 * Created by stefano on 25/11/2015.
 */
public class PasswordFragment extends TextFragment {

    public static PasswordFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        PasswordFragment f = new PasswordFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setJSONType(Constants.JSON_TYPE_PASSWORD);
        super.setJSONKey(Constants.JSON_PASSWORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getEditTextInputWrapper().getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
        return view;
    }
}

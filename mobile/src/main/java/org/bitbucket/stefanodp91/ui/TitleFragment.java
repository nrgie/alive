package org.bitbucket.stefanodp91.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.utils.Constants;


/**
 * Created by stefano on 25/11/2015.
 */
public class TitleFragment extends TextFragment {

    public static TitleFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        TitleFragment f = new TitleFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setJSONType(Constants.JSON_TYPE_TITLE);
        super.setJSONKey(Constants.JSON_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

}

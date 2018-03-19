package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;

import org.bitbucket.stefanodp91.ui.PasswordFragment;


/**
 * Created by stefano on 25/11/2015.
 */
public class PasswordPage extends TextPage {
    public PasswordPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return PasswordFragment.create(getKey());
    }
}

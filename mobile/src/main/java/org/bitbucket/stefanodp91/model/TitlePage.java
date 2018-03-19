package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;

import org.bitbucket.stefanodp91.ui.TitleFragment;


/**
 * Created by stefano on 25/11/2015.
 */
public class TitlePage extends TextPage {
    public TitlePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return TitleFragment.create(getKey());
    }
}

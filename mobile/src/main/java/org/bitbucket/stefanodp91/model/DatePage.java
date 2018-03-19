package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;

import org.bitbucket.stefanodp91.ui.date.DateFragment;


/**
 * Created by stefano on 25/11/2015.
 */
public class DatePage extends TextPage {
    public DatePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return DateFragment.create(getKey());
    }
}

package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;

import org.bitbucket.stefanodp91.ui.NoteFragment;


/**
 * Created by stefano on 25/11/2015.
 */
public class NotePage extends TextPage {
    public NotePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return NoteFragment.create(getKey());
    }
}

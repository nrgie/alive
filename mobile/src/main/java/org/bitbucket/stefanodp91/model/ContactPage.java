package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.bitbucket.stefanodp91.ui.BasicFragment;
import org.bitbucket.stefanodp91.ui.ContactFragment;
import org.bitbucket.stefanodp91.ui.TitleFragment;

import java.util.ArrayList;

public class ContactPage extends Page {
    public ContactPage(ModelCallbacks callbacks) {
        super(callbacks);
    }

    ContactFragment f;

    @Override
    public Fragment createFragment() {
        return f = ContactFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY),
                getKey()));

    }

    @Override
    public boolean isCompleted() { return (f==null) ? false : f.test(); }

    public ContactPage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}

package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.bitbucket.stefanodp91.ui.HealthFragment;
import org.bitbucket.stefanodp91.ui.TitleFragment;

import java.util.ArrayList;


/**
 * Created by stefano on 25/11/2015.
 */
public class HealthPage extends Page {

    public HealthPage(ModelCallbacks callbacks) {
        super(callbacks);
    }

    HealthFragment f;

    @Override
    public Fragment createFragment() {
        return f = HealthFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
    }

    @Override
    public boolean isCompleted() { return (f==null) ? false : f.test(); }

    public HealthPage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}

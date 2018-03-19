package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;

import org.bitbucket.stefanodp91.ui.pickers.DaysPickerFragment;


/**
 * Created by stefano on 25/11/2015.
 */
public class DaysPickerPage extends TextPage {
    private static int NUMBER_OF_ITEMS; //= 18;

    public DaysPickerPage(ModelCallbacks callbacks, String title, int numberOfItems) {
        super(callbacks, title);
        NUMBER_OF_ITEMS = numberOfItems;
    }

    @Override
    public Fragment createFragment() {
        return DaysPickerFragment.create(getKey(), NUMBER_OF_ITEMS);
    }

}

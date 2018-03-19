package org.bitbucket.stefanodp91.ui.date;

import android.widget.DatePicker;
import android.widget.TextView;

/**
 * Created by stefano on 24/02/16.
 */
public interface OnDateSetListener {
    void onDateSet(TextView vOutput, DatePicker view, int year, int monthOfYear, int dayOfMonth);
}

package org.bitbucket.stefanodp91.ui.date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import org.bitbucket.stefanodp91.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by stefano on 24/02/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = DatePickerFragment.class.getSimpleName();

    private static final String MIN_DATE_PARAM = "MIN_DATE_PARAM";

    private OnDateSetListener delegate;
    private Calendar mCalendar;
    private TextView vOutput;
    private String minDate;

    // crea una nuova istanza del fragment passando la data minima da cui far partire il picker
    public static DatePickerFragment newInstance(String date) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(MIN_DATE_PARAM, date);
        datePickerFragment.setArguments(args);
        return datePickerFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // recupera la data minima se esiste
        Bundle bundle = getArguments();
        if (bundle != null) {
            minDate = bundle.getString(MIN_DATE_PARAM);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // usa la data corrente come data di default per il picker
        mCalendar = Calendar.getInstance();

        // crea una nuova istanza di DatePickerDialog e la restituisce
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this,
                mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

        if (minDate != null && minDate.length() > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Constants.DATE_LOCALE);

            try {
                mCalendar.setTime(simpleDateFormat.parse(minDate)); // assegna a mCalendar la data da cui partire
                mCalendar.add(Calendar.DATE, 1); // aggiunge un giorno alla data passata
                datePickerDialog = new DatePickerDialog(getActivity(), this,
                        mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(mCalendar.getTimeInMillis());

            } catch (ParseException e) {
                Log.e(TAG, "onCreateDialog(...) ==> failed with an exception: " + e.getMessage());
            }
        } else {
            datePickerDialog.getDatePicker().setMinDate(mCalendar.getTimeInMillis());
        }

        return datePickerDialog;
    }

    // assegna la view su cui visualizzare la data
    public void setOutputView(TextView vOutput) {
        this.vOutput = vOutput;
    }

    // assegna una callback per restituire la data sull'activity chiamante
    public void setOnDateSetListener(OnDateSetListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        String date = format(year, month, day);
        vOutput.setText(date);
        delegate.onDateSet(vOutput, view, year, month, day);
    }

    // costruisce una data a partire da giorno/mese/anno
    private String format(int year, int month, int day) {
        mCalendar.set(year, month, day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Constants.DATE_LOCALE);
        return simpleDateFormat.format(mCalendar.getTime());
    }
}
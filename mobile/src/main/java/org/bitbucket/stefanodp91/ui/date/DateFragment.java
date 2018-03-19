package org.bitbucket.stefanodp91.ui.date;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.ui.PageFragmentCallbacks;
import org.bitbucket.stefanodp91.utils.Constants;
import org.bitbucket.stefanodp91.utils.DateUtils;
import org.bitbucket.stefanodp91.utils.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateFragment extends Fragment implements View.OnClickListener, OnDateSetListener {
    private static final String TAG = DateFragment.class.getName();

    protected static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;


    private static View rootView;
    private TextView vDateFrom, vDateTo;


    private static final String DATE_FROM = "DATE_FROM";
    private static final String DATE_TO = "DATE_TO";
    private String dateFromStr, dateToStr;


    public static DateFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        DateFragment f = new DateFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        // ripristina le date precedentemente inserite
        if (savedInstanceState != null) {
            String dateFrom = savedInstanceState.getString(DATE_FROM);
            if (TextUtils.isValid(dateFrom)) {
                dateFromStr = dateFrom;
                Log.i(TAG, "onCreate() ==> dateFromStr == " + dateFromStr);
            } else {
                Log.e(TAG, "onCreate() ==> dateFromStr == is empty or null");
            }

            String dateTo = savedInstanceState.getString(DATE_TO);
            if (TextUtils.isValid(dateTo)) {
                dateToStr = dateTo;
                Log.i(TAG, "onCreate() ==> dateToStr == " + dateToStr);
            } else {
                Log.e(TAG, "onCreate() ==> dateToStr == is empty or null");
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_page_date, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(android.R.id.title)).setText(mPage.getTitle());

        vDateFrom = (TextView) view.findViewById(R.id.from);
        vDateFrom.setOnClickListener(this);

        vDateTo = (TextView) view.findViewById(R.id.to);
        vDateTo.setOnClickListener(this);

        // ripristina le date precedentemente inserite
        if (savedInstanceState != null) {
            if (TextUtils.isValid(dateFromStr)) {
                vDateFrom.setText(dateFromStr);

                Log.i(TAG, "onViewCreated() ==> dateFromStr == " + dateFromStr);
            } else {
                Log.e(TAG, "onViewCreated() ==> dateFromStr == is empty or null");
            }

            if (TextUtils.isValid(dateToStr)) {

                vDateTo.setText(dateToStr);
                Log.i(TAG, "onViewCreated() ==> dateToStr == " + dateToStr);
            } else {
                Log.e(TAG, "onViewCreated() ==> dateToStr == is empty or null");
            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException(
                    "Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();


        if (id == R.id.from) {
            if (vDateFrom != null) {
                showDatePickerDialogFrom();
            } else {
                Log.e(TAG, "onClick(...) ==> vDateFrom is null");
            }
        } else if (id == R.id.to) {
            if (vDateTo != null) {
                showDatePickerDialogTo();
            } else {
                Log.e(TAG, "onClick(...) ==> vDateTo is null");
            }
        } else {
            Log.e(TAG, "onClick(...) ==> default");
        }
    }

    public void showDatePickerDialogFrom() {
        DialogFragment dialogFragment = new DatePickerFragment();
        ((DatePickerFragment) dialogFragment).setOutputView(vDateFrom);
        ((DatePickerFragment) dialogFragment).setOnDateSetListener(this);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void showDatePickerDialogTo() {
        DialogFragment dialogFragment;

        // se esiste una data di inizio, la uso come data minima per la data di fine
        if (vDateFrom.getText() != null && vDateFrom.getText().length() > 0) {
            dialogFragment = DatePickerFragment.newInstance(String.valueOf(vDateFrom.getText()));
        } else {
            dialogFragment = new DatePickerFragment();
        }

        ((DatePickerFragment) dialogFragment).setOutputView(vDateTo);
        ((DatePickerFragment) dialogFragment).setOnDateSetListener(this);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(TextView vOutput, DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (vDateFrom.getText().length() > 0 && vDateTo.getText().length() > 0) {
            // data di inizio
            dateFromStr = String.valueOf(vDateFrom.getText());
            Date dateFrom = DateUtils.convertStringToDate(dateFromStr, Constants.DATE_FORMAT, Constants.DATE_LOCALE);

            // data di fine
            dateToStr = String.valueOf(vDateTo.getText());
            Date dateTo = DateUtils.convertStringToDate(dateToStr, Constants.DATE_FORMAT, Constants.DATE_LOCALE);

            // validazione delle date
            validationDateRange(dateFrom, dateTo);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (TextUtils.isValid(dateFromStr)) {
            outState.putString(DATE_FROM, dateFromStr);
        }

        if (TextUtils.isValid(dateToStr)) {
            outState.putString(DATE_TO, dateToStr);
        }
    }

    private void validationDateRange(Date dateFrom, Date dateTo) {
        // recupera la data corrente
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Constants.DATE_LOCALE);
        Date date = new Date();
        String todayStr = dateFormat.format(date);

        // converte la data corrente in un formato conforme alle date del datepicker
        Date today = DateUtils.convertStringToDate(todayStr, Constants.DATE_FORMAT, Constants.DATE_LOCALE);

        // controlla che le date siano state valorizzate
        if (dateFrom != null && dateTo != null) {
            boolean isValidFromDate = dateFrom.compareTo(today) >= 0;
            Log.i(TAG, "fromDate == " + String.valueOf(dateFrom) + ", today == " + String.valueOf(today));

            boolean isValidToDate = dateTo.compareTo(dateFrom) > 0;
            Log.i(TAG, "toDate == " + String.valueOf(dateTo) + ", fromDate == " + String.valueOf(dateFrom));

            // controlla che il range delle date immesse sia valido
            if (isValidFromDate && isValidToDate) {
                // la data è valida, quindi salvala
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.JSON_TYPE, Constants.JSON_TYPE_DATE);
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_TYPE failed with JSONException. " + e.getMessage());
                }

                try {
                    jsonObject.put(Constants.JSON_PAGE_TITLE, mPage.getTitle());
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_PAGE_TITLE failed with JSONException. " + e.getMessage());
                }

                try {
                    jsonObject.put(Constants.JSON_DATE_FROM,
                            DateUtils.convertDateToString(dateFrom, Constants.DATE_FORMAT, Constants.DATE_LOCALE));
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_LAT failed with JSONException. " + e.getMessage());
                }

                try {
                    jsonObject.put(Constants.JSON_DATE_TO,
                            DateUtils.convertDateToString(dateTo, Constants.DATE_FORMAT, Constants.DATE_LOCALE));
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_LNG failed with JSONException. " + e.getMessage());
                }

                mPage.getData().putString(Page.SIMPLE_DATA_KEY, jsonObject.toString());
            } else {
//                data non valida
                if (!isValidFromDate) {
                    Log.e(TAG, "validationDateRange(...) ==> isValidFromDate is false");
                }

                if (!isValidToDate) {
                    vDateTo.setText("");// azzera la data di fine
                    Log.e(TAG, "validationDateRange(...) ==> isValidToDate is false");
                }
            }
        } else {
            if (dateFrom == null) {
                Log.e(TAG, "validationDateRange(...) ==> fromDate is null");
            }

            if (dateTo == null) {
                vDateTo.setText("");// azzera la data di fine
                Log.e(TAG, "validationDateRange(...) ==> toDate is null");
            }
        }
    }
}
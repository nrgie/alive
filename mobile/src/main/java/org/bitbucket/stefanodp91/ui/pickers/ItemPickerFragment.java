package org.bitbucket.stefanodp91.ui.pickers;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.ui.PageFragmentCallbacks;
import org.bitbucket.stefanodp91.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;


abstract class ItemPickerFragment extends Fragment implements NumberPicker.OnValueChangeListener {
    private static final String TAG = ItemPickerFragment.class.getName();

    protected static final String ARG_KEY = "key";

    protected static final String SAVED_INSTANCE_LIST = "SAVED_INSTANCE_LIST";
    protected static final String SAVED_INSTANCE_POSITION = "SAVED_INSTANCE_POSITION";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    private static View rootView;

    private int MIN_VALUE = 0;
    private static String[] mList;// = new String[]{"item 1", "item 2", "item 3", "item 4", "item 5"};
    private int currentPosition;
    private TextView textView;
    private NumberPicker numberPicker;

    private String selectedItemLabel;

    private String JSONType = Constants.JSON_TYPE_ITEM_PICKER;
    private String JSONKey = Constants.JSON_ITEM_PICKER;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        if (savedInstanceState != null) {
            // restore the position
            currentPosition = savedInstanceState.getInt(SAVED_INSTANCE_POSITION);

            // restore the list
            String[] mSavedList = savedInstanceState.getStringArray(SAVED_INSTANCE_LIST);
            if (mSavedList != null && mSavedList.length > 0) {
                setList(mSavedList);
            }
        }

        setList(mList);
        setSelectedItemLabel(getString(R.string.label_current_item_picked));
    }

    protected void setList(String[] mList) {
        this.mList = mList;
    }

    protected String[] getList() {
        return mList;
    }

    protected void setSelectedItemLabel(String selectedItemLabel) {
        this.selectedItemLabel = selectedItemLabel;
    }

    protected String getSelectedItemLabel() {
        return selectedItemLabel;
    }

    protected void setJSONKey(String JSONKey) {
        this.JSONKey = JSONKey;
    }

    protected String getJSONKey() {
        return JSONKey;
    }

    protected void setJSONType(String JSONType) {
        this.JSONType = JSONType;
    }

    protected String getJSONType() {
        return JSONType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_page_item_picker, container, false);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (getList() != null && getList().length > 0) {
            outState.putStringArray(SAVED_INSTANCE_LIST, getList());
        }

        outState.putInt(SAVED_INSTANCE_POSITION, currentPosition);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(android.R.id.title)).setText(mPage.getTitle());

        //Get the widgets reference from XML layout
        textView = (TextView) view.findViewById(R.id.display_value);
        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);

        //Populate NumberPicker values from String array values
        //Set the minimum value of NumberPicker
        numberPicker.setMinValue(MIN_VALUE); //from array first value

        //Specify the maximum value/number of NumberPicker
        numberPicker.setMaxValue(getList().length - 1); //to array last value

        //Specify the NumberPicker data source as array elements
        numberPicker.setDisplayedValues(getList());

        //Gets whether the selector wheel wraps when reaching the min/max value.
        numberPicker.setWrapSelectorWheel(true);

        //Set a value change listener for NumberPicker
        numberPicker.setOnValueChangedListener(this);

        // restore the previous state
        if (savedInstanceState != null) {
            textView.setText(getSelectedItemLabel() + getList()[currentPosition]);
            Log.i(TAG, "onViewCreated() ==> currentItem == " + getList()[currentPosition]);

            numberPicker.setValue(currentPosition);
        }
    }

    private void saveResult(String currentValue) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constants.JSON_PAGE_TITLE, mPage.getTitle());
        } catch (JSONException e) {
            Log.e(TAG, "updateLocation() ==> JSON_PAGE_TITLE failed with JSONException. " + e.getMessage());
        }

        try {
            jsonObject.put(Constants.JSON_TYPE, getJSONType());
        } catch (JSONException e) {
            Log.e(TAG, "updateLocation() ==> JSON_TYPE failed with JSONException. " + e.getMessage());
        }

        try {
            jsonObject.put(getJSONKey(), currentValue);
        } catch (JSONException e) {
            Log.e(TAG, "updateLocation() ==> " + getJSONKey() + " failed with JSONException. " + e.getMessage());
        }

        mPage.getData().putString(Page.SIMPLE_DATA_KEY, jsonObject.toString());
        mPage.notifyDataChanged();
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
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        //Display the newly selected value from picker
        textView.setText(getSelectedItemLabel() + getList()[newVal]);
        currentPosition = newVal;
        saveResult(getList()[newVal]);
    }
}
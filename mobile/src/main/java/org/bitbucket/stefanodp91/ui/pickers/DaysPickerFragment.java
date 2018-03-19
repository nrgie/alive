package org.bitbucket.stefanodp91.ui.pickers;

import android.os.Bundle;
import android.widget.NumberPicker;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.utils.Constants;


public class DaysPickerFragment extends ItemPickerFragment implements NumberPicker.OnValueChangeListener {
    private static final String TAG = DaysPickerFragment.class.getName();

    protected static final String ARG_KEY = "key";
    protected static final String ARG_NUMBER_OF_ITEMS = "ARG_NUMBER_OF_ITEMS";

//    private static int NUMBER_OF_ITEMS = 18;

    public static DaysPickerFragment create(String key, int numberOfItems) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putInt(ARG_NUMBER_OF_ITEMS, numberOfItems);

        DaysPickerFragment f = new DaysPickerFragment();
        f.setArguments(args);
        return f;
    }

//    public static void setNumberOfItems(int numberOfItems) {
//        NUMBER_OF_ITEMS = numberOfItems;
//    }
//
//    public static int getNumberOfItems() {
//        return NUMBER_OF_ITEMS;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int numberOfItems = getArguments().getInt(ARG_NUMBER_OF_ITEMS);

        String[] mList = new String[numberOfItems];
        int displayNumber;

        for (int i = 0; i < mList.length; i++) {
            displayNumber = i + 1;
            mList[i] = displayNumber + getString(R.string.label_days);
        }

        setList(mList);
        setSelectedItemLabel(getString(R.string.label_current_days_picked));
        setJSONKey(Constants.JSON_DAYS_PICKER);
        setJSONType(Constants.JSON_TYPE_DAYS_PICKER);
    }
}
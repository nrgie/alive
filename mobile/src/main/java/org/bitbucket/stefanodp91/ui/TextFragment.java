package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;


public class TextFragment extends Fragment {
    private static final String TAG = TextFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    protected EditText mEditTextInput;
    private TextInputLayout mEditTextInputWrapper;

    private String JSONType = Constants.JSON_TYPE_TEXT;
    private String JSONKey = Constants.JSON_TEXT;

    public static TextFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        TextFragment f = new TextFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_text, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        mEditTextInput = (EditText) rootView.findViewById(R.id.editTextInput);
        mEditTextInputWrapper = (TextInputLayout) rootView.findViewById(R.id.editTextInputWrapper);
        mEditTextInput.setText(mPage.getData().getString(Page.SIMPLE_DATA_KEY));
        return rootView;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setInputType();
        mEditTextInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put(Constants.JSON_TYPE, getJSONType());
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_TYPE failed with JSONException. " + e.getMessage());
                }

                try {
                    jsonObject.put(Constants.JSON_PAGE_TITLE, mPage.getTitle());
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_PAGE_TITLE failed with JSONException. " + e.getMessage());
                }

                try {
                    jsonObject.put(getJSONKey(), (editable != null) ? editable.toString() : null);
                } catch (JSONException e) {
                    Log.e(TAG, "updateLocation() ==> JSON_TEXT failed with JSONException. " + e.getMessage());
                }

                mPage.getData().putString(Page.SIMPLE_DATA_KEY, jsonObject.toString());
                mPage.notifyDataChanged();
            }
        });
    }

    protected void setJSONType(String JSONType) {
        this.JSONType = JSONType;
    }

    protected String getJSONType() {
        return JSONType;
    }

    protected void setInputType() {
        mEditTextInput.setInputType(InputType.TYPE_CLASS_TEXT);
    }

    protected void setJSONKey(String JSONKey) {
        this.JSONKey = JSONKey;
    }

    public String getJSONKey() {
        return JSONKey;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override
        // setUserVisibleHint
        // instead of setMenuVisibility.
        if (mEditTextInput != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    public TextInputLayout getEditTextInputWrapper() {
        return mEditTextInputWrapper;
    }

    protected void setHint(String hint) {
        Log.d(getClass().getName(), hint);
        mEditTextInput.setHint(hint);
    }
}

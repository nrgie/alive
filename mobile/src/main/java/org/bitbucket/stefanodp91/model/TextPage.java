package org.bitbucket.stefanodp91.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.bitbucket.stefanodp91.ui.TextFragment;

import java.util.ArrayList;


public class TextPage extends Page {

	public TextPage(ModelCallbacks callbacks, String title) {
		super(callbacks, title);
	}

	public TextPage(ModelCallbacks callbacks) {
		super(callbacks);
	}

	@Override
	public Fragment createFragment() {
		return TextFragment.create(getKey());
	}

	@Override
	public void getReviewItems(ArrayList<ReviewItem> dest) {
		dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY),
				getKey()));

	}

	@Override
	public boolean isCompleted() {
		return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
	}

	public TextPage setValue(String value) {
		mData.putString(SIMPLE_DATA_KEY, value);
		return this;
	}
}

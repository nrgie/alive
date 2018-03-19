/*
 * Copyright 2012 Roman Nurik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitbucket.stefanodp91;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.fragments.HelpDialogFragment;

import org.bitbucket.stefanodp91.model.AbstractWizardModel;
import org.bitbucket.stefanodp91.model.ModelCallbacks;
import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.model.ReviewItem;
import org.bitbucket.stefanodp91.ui.PageFragmentCallbacks;
import org.bitbucket.stefanodp91.ui.ReviewFragment;
import org.bitbucket.stefanodp91.ui.StepPagerStrip;

import java.util.ArrayList;
import java.util.List;


public abstract class WizardActivity extends LocalizationActivity implements
        PageFragmentCallbacks, ReviewFragment.Callbacks, ModelCallbacks, View.OnClickListener {
    private static final String TAG = WizardActivity.class.getName();

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private AbstractWizardModel mWizardModel;

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    /**
     * Create the wizard.
     * The wizard is created from an AbstractWizardModel.
     *
     * @param mWizardModel the model.
     */
    protected void initWizard(AbstractWizardModel mWizardModel) {
        this.mWizardModel = mWizardModel;

        setContentView(R.layout.activity_main_pager);

        final int[] helps = {R.string.wizardpage1, R.string.wizardpage2, R.string.wizardpage3, R.string.wizardpage4, R.string.wizardpage5, R.string.wizardpage6 };

        if(mWizardModel != null)
            mWizardModel.registerListener(this);





        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1,
                        position);

                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        ImageView info = (ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new HelpDialogFragment(getResources().getString(helps[mPager.getCurrentItem()]));
                newFragment.show(getSupportFragmentManager(), getResources().getString(R.string.help));
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                int workpos = mPager.getCurrentItem()-1;
                if(workpos < 0) workpos = 0;

                //Log.e("COMP", "position : "+position+ " | workpos : " + workpos);


                if (position > 0 && !mCurrentPageSequence.get(workpos).isCompleted()) {
                    mPager.setCurrentItem(workpos);
                    mStepPagerStrip.setCurrentPage(workpos);
                    return;
                }

                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(this);
        mPrevButton.setOnClickListener(this);

        onPageTreeChanged();
        updateBottomBar();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == mNextButton.getId()) {
            nextButtonAction();
        } else if (id == mPrevButton.getId()) {
            prevButtonAction();
        } else {
            Log.e(TAG, "default action click");
        }
    }

    private void prevButtonAction() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }

    private void nextButtonAction() {
        if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
            setReviewButtonAction();
        } else {
            if (mEditingAfterReview) {
                mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
            } else {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
            }
        }
    }

    /**
     * Customize the action when review button is pressed.
     */
    protected void setReviewButtonAction() {

    }

    /**
     * Returns the user - submitted content of each page
     *
     * @return the content list
     */
    protected List<ReviewItem> getReviewItems() {
        ArrayList<ReviewItem> mList = new ArrayList<>();

        // get the content for each page
        for (Page page : mCurrentPageSequence) {
            page.getReviewItems(mList);
        }
        return mList;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            //mWizardModel.load(savedInstanceState.getBundle("model"));
            initWizard(mWizardModel);
        }
    }

    @Override
    public void onPageTreeChanged() {
        if(mWizardModel != null) {
            mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
            recalculateCutOffPage();
            mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 =
            // review
            // step
            mPagerAdapter.notifyDataSetChanged();
            updateBottomBar();
        }
    }

    private void updateBottomBar() {

        int position = mPager.getCurrentItem();

        if(mCurrentPageSequence == null || mWizardModel == null)
            return;


        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            //mNextButton.setBackgroundResource(R.drawable.footer_button_save_selector);
            //mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            //mNextButton.setTextAppearance(this, R.style.TextAppearanceNext);

            mNextButton.setText(mEditingAfterReview ? R.string.save : R.string.next);
            if (mNextButton.getText().toString().compareToIgnoreCase(getString(R.string.save)) == 0) {
                //mNextButton.setBackgroundResource(R.drawable.footer_button_save_selector);
                //mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
            } else {
                //mNextButton.setBackgroundResource(R.drawable.footer_button_next_selector);
            }
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }
        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }

    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override
    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }

    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }

    @Override
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }

    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }

        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            return Math.min(mCutOffPage + 1, mCurrentPageSequence == null ? 1
                    : mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}

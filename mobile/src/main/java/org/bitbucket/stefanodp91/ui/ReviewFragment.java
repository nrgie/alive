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

package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.model.AbstractWizardModel;
import org.bitbucket.stefanodp91.model.ModelCallbacks;
import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.model.ReviewItem;
import org.bitbucket.stefanodp91.utils.Constants;
import org.bitbucket.stefanodp91.utils.ImageUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ReviewFragment extends Fragment implements ModelCallbacks {

    private static final String TAG = ReviewFragment.class.getName();

    private Callbacks mCallbacks;
    private AbstractWizardModel mWizardModel;

    public View v;

    public ReviewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_signal_setup, container, false);

        CheckBox soscall = (CheckBox) v.findViewById(R.id.soscall);
        CheckBox sossms = (CheckBox) v.findViewById(R.id.sossms);
        CheckBox gsoscall = (CheckBox) v.findViewById(R.id.sosguardcall);
        CheckBox gsossms = (CheckBox) v.findViewById(R.id.sosguardsms);
        CheckBox gsosemail = (CheckBox) v.findViewById(R.id.sosguardemail);

        CheckBox policecall = (CheckBox) v.findViewById(R.id.policecall);
        CheckBox policesms = (CheckBox) v.findViewById(R.id.policesms);
        CheckBox gpolicecall = (CheckBox) v.findViewById(R.id.policeguardcall);
        CheckBox gpolicesms = (CheckBox) v.findViewById(R.id.policeguardsms);
        CheckBox gpoliceemail = (CheckBox) v.findViewById(R.id.policeguardemail);

        CheckBox firecall = (CheckBox) v.findViewById(R.id.firecall);
        CheckBox firesms = (CheckBox) v.findViewById(R.id.firesms);
        CheckBox gfirecall = (CheckBox) v.findViewById(R.id.fireguardcall);
        CheckBox gfiresms = (CheckBox) v.findViewById(R.id.fireguardsms);
        CheckBox gfireemail = (CheckBox) v.findViewById(R.id.fireguardemail);

        CheckBox ambcall = (CheckBox) v.findViewById(R.id.ambcall);
        CheckBox ambsms = (CheckBox) v.findViewById(R.id.ambsms);
        CheckBox gambcall = (CheckBox) v.findViewById(R.id.ambguardcall);
        CheckBox gambsms = (CheckBox) v.findViewById(R.id.ambguardsms);
        CheckBox gambemail = (CheckBox) v.findViewById(R.id.ambguardemail);

        CheckBox tacall = (CheckBox) v.findViewById(R.id.tacall);
        CheckBox tasms = (CheckBox) v.findViewById(R.id.tasms);
        CheckBox gtacall = (CheckBox) v.findViewById(R.id.taguardcall);
        CheckBox gtasms = (CheckBox) v.findViewById(R.id.taguardsms);
        CheckBox gtaemail = (CheckBox) v.findViewById(R.id.taguardemail);

        soscall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.soscall = isChecked; Global.saveUser();}
        });
        sossms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.sossms = isChecked; Global.saveUser();}
        });
        gsoscall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gsoscall = isChecked; Global.saveUser();}
        });
        gsossms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gsossms = isChecked; Global.saveUser();}
        });
        gsosemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gsosemail = isChecked; Global.saveUser();}
        });

        policecall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.policecall = isChecked; Global.saveUser();}
        });
        policesms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.policesms = isChecked; Global.saveUser();}
        });
        gpolicecall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gpolicecall = isChecked; Global.saveUser();}
        });
        gpolicesms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gpolicesms = isChecked; Global.saveUser();}
        });
        gpoliceemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gpoliceemail = isChecked; Global.saveUser();}
        });

        firecall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.firecall = isChecked; Global.saveUser();}
        });
        firesms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.firesms = isChecked; Global.saveUser();}
        });
        gfirecall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gfirecall = isChecked; Global.saveUser();}
        });
        gfiresms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gfiresms = isChecked; Global.saveUser();}
        });
        gfireemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gfireemail = isChecked; Global.saveUser();}
        });

        ambcall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.ambcall = isChecked; Global.saveUser();}
        });
        ambsms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.ambsms = isChecked; Global.saveUser();}
        });
        gambcall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gambcall = isChecked; Global.saveUser();}
        });
        gambsms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gambsms = isChecked; Global.saveUser();}
        });
        gambemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gambemail = isChecked; Global.saveUser();}
        });

        tacall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.tacall = isChecked; Global.saveUser();}
        });
        tasms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.tasms = isChecked; Global.saveUser();}
        });
        gtacall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gtacall = isChecked; Global.saveUser();}
        });
        gtasms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gtasms = isChecked; Global.saveUser();}
        });
        gtaemail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { Global.user.gtaemail = isChecked; Global.saveUser();}
        });

        soscall.setChecked(Global.user.soscall);
        sossms.setChecked(Global.user.sossms);
        gsoscall.setChecked(Global.user.gsoscall);
        gsossms.setChecked(Global.user.gsossms);
        gsosemail.setChecked(Global.user.gsosemail);

        policecall.setChecked(Global.user.policecall);
        policesms.setChecked(Global.user.policesms);
        gpolicecall.setChecked(Global.user.gpolicecall);
        gpolicesms.setChecked(Global.user.gpolicesms);
        gpoliceemail.setChecked(Global.user.gpoliceemail);

        firecall.setChecked(Global.user.firecall);
        firesms.setChecked(Global.user.firesms);
        gfirecall.setChecked(Global.user.gfirecall);
        gfiresms.setChecked(Global.user.gfiresms);
        gfireemail.setChecked(Global.user.gfireemail);

        ambcall.setChecked(Global.user.ambcall);
        ambsms.setChecked(Global.user.ambsms);
        gambcall.setChecked(Global.user.gambcall);
        gambsms.setChecked(Global.user.gambsms);
        gambemail.setChecked(Global.user.gambemail);

        tacall.setChecked(Global.user.tacall);
        tasms.setChecked(Global.user.tasms);
        gtacall.setChecked(Global.user.gtacall);
        gtasms.setChecked(Global.user.gtasms);
        gtaemail.setChecked(Global.user.gtaemail);

        if(Global.user.guards.size() == 0) {

            /*
            gsoscall.setChecked(false);
            gsossms.setChecked(false);
            gsosemail.setChecked(false);
            gpolicecall.setChecked(false);
            gpolicesms.setChecked(false);
            gpoliceemail.setChecked(false);
            gfirecall.setChecked(false);
            gfiresms.setChecked(false);
            gfireemail.setChecked(false);
            gambcall.setChecked(false);
            gambsms.setChecked(false);
            gambemail.setChecked(false);
            gtacall.setChecked(false);
            gtasms.setChecked(false);
            gtaemail.setChecked(false);

*/

            gsoscall.setEnabled(false);
            gsossms.setEnabled(false);
            gsosemail.setEnabled(false);
            gpolicecall.setEnabled(false);
            gpolicesms.setEnabled(false);
            gpoliceemail.setEnabled(false);
            gfirecall.setEnabled(false);
            gfiresms.setEnabled(false);
            gfireemail.setEnabled(false);
            gambcall.setEnabled(false);
            gambsms.setEnabled(false);
            gambemail.setEnabled(false);
            gtacall.setEnabled(false);
            gtasms.setEnabled(false);
            gtaemail.setEnabled(false);

        } else {

            gsoscall.setEnabled(true);
            gsossms.setEnabled(true);
            gsosemail.setEnabled(true);
            gpolicecall.setEnabled(true);
            gpolicesms.setEnabled(true);
            gpoliceemail.setEnabled(true);
            gfirecall.setEnabled(true);
            gfiresms.setEnabled(true);
            gfireemail.setEnabled(true);
            gambcall.setEnabled(true);
            gambsms.setEnabled(true);
            gambemail.setEnabled(true);
            gtacall.setEnabled(true);
            gtasms.setEnabled(true);
            gtaemail.setEnabled(true);

        }


        return v;
    }

    Activity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        /*
        if (!(activity instanceof Callbacks)) {
            throw new ClassCastException("Activity must implement fragment's callbacks");
        }

        mCallbacks = (Callbacks) activity;

        mWizardModel = mCallbacks.onGetModel();
        mWizardModel.registerListener(this);
        */

        onPageTreeChanged();
    }

    @Override
    public void onPageTreeChanged() {
        onPageDataChanged(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        CheckBox soscall = (CheckBox) v.findViewById(R.id.soscall);
        CheckBox sossms = (CheckBox) v.findViewById(R.id.sossms);
        CheckBox gsoscall = (CheckBox) v.findViewById(R.id.sosguardcall);
        CheckBox gsossms = (CheckBox) v.findViewById(R.id.sosguardsms);
        CheckBox gsosemail = (CheckBox) v.findViewById(R.id.sosguardemail);

        CheckBox policecall = (CheckBox) v.findViewById(R.id.policecall);
        CheckBox policesms = (CheckBox) v.findViewById(R.id.policesms);
        CheckBox gpolicecall = (CheckBox) v.findViewById(R.id.policeguardcall);
        CheckBox gpolicesms = (CheckBox) v.findViewById(R.id.policeguardsms);
        CheckBox gpoliceemail = (CheckBox) v.findViewById(R.id.policeguardemail);

        CheckBox firecall = (CheckBox) v.findViewById(R.id.firecall);
        CheckBox firesms = (CheckBox) v.findViewById(R.id.firesms);
        CheckBox gfirecall = (CheckBox) v.findViewById(R.id.fireguardcall);
        CheckBox gfiresms = (CheckBox) v.findViewById(R.id.fireguardsms);
        CheckBox gfireemail = (CheckBox) v.findViewById(R.id.fireguardemail);

        CheckBox ambcall = (CheckBox) v.findViewById(R.id.ambcall);
        CheckBox ambsms = (CheckBox) v.findViewById(R.id.ambsms);
        CheckBox gambcall = (CheckBox) v.findViewById(R.id.ambguardcall);
        CheckBox gambsms = (CheckBox) v.findViewById(R.id.ambguardsms);
        CheckBox gambemail = (CheckBox) v.findViewById(R.id.ambguardemail);

        CheckBox tacall = (CheckBox) v.findViewById(R.id.tacall);
        CheckBox tasms = (CheckBox) v.findViewById(R.id.tasms);
        CheckBox gtacall = (CheckBox) v.findViewById(R.id.taguardcall);
        CheckBox gtasms = (CheckBox) v.findViewById(R.id.taguardsms);
        CheckBox gtaemail = (CheckBox) v.findViewById(R.id.taguardemail);

        if(Global.user.guards.size() == 0) {

            /*
            gsoscall.setChecked(false);
            gsossms.setChecked(false);
            gsosemail.setChecked(false);
            gpolicecall.setChecked(false);
            gpolicesms.setChecked(false);
            gpoliceemail.setChecked(false);
            gfirecall.setChecked(false);
            gfiresms.setChecked(false);
            gfireemail.setChecked(false);
            gambcall.setChecked(false);
            gambsms.setChecked(false);
            gambemail.setChecked(false);
            gtacall.setChecked(false);
            gtasms.setChecked(false);
            gtaemail.setChecked(false);
            */

            gsoscall.setEnabled(false);
            gsossms.setEnabled(false);
            gsosemail.setEnabled(false);
            gpolicecall.setEnabled(false);
            gpolicesms.setEnabled(false);
            gpoliceemail.setEnabled(false);
            gfirecall.setEnabled(false);
            gfiresms.setEnabled(false);
            gfireemail.setEnabled(false);
            gambcall.setEnabled(false);
            gambsms.setEnabled(false);
            gambemail.setEnabled(false);
            gtacall.setEnabled(false);
            gtasms.setEnabled(false);
            gtaemail.setEnabled(false);

        } else {

            gsoscall.setEnabled(true);
            gsossms.setEnabled(true);
            gsosemail.setEnabled(true);
            gpolicecall.setEnabled(true);
            gpolicesms.setEnabled(true);
            gpoliceemail.setEnabled(true);
            gfirecall.setEnabled(true);
            gfiresms.setEnabled(true);
            gfireemail.setEnabled(true);
            gambcall.setEnabled(true);
            gambsms.setEnabled(true);
            gambemail.setEnabled(true);
            gtacall.setEnabled(true);
            gtasms.setEnabled(true);
            gtaemail.setEnabled(true);

        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        //mWizardModel.unregisterListener(this);
    }

    @Override
    public void onPageDataChanged(Page changedPage) {
        ArrayList<ReviewItem> reviewItems = new ArrayList<ReviewItem>();
        /*
        for (Page page : mWizardModel.getCurrentPageSequence()) {
            page.getReviewItems(reviewItems);
        }
        Collections.sort(reviewItems, new Comparator<ReviewItem>() {
            @Override
            public int compare(ReviewItem a, ReviewItem b) {
                return a.getWeight() > b.getWeight() ? +1 : a.getWeight() < b.getWeight() ? -1 : 0;
            }
        });
        */
    }

    public interface Callbacks {
        AbstractWizardModel onGetModel();
        void onEditScreenAfterReview(String pageKey);
    }

    public AbstractWizardModel getWizardModel() {
        return mWizardModel;
    }

    public void setWizardModel(AbstractWizardModel mWizardModel) {
        this.mWizardModel = mWizardModel;
    }

    public Callbacks getCallbacks() {
        return mCallbacks;
    }

    public void setCallbacks(Callbacks mCallbacks) {
        this.mCallbacks = mCallbacks;
    }
}

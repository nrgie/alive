package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.MedicalModel;

import java.util.ArrayList;

public class AllergySetupListAdapter extends RecyclerView.Adapter<AllergySetupListAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<AllergyModel> data;

    public String name = "";

    public AllergySetupListAdapter(Context context, ArrayList<AllergyModel> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.allergy_setuplist_item, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder v, int i) {

        final AllergyModel item = data.get(i);

        /*
        if(Global.user.national.toLowerCase().equals("hu"))
            v.name.setText(item.hu);

        else if(Global.user.national.toLowerCase().equals("en"))
            v.name.setText(item.en);

        else if(Global.user.national.toLowerCase().equals("de"))
            v.name.setText(item.de);

        else
            v.name.setText(item.en);
           */

        v.name.setText(item.name);

        v.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                item.checked = isChecked;
                Global.user.allergies.clear();

                for(AllergyModel a: data) {
                    if(a.checked) {
                        Global.user.allergies.add(a);
                    }
                }

            }
        });

        if(item.checked) {
            v.sw.setChecked(true);
        } else {
            v.sw.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public android.support.v7.widget.SwitchCompat sw;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.sw = (android.support.v7.widget.SwitchCompat) view.findViewById(R.id.sw);
        }

    }



}

package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.AllergyModel;
import com.blueobject.app.alive.helper.LangModel;

import java.util.ArrayList;

public class LangSetupListAdapter extends RecyclerView.Adapter<LangSetupListAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<LangModel> data;

    public String name = "";

    public LangSetupListAdapter(Context context, ArrayList<LangModel> list) {
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

        final LangModel item = data.get(i);
        v.name.setText(item.name);
        v.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                item.checked = isChecked;
                Global.user.langs.clear();
                for(LangModel a: data) {
                    if(a.checked) {
                        Global.user.langs.add(a);
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

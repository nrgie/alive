package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.MedicalModel;

import java.util.ArrayList;

public class MedicalListViewAdapter extends RecyclerView.Adapter<MedicalListViewAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<MedicalModel> data;

    public String name = "";

    public MedicalListViewAdapter(Context context, ArrayList<MedicalModel> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.medical_list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder v, int i) {

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:

                        ArrayList<MedicalModel> glist = (ArrayList<MedicalModel>) Global.user.medinfo.clone();
                        for(MedicalModel g : glist) {
                            if(g.name.equals(name)) {
                                Global.user.medinfo.remove(g);
                            }
                        }

                        Global.saveUser();

                        data.clear();
                        data.addAll(Global.user.medinfo);
                        notifyDataSetChanged();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        v.del.setVisibility(View.GONE);

        final MedicalModel item = data.get(i);
        v.name.setText(item.name);
        v.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = item.name;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.confirm_delete_item))
                        .setPositiveButton(context.getResources().getString(R.string.OK), dialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.cancel), dialogClickListener);

                AlertDialog alertDialog = builder.show();

                int textColorId = context.getResources().getIdentifier("alertMessage", "id", "android");
                TextView textColor = (TextView) alertDialog.findViewById(textColorId);
                if (textColor != null) { textColor.setTextColor(Color.BLACK); }

            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageButton del;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.del = (ImageButton) view.findViewById(R.id.delete);
        }
    }



}

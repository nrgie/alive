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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.DoctorModel;

import java.util.ArrayList;

public class DoctorsListViewAdapter extends RecyclerView.Adapter<DoctorsListViewAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<DoctorModel> data;

    public String name = "";

    public DoctorsListViewAdapter(Context context, ArrayList<DoctorModel> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.doctor_list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder v, int i) {

        final DialogInterface.OnClickListener DeldialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:

                        ArrayList<DoctorModel> glist = (ArrayList<DoctorModel>) Global.user.doctors.clone();
                        for(DoctorModel g : glist) {
                            if(g.name.equals(name)) {
                                Global.user.doctors.remove(g);
                            }
                        }

                        Global.saveUser();

                        data.clear();
                        data.addAll(Global.user.doctors);
                        notifyDataSetChanged();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        final DialogInterface.OnClickListener CalldialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:

                        ArrayList<DoctorModel> glist = (ArrayList<DoctorModel>) Global.user.doctors.clone();
                        for(DoctorModel g : glist) {
                            if(g.name.equals(name)) {
                                Global.simpleCALL(g.phone);
                            }
                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };


        v.del.setVisibility(View.GONE);

        final DoctorModel item = data.get(i);
        v.name.setText(item.name);

        if(item.phone.equals(""))
            v.phone.setText(" - ");
        else
            v.phone.setText(item.phone);

        if(item.email.equals(""))
            v.email.setText(item.email);
        else
            v.email.setText(item.email);


        v.phone.setTypeface(Global.textfont);
        v.email.setTypeface(Global.textfont);


        v.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = item.name;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.confirm_call_doctor))
                        .setPositiveButton(context.getResources().getString(R.string.OK), CalldialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.cancel), CalldialogClickListener);

                AlertDialog alertDialog = builder.show();

                int textColorId = context.getResources().getIdentifier("alertMessage", "id", "android");
                TextView textColor = (TextView) alertDialog.findViewById(textColorId);
                if (textColor != null) { textColor.setTextColor(Color.BLACK); }

            }
        });

        v.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = item.name;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.confirm_delete_item))
                        .setPositiveButton(context.getResources().getString(R.string.OK), DeldialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.cancel), DeldialogClickListener);

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
        public ImageButton call;
        public ImageButton del;
        public TextView phone;
        public TextView email;

        public CustomViewHolder(View view) {
            super(view);

            this.email = (TextView) view.findViewById(R.id.email);
            this.phone = (TextView) view.findViewById(R.id.phone);

            this.name = (TextView) view.findViewById(R.id.name);
            this.call = (ImageButton) view.findViewById(R.id.callGuard);
            this.del = (ImageButton) view.findViewById(R.id.delete);
        }
    }



}

package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MediEditActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.DoctorModel;
import com.blueobject.app.alive.helper.MedicalModel;

import java.util.ArrayList;

public class MedicalListAdapter extends RecyclerView.Adapter<MedicalListAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<MedicalModel> data;

    public String name = "";

    public MedicalListAdapter(Context context, ArrayList<MedicalModel> list) {
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
    public void onBindViewHolder(final CustomViewHolder v, final int j) {

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

        final MedicalModel item = data.get(j);
        v.name.setText(item.name);
        v.date.setText(item.date);
        v.name.setTypeface(Global.textfont);
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

        v.date.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {

                                          Intent i = new Intent(context, MediEditActivity.class);
                                          Bundle b = new Bundle();
                                          b.putString("name", item.name);
                                          b.putString("date", item.date);
                                          b.putInt("id", j);
                                          i.putExtras(b);
                                          context.startActivity(i);
                                    }
        });

        v.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, MediEditActivity.class);
                Bundle b = new Bundle();
                b.putString("name", item.name);
                b.putString("date", item.date);
                b.putInt("id", j);
                i.putExtras(b);
                context.startActivity(i);

                /*
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View dialogView = inflater.inflate(R.layout.add_medical, null);
                dialogBuilder.setView(dialogView);

                final EditText name = (EditText) dialogView.findViewById(R.id.name);
                final EditText date = (EditText) dialogView.findViewById(R.id.date);

                name.setText(item.name);
                date.setText(item.date);

                Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                Button save = (Button) dialogView.findViewById(R.id.save);

                final AlertDialog alertDialog = dialogBuilder.create();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(name.getText().toString().equals("")) {
                            Toast.makeText(context, "A leírás nem lehet üres", Toast.LENGTH_LONG);
                            name.setFocusable(true);
                            return;
                        }


                        item.name = name.getText().toString();
                        item.date = date.getText().toString();

                        data.clear();
                        data.addAll(Global.user.medinfo);
                        notifyDataSetChanged();


                        alertDialog.dismiss();
                        Global.saveUser();

                    }});

                alertDialog.show();
                */


            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView date;
        public ImageButton del;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.date = (TextView) view.findViewById(R.id.date);
            this.del = (ImageButton) view.findViewById(R.id.delete);
        }
    }



}

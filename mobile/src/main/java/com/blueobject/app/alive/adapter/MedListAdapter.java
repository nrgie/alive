package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MediEditActivity;
import com.blueobject.app.alive.MedicineEditActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.MedModel;

import java.io.File;
import java.util.ArrayList;

public class MedListAdapter extends RecyclerView.Adapter<MedListAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<MedModel> data;

    public String name = "";
    LayoutInflater inflater;

    public MedListAdapter(Context context, ArrayList<MedModel> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.med_list_item, null);
        this.inflater = LayoutInflater.from(viewGroup.getContext());
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

                        ArrayList<MedModel> glist = (ArrayList<MedModel>) Global.user.med.clone();
                        for(MedModel g : glist) {
                            if(g.name.equals(name)) {
                                Global.user.med.remove(g);
                            }
                        }

                        Global.saveUser();

                        data.clear();
                        data.addAll(Global.user.med);
                        notifyDataSetChanged();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        final MedModel item = data.get(j);

        if(!item.qty.equals("")) {
            v.name.setText(item.name + " (" + item.qty + ")");
        } else {
            v.name.setText(item.name);
        }


        if(!item.pic.equals("")) {
            File avfile = new File(Global.appContext.getFilesDir() + item.pic);
            if (avfile.exists()) {
                Bitmap decav = BitmapFactory.decodeFile(avfile.getAbsolutePath());
                v.pic.setImageBitmap(decav);
            }
        } else {
            v.pic.setVisibility(View.GONE);
        }

        v.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(item.pic.equals("")) return;

                AlertDialog.Builder photoBuilder = new AlertDialog.Builder(context, R.style.fs_dialog);
                View photoView = inflater.inflate(R.layout.dialog_photoview, null);
                photoBuilder.setView(photoView);
                final AlertDialog photoDialog = photoBuilder.create();
                photoDialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                ImageView photo = (ImageView) photoView.findViewById(R.id.photo);
                photo.setImageDrawable(v.pic.getDrawable());
                ImageView cancel = (ImageView) photoView.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {photoDialog.dismiss();
                    }
                });
                photoDialog.show();
            }
        });


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

        v.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MedicineEditActivity.class);
                Bundle b = new Bundle();
                b.putString("name", item.name);
                b.putString("qty", item.qty);
                b.putInt("id", j);
                i.putExtras(b);
                context.startActivity(i);
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
        public ImageView pic;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.del = (ImageButton) view.findViewById(R.id.delete);
            this.pic = (ImageView) view.findViewById(R.id.icon);
        }
    }



}

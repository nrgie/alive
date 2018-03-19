package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.GuardEditActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.UserModel;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

public class GuardListAdapter extends RecyclerView.Adapter<GuardListAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<UserModel> data;

    public int delid = -1;

    public GuardListAdapter(Context context, ArrayList<UserModel> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guard_list_item_new, null);
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

                        ArrayList<UserModel> glist = (ArrayList<UserModel>) Global.user.guards.clone();
                        for(UserModel g : glist) {
                            if(g.id == delid) {
                                Global.user.guards.remove(g);

                                // remove from another phone
                                new Global.SendRemove(g.email).execute();
                            }
                        }

                        delid = -1;

                        data.clear();
                        data.addAll(Global.user.guards);
                        notifyDataSetChanged();
                        Global.saveUser();

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        final UserModel item = data.get(i);


        v.name.setText(item.name);
        v.key.setText("Őrangyal "+ (i+1));
        v.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.guard_icon_red));

        if(item.national.equals(""))
            if(Global.user.national.equals(""))
                v.flag.setCountryForNameCode("hu");
            else
                v.flag.setCountryForNameCode(Global.user.national);
        else
            v.flag.setCountryForNameCode(item.national);

        v.flag.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                //ArrayList<UserModel> glist = (ArrayList<UserModel>) Global.user.guards.clone();
                for(UserModel g : Global.user.guards) {
                    if(g.email.equals(item.email)) {
                        g.national = v.flag.getSelectedCountryNameCode();
                    }
                }
                Global.saveUser();
            }
        });

        v.guardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialogIntent = new Intent(context, GuardEditActivity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                dialogIntent.putExtra("new", false);
                dialogIntent.putExtra("guard", Global.gson.toJson(item));
                context.startActivity(dialogIntent);
            }
        });


        v.invited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(item.accepted) {
                    Toast.makeText(context, "Az elfogadta a felkérést", Toast.LENGTH_LONG).show();
                } else if(item.rejected) {
                    Toast.makeText(context, "Az őrangyal visszautasította a felkérést", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Felkérés függőben", Toast.LENGTH_LONG).show();
                }

            }
        });

        if(item.accepted) {
            v.invited.setVisibility(View.VISIBLE);
            v.invited.setBackground(context.getDrawable(R.drawable.check_mark_ok_good_128));
        } else {
            v.invited.setVisibility(View.VISIBLE);
            v.invited.setBackground(context.getDrawable(R.drawable.question_mark_ask_more_128));
        }

        Log.e("ITEM", "accepted:"+item.accepted + " rejected:"+item.rejected);


        if(item.rejected) {
            //v.invited.setText(context.getResources().getString(R.string.rejected));
            v.invited.setBackground(context.getDrawable(R.drawable.denied_cross_stop_delete_128));
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView key;
        public ImageView icon;
        public CountryCodePicker flag;
        public TextView invited;
        public ImageButton delete;
        public RelativeLayout guardLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.flag = (CountryCodePicker) view.findViewById(R.id.flag);
            this.invited = (TextView) view.findViewById(R.id.invited);
            this.key = (TextView) view.findViewById(R.id.key);
            this.icon = (ImageView) view.findViewById(R.id.gicon);
            this.guardLayout = (RelativeLayout) view.findViewById(R.id.guardLayout);
        }
    }



}

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
import com.blueobject.app.alive.helper.UserModel;

import java.util.ArrayList;

public class GuardListSimpleAdapter extends RecyclerView.Adapter<GuardListSimpleAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<UserModel> data;

    public int delid = -1;

    public GuardListSimpleAdapter(Context context, ArrayList<UserModel> list) {
        this.data = list;
        this.context = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.guard_list_simple_item, null);
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
                               Global.simpleCALL(g.phone);
                            }
                        }

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        final UserModel item = data.get(i);
        v.name.setText(item.name);
        v.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delid = item.id;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getResources().getString(R.string.confirm_call_guardian))
                        .setPositiveButton(context.getResources().getString(R.string.OK), dialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.cancel), dialogClickListener);

                AlertDialog alertDialog = builder.show();

                int textColorId = context.getResources().getIdentifier("alertMessage", "id", "android");
                TextView textColor = (TextView) alertDialog.findViewById(textColorId);
                if (textColor != null) { textColor.setTextColor(Color.BLACK); }
            }
        });

        //if(item.accepted)
            //v.invited.setVisibility(View.GONE);
        //else

        v.invited.setVisibility(View.VISIBLE);

        if(item.accepted) {
            v.invited.setBackground(context.getDrawable(R.drawable.check_mark_ok_good_128));
        } else {
            v.invited.setBackground(context.getDrawable(R.drawable.question_mark_ask_more_128));
        }

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
        public TextView invited;
        public ImageButton call;

        public CustomViewHolder(View view) {
            super(view);
            this.name = (TextView) view.findViewById(R.id.name);
            this.invited = (TextView) view.findViewById(R.id.invited);
            this.call = (ImageButton) view.findViewById(R.id.callGuard);
        }
    }



}

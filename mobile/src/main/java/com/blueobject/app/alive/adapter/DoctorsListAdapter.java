package com.blueobject.app.alive.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blueobject.app.alive.DoctorsEditActivity;
import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.MedicineEditActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.helper.DoctorModel;

import java.util.ArrayList;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.CustomViewHolder> {

    private Context context;
    public ArrayList<DoctorModel> data;

    public String name = "";

    public boolean disabled = false;

    public DoctorsListAdapter(Context context, ArrayList<DoctorModel> list) {
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
    public void onBindViewHolder(final CustomViewHolder v, final int j) {

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


        final DoctorModel item = data.get(j);
        v.name.setText(item.name);
        //v.name.setTypeface(Global.textfont);

        if(item.phone.equals(""))
            v.phone.setText(" - ");
        else
            v.phone.setText(item.phone);

        if(item.email.equals(""))
            v.email.setText(item.email);
        else
            v.email.setText(item.email);


        v.special.setText(item.special);
        v.custom.setText(item.custom);

        v.phone.setTypeface(Global.textfont);
        v.email.setTypeface(Global.textfont);

        v.special.setTypeface(Global.textfont);
        v.custom.setTypeface(Global.textfont);

        v.nameblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                if(v.expand.getVisibility() == View.VISIBLE) {
                    v.expand.setVisibility(View.GONE);
                } else {
                    v.expand.setVisibility(View.VISIBLE);
                }
            }
        });

        v.expand.setVisibility(View.GONE);


        if(disabled) {
            //v.del.setVisibility(View.GONE);
            v.call.setVisibility(View.GONE);
            v.edit.setVisibility(View.GONE);
            v.dd1.setText("");
            v.dd2.setText("");
            v.dd3.setText("");
            v.dd4.setText("");

            v.dd1.setBackground(context.getDrawable(R.drawable.ic_call_black_24dp));
            v.dd2.setBackground(context.getDrawable(R.drawable.ic_mail_outline_black_24dp));

            v.dd1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Global.simpleCALL(item.phone);
                }
            });

            v.dd2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String to = item.email;
                    String subject= Global.user.name1 + " " + Global.user.name2 + " " + Global.user.name3 + " request email from Save Me application";
                    String body="Hi!\n I have a problem about : \n";
                    String mailTo = "mailto:" + to +
                            "?&subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(body);
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    emailIntent.setData(Uri.parse(mailTo));
                    context.startActivity(emailIntent);
                }
            });

            v.del.setVisibility(View.VISIBLE);
            v.del.setBackground(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp, context.getTheme()));

            v.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(v.expand.getVisibility() == View.VISIBLE) {
                        v.expand.setVisibility(View.GONE);
                        view.setBackground(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp, context.getTheme()));
                    } else {
                        v.expand.setVisibility(View.VISIBLE);
                        view.setBackground(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_black_24dp, context.getTheme()));
                    }
                }
            });

        } else {

            v.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DoctorsEditActivity.class);
                    Bundle b = new Bundle();
                    b.putString("name", item.name);
                    b.putString("email", item.email);
                    b.putString("phone", item.phone);
                    b.putString("special", item.special);
                    b.putString("custom", item.custom);
                    b.putInt("id", j);
                    i.putExtras(b);
                    context.startActivity(i);
                }
            });

            v.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, DoctorsEditActivity.class);
                    Bundle b = new Bundle();
                    b.putString("name", item.name);
                    b.putString("email", item.email);
                    b.putString("phone", item.phone);
                    b.putString("special", item.special);
                    b.putString("custom", item.custom);
                    b.putInt("id", j);
                    i.putExtras(b);
                    context.startActivity(i);
                }
            });

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

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView phone;
        public TextView email;
        public TextView dd1;
        public TextView dd2;

        public TextView dd3;
        public TextView dd4;

        public TextView special;
        public TextView custom;

        public ImageButton edit;
        public ImageButton call;
        public ImageButton del;
        public LinearLayout block;
        public LinearLayout expand;
        public RelativeLayout nameblock;

        public CustomViewHolder(View view) {
            super(view);

            this.nameblock = (RelativeLayout) view.findViewById(R.id.nameblock);

            this.block = (LinearLayout) view.findViewById(R.id.block);
            this.expand = (LinearLayout) view.findViewById(R.id.expand);
            this.name = (TextView) view.findViewById(R.id.name);
            this.email = (TextView) view.findViewById(R.id.email);
            this.phone = (TextView) view.findViewById(R.id.phone);

            this.dd1 = (TextView) view.findViewById(R.id.ddnext1);
            this.dd2 = (TextView) view.findViewById(R.id.ddnext2);

            this.dd3 = (TextView) view.findViewById(R.id.ddnext3);
            this.dd4 = (TextView) view.findViewById(R.id.ddnext4);
            this.special = (TextView) view.findViewById(R.id.special);
            this.custom = (TextView) view.findViewById(R.id.custom);

            this.edit = (ImageButton) view.findViewById(R.id.edit);
            this.call = (ImageButton) view.findViewById(R.id.callGuard);
            this.del = (ImageButton) view.findViewById(R.id.delete);

        }
    }

}


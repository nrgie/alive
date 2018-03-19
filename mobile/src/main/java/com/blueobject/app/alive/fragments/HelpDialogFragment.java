package com.blueobject.app.alive.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.blueobject.app.alive.R;

/**
 * Created by nrgie on 2017.08.24..
 */

public class HelpDialogFragment extends DialogFragment {

    String mess = "";

    public HelpDialogFragment() {

    }

    public HelpDialogFragment(String m) {
        mess = m;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mess)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        //mListener.onDialogPositiveClick(HelpDialogFragment.this);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        //mListener.onDialogNegativeClick(HelpDialogFragment.this);
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}


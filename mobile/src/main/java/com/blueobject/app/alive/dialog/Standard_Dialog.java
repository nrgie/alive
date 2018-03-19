package com.blueobject.app.alive.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blueobject.app.alive.R;

public class Standard_Dialog extends DialogFragment implements View.OnClickListener{

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_NEGATIVEBUTTON = "negativeButton";
    private static final String KEY_POSITIVEBUTTON = "positiveButton";

    private TextView dialogTitle;
    private TextView dialogMessage;
    private Button dialogNegativeButton;
    private Button dialogPositiveButton;

    private static MyDialogListener myDialogListener;

    public static Standard_Dialog newInstance(String title, String message, String negativeButton, String positiveButton, MyDialogListener dialogListener) {
        Standard_Dialog f = new Standard_Dialog();

        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putString(KEY_NEGATIVEBUTTON, negativeButton);
        args.putString(KEY_POSITIVEBUTTON, positiveButton);
        f.setArguments(args);

        myDialogListener = dialogListener;

        return f;
    }

    public Standard_Dialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog, null);

        dialogTitle = (TextView) dialogView.findViewById(R.id.dialogTitle);
        dialogMessage = (TextView) dialogView.findViewById(R.id.dialogMessage);
        dialogNegativeButton = (Button) dialogView.findViewById(R.id.dialogButtonNegative);
        dialogPositiveButton = (Button) dialogView.findViewById(R.id.dialogButtonPositive);

        dialogTitle.setText(getArguments().getString(KEY_TITLE));
        dialogMessage.setText(getArguments().getString(KEY_MESSAGE));
        dialogNegativeButton.setText(getArguments().getString(KEY_NEGATIVEBUTTON));
        dialogPositiveButton.setText(getArguments().getString(KEY_POSITIVEBUTTON));

        dialogNegativeButton.setOnClickListener(this);
        dialogPositiveButton.setOnClickListener(this);

        builder.setView(dialogView);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialogButtonNegative) {
            myDialogListener.onDialogNegativeClick(this);
        }
        if (v.getId() == R.id.dialogButtonPositive) {
            myDialogListener.onDialogPositiveClick(this);
        }

    }


    public interface MyDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }
}
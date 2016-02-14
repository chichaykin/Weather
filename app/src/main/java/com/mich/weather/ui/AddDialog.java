package com.mich.weather.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mich.weather.R;

public class AddDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    public interface AddDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, String city, String country);
    }

    private EditText mCityView;
    private EditText mCountryView;
    private AddDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_location, null);
        mCityView = (EditText) view.findViewById(R.id.city);
        mCountryView = (EditText) view.findViewById(R.id.country);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton(R.string.action_add, this)
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        if (id == DialogInterface.BUTTON_POSITIVE) {
            String city = mCityView.getText().toString();
            if (TextUtils.isEmpty(city)) {
                Toast.makeText(this.getActivity(), R.string.wrong_name, Toast.LENGTH_LONG).show();
                return;
            }
            String country = mCountryView.getText().toString();
            if (TextUtils.isEmpty(country)) {
                Toast.makeText(this.getActivity(), R.string.wrong_name, Toast.LENGTH_LONG).show();
                return;
            }
            mListener.onDialogPositiveClick(this, city, country);
        } else {
            dismiss();
        }

    }
}

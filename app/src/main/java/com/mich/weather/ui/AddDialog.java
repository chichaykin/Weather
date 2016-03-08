package com.mich.weather.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.mich.weather.R;

public class AddDialog extends DialogFragment
        implements DialogInterface.OnClickListener {

    public interface AddDialogListener {
        void onDialogPositiveClick(String city, String country);
    }

    private EditText mCityView;
    private EditText mCountryView;
    private AddDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") final View view = inflater.inflate(R.layout.dialog_add_location, null);
        mCityView = (EditText) view.findViewById(R.id.city);
        mCountryView = (EditText) view.findViewById(R.id.country);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setPositiveButton(R.string.action_add, this)
                .setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(isInsertEnabled());

                final ChangesWatcher changesWatcher = new ChangesWatcher();
                mCityView.addTextChangedListener(changesWatcher);
                mCountryView.addTextChangedListener(changesWatcher);
            }
        });
        return dialog;
    }

    private boolean isInsertEnabled() {
        return !TextUtils.isEmpty(mCityView.getText());
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
            String country = mCountryView.getText().toString();
            mListener.onDialogPositiveClick(city, country);
        }
        dismissAllowingStateLoss();

    }

    private class ChangesWatcher implements TextWatcher {

        @Override
        public void afterTextChanged(final Editable s) {
            final boolean enabled = isInsertEnabled();
            ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(enabled);
        }

        @Override
        public void beforeTextChanged(final CharSequence s, final int start,
                                      final int count, final int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, final int start,
                                  final int before, final int count) {
        }
    }
}

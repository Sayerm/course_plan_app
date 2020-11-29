package com.example.coursePlan.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.coursePlan.R;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordDialogue extends AppCompatDialogFragment {

    Button negativeButton, positiveButton;

    SendResetPassword sendResetPassword;
    TextInputEditText forgotPassEmailEt;

    public ForgotPasswordDialogue() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.exit_dialogue, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        negativeButton = view.findViewById(R.id.negativeButton);
        positiveButton = view.findViewById(R.id.posiiveButton);

        AlertDialog alert = builder.create();
        setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(forgotPassEmailEt.getText().toString())) {
                    forgotPassEmailEt.setError("Enter an email");
                    forgotPassEmailEt.requestFocus();
                } else {
//
                    sendResetPassword.ResetEmail(forgotPassEmailEt.getText().toString());
                    dismiss();
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return alert;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            sendResetPassword = (SendResetPassword) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement unfollowListener");
        }
    }

    public interface SendResetPassword {
        void ResetEmail(String email);
    }

}
package com.example.coursePlan.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.coursePlan.R;

public class ExitDialogue extends AppCompatDialogFragment {

    Button negativeButton,positiveButton;

    ApplyExitApp applyExitApp;
    TextView titleTV;

    public ExitDialogue() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.exit_dialogue, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        negativeButton=view.findViewById(R.id.negativeButton);
        positiveButton=view.findViewById(R.id.posiiveButton);
        titleTV=view.findViewById(R.id.titleTV);

        AlertDialog alert = builder.create();
        setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyExitApp.exitApp();
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
            applyExitApp = (ApplyExitApp) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddBusinessValueListener");
        }
    }

    public interface ApplyExitApp {
        void exitApp();
    }

}
package ru.andypunch.ssorganizer.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;

import ru.andypunch.ssorganizer.MainActivity;
import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.utils.CustomSnack;


public class DeleteFieldOfStudyFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        String title = "";
        if (mArgs != null && mArgs.getString("fosDelete", "") != null) {
            title = mArgs.getString("fosDelete", "");

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete);
        final String finalTitle = title;
        builder.setPositiveButton(R.string.ok, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                if (getTag().equals("deleteFosFragment")) {
                    ((MainActivity) getActivity()).onDeleteClicked(finalTitle);
                }
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final View view = getActivity().findViewById(R.id
                        .drawer_layout_main);
                CustomSnack.setSnack(view, R.string.delete_cancel).show();
            }
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(android.support.v7.app
                .AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat
                .getColor(getActivity(),
                        R.color.custom_action_bar_color));
        ((AlertDialog) getDialog()).getButton(android.support.v7.app
                .AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat
                .getColor(getActivity(),
                        R.color.custom_action_bar_color));
    }
}

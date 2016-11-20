package ru.andypunch.ssorganizer.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import ru.andypunch.ssorganizer.R;


public class DeleteCommentFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogCustom);

        builder.setTitle(R.string.delete);
        builder.setPositiveButton(R.string.ok, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                ((CommentResourceFragment) getTargetFragment())
                        .onCommentDeleteClicked();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(getActivity(),
                        R.color.custom_action_bar_color));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(getActivity(),
                        R.color.custom_action_bar_color));
    }

}

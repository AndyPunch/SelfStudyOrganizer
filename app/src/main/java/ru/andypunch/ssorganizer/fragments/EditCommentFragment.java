package ru.andypunch.ssorganizer.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.utils.CustomSnack;


public class EditCommentFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogCustom);

        // Use an EditText view to get user input.
        final EditText input = new EditText(getActivity());

        input.setSelectAllOnFocus(true);

        input.getBackground().setColorFilter(ContextCompat.getColor
                (getActivity(),
                        R.color.edittext_underline_color), PorterDuff.Mode
                .SRC_IN);
        input.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.custom_text_color));
        input.setHighlightColor(ContextCompat.getColor(getActivity(),
                R.color.table_line2_color));
        //вводим данные в несколько строк
        input.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        Bundle mArgs = getArguments();
        String commentText = mArgs.getString("commentText");
        input.setText(commentText);
        builder.setView(input);


        builder.setPositiveButton(R.string.ok, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String editedCommentText = input.getText().toString().trim();

                if (!editedCommentText.isEmpty()) {
                    ((CommentResourceFragment) getTargetFragment())
                            .onCommentEditClicked(editedCommentText);
                } else {

                    final View snView = getDialog().findViewById(R.id
                            .toolbar_comment);
                    CustomSnack.setSnack(snView, R.string.enter_text).show();
                }
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

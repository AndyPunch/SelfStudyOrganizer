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
import android.widget.LinearLayout;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.StudyResourcesActivity;
import ru.andypunch.ssorganizer.utils.CustomSnack;


public class CopyPastUrlFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogCustom);
        builder.setTitle(R.string.enter_link);
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Use an EditText view to get user input.
        final EditText input = new EditText(getActivity());
        input.setHint(R.string.enter_link_hint);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        input.getBackground().setColorFilter(ContextCompat.getColor
                (getActivity(),
                        R.color.edittext_underline_color), PorterDuff.Mode
                .SRC_IN);
        input.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.custom_text_color));
        input.setHighlightColor(ContextCompat.getColor(getActivity(),
                R.color.table_line2_color));

        layout.addView(input);


        // Use an EditText view to get user input.
        final EditText input2 = new EditText(getActivity());
        input2.setHint(R.string.internet_title_hint);
        input2.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);

        input2.getBackground().setColorFilter(ContextCompat.getColor
                (getActivity(),
                        R.color.edittext_underline_color), PorterDuff.Mode
                .SRC_IN);
        input2.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.custom_text_color));
        input2.setHighlightColor(ContextCompat.getColor(getActivity(),
                R.color.table_line2_color));
        layout.addView(input2);

        builder.setView(layout);

        builder.setPositiveButton(R.string.ok, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String link = input.getText().toString().trim();
                String title = input2.getText().toString().trim();
                if (!link.isEmpty()) {
                    if (title.isEmpty()) {
                        title = null;
                    }
                    ((StudyResourcesActivity) getActivity()).addUrl(link, title);
                } else {
                    final View snackView = getActivity().findViewById(R.id
                            .rellay_post_finish_activity);
                    CustomSnack.setSnack(snackView, R.string.enter_link_warning).show();
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

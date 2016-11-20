package ru.andypunch.ssorganizer.fragments;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import ru.andypunch.ssorganizer.MainActivity;
import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.utils.CustomSnack;


public class AddFieldOfStudyFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogCustom);
        builder.setTitle(R.string.new_field_of_study);
        final EditText input = new EditText(getActivity());
        //set length and color of text
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(40);
        input.setFilters(FilterArray);
        input.getBackground().setColorFilter(ContextCompat.getColor
                (getActivity(),
                        R.color.edittext_underline_color), PorterDuff.Mode
                .SRC_IN);
        input.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.custom_text_color));
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        //to prevent full screen of edit text in land mode
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        builder.setView(input);

        builder.setPositiveButton(R.string.ok, new DialogInterface
                .OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                // for right sql query
                value = value.replaceAll("'", "`");
                if (!value.isEmpty())
                    ((MainActivity) getActivity()).addStudyField(value);
                else {
                    final View view = getActivity().findViewById(R.id
                            .drawer_layout_main);
                    CustomSnack.setSnack(view, R.string.enter_name).show();
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

package ru.andypunch.ssorganizer.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.StudyResourcesActivity;
import ru.andypunch.ssorganizer.utils.CustomSnack;


public class RenameResourceFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle mArgs = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AlertDialogCustom);
        builder.setTitle(R.string.enter_name);

        // Use an EditText view to get user input.
        final EditText input = new EditText(getActivity());
        input.setSelectAllOnFocus(true);
        //устанавливаем длину вводимого текста
        /*InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(40);
        input.setFilters(FilterArray);*/
        input.getBackground().setColorFilter(ContextCompat.getColor
                (getActivity(),
                        R.color.edittext_underline_color), PorterDuff.Mode
                .SRC_IN);
        input.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.custom_text_color));
        input.setHighlightColor(ContextCompat.getColor(getActivity(),
                R.color.table_line2_color));
        input.setText(mArgs.getString("resourceName"));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        //для того чтобы edit text не раскрывался на весь экран в lend mode
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        builder.setView(input);


        builder.setPositiveButton(R.string.ok, new DialogInterface
                .OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                // for right sql query
                value = value.replaceAll("'", "`");
                if (!value.isEmpty()) {

                    ((StudyResourcesActivity) getActivity()).onRenameResourceClicked
                            (value);
                } else {
                    final View snackView = getActivity().findViewById(R.id
                            .rellay_post_finish_activity);
                    CustomSnack.setSnack(snackView, R.string.enter_name).show();
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

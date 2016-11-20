package ru.andypunch.ssorganizer.utils;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;


public class CustomSnack {

    public static Snackbar setSnack(View view, int message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar
                .LENGTH_LONG);
        return customSnackBar(snackbar);
    }

    private static View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static Snackbar customSnackBar(Snackbar snackbar) {
        View snackBarView = getSnackBarLayout(snackbar);
        snackBarView.setBackgroundColor(Color.parseColor("#9A8C87"));
        TextView textView = (TextView) snackBarView.findViewById(android
                .support.design.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        return snackbar;
    }

     /* void setSnack(View view, int message) {
        Snackbar snackbar;
        snackbar = Snackbar.make(view, message, Snackbar
                .LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color
                .custom_action_bar_color2));
        TextView textView = (TextView) snackBarView.findViewById(android
        .support.design.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER);
        snackbar.show();
    }*/

    /*public void setToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }*/

    /*public void setToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }*/
}

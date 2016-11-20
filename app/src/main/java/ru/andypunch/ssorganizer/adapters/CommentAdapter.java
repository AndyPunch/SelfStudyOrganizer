package ru.andypunch.ssorganizer.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.utils.Time;

/**
 * CommentAdapter.java
 * <p>
 * Custom adapter for listView in CommentResourceFragment.java
 */

public class CommentAdapter extends SimpleCursorAdapter {

    public CommentAdapter(Context context, int layout, Cursor c,
                          String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == R.id.commentDate) {
            String dateSimbol = "dd-MM-yyyy";
            //get date from miliseconds
            text = Time.getDate(Long.parseLong(text), dateSimbol);
        }
        v.setText(text);
    }
}

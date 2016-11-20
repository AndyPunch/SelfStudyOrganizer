package ru.andypunch.ssorganizer.adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.fragments.DeleteFieldOfStudyFragment;
import ru.andypunch.ssorganizer.fragments.UpdateFosTitleFragment;
import ru.andypunch.ssorganizer.utils.Time;

/**
 * MySimpleCursorAdapter.java
 * <p>
 * Custom adapter for listView in MainActivity.java
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter implements View.OnClickListener {

    private Cursor mCursor;
    private Context mContext;

    public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                 String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
    }

    @Override
    public void setViewText(TextView v, String text) {
        if (v.getId() == R.id.startStudyField_date) {
            String dateSimbol = "dd-MM-yyyy";
            //get date from miliseconds
            text = Time.getDate(Long.parseLong(text), dateSimbol);
        }
        v.setText(text);
    }

    //Bind an existing view to the data pointed to by cursor
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        mCursor = cursor;
        ImageView popupImg = (ImageView) view.findViewById(R.id.studyField_popup);
        popupImg.setTag(cursor.getPosition());
        popupImg.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        final int position = (int) view.getTag();
        mCursor.moveToPosition(position);
        final String fosName;
        fosName = mCursor.getString(1);

        //create PopupMenu
        PopupMenu popup = new PopupMenu(mContext, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu,
                popup.getMenu());
        popup.show();

        //handle click on popupMenu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bundle args = new Bundle();
                FragmentManager manager = ((Activity) mContext).getFragmentManager();
                switch (item.getItemId()) {

                    case R.id.popup_update:
                        args.putString("fosNameUpdate", fosName);
                        UpdateFosTitleFragment updateFosTitleFragment = new
                                UpdateFosTitleFragment();
                        updateFosTitleFragment.setArguments(args);
                        updateFosTitleFragment.show(manager, "updateFosTitleFragment");
                        break;
                    case R.id.popup_delete:
                        args.putString("fosDelete", fosName);
                        DeleteFieldOfStudyFragment deleteFieldOfStudyFragment = new
                                DeleteFieldOfStudyFragment();
                        deleteFieldOfStudyFragment.setArguments(args);
                        deleteFieldOfStudyFragment.show(manager, "deleteFosFragment");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}

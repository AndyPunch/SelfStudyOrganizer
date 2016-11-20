package ru.andypunch.ssorganizer.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.StudyArrays;
import ru.andypunch.ssorganizer.databases.CommentDb;
import ru.andypunch.ssorganizer.databases.RunDb;
import ru.andypunch.ssorganizer.utils.Time;

/**
 * MyExpandableListAdapter.java
 * <p>
 * Custom adapter for listView in StudyResourcesActivity.java
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    public static String stFosTitle;
    // header titles
    private List<String> listDataHeader;
    // child data in format of header title, child title
    private HashMap<String, List<String>> listDataChild;
    private LayoutInflater lInflater;
    private ViewHolder holder = null;
    private CommentDb commentDb;
    private RunDb runDb;
    private final String BAD = "bad";
    private final String GOOD = "good";


    public MyExpandableListAdapter(Context context, List<String> listDataHeader,
                                   HashMap<String, List<String>> listChildData) {
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        commentDb = new CommentDb(context);
        commentDb.open();
        runDb = new RunDb(context);
        runDb.open();
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        String strGroupPosition = String.valueOf(groupPosition);
        return this.listDataChild.get(strGroupPosition).get(childPosititon);
    }

    //speed up rendering of ListView
    private static class ViewHolder {

        TextView txtListChild;
        TextView commentInExpText;
        ImageView thumbDown;
        ImageView thumbUp;
        ImageView diligenceLow;
        ImageView diligenceMiddle;
        ImageView diligenceHigh;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup
                                     parent) {
        View view = convertView;
        final String childText = (String) getChild(groupPosition,
                childPosition);
        if (view == null) {
            view = lInflater.inflate(R.layout
                    .study_field_finish_list_item, null);
            holder = new ViewHolder();
            holder.txtListChild = (TextView) view
                    .findViewById(R.id.finish_exp_list_item);
            holder.commentInExpText = (TextView) view.findViewById(R.id
                    .commentInExpText);
            holder.thumbDown = (ImageView) view.findViewById(R.id
                    .thumbDownExp);
            holder.thumbUp = (ImageView) view.findViewById(R.id
                    .thumbUpExp);
            holder.diligenceLow = (ImageView) view.findViewById(R.id
                    .diligence_low);
            holder.diligenceMiddle = (ImageView) view.findViewById(R.id
                    .diligence_middle);
            holder.diligenceHigh = (ImageView) view.findViewById(R.id
                    .diligence_high);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.txtListChild.setText(childText);

        //add comment count
        int count = commentDb.getCommentCount(stFosTitle, childText, String
                .valueOf(groupPosition));
        holder.commentInExpText.setText(String.valueOf(count));
        holder.commentInExpText.setTextColor(Color.parseColor("#9A8C87"));


        int[] grades = runDb.getGrades(stFosTitle, childText, String
                .valueOf(groupPosition));
        int gradeGood = grades[0];
        int gradeBad = grades[1];
        String currentGrade = "";

        //calculate grade
        if ((gradeBad * 3 >= gradeGood) && (gradeBad > 0 || gradeGood > 0)) {
            currentGrade = BAD;
        } else if (gradeBad * 3 < gradeGood && (gradeBad > 0 || gradeGood > 0)) {
            currentGrade = GOOD;
        }

        //add icon to view
        switch (currentGrade) {
            case BAD:
                holder.thumbDown.setVisibility(View.VISIBLE);
                holder.thumbUp.setVisibility(View.GONE);
                break;
            case GOOD:
                holder.thumbDown.setVisibility(View.GONE);
                holder.thumbUp.setVisibility(View.VISIBLE);
                break;
            default:
                holder.thumbDown.setVisibility(View.GONE);
                holder.thumbUp.setVisibility(View.GONE);
        }

        //add diligence icon
        long oldTimeLong = runDb.getResourceRunTime(stFosTitle, childText, String.valueOf
                (groupPosition));
        if (oldTimeLong > 0) {
            long timeWarning = Time.getTimePeriod(oldTimeLong);
            if (timeWarning <= 1) {
                holder.diligenceLow.setVisibility(View.GONE);
                holder.diligenceMiddle.setVisibility(View.GONE);
                holder.diligenceHigh.setVisibility(View.VISIBLE);
            } else if (timeWarning > 1 && timeWarning <= 3) {
                holder.diligenceLow.setVisibility(View.GONE);
                holder.diligenceMiddle.setVisibility(View.VISIBLE);
                holder.diligenceHigh.setVisibility(View.GONE);
            } else if (timeWarning > 3) {
                holder.diligenceLow.setVisibility(View.VISIBLE);
                holder.diligenceMiddle.setVisibility(View.GONE);
                holder.diligenceHigh.setVisibility(View.GONE);
            }
        } else {
            holder.diligenceLow.setVisibility(View.GONE);
            holder.diligenceMiddle.setVisibility(View.GONE);
            holder.diligenceHigh.setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String strGroupPosition = String.valueOf(groupPosition);
        return this.listDataChild.get(strGroupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        String explHeaderPosition = String.valueOf(groupPosition);
        if (convertView == null) {

            convertView = lInflater.inflate(R.layout
                    .study_field_list_group, null);
        }

        TextView expListHeader = (TextView) convertView
                .findViewById(R.id.finish_exp_list_group);
        expListHeader.setTypeface(null, Typeface.ITALIC);
        expListHeader.setText(headerTitle + " (" + StudyArrays.resourceData
                .get(explHeaderPosition).size() + ")");
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
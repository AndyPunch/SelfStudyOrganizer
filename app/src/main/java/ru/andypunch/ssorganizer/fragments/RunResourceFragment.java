package ru.andypunch.ssorganizer.fragments;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.StudyResourcesActivity;
import ru.andypunch.ssorganizer.databases.RunDb;
import ru.andypunch.ssorganizer.utils.Time;

import static ru.andypunch.ssorganizer.databases.RunDb.BAD;
import static ru.andypunch.ssorganizer.databases.RunDb.GOOD;

public class RunResourceFragment extends DialogFragment implements View
        .OnClickListener {
    private View runResourceDialog;
    private TextView tvRunTime, tvDiligence, tvRunTitle;
    private PieChart mPieChart;
    private CardView cardViewDilligence, cardViewRun, cardViewThumbs, cardViewRunGrade;

    private String explHeaderPosition;
    private RunDb db;
    private String resourceName;
    private String fosTitle;
    private int gradeGood, gradeBad;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        runResourceDialog = inflater.inflate(R.layout.run_resource_dialog,
                null);

        //initialise view
        initView();

        //get bundle extras
        getExtras();

        db = new RunDb(getActivity());
        db.open();

        //make dialog header invisible
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return runResourceDialog;
    }

    //initialise actions
    private void initView() {
        ImageView runResource = (ImageView) runResourceDialog.findViewById(R.id
                .run_resource);
        tvRunTime = (TextView) runResourceDialog.findViewById(R.id.tvRunTime);
        tvDiligence = (TextView) runResourceDialog.findViewById(R.id
                .tvDiligence);
        tvRunTitle = (TextView) runResourceDialog.findViewById(R.id.tvRunTitle);
        ImageView imgThumbDown = (ImageView) runResourceDialog.findViewById(R.id
                .imgThumbDown);
        ImageView imgThumbUp = (ImageView) runResourceDialog.findViewById(R.id
                .imgThumbUp);
        mPieChart = (PieChart) runResourceDialog.findViewById(R.id.piechart);
        mPieChart.setEmptyDataText(getString(R.string.no_grades));
        cardViewRunGrade = (CardView) runResourceDialog.findViewById(R.id
                .cardViewRunGrade);
        cardViewDilligence = (CardView) runResourceDialog.findViewById(R.id
                .cardViewDilligence);
        cardViewRun = (CardView) runResourceDialog.findViewById(R.id.cardViewRun);
        cardViewThumbs = (CardView) runResourceDialog.findViewById(R.id.cardViewThumbs);
        cardViewThumbs.setVisibility(View.GONE);
        runResource.setOnClickListener(this);
        imgThumbDown.setOnClickListener(this);
        imgThumbUp.setOnClickListener(this);
    }

    //get bundle extras
    private void getExtras() {
        Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey("explHeaderPosition")) {
                explHeaderPosition = extras.getString("explHeaderPosition", "");
            }
            if (extras.containsKey("resourceName")) {
                resourceName = extras.getString("resourceName", "");
            }
            if (extras.containsKey("fosTitle")) {
                fosTitle = extras.getString("fosTitle", "");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null)
            return;
        //size of dialog in percentage
        int width = (int) (getResources().getDisplayMetrics().widthPixels *
                0.99);
        int height = (int) (getResources().getDisplayMetrics().heightPixels *
                0.99);
        getDialog().getWindow().setLayout(width, height);

        tvRunTitle.setText(resourceName);

        //last run of resource
        setLastEnter();

        //add or not resource evaluation
        setGradeMode();
    }

    //add or don't add resource evaluation
    private void setGradeMode() {
        int[] grades = db.getGrades(fosTitle, resourceName, explHeaderPosition);
        gradeGood = grades[0];
        gradeBad = grades[1];
        if (gradeGood > 0 || gradeBad > 0) {
            getPieChart(gradeBad, gradeGood);
        }



        /*int customGradeMode = Integer.parseInt(settings.getString("gradeMode", "4"));
        if (getRunCount() > customGradeMode) {
            int[] grades = db.getGrades(fosTitle, resourceName, explHeaderPosition);
            gradeGood = grades[0];
            gradeBad = grades[1];
            if (gradeGood > 0 || gradeBad > 0) {
                getPieChart(gradeBad, gradeGood);
            }
            linearPiechart.setVisibility(View.VISIBLE);
            runResource.setVisibility(View.GONE);
        } else {
            linearPiechart.setVisibility(View.GONE);
            runResource.setVisibility(View.VISIBLE);
        }*/
    }

    //get count of resource run
    private int getRunCount() {
        return db.getRunCount(fosTitle, resourceName, explHeaderPosition);
    }

    //method to determine run time of resource
    private void setLastEnter() {
        long oldTimeLong = db.getResourceRunTime(fosTitle, resourceName, explHeaderPosition);
        if (oldTimeLong > 0) {
            String howLong = Time.handleHowLongClick(oldTimeLong);
            tvRunTime.setText(String.format("%s%s", getString(R.string.you_enter_here), howLong));
            //set diligence
            long timeWarning = Time.getTimePeriod(oldTimeLong);
            if (timeWarning <= 1) {
                tvDiligence.setText(R.string.attendance_good);
                cardViewDilligence.setBackgroundColor(Color.parseColor
                        ("#C4DAC9"));
            } else if (timeWarning > 1 && timeWarning <= 3) {
                tvDiligence.setText(R.string.attendance_middle);
                cardViewDilligence.setBackgroundColor(Color.parseColor
                        ("#F9EAA4"));
            } else if (timeWarning > 3) {
                tvDiligence.setText(R.string.attendance_bad);
                cardViewDilligence.setBackgroundColor(Color.parseColor
                        ("#F4CBCB"));
            }
        }
    }

    //actions after click run button
    private void onRun() {
        String fullPath = db.getFullPath(fosTitle, resourceName, explHeaderPosition);
        fullPath = fullPath.trim();
        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(fullPath));

        switch (explHeaderPosition) {
            case "0":
                it.setDataAndType(Uri.parse(fullPath), "text/*");
                break;
            case "1":
                it.setDataAndType(Uri.parse(fullPath), "video/*");
                break;
            case "2":
                it.setDataAndType(Uri.parse(fullPath), "audio/*");
                break;
            case "3":
                if (!fullPath.startsWith("http://") && !fullPath.startsWith
                        ("https://")) {
                    fullPath = "http://" + fullPath;
                    it.setDataAndType(Uri.parse(fullPath), "text/html");
                }
                break;
        }
        try {
            startActivity(it);
        } catch (ActivityNotFoundException e) {
            setToast(getString(R.string.couldnt_find_device));
        }
        long millis = System.currentTimeMillis();
        int runCount = getRunCount();
        if (runCount == 0) {
            runCount++;
            db.addRunTimeAndCount(fosTitle, resourceName, explHeaderPosition, millis, runCount);
        } else {
            runCount++;
            db.updateRunTimeAndCount(fosTitle, resourceName, explHeaderPosition, millis, runCount);
        }
        ((StudyResourcesActivity) getActivity()).restartExpList();
        cardViewDilligence.setVisibility(View.GONE);
        cardViewRun.setVisibility(View.GONE);
        cardViewRunGrade.setVisibility(View.GONE);
        cardViewThumbs.setVisibility(View.VISIBLE);
    }

    //устанавливаем круговую  диаграмму
    private void getPieChart(int bad, int good) {
        mPieChart.addPieSlice(new PieModel(getString(R.string.bad_resource),
                bad, Color.parseColor("#F4CBCB")));
        mPieChart.addPieSlice(new PieModel(getString(R.string.good_resource),
                good, Color.parseColor("#C4DAC9")));
        //mPieChart.addPieSlice(new PieModel("Отлично", great, Color
        // .parseColor("#CDA67F")));
        mPieChart.startAnimation();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //run resource button
            case R.id.run_resource:
                onRun();
                break;

            //bad grade button
            case R.id.imgThumbDown:
                gradeBad++;
                db.updateGrade(fosTitle, resourceName, explHeaderPosition, gradeBad, BAD);
                ((StudyResourcesActivity) getActivity()).restartExpList();
                //onRun();
                getDialog().cancel();
                break;

            //good grade button
            case R.id.imgThumbUp:
                gradeGood++;
                db.updateGrade(fosTitle, resourceName, explHeaderPosition, gradeGood, GOOD);
                ((StudyResourcesActivity) getActivity()).restartExpList();
                //onRun();
                getDialog().cancel();
                break;
        }
    }

    public void setToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
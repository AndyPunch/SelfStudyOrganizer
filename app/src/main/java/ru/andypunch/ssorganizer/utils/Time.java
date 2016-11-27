package ru.andypunch.ssorganizer.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;

/**
 * Time.java
 *
 * Methods to convert time and dates
 */

public class Time {

    //convert second to millisecond and output in human-readable text
    public static String getDate(long seconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(new java.sql.Date(seconds));
    }


    //method to determine run time of resource
    public static String handleHowLongClick(long oldTimeLong) {
        CharSequence cs = DateUtils.getRelativeTimeSpanString(oldTimeLong);
        return cs.toString();
    }

    //calculate time period
    public static long getTimePeriod(long oldDate) {
        long currentDate = System.currentTimeMillis();
        //milliseconds
        long different = currentDate - oldDate;
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;
        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;
        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;
        long elapsedSeconds = different / secondsInMilli;
        return elapsedDays;
    }
}

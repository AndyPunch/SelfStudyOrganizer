package ru.andypunch.ssorganizer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * RunDb.java
 * <p>
 * class to manage runTable
 */

public class RunDb extends ResourceDb {
    final public static String GOOD = "good";
    final public static String BAD = "bad";

    public RunDb(Context ctx) {
        super(ctx);
    }

    public void addRunTimeAndCount(String fosTitle, String resourceName, String
            explHeaderPosition, long
                                           millis, int count) {
        ContentValues cv = new ContentValues();
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        cv.put(COLUMN_RESOURCE_RUN_TIME, millis);
        cv.put(COLUMN_RESOURCE_RUN_COUNT, count);
        cv.put(COLUMN_RESOURCE_REF_RUN_ID, id);
        mDB.insert(DB_RUN_TABLE, null, cv);
    }

    //update time of use of resource and count of use of resource
    public void updateRunTimeAndCount(String fosTitle, String resourceName, String
            explHeaderPosition, long millis, int runCount) {
        ContentValues cv = new ContentValues();
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        cv.put(COLUMN_RESOURCE_RUN_TIME, millis);
        cv.put(COLUMN_RESOURCE_RUN_COUNT, runCount);
        mDB.update(DB_RUN_TABLE, cv, COLUMN_RESOURCE_REF_RUN_ID + " = ?", new
                String[]{String.valueOf(id)});
    }

    //get time of resourse using
    public long getResourceRunTime(String fosTitle, String resourceName, String
            explHeaderPosition) {
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        Cursor cursor = mDB.rawQuery("SELECT resourceRunTime " +
                "FROM runTable " +
                "WHERE resourceId = " + id +
                ";", null);
        long resourceRunTime = 0L;
        if (cursor.moveToFirst()) {
            resourceRunTime = cursor.getLong(cursor.getColumnIndex(COLUMN_RESOURCE_RUN_TIME));
        }
        cursor.close();

        return resourceRunTime;
    }

    //get count of use of resource
    public int getRunCount(String fosTitle, String resourceName, String explHeaderPosition) {
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        Cursor cursor = mDB.rawQuery("SELECT resourceRunCount " +
                "FROM runTable " +
                "WHERE resourceId = " + id +
                ";", null);
        int resourceRunCount = 0;
        if (cursor.moveToFirst()) {
            resourceRunCount = cursor.getInt(cursor.getColumnIndex(COLUMN_RESOURCE_RUN_COUNT));
        }
        cursor.close();
        return resourceRunCount;
    }

    //get grades of resource
    public int[] getGrades(String fosTitle, String resourceName, String explHeaderPosition) {
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        Cursor cursor = mDB.rawQuery("SELECT resourceGradeGood, resourceGradeBad " +
                "FROM runTable " +
                "WHERE resourceId = " + id +
                ";", null);
        int gradeGood = 0;
        int gradeBad = 0;
        if (cursor.moveToFirst()) {
            gradeGood = cursor.getInt(cursor.getColumnIndex(COLUMN_RESOURCE_GRADE_GOOD));
            gradeBad = cursor.getInt(cursor.getColumnIndex(COLUMN_RESOURCE_GRADE_BAD));
        }
        cursor.close();
        return new int[]{gradeGood, gradeBad};
    }

    //update grades in database
    public void updateGrade(String fosTitle, String resourceName, String explHeaderPosition, int
            grade, String code) {
        ContentValues cv = new ContentValues();
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        if (code.equals(BAD)) {
            cv.put(COLUMN_RESOURCE_GRADE_BAD, grade);
        } else if (code.equals(GOOD)) {
            cv.put(COLUMN_RESOURCE_GRADE_GOOD, grade);
        }
        mDB.update(DB_RUN_TABLE, cv, COLUMN_RESOURCE_REF_RUN_ID + " = ?", new
                String[]{String.valueOf(id)});
    }
}


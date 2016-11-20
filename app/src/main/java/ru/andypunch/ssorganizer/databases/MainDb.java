package ru.andypunch.ssorganizer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * MainDb.java
 * <p>
 * class to  manage mainTable
 */

public class MainDb extends Db {
    public MainDb(Context ctx) {
        super(ctx);
    }

    //add to database fieldOfStudy name and date
    public void addMainTitleRec(String title) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STUDY_FIELD_TITLE, title);
        cv.put(COLUMN_STUDY_FIELD_DATE, System.currentTimeMillis());
        mDB.insert(DB_MAIN_TABLE, null, cv);
    }

    //get data for main activity listview
    public Cursor getTitleData() {
        return mDB.query(DB_MAIN_TABLE, new String[]{
                COLUMN_ID, COLUMN_STUDY_FIELD_TITLE,
                COLUMN_STUDY_FIELD_DATE}, null, null, null, null, null);
    }

    //update field of study name
    public void updateFOS(String FOSname, String newFOSname) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STUDY_FIELD_TITLE, newFOSname);
        mDB.update(DB_MAIN_TABLE, cv, COLUMN_STUDY_FIELD_TITLE + " = ?", new
                String[]{FOSname});
    }

    //delete field of study
    public void delFieldOfStudy(String fosTitle) {
        mDB.execSQL("PRAGMA foreign_keys=ON");
        mDB.delete(DB_MAIN_TABLE, COLUMN_STUDY_FIELD_TITLE + " = ?", new
                String[]{fosTitle});
    }

    //get note's id by title
    Integer getFosID(String fosName) {
        Cursor cursor = mDB.query(DB_MAIN_TABLE, new String[]{COLUMN_ID},
                COLUMN_STUDY_FIELD_TITLE + " = ?", new String[]{fosName}, null, null,
                null);
        Integer fosId = null;
        if (cursor.moveToFirst()) {
            do {
                fosId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fosId;
    }

    //get note's id by title
    Integer getResourceID(String fosTitle, String resourceName, String explHeaderPosition) {
        Cursor cursor = mDB.query(DB_MAIN_TABLE, new String[]{COLUMN_ID},
                COLUMN_STUDY_FIELD_TITLE + " = ?", new String[]{fosTitle}, null, null,
                null);
        Integer fosId = null;
        if (cursor.moveToFirst()) {
            do {
                fosId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            } while (cursor.moveToNext());
        }
        cursor = mDB.query(DB_RESOURCE_TABLE, new String[]{COLUMN_RESOURCE_ID},
                COLUMN_RESOURCE_TYPE + " = ? AND " +
                        COLUMN_RESOURCE_SHORT_TITLE + " = ? AND " + COLUMN_FOS_REF_ID + " = ?",
                new String[]{explHeaderPosition, resourceName, String.valueOf(fosId)}, null, null,
                null);
        Integer resourceId = null;
        if (cursor.moveToFirst()) {
            do {
                resourceId = cursor.getInt(cursor.getColumnIndex(COLUMN_RESOURCE_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resourceId;
    }
}

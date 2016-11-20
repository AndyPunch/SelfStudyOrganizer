package ru.andypunch.ssorganizer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

import ru.andypunch.ssorganizer.StudyArrays;

/**
 * ResourceDb.java
 * <p>
 * class to manage resourceTable
 */

public class ResourceDb extends MainDb {
    public ResourceDb(Context ctx) {
        super(ctx);
    }

    //add resource
    public void addResourceData(String fosTitle, String type, List<String> resourceURIList,
                                List<String> resourceShortName) {
        ContentValues cv = new ContentValues();
        Integer id = getFosID(fosTitle);
        for (int i = 0; i < resourceURIList.size(); i++) {
            cv.put(COLUMN_RESOURCE_TYPE, type);
            cv.put(COLUMN_RESOURCE_FULL_TITLE, resourceURIList.get(i));
            cv.put(COLUMN_RESOURCE_SHORT_TITLE, resourceShortName.get(i));
            cv.put(COLUMN_FOS_REF_ID, id);
            mDB.insert(DB_RESOURCE_TABLE, null, cv);
        }
    }

    //get resources for arraylist
    public void getResourceData(String fosTitle) {
        Integer id = getFosID(fosTitle);
        String fileName, type;
        Cursor cursor = mDB.rawQuery("SELECT resourceType, resourceShortTitle " +
                "FROM resourceTable " +
                "WHERE fosId = " + id + ";", null);
        if (cursor.moveToFirst()) {
            do {
                type = cursor.getString(cursor.getColumnIndex(COLUMN_RESOURCE_TYPE));
                fileName = cursor.getString(cursor.getColumnIndex(COLUMN_RESOURCE_SHORT_TITLE));
                StudyArrays.resourceData.get(type).add
                        (fileName);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //add internetResources
    public void addInternetResourceData(String fosTitle, String type, String link, String urlTitle) {
        ContentValues cv = new ContentValues();
        Integer id = getFosID(fosTitle);
        cv.put(COLUMN_RESOURCE_TYPE, type);
        cv.put(COLUMN_RESOURCE_FULL_TITLE, link);
        cv.put(COLUMN_RESOURCE_SHORT_TITLE, urlTitle);
        cv.put(COLUMN_FOS_REF_ID, id);
        mDB.insert(DB_RESOURCE_TABLE, null, cv);
    }

    //get full path to resource
    public String getFullPath(String fosTitle, String resourceName, String explHeaderPosition) {
        Integer id = getFosID(fosTitle);
        Cursor cursor = mDB.rawQuery("SELECT resourceFullTitle " +
                "FROM resourceTable " +
                "WHERE resourceType = '" + explHeaderPosition + "'" +
                " AND fosId = " + id +
                " AND resourceShortTitle = '" + resourceName + "'" +
                ";", null);
        String fullPath = null;
        if (cursor.moveToFirst()) {
            fullPath = cursor.getString(cursor.getColumnIndex(COLUMN_RESOURCE_FULL_TITLE));
        }
        cursor.close();

        return fullPath;
    }

    //rename resource
    public void renameResource(String fosTitle, String resourceName, String explHeaderPosition,
                               String newResourceName) {
        ContentValues cv = new ContentValues();
        Integer id = getFosID(fosTitle);
        cv.put(COLUMN_RESOURCE_SHORT_TITLE, newResourceName);
        mDB.update(DB_RESOURCE_TABLE, cv, COLUMN_RESOURCE_TYPE + " = ? AND " +
                COLUMN_RESOURCE_SHORT_TITLE + " = ? AND " + COLUMN_FOS_REF_ID + " = ?", new
                String[]{explHeaderPosition, resourceName, String.valueOf(id)});
    }

    //delete resource
    public void deleteResource(String fosTitle, String resourceName, String explHeaderPosition) {
        Integer id = getFosID(fosTitle);
        mDB.execSQL("PRAGMA foreign_keys=ON");
        mDB.delete(DB_RESOURCE_TABLE, COLUMN_RESOURCE_TYPE + " = ? AND " +
                COLUMN_RESOURCE_SHORT_TITLE + " = ? AND " + COLUMN_FOS_REF_ID + " = ?", new
                String[]{explHeaderPosition, resourceName, String.valueOf(id)});
    }
}

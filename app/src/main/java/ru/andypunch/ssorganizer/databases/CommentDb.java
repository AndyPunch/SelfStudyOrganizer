package ru.andypunch.ssorganizer.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * CommentDb.java
 * <p>
 * class to manage commentTable
 */

public class CommentDb extends ResourceDb {

    public CommentDb(Context ctx) {
        super(ctx);
    }

    //get comments for listview
    public Cursor getCommentData(String fosTitle, String resourceName, String explHeaderPosition) {
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        Cursor cursor = mDB.rawQuery("SELECT _id, resourceCommentTime, resourceCommentBody " +
                "FROM commentTable " +
                "WHERE resourceId = " + id +
                ";", null);
        return cursor;
    }

    //add comments to database
    public void addComment(String fosTitle, String resourceName, String explHeaderPosition, long
            millisDate, String commentText) {
        ContentValues cv = new ContentValues();
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        cv.put(COLUMN_RESOURCE_COMMENT_TIME, millisDate);
        cv.put(COLUMN_RESOURCE_COMMENT_BODY, commentText);
        cv.put(COLUMN_RESOURCE_REF_COMMENT_ID, id);
        mDB.insert(DB_COMMENT_TABLE, null, cv);
    }

    //delete comments from database
    public void deleteResourceComment(String fosTitle, String resourceName, String
            explHeaderPosition, String commentBody) {
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        mDB.execSQL("PRAGMA foreign_keys=ON");
        mDB.delete(DB_COMMENT_TABLE, COLUMN_RESOURCE_COMMENT_BODY + " = ? AND " +
                COLUMN_RESOURCE_REF_COMMENT_ID + " = ?", new
                String[]{commentBody, String.valueOf(id)});
    }

    //update edited comment
    public void editResourceComment(String fosTitle, String resourceName, String
            explHeaderPosition, String commentOldText, String editedCommentText) {
        ContentValues cv = new ContentValues();
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        cv.put(COLUMN_RESOURCE_COMMENT_BODY, editedCommentText);
        mDB.update(DB_COMMENT_TABLE, cv, COLUMN_RESOURCE_COMMENT_BODY + " = ? AND " +
                COLUMN_RESOURCE_REF_COMMENT_ID + " = ?", new
                String[]{commentOldText, String.valueOf(id)});
    }

    //coun tags for certain note
    public int getCommentCount(String fosTitle, String resourceName, String
            explHeaderPosition) {
        Integer id = getResourceID(fosTitle, resourceName, explHeaderPosition);
        Cursor cursor = mDB.rawQuery("SELECT count(*) " +
                "FROM commentTable " +
                "WHERE resourceId = '" + id
                + "';", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}

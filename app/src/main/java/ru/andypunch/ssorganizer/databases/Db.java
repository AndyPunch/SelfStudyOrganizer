package ru.andypunch.ssorganizer.databases;

/**
 * Db.java
 * <p>
 * class to create and manage database and tables
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Db {

    //db name an version
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;

    //all tables names
    static final String DB_MAIN_TABLE = "mainTable";
    static final String DB_RESOURCE_TABLE = "resourceTable";
    static final String DB_RUN_TABLE = "runTable";
    static final String DB_COMMENT_TABLE = "commentTable";


    //main table fields
    static final String COLUMN_ID = "_id";
    public static final String COLUMN_STUDY_FIELD_TITLE = "studyFieldTitle";
    public static final String COLUMN_STUDY_FIELD_DATE = "date";


    private static final String DB_CREATE =
            "create table " + DB_MAIN_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_STUDY_FIELD_TITLE + " text not null unique, " +
                    COLUMN_STUDY_FIELD_DATE + " integer" +
                    ");";


    //resource table
    static final String COLUMN_RESOURCE_ID = "_id";
    static final String COLUMN_RESOURCE_TYPE = "resourceType";
    static final String COLUMN_RESOURCE_SHORT_TITLE = "resourceShortTitle";
    static final String COLUMN_RESOURCE_FULL_TITLE = "resourceFullTitle";
    static final String COLUMN_FOS_REF_ID = "fosId";

    private static final String DB_RESOURCE_CREATE =
            "create table " + DB_RESOURCE_TABLE + "(" +
                    COLUMN_RESOURCE_ID + " integer primary key autoincrement, " +
                    COLUMN_RESOURCE_TYPE + " text, " +
                    COLUMN_RESOURCE_FULL_TITLE + " text, " +
                    COLUMN_RESOURCE_SHORT_TITLE + " text, " +
                    COLUMN_FOS_REF_ID + " integer, " +
                    "foreign key(" + COLUMN_FOS_REF_ID + ") references "
                    + DB_MAIN_TABLE +
                    "(" + COLUMN_ID + ") ON DELETE CASCADE" +
                    ");";


    //run table
    private static final String COLUMN_RESOURCE_RUN_ID = "_id";
    static final String COLUMN_RESOURCE_RUN_TIME = "resourceRunTime";
    static final String COLUMN_RESOURCE_RUN_COUNT = "resourceRunCount";
    static final String COLUMN_RESOURCE_GRADE_GOOD = "resourceGradeGood";
    static final String COLUMN_RESOURCE_GRADE_BAD = "resourceGradeBad";
    static final String COLUMN_RESOURCE_REF_RUN_ID = "resourceId";

    private static final String DB_RUN_CREATE =
            "create table " + DB_RUN_TABLE + "(" +
                    COLUMN_RESOURCE_RUN_ID + " integer primary key autoincrement, " +
                    COLUMN_RESOURCE_RUN_TIME + " integer, " +
                    COLUMN_RESOURCE_RUN_COUNT + " integer, " +
                    COLUMN_RESOURCE_GRADE_GOOD + " integer, " +
                    COLUMN_RESOURCE_GRADE_BAD + " integer, " +
                    COLUMN_RESOURCE_REF_RUN_ID + " integer, " +
                    "foreign key(" + COLUMN_RESOURCE_REF_RUN_ID + ") references "
                    + DB_RESOURCE_TABLE +
                    "(" + COLUMN_RESOURCE_ID + ") ON DELETE CASCADE" +
                    ");";


    //comment table
    private static final String COLUMN_RESOURCE_COMMENT_ID = "_id";
    public static final String COLUMN_RESOURCE_COMMENT_TIME = "resourceCommentTime";
    public static final String COLUMN_RESOURCE_COMMENT_BODY = "resourceCommentBody";
    static final String COLUMN_RESOURCE_REF_COMMENT_ID = "resourceId";


    private static final String DB_COMMENT_CREATE =
            "create table " + DB_COMMENT_TABLE + "(" +
                    COLUMN_RESOURCE_COMMENT_ID + " integer primary key autoincrement, " +
                    COLUMN_RESOURCE_COMMENT_TIME + " integer, " +
                    COLUMN_RESOURCE_COMMENT_BODY + " text, " +
                    COLUMN_RESOURCE_REF_COMMENT_ID + " integer, " +
                    "foreign key(" + COLUMN_RESOURCE_REF_COMMENT_ID + ") references "
                    + DB_RESOURCE_TABLE +
                    "(" + COLUMN_RESOURCE_ID + ") ON DELETE CASCADE" +
                    ");";

    private DBHelper mDBHelper;
    SQLiteDatabase mDB;

    private final Context mCtx;

    //конструктор класса
    Db(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DBHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // класс по созданию и управлению БД
    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase
                .CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
            db.execSQL(DB_RESOURCE_CREATE);
            db.execSQL(DB_RUN_CREATE);
            db.execSQL(DB_COMMENT_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        }
    }

}

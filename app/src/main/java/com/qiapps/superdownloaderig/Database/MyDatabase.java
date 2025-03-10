package com.qiapps.superdownloaderig.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase extends SQLiteOpenHelper {

    public static final String NAME_DATABASE = "DOWNLOAD_VIDEOS";
    public static final String TABLE_NAME = "TABLE_INSTAGRAM_RESOURCES";

    public static final String ID = "ID";
    public static final String TITLE = "TITLE";
    public static final String IS_VIDEO = "IS_VIDEO";
    public static final String URL = "URL";
    public static final String IMG_PROFILE = "IMG_PROFILE";
    public static final String USERNAME = "USERNAME";
    public static final String FILEPATH = "FILEPATH";
    public static final String INSTAGRAMPATH = "INSTAGRAMPATH";
    public static final String VIDEOURL = "VIDEOURL";
    public static final String DURATION = "DURATION";
    public static final String IS_SIDECAR = "IS_SIDECAR";


    private static MyDatabase INSTANCE;


    public MyDatabase(Context context) {
        super(context, NAME_DATABASE, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "'(" +
                "ID INTEGER ,PRIMARY_KEY AUTO_INCREMENT, " +
                TITLE + " VARCHAR, "+
                IS_VIDEO + " INTEGER, "+
                URL + " VARCHAR, "+
                IMG_PROFILE + " VARCHAR, "+
                USERNAME + " VARCHAR, "+
                INSTAGRAMPATH + " VARCHAR, "+
                VIDEOURL + " VARCHAR, "+
                DURATION + " INTEGER, "+
                FILEPATH+ " VARCHAR);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public synchronized static SQLiteDatabase getDatabaseReference(Context context) {
        if(INSTANCE !=null){
            return INSTANCE.getWritableDatabase();
        }else {
            INSTANCE = new MyDatabase(context);
            return INSTANCE.getWritableDatabase();
        }
    }
}

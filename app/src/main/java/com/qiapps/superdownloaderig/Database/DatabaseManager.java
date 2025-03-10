package com.qiapps.superdownloaderig.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.Model.InstagramResource;

public class DatabaseManager {

    public static void saveInstagramResource(Context context, InstagramResource instagramResource){

        SQLiteDatabase reference = MyDatabase.getDatabaseReference(context);

        ContentValues values = new ContentValues();
        values.put(MyDatabase.TITLE,instagramResource.getTitle());
        values.put(MyDatabase.IS_VIDEO,instagramResource.getIsVideo());
        values.put(MyDatabase.URL,instagramResource.getUrl());
        values.put(MyDatabase.IMG_PROFILE,instagramResource.getImg_profile());
        values.put(MyDatabase.USERNAME,instagramResource.getUsername());
        values.put(MyDatabase.FILEPATH,instagramResource.getFilepath());
        values.put(MyDatabase.INSTAGRAMPATH,instagramResource.getInstagramPath());
        values.put(MyDatabase.VIDEOURL,instagramResource.getVideo_url());
        values.put(MyDatabase.DURATION,instagramResource.getDuration());

        if(isInstagramPathExists(context,instagramResource.getInstagramPath())){
            String clause = MyDatabase.INSTAGRAMPATH + " = '" + instagramResource.getInstagramPath() + "'" + ";";
            reference.update(MyDatabase.TABLE_NAME,values,clause,null);
        }else {
            reference.insertOrThrow(MyDatabase.TABLE_NAME, null, values);
        }

    }

    public static ArrayList<InstagramResource> getAllInstagramResources(Context context){

        SQLiteDatabase reference = MyDatabase.getDatabaseReference(context);

        ArrayList<InstagramResource> resources = new ArrayList<>();
        String sql = "select * from " + MyDatabase.TABLE_NAME + " order by " + MyDatabase.ID + " desc;";
        Cursor cursor = reference.rawQuery(sql,null);
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {

                InstagramResource ir = new InstagramResource();
                ir.setId(cursor.getInt(cursor.getColumnIndex(MyDatabase.ID)));
                ir.setTitle(cursor.getString(cursor.getColumnIndex(MyDatabase.TITLE)));
                ir.setIsVideo(cursor.getInt(cursor.getColumnIndex(MyDatabase.IS_VIDEO)));
                ir.setUrl(cursor.getString(cursor.getColumnIndex(MyDatabase.URL)));
                ir.setImg_profile(cursor.getString(cursor.getColumnIndex(MyDatabase.IMG_PROFILE)));
                ir.setUsername(cursor.getString(cursor.getColumnIndex(MyDatabase.USERNAME)));
                ir.setFilepath(cursor.getString(cursor.getColumnIndex(MyDatabase.FILEPATH)));
                ir.setVideo_url(cursor.getString(cursor.getColumnIndex(MyDatabase.VIDEOURL)));
                ir.setInstagramPath(cursor.getString(cursor.getColumnIndex(MyDatabase.INSTAGRAMPATH)));
                ir.setDuration(cursor.getInt(cursor.getColumnIndex(MyDatabase.DURATION)));


                resources.add(ir);
            }while (cursor.moveToNext());
        }

        Collections.reverse(resources);
        cursor.close();

        return resources;
    }

    public static InstagramResource getInstagramResourceByFilePath(Context context,String path){

        SQLiteDatabase reference = MyDatabase.getDatabaseReference(context);

        InstagramResource ir = new InstagramResource();
        String sql = "select * from " + MyDatabase.TABLE_NAME +" where " + MyDatabase.FILEPATH + " = '" + path + "'" + ";";
        Cursor cursor = reference.rawQuery(sql,null);
        //Log.i("database","Recuperou objetivos do banco de dados");
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {

                ir.setId(cursor.getInt(cursor.getColumnIndex(MyDatabase.ID)));
                ir.setTitle(cursor.getString(cursor.getColumnIndex(MyDatabase.TITLE)));
                ir.setIsVideo(cursor.getInt(cursor.getColumnIndex(MyDatabase.IS_VIDEO)));
                ir.setUrl(cursor.getString(cursor.getColumnIndex(MyDatabase.URL)));
                ir.setImg_profile(cursor.getString(cursor.getColumnIndex(MyDatabase.IMG_PROFILE)));
                ir.setUsername(cursor.getString(cursor.getColumnIndex(MyDatabase.USERNAME)));
                ir.setFilepath(cursor.getString(cursor.getColumnIndex(MyDatabase.FILEPATH)));
                ir.setVideo_url(cursor.getString(cursor.getColumnIndex(MyDatabase.VIDEOURL)));
                ir.setInstagramPath(cursor.getString(cursor.getColumnIndex(MyDatabase.INSTAGRAMPATH)));
                ir.setDuration(cursor.getInt(cursor.getColumnIndex(MyDatabase.DURATION)));

            }while (cursor.moveToNext());
        }
        cursor.close();

        return ir;
    }


    public static void deleteByPath(Context context,String path){
        SQLiteDatabase reference = MyDatabase.getDatabaseReference(context);
        String whereClause = MyDatabase.FILEPATH + " = '" + path + "';";
        reference.delete(MyDatabase.TABLE_NAME,whereClause,null);
    }

    public static boolean isUrlExists(Context context, String url){
        SQLiteDatabase reference = MyDatabase.getDatabaseReference(context);
        InstagramResource ir = new InstagramResource();
        String sql = "select * from " + MyDatabase.TABLE_NAME +" where " + MyDatabase.INSTAGRAMPATH + " = '" + url + "'" + ";";
        Cursor cursor = reference.rawQuery(sql,null);
        //Log.i("database","Recuperou objetivos do banco de dados");
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {

                ir.setId(cursor.getInt(cursor.getColumnIndex(MyDatabase.ID)));
                ir.setTitle(cursor.getString(cursor.getColumnIndex(MyDatabase.TITLE)));
                ir.setIsVideo(cursor.getInt(cursor.getColumnIndex(MyDatabase.IS_VIDEO)));
                ir.setUrl(cursor.getString(cursor.getColumnIndex(MyDatabase.URL)));
                ir.setImg_profile(cursor.getString(cursor.getColumnIndex(MyDatabase.IMG_PROFILE)));
                ir.setUsername(cursor.getString(cursor.getColumnIndex(MyDatabase.USERNAME)));
                ir.setFilepath(cursor.getString(cursor.getColumnIndex(MyDatabase.FILEPATH)));
                ir.setVideo_url(cursor.getString(cursor.getColumnIndex(MyDatabase.VIDEOURL)));
                ir.setInstagramPath(cursor.getString(cursor.getColumnIndex(MyDatabase.INSTAGRAMPATH)));
                ir.setDuration(cursor.getInt(cursor.getColumnIndex(MyDatabase.DURATION)));

            }while (cursor.moveToNext());
        }else{
            return false;
        }
        cursor.close();
        return FileManager.isFileExists(context,ir.getFilepath().split(";")[0]);
    }

    public static boolean isInstagramPathExists(Context context, String url){
        SQLiteDatabase reference = MyDatabase.getDatabaseReference(context);
        InstagramResource ir = new InstagramResource();
        String sql = "select * from " + MyDatabase.TABLE_NAME +" where " + MyDatabase.INSTAGRAMPATH + " = '" + url + "'" + ";";
        Cursor cursor = reference.rawQuery(sql,null);
        //Log.i("database","Recuperou objetivos do banco de dados");
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do {

                ir.setId(cursor.getInt(cursor.getColumnIndex(MyDatabase.ID)));
                ir.setTitle(cursor.getString(cursor.getColumnIndex(MyDatabase.TITLE)));
                ir.setIsVideo(cursor.getInt(cursor.getColumnIndex(MyDatabase.IS_VIDEO)));
                ir.setUrl(cursor.getString(cursor.getColumnIndex(MyDatabase.URL)));
                ir.setImg_profile(cursor.getString(cursor.getColumnIndex(MyDatabase.IMG_PROFILE)));
                ir.setUsername(cursor.getString(cursor.getColumnIndex(MyDatabase.USERNAME)));
                ir.setFilepath(cursor.getString(cursor.getColumnIndex(MyDatabase.FILEPATH)));
                ir.setVideo_url(cursor.getString(cursor.getColumnIndex(MyDatabase.VIDEOURL)));
                ir.setInstagramPath(cursor.getString(cursor.getColumnIndex(MyDatabase.INSTAGRAMPATH)));
                ir.setDuration(cursor.getInt(cursor.getColumnIndex(MyDatabase.DURATION)));

            }while (cursor.moveToNext());
        }else{
            return false;
        }
        cursor.close();
        return true;
    }

}

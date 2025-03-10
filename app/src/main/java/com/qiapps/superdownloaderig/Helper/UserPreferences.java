package com.qiapps.superdownloaderig.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {

    private static final String NAME = "USER_PREFERENCES";
    private static final String PREMIUM = "PREMIUM";
    private static final String CONT = "CONT";
    private static final String CONT_EVENT_VIDEO = "CONT_EVENT_VIDEO";
    private static final String COUNT_ENTRADA_ADS = "COUNT_ENTRADA_ADS";
    private static final String COUNT_VIDEO_DOWNLOAD_ADS = "COUNT_VIDEO_DOWNLOAD_ADS";
    private static final String SHOW_MODAL_POLICY = "SHOW_MODAL_POLICY";



    public static boolean initUserPremium(Context context){
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREMIUM, true).apply();
        return true;

    }

    public static boolean isUserPremium(Context context){
        return context.getSharedPreferences(NAME,Context.MODE_PRIVATE).getBoolean(PREMIUM,false);
    }

    public static boolean resetUserPremium(Context context){
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREMIUM, false).apply();
        return true;
    }

    public static void addEventVideo(Context context){
        int i = getEventVideo(context) + 1;
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(CONT_EVENT_VIDEO, i).apply();
    }

    public static void resetEventVideo(Context context){
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(CONT_EVENT_VIDEO, 0).apply();
    }

    public static int getEventVideo(Context context){
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(CONT_EVENT_VIDEO,3);
    }

    public static boolean canShowVideoDownloadAds(Context context){
        int i = getEventVideo(context);
        int countVideoAds = getCountVideoDownloadAds(context);

        return i >= countVideoAds;
    }

    public static void addContScreen(Context context){
        int i = getContScreen(context) + 1;
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(CONT, i).apply();
    }

    public static int getContScreen(Context context){
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(CONT,0);
    }

    public static void changeCountEntradaAds(Context context,int cont){
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(COUNT_ENTRADA_ADS, cont).apply();
    }

    public static int getCountEntradaAds(Context context){
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(COUNT_ENTRADA_ADS,3);
    }

    public static void changeCountVideoDownloadAds(Context context,int cont){
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putInt(COUNT_VIDEO_DOWNLOAD_ADS, cont).apply();
    }

    public static int getCountVideoDownloadAds(Context context){
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getInt(COUNT_VIDEO_DOWNLOAD_ADS,3);
    }

    public static boolean isShowModalPolicy(Context context){
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE).getBoolean(SHOW_MODAL_POLICY,false);
    }

    public static void disableModalPolicy(Context context){
        SharedPreferences preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean(SHOW_MODAL_POLICY, true).apply();
    }


}

package com.qiapps.superdownloaderig.qiadsopenapp;

import android.content.Context;
import android.content.SharedPreferences;

public final class EventManager {

    private static final String SHARED_PREFERENCES_NAME = "EventManager";
    //private static final String COUNT = "ContadorEventos";

    //adicionar um evento positivo
    public static void addPositiveEvent(Context context,String eventkey) {
        int i = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(eventkey, 0);
        i++;
        SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(eventkey, i);
        editor.apply();
    }

    public static boolean isEventCountEnable(Context context,String eventkey, int count){
        int i = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(eventkey, 0);
        if (i == 0) return false;
        if(i>=count) {
            SharedPreferences.Editor editor = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
            editor.putInt(eventkey, 0);
            editor.apply();
            return true;
        }

        return false;
    }




}


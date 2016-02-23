package com.dev.thiago.ambientmonitoring.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by thiago on 23/02/16.
 */
public class MeasurerUtils {

    public static Boolean isAttached(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getBoolean("is_attached", false);
    }

    public static void setIsAttached(Context context, Boolean isAttached) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("is_attached", isAttached);

        editor.apply();
    }

    public static Integer getTrackedRoomId(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getInt("tracked_room", 0);
    }

    public static void setTrackedRoomId(Context context, Integer id) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("tracked_room", id);

        editor.apply();
    }
}

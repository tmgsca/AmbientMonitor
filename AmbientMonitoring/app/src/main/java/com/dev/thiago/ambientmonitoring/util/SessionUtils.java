package com.dev.thiago.ambientmonitoring.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dev.thiago.ambientmonitoring.model.Session;
import com.dev.thiago.ambientmonitoring.model.User;

import io.realm.Realm;

/**
 * Created by thiago on 22/02/16.
 */
public class SessionUtils {

    public static boolean isLoggedIn(Context context) {

        Realm realm = Realm.getInstance(context);

        Boolean isLoggedIn = !realm.allObjects(Session.class).isEmpty();

        realm.close();

        return isLoggedIn;
    }

    public static String getAuthHeader(Context context) {

        Realm realm = Realm.getInstance(context);

        Session session = realm.allObjects(Session.class).first();

        String auth = "Token token=" + session.getToken();

        realm.close();

        return auth;
    }

    public static User getLoggedUser(Context context, Realm realm) {

        User user = realm.allObjects(User.class).first();

        return user;
    }

    public static DeviceType getDeviceType(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String deviceTypeString = sharedPreferences.getString("device_type", null);

        if (deviceTypeString == null) {

            return null;

        } else if (DeviceType.valueOf(deviceTypeString) == DeviceType.MEASURER) {

            return DeviceType.MEASURER;

        } else {

            return DeviceType.CLIENT;
        }
    }

    public static void setDeviceType(Context context, DeviceType deviceType) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("device_type", deviceType.toString());

        editor.apply();
    }

    public static void clearDeviceType(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("device_type");

        editor.apply();
    }

    public static void clearPreferences(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear().apply();
    }
}

package com.unlp.tesis.steer.utils;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

/**
 * Created by Pedro on 08/08/2017.
 */

public class Preferences {

    static final String KEY_AUDIO_PREFERENCES= "audio_preferences";
    public static final String KEY_GEOFENCE_PREVIUOS_STATUS= "geofence_previous_status";
    public static final String KEY_GEOFENCE_STATUS= "geofence_status";
    public static final String KEY_GEOFENCE_STATUS_TRIGGERED_ID= "geofence_status_triggered_id";

    public static final String KEY_GEOFENCE_STATUS_IN= "geofence_status_in";
    public static final String KEY_GEOFENCE_STATUS_OUT= "geofence_status_out";
    public static final String KEY_GEOFENCE_STATUS_PAID= "geofence_status_paid";

    public static final String KEY_GEOFENCE_SHOWMESSAGE= "geofence_showmessage";
    /**
     * Get audio preference
     */
    public static boolean getAudioPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_AUDIO_PREFERENCES,false);
    }

    /**
     * Set audio on/off.
     */
    public static void setAudioPreference(Context context, boolean audioPreference) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_AUDIO_PREFERENCES, audioPreference)
                .apply();
    }

    public static void setGeofenceStatus(Context context, String value) {
        //Save the last status
        Preferences.setGeofencePreviousStatus(context, Preferences.getGeofenceStatus(context));
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_GEOFENCE_STATUS, value)
                .apply();
    }

    public static String getGeofenceStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_GEOFENCE_STATUS, "");
    }

    public static void setGeofencePreviousStatus(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_GEOFENCE_PREVIUOS_STATUS, value)
                .apply();
    }

    public static String getGeofencePreviousStatus(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_GEOFENCE_PREVIUOS_STATUS, "");
    }


    public static void setGeofenceStatusTriggeredId(Context context, String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(KEY_GEOFENCE_STATUS_TRIGGERED_ID, value)
                .apply();
    }

    public static String getGeofenceStatusTriggeredId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_GEOFENCE_STATUS_TRIGGERED_ID, "");
    }

    public static void setGeofenceShowmessage(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(KEY_GEOFENCE_SHOWMESSAGE, value)
                .apply();
    }

    public static boolean getGeofenceShowmessage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_GEOFENCE_SHOWMESSAGE, false);
    }
}

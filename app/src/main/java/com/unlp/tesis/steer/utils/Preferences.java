package com.unlp.tesis.steer.utils;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

/**
 * Created by Pedro on 08/08/2017.
 */

public class Preferences {

    static final String KEY_AUDIO_PREFERENCES= "audio_preferences";

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

}

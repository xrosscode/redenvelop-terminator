package com.xrosscode.plugin.wechat.redenvelop;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author johnsonlee
 */
public class Preferences {

    public static final String PREFERENCES_NAME = "preferences";

    public static final String PREFERENCES_TTS = "preference.tts";

    private final SharedPreferences mSharedPreferences;

    public Preferences(final Context context) {
        this.mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public boolean isTtsEnabled() {
        return this.mSharedPreferences.getBoolean(PREFERENCES_TTS, false);
    }

}

package com.xrosscode.plugin.wechat.redenvelop;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author johnsonlee
 */
public class Preferences {

    public static final String PREFERENCES_NAME = "preferences";

    public static final String PREFERENCES_AUTO_GO_HOME = "preference.autogohome";

    public static final String PREFERENCES_AUTO_GO_HOME_DELAY_TIME = "preference.autogohome.delaytime";

    private final SharedPreferences mSharedPreferences;

    public Preferences(final Context context) {
        this.mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, 0);
    }

    public boolean autoGoHome() {
        return this.mSharedPreferences.getBoolean(PREFERENCES_AUTO_GO_HOME, false);
    }

    public long getAutoGoHomeDelayTime() {
        String s = mSharedPreferences.getString(PREFERENCES_AUTO_GO_HOME_DELAY_TIME, "5");
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return 5L;
        }
    }

}

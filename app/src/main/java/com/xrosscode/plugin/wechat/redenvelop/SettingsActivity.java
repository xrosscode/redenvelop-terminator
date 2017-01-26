package com.xrosscode.plugin.wechat.redenvelop;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author johnsonlee
 */
public class SettingsActivity extends PreferenceActivity {

    private Preferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getPreferenceManager().setSharedPreferencesName(Preferences.PREFERENCES_NAME);
        super.addPreferencesFromResource(R.xml.preferences);
        this.mPreferences = new Preferences(this);
    }

}

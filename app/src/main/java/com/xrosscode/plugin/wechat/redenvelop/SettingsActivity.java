package com.xrosscode.plugin.wechat.redenvelop;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * @author johnsonlee
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getPreferenceManager().setSharedPreferencesName(Preferences.PREFERENCES_NAME);
        super.addPreferencesFromResource(R.xml.preferences);

        final Preferences preferences = new Preferences(this);
        final Preference prefAutoGoHome = findPreference(Preferences.PREFERENCES_AUTO_GO_HOME);
        final Preference prefAutoGoHomeDelayTime = findPreference(Preferences.PREFERENCES_AUTO_GO_HOME_DELAY_TIME);

        prefAutoGoHome.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                final Boolean enabled = Boolean.parseBoolean(String.valueOf(newValue));
                prefAutoGoHomeDelayTime.setEnabled(enabled);
                return true;
            }
        });

        prefAutoGoHomeDelayTime.setEnabled(preferences.autoGoHome());
        prefAutoGoHomeDelayTime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                try {
                    return 0 < Long.parseLong(String.valueOf(newValue));
                } catch (final NumberFormatException e) {
                    return false;
                }
            }
        });
    }

}

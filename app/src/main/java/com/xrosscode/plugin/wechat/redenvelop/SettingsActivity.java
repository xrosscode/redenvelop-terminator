package com.xrosscode.plugin.wechat.redenvelop;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author johnsonlee
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.addPreferencesFromResource(R.xml.preferences);
    }

}

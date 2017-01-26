package com.xrosscode.plugin.wechat.redenvelop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * @author johnsonlee
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private TextView mStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main_activity);
        this.mStatusView = (TextView) findViewById(R.id.main_activity_status);
        this.mStatusView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final boolean available = RedEnvelopTerminatorService.available(this);
        final int label = available
                ? R.string.label_of_main_activity_red_envelop_terminator_service_already_started
                : R.string.label_of_main_activity_tap_to_start_red_envelop_terminator_service;
        this.mStatusView.setText(label);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_activity_status:
                RedEnvelopTerminatorService.enableIfNecessary(this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_activity_options_menu_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

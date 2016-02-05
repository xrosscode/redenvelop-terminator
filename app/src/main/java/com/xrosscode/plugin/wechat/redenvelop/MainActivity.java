package com.xrosscode.plugin.wechat.redenvelop;

import android.app.Activity;
import android.os.Bundle;
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
        this.mStatusView.setEnabled(!available);
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
}

package com.xrosscode.plugin.wechat.redenvelop;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author johnsonlee
 */
public class RedEnvelopTerminatorService extends AccessibilityService {

    static final String TAG = RedEnvelopTerminatorService.class.getSimpleName();

    public static boolean available(final Context context) {
        final AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        final List<AccessibilityServiceInfo> asis = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
        if (null == asis || asis.isEmpty()) {
            return false;
        }

        for (final AccessibilityServiceInfo asi : asis) {
            if (RedEnvelopTerminatorService.class.getName().equals(asi.getResolveInfo().serviceInfo.name)) {
                return true;
            }
        }

        return false;
    }

    public static void enableIfNecessary(final Activity context) {
        if (available(context))
            return;

        context.startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    private final Handler mHandler = new Handler();

    private Preferences mPreferences;

    private PowerManager.WakeLock mWakeLock;

    private boolean mFirstOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();

        final Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.label_of_main_activity_red_envelop_terminator_service_already_started))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.accessibility_service_description))
                .setAutoCancel(false)
                .build();
        startForeground(R.string.accessibility_service_description, notification);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, getClass().getName());
        this.mWakeLock.acquire();
        this.mPreferences = new Preferences(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != this.mWakeLock && this.mWakeLock.isHeld()) {
            this.mWakeLock.release();
            this.mWakeLock = null;
       }
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        Log.i(TAG, event.toString());

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED: {
                final List<CharSequence> text = event.getText();
                if (null == text || text.isEmpty()) {
                    return;
                }

                // 凡是带有 [微信红包] 字样的消息，通通打开
                if (text.toString().contains("[微信红包]")) {
                    openRedEnvelopFromNotification(event);
                }
                break;
            }
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: {
                openRedEnvelop(event);
                break;
            }
        }
    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, R.string.toast_red_envelop_terminator_service_interrupted, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this, R.string.toast_red_envelop_terminator_service_connected, Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开内容中带有 <b>[微信红包]</b> 字样的消息通知
     *
     * @param event
     */
    private void openRedEnvelopFromNotification(final AccessibilityEvent event) {
        final Parcelable data = event.getParcelableData();
        if (!(data instanceof Notification)) {
            return;
        }

        this.mFirstOpen = true;
        final Notification notification = (Notification) data;

        try {
            notification.contentIntent.send();
        } catch (final PendingIntent.CanceledException e) {
            Toast.makeText(this, R.string.toast_open_notification_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 抢红包
     *
     * 0. 从通知栏打开微信红包的消息，跳转到聊天界面
     * 1. 从聊天历史中找到红包并点开
     * 3. 在拆红包界面找到“抢”按钮，拆红包
     *
     * @param event
     */
    private void openRedEnvelop(final AccessibilityEvent event) {
        final String clazz = String.valueOf(event.getClassName());
        final AccessibilityNodeInfo root = getRootInActiveWindow();

        if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(clazz)) { // 抢红包界面
            final List<AccessibilityNodeInfo> nodes = findAccessibilityNodeInfoByClassName(root, "android.widget.Button");
            for (int i = nodes.size() - 1; i >= 0; i--) {
                final AccessibilityNodeInfo node = nodes.get(i);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            this.mFirstOpen = false;

            // 抢完红包后退到 HOME，这样才会有消息通知
            if (this.mPreferences.autoGoHome()) {
                this.mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }, this.mPreferences.getAutoGoHomeDelayTime() * 1000L);
            }
        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(clazz)) { // 红包详情界面
            // TODO
        } else if ("com.tencent.mm.ui.LauncherUI".equals(clazz)) { // 聊天界面
            findRedEnvelopsFromChatHistory(root);
        }
    }

    /**
     * 根据 UI 组件的类名查找
     *
     * @param root
     * @param clazzName
     * @return
     */
    private List<AccessibilityNodeInfo> findAccessibilityNodeInfoByClassName(final AccessibilityNodeInfo root, final String clazzName) {
        final List<AccessibilityNodeInfo> nodes = new ArrayList<AccessibilityNodeInfo>();
        final Stack<AccessibilityNodeInfo> stack = new Stack<AccessibilityNodeInfo>();
        stack.push(root);

        while (!stack.isEmpty()) {
            final AccessibilityNodeInfo node = stack.pop();

            if (node.getClassName().equals(clazzName)) {
                nodes.add(node);
            }

            final int n = node.getChildCount();
            for (int i = 0; i < n; i++) {
                stack.push(node.getChild(i));
            }
        }

        return nodes;
    }

    /**
     * 从聊天记录中找红包，并点开
     *
     * @param root
     */
    private void findRedEnvelopsFromChatHistory(final AccessibilityNodeInfo root) {
        final List<AccessibilityNodeInfo> redEnvelops = root.findAccessibilityNodeInfosByText("领取红包");
        if (null == redEnvelops || redEnvelops.isEmpty()) {
            return;
        }

        // 点开最近收到的红包
        final AccessibilityNodeInfo parent = redEnvelops.get(redEnvelops.size() - 1).getParent();
        if (this.mFirstOpen) {
            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            this.mFirstOpen = true;
        }
    }

}

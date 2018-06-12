package com.ehi.enterprise.android.ui.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;

public class NotificationBootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            for (EHINotification notification : EHINotificationManager.getInstance().getAllNotifications()) {
                notification.schedule(context, EHINotificationManager.getInstance());
            }
        }
    }
}

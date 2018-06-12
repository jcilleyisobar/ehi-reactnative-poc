package com.ehi.enterprise.android.utils.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.notification.EHINotification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EHINotificationManager extends BaseDataManager {
    public static final String NOTIFICATIONS = "Notifications";
    public static final String SHOULD_SHOW_NOTIFICATION_PROMPT = "SHOULD_SHOW_NOTIFICATION_PROMPT";
    public static final String WAS_SHOWN_PREFIX = "SHOWN_";

    public static final String DEBUG_CHANNEL = "DEBUG_CHANNEL";
    public static final String RESERVATION_CHANNEL = "RESERVATION_CHANNEL";

    private boolean isPushNotificationEnabled;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isLoggedIn = intent.getBooleanExtra(LoginManager.LOGGED_IN, false);
            if (!isLoggedIn) {
                removeAllNotifications();
            }
        }
    };

    private static EHINotificationManager sInstance;

    protected EHINotificationManager() {
    }

    @Override
    public void initialize(@NonNull final Context context) {
        super.initialize(context);
        sInstance = this;
        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mBroadcastReceiver, new IntentFilter(LoginManager.LOGIN_EVENT));
        isPushNotificationEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createChannels(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels(Context context) {
        if (BuildConfig.DEBUG) {
            createChannel(context, DEBUG_CHANNEL,
                    R.string.notification_channel_debug_name,
                    0);
        }
        createChannel(context, RESERVATION_CHANNEL,
                R.string.notification_channel_reservation_name,
                R.string.notification_channel_reservation_description);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel(Context context, String id, int name, int description) {
        NotificationChannel channel = new NotificationChannel(
                id, context.getString(name),
                NotificationManager.IMPORTANCE_DEFAULT);
        if (description != 0) {
            channel.setDescription(context.getString(description));
        }
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static EHINotificationManager getInstance() {
        if (sInstance == null) {
            sInstance = new EHINotificationManager();
        }

        return sInstance;
    }

    public boolean shouldShowNotificationPrompt() {
        return getBoolean(SHOULD_SHOW_NOTIFICATION_PROMPT, true);
    }

    public void setShouldShowNotificationPrompt(boolean shouldShow) {
        set(SHOULD_SHOW_NOTIFICATION_PROMPT, shouldShow);
    }

    public void addNotification(EHINotification notification) {
        set(notification.getId(), notification);
    }

    public EHINotification getNotification(String confirmationNumber) {
        return getEhiModel(confirmationNumber, EHINotification.class);
    }

    public void removeNotification(EHINotification notification) {
        remove(notification.getId());
    }

    public void removeAllNotifications() {
        for (EHINotification notification : getAllNotifications()) {
            notification.unschedule(getContext(), getInstance());
        }
    }

    public void setNotificationWasShown(EHINotification notification) {
        set(WAS_SHOWN_PREFIX + notification.getId(), true);
    }

    public boolean wasNotificationShown(EHINotification notification) {
        return getBoolean(WAS_SHOWN_PREFIX + notification.getId(), false);
    }

    public List<EHINotification> getAllNotifications() {
        final Map<String, ?> all = getAll();
        final List<EHINotification> allNotifications = new ArrayList<>();
        for (String key : all.keySet()) {
            if ((!SHOULD_SHOW_NOTIFICATION_PROMPT.equals(key)
                    && !key.contains(WAS_SHOWN_PREFIX))) {
                allNotifications.add(getNotification(key));
            }
        }

        return allNotifications;
    }

    @Override
    protected String getSharedPreferencesName() {
        return NOTIFICATIONS;
    }

    public boolean isPushNotificationEnabled() {
        return isPushNotificationEnabled;
    }

}

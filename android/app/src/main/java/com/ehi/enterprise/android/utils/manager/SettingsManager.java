package com.ehi.enterprise.android.utils.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.ui.notification.NotificationBootReceiver;

public class SettingsManager extends BaseDataManager {
    private static final String SETTINGS_MANAGER_ID = "SETTINGS_MANAGER_ID";
    private static final String SETTINGS_TRACKING_ID = "SETTINGS_TRACKING_ID";
    private static final String ANALYTICS_TRACKING_KEY = "SETTINGS_ANALYTICS_TRACKING_ID";
    private static final String SETTINGS_SEARCH_HISTORY_ID = "SETTINGS_SEARCH_HISTORY_ID";
    public static final String PICKUP_NOTIFICATION_TIME = "PICKUP_NOTIFICATION_TIME";
    public static final String RETURN_NOTIFICATION_TIME = "RETURN_NOTIFICATION_TIME";
    public static final String ENTERPRISE_RENTAL_ASSISTANT = "ENTERPRISE_RENTAL_ASSISTANT";



    private static SettingsManager mManager;


    protected SettingsManager() {
    }

    @Override
    protected String getSharedPreferencesName() {
        return SETTINGS_MANAGER_ID;
    }

    @NonNull
    public static SettingsManager getInstance() {
        if (mManager == null) {
            mManager = new SettingsManager();
        }
        return mManager;
    }

    public void setAutoSaveEnabled(boolean enabled) {
        set(SETTINGS_TRACKING_ID, enabled);
    }

    public boolean isAnalyticsEnabled() {
        return getBoolean(ANALYTICS_TRACKING_KEY, true);
    }

    public void setAnalyticsEnabled(boolean enable) {
        set(ANALYTICS_TRACKING_KEY, enable);
    }


    public void setSearchHistoryEnabled(boolean enabled) {
        set(SETTINGS_SEARCH_HISTORY_ID, enabled);
    }

    public boolean isSearchHistoryEnabled() {
        return getBoolean(SETTINGS_SEARCH_HISTORY_ID, true);
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        mManager = this;
    }

    public boolean isAutoSaveEnabled(boolean defaultValue) {
        return getBoolean(SETTINGS_TRACKING_ID, defaultValue);
    }

    public void setPickupNotificationTime(EHINotification.NotificationTime notificationTime) {
        set(PICKUP_NOTIFICATION_TIME, notificationTime.name());
        enableBootReceiver(notificationTime != EHINotification.NotificationTime.OFF);
    }

    public void setReturnNotificationTime(EHINotification.NotificationTime returnNotificationTime) {
        set(RETURN_NOTIFICATION_TIME, returnNotificationTime.name());
        enableBootReceiver(returnNotificationTime != EHINotification.NotificationTime.OFF);
    }

    private void enableBootReceiver(final boolean enable) {
        //enable boot receiver if push notifications are being scheduled
        if (enable) {
            ComponentName receiver = new ComponentName(getContext(), NotificationBootReceiver.class);
            PackageManager pm = getContext().getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);


        } else {
            ComponentName receiver = new ComponentName(getContext(), NotificationBootReceiver.class);
            PackageManager pm = getContext().getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }


    public EHINotification.NotificationTime getPickupNotificationTime() {
        return EHINotification.NotificationTime.valueOf(getString(PICKUP_NOTIFICATION_TIME, EHINotification.NotificationTime.OFF.name()));
    }

    public EHINotification.NotificationTime getReturnNotificationTime() {
        return EHINotification.NotificationTime.valueOf(getString(RETURN_NOTIFICATION_TIME, EHINotification.NotificationTime.OFF.name()));
    }

    public void setEnterpriseRentalAssistant(boolean enabled) {
        set(ENTERPRISE_RENTAL_ASSISTANT, enabled);
    }

    public boolean isEnterpriseRentalAssistantEnabled() {
        return getBoolean(ENTERPRISE_RENTAL_ASSISTANT, false);
    }

}

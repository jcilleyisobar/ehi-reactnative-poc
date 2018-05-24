package com.ehi.enterprise.android.utils.manager;


import com.localytics.android.Localytics;

public class PushNotificationManager {

    private static PushNotificationManager instance = new PushNotificationManager();

    private PushNotificationManager() {}

    public static PushNotificationManager getInstance() {
        if (instance == null) {
            instance = new PushNotificationManager();
        }
        return instance;
    }

    public void initialize() {
        Localytics.registerPush();
    }

}

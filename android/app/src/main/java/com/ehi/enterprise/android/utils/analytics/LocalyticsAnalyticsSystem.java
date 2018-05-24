package com.ehi.enterprise.android.utils.analytics;

import android.app.Activity;
import android.app.Application;

import com.ehi.enterprise.android.utils.DLog;
import com.localytics.android.Localytics;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;

import java.util.HashMap;
import java.util.Map;

public class LocalyticsAnalyticsSystem implements IAnalyticsSystem {

    private static final String TAG = "LocalyticsAnalyticsSystem";

    private static String sPreviousScreen;
    private static String sCurrentScreen;
    private static String sCurrentScreenUrl;
    private static String sPreviousScreenUrl;

    @Override
    public void initialize(Application application) {
        application.registerActivityLifecycleCallbacks(new LocalyticsActivityLifecycleCallbacks(application));
    }

    @Override
    public void initialize(Activity activity) {
        //localytics should be initialised on app level so nothing should be here
    }

    @Override
    public void setCustomDimension(EHIAnalyticsEvent event) {
        try {
            Map<Integer, String> dimensions = event.getCustomDimensionsDictionary();
            for (Map.Entry<Integer, String> e : dimensions.entrySet()) {
                Localytics.setCustomDimension(e.getKey(), e.getValue());
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    @Override
    public void tagScreen(EHIAnalyticsEvent event) {
        try {
            final String eventName = generateEventName(event);
            if (sCurrentScreen == null
                    || !sCurrentScreen.equals(event.getScreen())) {
                sPreviousScreen = sCurrentScreen;
                sCurrentScreen = eventName;
            }

            if (sCurrentScreenUrl == null
                    || !sCurrentScreenUrl.equals(event.getScreenUrl())) {
                sPreviousScreenUrl = sCurrentScreenUrl;
                sCurrentScreenUrl = event.getScreenUrl();
            }

            Localytics.tagScreen(generateEventName(event));
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    @Override
    public void tagEvent(EHIAnalyticsEvent event) {
        try {
            String eventName = generateEventName(event);
            populateBaseDictionary(event);

            if (event.getEventDictionary() != null) {
                if (event.getCustomerValue() == 0) {
                    Localytics.tagEvent(eventName, event.getEventDictionary());
                } else {
                    Localytics.tagEvent(eventName, event.getEventDictionary(), event.getCustomerValue());
                }
                DLog.d(TAG, String.format("Event: %s Dictionary: %s", eventName, event.getEventDictionary().toString()));
            } else {
                Localytics.tagEvent(eventName);
                DLog.d(TAG, String.format("Event: %s", eventName));
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    @Override
    public void tagMacroEvent(EHIAnalyticsEvent event) {
        try {
            String eventName = event.getMacroEvent();
            populateBaseDictionary(event);

            if (event.getEventDictionary() != null) {
                Localytics.tagEvent(eventName, event.getEventDictionary());
            } else {
                Localytics.tagEvent(eventName);
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    @Override
    public void forceUpload() {
        Localytics.upload();
    }

    private String generateEventName(EHIAnalyticsEvent event) throws NullPointerException {
        StringBuilder bld = new StringBuilder();

        if (event.getScreen() != null) {
            bld.append(event.getScreen());
        } else {
            throw new NullPointerException("No screen was defined when trying to tag an event");
        }

        if (event.getState() != null) {
            bld.append(":");
            bld.append(event.getState());
        }

        if (event.getMotion() != null && event.getAction() != null) {
            bld.append(":");
            bld.append(event.getMotion());
            bld.append(event.getAction());
        }
        return bld.toString();
    }

    private void populateBaseDictionary(EHIAnalyticsEvent event) {
        Map<String, String> originalDictionary = event.getEventDictionary();
        if (originalDictionary == null) {
            originalDictionary = new HashMap<>();
        }
        if (sPreviousScreen != null) {
            originalDictionary.put("previousScreenName", sPreviousScreen);
        }
        if (sCurrentScreen != null) {
            originalDictionary.put("currentScreenName", sCurrentScreen);
        }
        if (sCurrentScreenUrl != null) {
            originalDictionary.put("screenUrl", sCurrentScreenUrl);
        }
        if (sPreviousScreenUrl != null) {
            originalDictionary.put("previousScreenUrl", sPreviousScreenUrl);
        }
        if (event.getMotion() != null) {
            originalDictionary.put("screenActionType", event.getMotion());
        }
        if (event.getAction() != null) {
            originalDictionary.put("screenActionName", event.getAction());
        }
        if (originalDictionary.containsKey("previousScreenUrl")
                && originalDictionary.containsKey("errorCode")
                && originalDictionary.containsKey("correlationId")) {
            originalDictionary.put("errorData", originalDictionary.get("correlationId")
                    + ":"
                    + originalDictionary.get("errorCode")
                    + ":"
                    + originalDictionary.get("previousScreenUrl"));
        }
        event.addEventDictionary(originalDictionary);
    }

}
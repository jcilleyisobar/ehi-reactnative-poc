package com.ehi.enterprise.android.utils.analytics;

import android.app.Activity;
import android.app.Application;

import com.appsee.Appsee;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.utils.DLog;

import java.util.Map;


public class AppSeeAnalyticsSystem implements IAnalyticsSystem {

    private static final String TAG = "AppSeeAnalyticsSystem";

    @Override
    public void initialize(Application app) {
        //app see initialization should be on activity level.
    }

    @Override
    public void initialize(Activity activity) {
        Appsee.start(Settings.APPSEE_KEY);
    }

    @Override
    public void setCustomDimension(EHIAnalyticsEvent event) {
        //nothing here
    }

    @Override
    public void tagScreen(EHIAnalyticsEvent event) {
        //tracking only events that tracked in onResume() so Screen+State
        try {
            if (event.getScreen() != null
                    && event.getState() != null
                    && event.getMotion() == null
                    && event.getAction() == null) {
                Appsee.startScreen(generateEventName(event));
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }

    }

    @Override
    public void tagEvent(EHIAnalyticsEvent event) {
        //nothing here
        try {
            if (event.getScreen() != null
                    && event.getState() != null
                    && event.getMotion() != null
                    && event.getAction() != null) {
                Appsee.addEvent(generateEventName(event), (Map) event.getEventDictionary());
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    @Override
    public void tagMacroEvent(EHIAnalyticsEvent event) {
        //nothing here
        try {
            String eventName = event.getMacroEvent();

            if (eventName != null) {
                Appsee.addEvent(eventName, (Map) event.getEventDictionary());
            }
        } catch (Exception e) {
            DLog.w(TAG, e);
        }
    }

    @Override
    public void forceUpload() {
        Appsee.upload();
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
}

package com.ehi.enterprise.android.utils.manager;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;

import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.analytics.AppSeeAnalyticsSystem;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IAnalyticsSystem;
import com.ehi.enterprise.android.utils.analytics.LocalyticsAnalyticsSystem;

import java.util.LinkedList;
import java.util.List;

public class AnalyticsManager implements IAnalyticsSystem {

    private static final String TAG = "AnalyticsManager";

    private static AnalyticsManager mManager;

    private List<IAnalyticsSystem> mAnalytics = new LinkedList<>();

    /**
     * Storing last tracked event for smart tracking purpose
     */
    private EHIAnalyticsEvent mLastEvent;

    private AnalyticsManager() {
    }

    public static AnalyticsManager getInstance() {
        if (mManager == null) {
            mManager = new AnalyticsManager();
        }
        return mManager;
    }

    @Override
    public void initialize(Application application) {
        //localytics
        IAnalyticsSystem localytics = new LocalyticsAnalyticsSystem();
        localytics.initialize(application);
        mAnalytics.add(localytics);

        //appsee custom screen tracking
        IAnalyticsSystem appSee = new AppSeeAnalyticsSystem();
        appSee.initialize(application);
        mAnalytics.add(appSee);

        //add more analytics systems here
    }

    @Override
    public void initialize(Activity activity) {
        for (IAnalyticsSystem system : mAnalytics) {
            system.initialize(activity);
        }
    }

    @Override
    public void setCustomDimension(EHIAnalyticsEvent event) {
        if (!SettingsManager.getInstance().isAnalyticsEnabled()) {
            return;
        }
        if (handleSmartTracking(event)) {
            for (IAnalyticsSystem system : mAnalytics) {
                system.setCustomDimension(event);
            }
        }
    }

    @Override
    public void tagScreen(EHIAnalyticsEvent event) {
        if (!SettingsManager.getInstance().isAnalyticsEnabled()) {
            return;
        }
        if (handleSmartTracking(event)) {
            for (IAnalyticsSystem system : mAnalytics) {
                system.tagScreen(event);
            }
        }
    }

    @Override
    public void tagEvent(EHIAnalyticsEvent event) {
        if (!SettingsManager.getInstance().isAnalyticsEnabled()) {
            return;
        }
        if (handleSmartTracking(event)) {
            for (IAnalyticsSystem system : mAnalytics) {
                system.tagEvent(event);
            }
        }
    }

    @Override
    public void tagMacroEvent(EHIAnalyticsEvent event) {
        if (!SettingsManager.getInstance().isAnalyticsEnabled()) {
            return;
        }
        if (handleSmartTracking(event)) {
            for (IAnalyticsSystem system : mAnalytics) {
                system.tagMacroEvent(event);
            }
        }
    }

    @Override
    public void forceUpload() {
        for (IAnalyticsSystem system : mAnalytics) {
            system.forceUpload();
        }
    }

    private boolean handleSmartTracking(EHIAnalyticsEvent event) {
        if (event.isSmartTrackAction()) {
            if (mLastEvent != null
                    && !TextUtils.isEmpty(mLastEvent.getScreen())
                    && !TextUtils.isEmpty(mLastEvent.getScreenUrl())) {
                event.screen(mLastEvent.getScreen(), mLastEvent.getScreenUrl())
                        .state(mLastEvent.getState());
                DLog.w(TAG, "Smart action tracked, copying SCREEN=["
                        + mLastEvent.getScreen()
                        + "|"
                        + mLastEvent.getScreenUrl()
                        + "] and STATE=["
                        + mLastEvent.getState()
                        + "] to smart event.");
            } else {
                DLog.e(TAG, "");
                return false;
            }
        }
        mLastEvent = event;
        return true;
    }
}

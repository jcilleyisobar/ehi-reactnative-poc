package com.ehi.enterprise.android.utils.analytics;

import android.app.Activity;
import android.app.Application;

public interface IAnalyticsSystem {

    void initialize(Application app);

    void initialize(Activity activity);

    void setCustomDimension(EHIAnalyticsEvent event);

    void tagScreen(EHIAnalyticsEvent event);

    void tagEvent(EHIAnalyticsEvent event);

    void tagMacroEvent(EHIAnalyticsEvent event);

    void forceUpload();
}

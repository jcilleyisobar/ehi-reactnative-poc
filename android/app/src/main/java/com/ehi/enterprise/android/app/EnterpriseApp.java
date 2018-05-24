package com.ehi.enterprise.android.app;

import android.support.multidex.MultiDexApplication;

import com.crittercism.app.Crittercism;
import com.ehi.enterprise.android.app.stetho.StethoInjector;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.manager.AnalyticsManager;
import com.ehi.enterprise.android.utils.manager.ConfigFeedManager;
import com.ehi.enterprise.android.utils.manager.DataPassManager;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.ehi.enterprise.android.utils.manager.ForeSeeSurveyManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LocationManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.PushNotificationManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;
import com.ehi.enterprise.android.utils.manager.SettingsManager;
import com.ehi.enterprise.android.utils.manager.SupportInfoManager;

public class EnterpriseApp extends MultiDexApplication {
	public void onCreate() {
		super.onCreate();
		if (Settings.LOG_CRASHES) {
			Crittercism.initialize(getApplicationContext(), Settings.CRITTERCISM_KEY);
		}

        StethoInjector.injectStetho(this);

		DataPassManager.getInstance().initialize(this);
		LocalDataManager.getInstance().initialize(this);
		ReservationManager.getInstance().initialize(this);
		LocationManager.getInstance().initialize(this);
		LoginManager.getInstance().initialize(this);
		SettingsManager.getInstance().initialize(this);
		SupportInfoManager.getInstance().initialize(this);
		ConfigFeedManager.getInstance().initialize(this);
        DateUtilManager.getInstance().initialize(this);
        EHINotificationManager.getInstance().initialize(this);
        GeofenceManager.getInstance().initialize(this, false);

		AnalyticsManager.getInstance().initialize(this);
		EHIAnalyticsEvent
				.create()
				.addCustomDimensions(EHIAnalyticsDictionaryUtils.customDimensions());

		ForeSeeSurveyManager.getInstance().initialize(this);
		PushNotificationManager.getInstance().initialize();
	}
}

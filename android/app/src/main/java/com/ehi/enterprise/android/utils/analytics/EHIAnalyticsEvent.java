package com.ehi.enterprise.android.utils.analytics;

import android.support.annotation.NonNull;

import com.crittercism.app.Crittercism;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.utils.manager.AnalyticsManager;

import java.util.HashMap;
import java.util.Map;

public class EHIAnalyticsEvent {

	private String mMacroEvent;
	private String mScreen;
	private String mScreenUrl;
	private String mState;
	private String mMotion;
	private String mAction;
	private int mCustomerValue = 0;
	private Map<String, String> mEventDictionary = new HashMap<>();
	private Map<Integer, String> mCustomDimensionsDictionary = new HashMap<>();

	/**
	 * If flag is @true no screen and state necessary, action will be tracked with latest screen and state
	 * that passed through analytics layer (e.g. tracked within current screen).
	 * */
	private boolean mSmartTrackAction = false;

	public static EHIAnalyticsEvent create() {
		return new EHIAnalyticsEvent();
	}

	public EHIAnalyticsEvent screen(@NonNull String screen, @NonNull String screenUrl) {
		mScreen = screen;
		mScreenUrl = screenUrl;
		return this;
	}

	public EHIAnalyticsEvent state(@NonNull String state) {
		mState = state;
		return this;
	}

	public EHIAnalyticsEvent action(@NonNull String motion, @NonNull String action) {
		mMotion = motion;
		mAction = action;
		return this;
	}

	public EHIAnalyticsEvent macroEvent(@NonNull String eventName) {
		mMacroEvent = eventName;
		return this;
	}

	public EHIAnalyticsEvent addDictionary(@NonNull Map<String, String> map) {
		mEventDictionary.putAll(map);
		return this;
	}

	public EHIAnalyticsEvent addCustomDimensions(@NonNull Map<Integer, String> map) {
		mCustomDimensionsDictionary.putAll(map);
		AnalyticsManager.getInstance().setCustomDimension(this);
		return this;
	}

	public EHIAnalyticsEvent addCustomerValues(int values) {
		mCustomerValue = values;
		return this;
	}

	public EHIAnalyticsEvent smartTrackAction(boolean smartTrackEnabled) {
		mSmartTrackAction = smartTrackEnabled;
		return this;
	}

	public EHIAnalyticsEvent tagScreen() {
		AnalyticsManager.getInstance().tagScreen(this);
		if(Settings.LOG_CRASHES){
			String critterCrumb = new StringBuilder("[ Screen = ")
					.append(mScreenUrl)
					.append(", State = ")
					.append(mState)
					.append(", Action = ")
					.append(mAction )
					.append(", MacroEvent = ")
					.append(mMacroEvent)
					.append(", Motion = ")
					.append(mMotion)
					.append("]")
					.toString();
			Crittercism.leaveBreadcrumb(critterCrumb);
		}
		return this;
	}

	public EHIAnalyticsEvent tagEvent() {
		AnalyticsManager.getInstance().tagEvent(this);
		return this;
	}

	public EHIAnalyticsEvent tagMacroEvent() {
		AnalyticsManager.getInstance().tagMacroEvent(this);
		return this;
	}

	public EHIAnalyticsEvent addEventDictionary(Map<String, String> eventDictionary) {
		mEventDictionary.putAll(eventDictionary);
		return this;
	}

	public String getScreen() {
		return mScreen;
	}

	public String getScreenUrl() {
		return mScreenUrl;
	}

	public String getState() {
		return mState;
	}

	public String getMotion() {
		return mMotion;
	}

	public String getAction() {
		return mAction;
	}

	public String getMacroEvent() {
		return mMacroEvent;
	}

	public boolean isSmartTrackAction() {
		return mSmartTrackAction;
	}

	public Map<String, String> getEventDictionary() {
		return mEventDictionary;
	}

	public Map<Integer, String> getCustomDimensionsDictionary() {
		return mCustomDimensionsDictionary;
	}

	public int getCustomerValue() {
		return mCustomerValue;
	}

}
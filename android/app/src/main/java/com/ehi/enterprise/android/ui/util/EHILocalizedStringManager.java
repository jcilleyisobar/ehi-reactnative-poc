package com.ehi.enterprise.android.ui.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

public class EHILocalizedStringManager {
	private static EHILocalizedStringManager sInstance;
	Map<String, String> mLocalizedStringMap;

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({
			LOCALIZED_KEY_STRING_0,
			LOCALIZED_KEY_STRING_1
	})
	public @interface LocalizedKey {
	}

	public static final String LOCALIZED_KEY_STRING_0 = "string 0";
	public static final String LOCALIZED_KEY_STRING_1 = "string 1";

	EHILocalizedStringManager() {
		mLocalizedStringMap = new HashMap<>();
	}

	@Nullable
	private @LocalizedKey String getKeyForId(int id) {
		switch (id) {
			case 0:
				return LOCALIZED_KEY_STRING_0;
			case 1:
				return LOCALIZED_KEY_STRING_1;
			default:
				return null;
		}
	}

	@NonNull
	public static EHILocalizedStringManager getInstance() {
		if (sInstance == null) {
			sInstance = new EHILocalizedStringManager();
		}

		return sInstance;
	}

	@Nullable
	public String getLocalizedStringForId(int id) {
		final @LocalizedKey String keyForId = getKeyForId(id);
		if (keyForId != null) {
			return mLocalizedStringMap.get(keyForId);
		}

		return null;
	}

	public void setLocalizedStringMap(@NonNull Map<String, String> localizedStringMap) {
		mLocalizedStringMap = localizedStringMap;
	}

}


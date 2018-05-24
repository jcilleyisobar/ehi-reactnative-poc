package com.ehi.enterprise.android.models.support;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Locale;

public class EHIConfigFeed extends BaseResponse {

	@SerializedName("mapped_cor")
	private String mMappedCOR;

	@SerializedName("mapped_locale")
	private String mMappedLocale;

	@SerializedName("supported_locales")
	private List<String> mSupportedLocele;

	public String getMappedCOR() {
		return mMappedCOR;
	}

	public String getMappedLocale() {
		return mMappedLocale;
	}

	public Locale getMappedLocaleObject() {
		if (mMappedLocale != null) {
			String language = null;
			String region = null;

			String[] parts = mMappedLocale.split("_");
			if (parts.length == 2) {
				language = parts[0].trim();
				region = parts[1].trim();
			}

			if (language != null
					&& region != null) {
				return new Locale(language, region);
			}
		}
		return Locale.getDefault();
	}

	public List<String> getSupportedLocele() {
		return mSupportedLocele;
	}

}

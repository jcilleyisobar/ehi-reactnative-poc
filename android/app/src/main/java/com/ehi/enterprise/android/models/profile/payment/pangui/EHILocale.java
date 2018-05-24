package com.ehi.enterprise.android.models.profile.payment.pangui;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHILocale extends EHIModel {

	@SerializedName("CountryIso3Code")
	private String mCountryIso3Code;

	@SerializedName("LanguageIso3Code")
	private String mLanguageIso3Code;

	public EHILocale(String countryIso3Code, String languageIso3Code) {
		mCountryIso3Code = countryIso3Code;
		mLanguageIso3Code = languageIso3Code;
	}
}

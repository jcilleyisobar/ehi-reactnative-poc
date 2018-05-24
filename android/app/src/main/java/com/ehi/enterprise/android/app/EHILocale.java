package com.ehi.enterprise.android.app;

import java.util.Locale;

public enum EHILocale implements CharSequence {

	de_DE("de", "DE"),
	en_DE("en", "DE"),
	en_CA("en", "CA"),
	en_GB("en", "GB"),
	en_US("en", "US"),
	es_ES("es", "ES"),
	en_ES("en", "ES"),
	es_US("es", "US"),
	fr_CA("fr", "CA"),
	en_FR("en", "FR"),
	fr_FR("fr", "FR");

	private final String mLanguage;
	private final String mCountry;

	EHILocale(String language, String country) {
		mLanguage = language;
		mCountry = country;
	}

	public String getValue() {
		return mLanguage + "_" + mCountry;
	}

	@Override
	public int length() {
		return getValue().length();
	}

	@Override
	public char charAt(int i) {
		return getValue().charAt(i);
	}

	@Override
	public CharSequence subSequence(int i, int i1) {
		return getValue().subSequence(i, i1);
	}

	public Locale getLocale() {
		return new Locale(mLanguage, mCountry);
	}
}

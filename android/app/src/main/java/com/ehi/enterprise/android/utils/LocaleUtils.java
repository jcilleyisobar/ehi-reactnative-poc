package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Arrays;
import java.util.Locale;

public final class LocaleUtils {
    private static final String ENGLISH_LANGUAGE = "en";
    private static final String[] SUPPORTED_LANGUAGES = new String[]{"en", "de", "es", "fr"};

    /**
     * Checks if any of the passed in locales are the same as the user's default locale
     * <p/>
     * This differs from {@link #isCountryInLocale(Locale...)} by checking against the full locale and not just the country
     *
     * @param locales
     * @return
     */
    public static boolean isLocale(Locale... locales) {
        boolean isLocale = false;

        for (Locale locale : locales) {
            if (Locale.getDefault().equals(locale)) {
                isLocale = true;
                break;
            }
        }

        return isLocale;
    }

    /**
     * Check if the default locale is one of the locales that are passed in
     *
     * @param locales the locales to check against
     * @return true if any of the locales match the default
     */
    public static boolean isCountryInLocale(Locale... locales) {
        return isCountryInLocale(Locale.getDefault().getCountry(), locales);
    }

    /**
     * Check if the passed in countryCode matches any of the locales
     *
     * @param countryCode code to check
     * @param locales     locales to compare against
     * @return true if any of the locales match the default
     */
    public static boolean isCountryInLocale(String countryCode, Locale... locales) {
        boolean isLocale = false;
        for (Locale locale : locales) {
            if (locale.getCountry().equalsIgnoreCase(countryCode)) {
                isLocale = true;
                break;
            }
        }

        return isLocale;
    }

    public static boolean isEnglishLanguageDefault() {
        return Locale.getDefault().equals(new Locale(ENGLISH_LANGUAGE, Locale.US.getCountry()));
    }

    public static boolean updateAppLocale(Context context, Locale newLocale) {
        if (context == null
                || newLocale == null
                || Locale.getDefault().toString().equalsIgnoreCase(newLocale.toString())) {
            return false;
        }

        Configuration config = context.getResources().getConfiguration();
        Locale.setDefault(newLocale);
        Configuration conf = new Configuration(config);
        conf.locale = newLocale;
        context.getApplicationContext().getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());
        return true;
    }

    public static boolean isLanguageSupported(Locale locale) {
        return Arrays.asList(SUPPORTED_LANGUAGES).contains(locale.getLanguage());
    }

    public static Locale getLocaleWithPreferedCountryCode(String countryCode) {
        return new Locale(Locale.getDefault().getLanguage(), countryCode);
    }
}

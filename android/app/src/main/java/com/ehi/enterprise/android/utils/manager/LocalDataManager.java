package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocalDataManager extends BaseDataManager {

    private static final String TAG = LocalDataManager.class.getSimpleName();

    private static final String APP_SETTINGS_TAG = "Settings";

    private static final String FIRST_START_KEY = "firstStartKey";
    private static final String PREFERENCE_FIRST_START_IN_GERMAN_KEY = "ehi.PREFERENCE_FIRST_START_IN_GERMAN_KEY";
    private static final String PREFERENCE_SHOW_POINTS = "ehi.PREFERENCE_SHOW_POINTS";
    private static final String PREFERENCE_SHOW_TOTAL_COST_ASTERISKS = "ehi.SHOW_TOTAL_COST_ASTERISKS";
    private static final String SHOULD_SHOW_NA_PAYMENT_INTRODUCTION_MESSAGE = "ehi.SHOULD_SHOW_NA_PAYMENT_INTRODUCTION_MESSAGE";
    private static final String PREFERENCE_PREFERRED_REGION = "ehi.PREFERENCE_PREFERRED_REGION";
    private static final String PREFERENCE_COUNTRIES_LIST = "ehi.PREFERENCE_COUNTRIES_LIST";
    private static final String OLD_VERSION = "ehi.OLD_VERSION";
    private static final String CURRENT_VERSION = "ehi.CURRENT_VERSION";
    private static final String SHOULD_SHOW_GEOFENCE_NOTIFICATION_INTRO = "SHOULD_SHOW_GEOFENCE_NOTIFICATION_INTRO";
    private static final String SHOULD_SHOW_WEEKEND_SPECIAL_MODAL = "ehi.SHOULD_SHOW_WEEKEND_SPECIAL_MODAL";
    private static final String WEEKEND_SPECIAL_CONTRACT = "ehi.WEEKEND_SPECIAL_CONTRACT";
    private static final String SELECTED_ENVIRONMENT_NAME = "SELECTED_ENVIRONMENT_NAME";
    private static final String SELECTED_SOLR_ENVIRONMENT_NAME = "SELECTED_SOLR_ENVIRONMENT_NAME";
    private static final String SELECTED_LOCALE = "SELECTED_LOCALE";
    private static final String DATA_COLLECTION_NEXT_SHOW_TIMESTAMP = "DATA_COLLECTION_NEXT_SHOW_TIMESTAMP";
    private static final String CONFIRMATION_RATE_US_COUNT = "CONFIRMATION_RATE_US_COUNT";
    private static final String CONFIRMATION_RATE_US_SHOW = "CONFIRMATION_RATE_US_SHOW";
    private static final String ANALYTICS_STATE = "ANALYTICS_STATE";
    private static final String ENROLLMENT_PROFILE = "ENROLLMENT_PROFILE";
    private static final String SHOULD_AUTOMATICALLY_SELECT_CARD = "ehi.SHOULD_AUTOMATICALLY_SELECT_CARD";
    private static final String SAVED_TIER = "ehi.SAVED_TIER:";
    private static final String FIRST_TIME_MAP_SCREEN = "ehi.FIRST_TIME_MAP_SCREEN";
    private static final String HAS_PAY_SYSTEM = "ehi.HAS_PAY_SYSTEM";
    private static final String FORCE_GBO_INVALID_KEY = "ehi.FORCE_GBO_INVALID_KEY";
    public static final String GBO_REGION_COOKIE = "ehi.GBO_REGION_COOKIE";

    public static final String COUNTRY_CHANGE_EVENT = "COUNTRY_CHANGE_EVENT";
    public static final String SHOWN_REGISTER_MODAL = "ehi.SHOWN_REGISTER_MODAL";


    private static LocalDataManager sSharedInstance;
    private DiskCacheManager mCacheManager;
    private Map<String, Boolean> mEuropeanAddressMap;

    public static LocalDataManager getInstance() {
        if (sSharedInstance == null) {
            sSharedInstance = new LocalDataManager();
        }
        return sSharedInstance;
    }

    protected LocalDataManager() {
    }

    @Override
    protected String getSharedPreferencesName() {
        return APP_SETTINGS_TAG;
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        mContext = context;
        mCacheManager = new DiskCacheManager(context);

        sSharedInstance = this;
    }

    public boolean shouldShowGeofenceNotificationIntro() {
        return getBoolean(SHOULD_SHOW_GEOFENCE_NOTIFICATION_INTRO, true);
    }

    public void setShouldShowGeofenceNotificationIntro(boolean shouldShowGeofenceNotificationIntro) {
        set(SHOULD_SHOW_GEOFENCE_NOTIFICATION_INTRO, shouldShowGeofenceNotificationIntro);
    }

    public int getOldVersion() {
        return getInt(OLD_VERSION, -1);
    }

    public void setOldVersion(int oldVersion) {
        set(OLD_VERSION, oldVersion);
    }

    public int getCurrentVersion() {
        return getInt(CURRENT_VERSION, 0);
    }

    public void setCurrentVersion(int currentVersion) {
        set(CURRENT_VERSION, currentVersion);
    }

    public boolean needShowPoints() {
        return getBoolean(PREFERENCE_SHOW_POINTS, true);
    }

    public void setNeedShowPoints(boolean showPoints) {
        set(PREFERENCE_SHOW_POINTS, showPoints);
    }

    public boolean isFirstStartInGerman() {
        return getBoolean(PREFERENCE_FIRST_START_IN_GERMAN_KEY, true);
    }

    public void setFirstStartInGerman(boolean firstStart) {
        set(PREFERENCE_FIRST_START_IN_GERMAN_KEY, firstStart);
    }

    public boolean havePreferredRegion() {
        return getString(PREFERENCE_PREFERRED_REGION, null) != null;
    }

    public String getPreferredCountryCode() {
        return getString(PREFERENCE_PREFERRED_REGION, Locale.getDefault().getCountry());
    }

    public void setPreferredRegion(String preferredRegion) {
        set(PREFERENCE_PREFERRED_REGION, preferredRegion);
        Intent loginIntent = new Intent(COUNTRY_CHANGE_EVENT);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(loginIntent);
    }

    public List<EHICountry> getCountriesList() {
        return getEhiModel(PREFERENCE_COUNTRIES_LIST, new TypeToken<List<EHICountry>>() {
        }.getType());
    }

    public void setCountriesList(List<EHICountry> countries) {
        set(PREFERENCE_COUNTRIES_LIST, countries);
    }

    public boolean isPreferredRegionEmailOptInEnabled() {
        final EHICountry country = getPreferredCountry();

        return country != null && country.isDefaultEmailOptIn();
    }

    @Nullable
    public EHICountry getPreferredCountry() {
        final List<EHICountry> countries = getCountriesList();
        final String preferredCountryCode = getPreferredCountryCode();

        if (countries == null || preferredCountryCode == null) {
            return null;
        }

        for (int i = 0, size = countries.size(); i < size; i++) {
            final EHICountry country = countries.get(i);
            if (country.getCountryCode().equalsIgnoreCase(preferredCountryCode)) {
                return country;
            }
        }

        return null;
    }

    //Introduction flag
    public boolean getFirstStartFlag() {
        return getBoolean(FIRST_START_KEY, true);
    }

    public void setFirstStartFlag(boolean intro_flag) {
        set(FIRST_START_KEY, intro_flag);
    }

    //showing of total cost asterisks and explanation
    public boolean getShowClassTotalCostAsterisks() {
        return getBoolean(PREFERENCE_SHOW_TOTAL_COST_ASTERISKS, false);
    }

    public void setShowClassTotalCostAsterisks(boolean show) {
        set(PREFERENCE_SHOW_TOTAL_COST_ASTERISKS, show);
    }

    public boolean isEuropeanAddress(String countryCode) {
        if (mEuropeanAddressMap == null || mEuropeanAddressMap.size() == 0) {
            final List<EHICountry> countries = getCountriesList();
            if (countries == null) {
                return false;
            }
            mEuropeanAddressMap = new HashMap<>(countries.size());
            for (int i = 0, size = countries.size(); i < size; i++) {
                final EHICountry country = countries.get(i);
                mEuropeanAddressMap.put(country.getCountryCode(), country.isEuropeanAddressFlag());
            }
        }

        Boolean isEuropeanAddress = mEuropeanAddressMap.get(countryCode);
        return isEuropeanAddress != null ? isEuropeanAddress : false;
    }

    public boolean shouldShowWeekendSpecialModal() {
        return getBoolean(SHOULD_SHOW_WEEKEND_SPECIAL_MODAL, true);
    }

    public void setShouldShowWeekendSpecialModal(boolean shouldShow) {
        set(SHOULD_SHOW_WEEKEND_SPECIAL_MODAL, shouldShow);
    }

    public boolean isWeekendSpecialAvailable() {
        final EHICountry country = getPreferredCountry();

        return country != null && country.getWeekendSpecialPromotion() != null;
    }

    public EHIContract getWeekendSpecialContract() {
        return getEhiModel(WEEKEND_SPECIAL_CONTRACT, EHIContract.class);
    }

    public void setWeekendSpecialContract(EHIContract ehiContract) {
        set(WEEKEND_SPECIAL_CONTRACT, ehiContract);
    }

    public void removeWeekendSpecialContract() {
        remove(WEEKEND_SPECIAL_CONTRACT);
    }

    public String getSelectedEnvironmentName() {
        return getString(SELECTED_ENVIRONMENT_NAME, Settings.ENVIRONMENT.name());
    }

    public void setSelectedEnvironmentName(String environmentName) {
        set(SELECTED_ENVIRONMENT_NAME, environmentName);
    }

    public String getSelectedSolrEnvironmentName() {
        return getString(SELECTED_SOLR_ENVIRONMENT_NAME, Settings.SOLR_ENVIRONMENT.name());
    }

    public void setSelectedSolrEnvironmentName(String environmentName) {
        set(SELECTED_SOLR_ENVIRONMENT_NAME, environmentName);
    }

    public String getSelectedLocaleName() {
        return getString(SELECTED_LOCALE, null);
    }

    public void setSelectedLocaleName(String localeName) {
        set(SELECTED_LOCALE, localeName);
    }

    public long getDataCollectionReminderNextShowTimestamp() {
        return getLong(DATA_COLLECTION_NEXT_SHOW_TIMESTAMP, 0);
    }

    public void setDataCollectionReminderNextShowTimestamp(long timestamp) {
        set(DATA_COLLECTION_NEXT_SHOW_TIMESTAMP, timestamp);
    }

    public boolean shouldShowConfirmationRateUs() {
        return getBoolean(CONFIRMATION_RATE_US_SHOW, true)
                && getInt(CONFIRMATION_RATE_US_COUNT, 0) > 1;
    }

    public void markConfirmationRateUsDone() {
        set(CONFIRMATION_RATE_US_SHOW, false);
    }

    public void increaseConfirmationRateUsCounter() {
        set(CONFIRMATION_RATE_US_COUNT, Math.min(getInt(CONFIRMATION_RATE_US_COUNT, 0) + 1, 2));
    }

    public void setEnrollmentProfile(EHIEnrollProfile enrollmentProfile) {
        set(ENROLLMENT_PROFILE, enrollmentProfile);
    }

    public EHIEnrollProfile getEnrollProfile() {
        return getEhiModel(ENROLLMENT_PROFILE, EHIEnrollProfile.class);
    }

    public boolean shouldShowPrepayIntroducingMessage() {
        return getBoolean(SHOULD_SHOW_NA_PAYMENT_INTRODUCTION_MESSAGE, true);
    }

    public void setPrepayIntroductionMessageShown() {
        set(SHOULD_SHOW_NA_PAYMENT_INTRODUCTION_MESSAGE, false);
    }

    public void clearEnrollmentProfile() {
        remove(ENROLLMENT_PROFILE);
    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    /**
     * @param key key to remove from cache
     * @return true if deleted
     */
    public synchronized boolean deleteObjectFromCache(final String key) {
        try {
            return mCacheManager.delete(key);
        } catch (IOException e) {
            Thread.dumpStack();
            return false;
        }
    }

    /**
     * @param key   key to add from cache
     * @param value value to cache
     */
    public synchronized void addObjectToCache(final String key, final Object value) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mCacheManager.put(key, value);
                } catch (Exception e) {
                    Thread.dumpStack();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Retrieve an {@link java.lang.Object} from the Cache
     *
     * @param key key to retrieve
     * @param <T> the infered type of the object to retrieve
     * @return {@link java.lang.Object} retrieved
     */
    public synchronized <T> T getObjectFromCache(final String key, Type type) {
        try {
            return mCacheManager.get(key, type);
        } catch (IOException | NullPointerException e) {
            Thread.dumpStack();
            return null;
        }
    }

    /**
     * Clears the Cache
     */
    public synchronized void clearCache() {
        try {
            mCacheManager.clear();
        } catch (IOException e) {
            Thread.dumpStack();
        }
    }

    public void setAnalyticsState(EHIAnalytics.State state) {
        set(ANALYTICS_STATE, state.value);
    }

    public String getAnalyticsState() {
        return getString(ANALYTICS_STATE, "");
    }

    public boolean shouldAutomaticallySelectCard() {
        return getBoolean(SHOULD_AUTOMATICALLY_SELECT_CARD, false);
    }

    public void setShouldAutomaticallySelectCard(boolean value) {
        set(SHOULD_AUTOMATICALLY_SELECT_CARD, value);
    }

    public String getLoyaltyTier(String userId) {
        return getString(SAVED_TIER + userId, null);
    }

    public void setLoyaltyTier(String loyaltyNumber, String loyaltyTier) {
        set(SAVED_TIER + loyaltyNumber, loyaltyTier);
    }

    public boolean isFirstTimeOnMapScreen() {
        final boolean value = getBoolean(FIRST_TIME_MAP_SCREEN, true);
        set(FIRST_TIME_MAP_SCREEN, false);
        return value;
    }

    public void setHasPaySystem(boolean value) {
        set(HAS_PAY_SYSTEM, value);
    }

    public boolean hasPaySystem() {
        return getBoolean(HAS_PAY_SYSTEM, false);
    }

    public boolean shouldForceGboInvalidKey() {
        return getBoolean(FORCE_GBO_INVALID_KEY, false);
    }

    public void toggleForceGboInvalidKey() {
        set(FORCE_GBO_INVALID_KEY, !shouldForceGboInvalidKey());
    }

    public void setGboRegionCookie(String value) {
        set(GBO_REGION_COOKIE, value);
    }

    public String getGboRegionCookie() {
        return getString(GBO_REGION_COOKIE, "");
    }


    public boolean hasShownRegisterModal(){
        return getBoolean(SHOWN_REGISTER_MODAL, false);
    }

    public void setShownRegisterModal(){
        set(SHOWN_REGISTER_MODAL, true);
    }



}
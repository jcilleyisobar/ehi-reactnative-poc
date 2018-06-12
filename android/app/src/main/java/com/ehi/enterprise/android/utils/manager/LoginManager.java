package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerViewModel;

public class LoginManager extends BaseDataManager {

    public static final String EHI_PROFILE = "EHI_PROFILE";
    public static final String LOGGED_IN = "LOGGED_IN";
    public static final String LOGIN_EVENT = "LOGIN_EVENT";

    private static final String LOGINSAVEDATA = "LOGIN_SAVE_DATA";
    private static final String TERMS_CONDITIONS_VERSION = "TERMS_CONDITIONS_VERSION";
    public static final String USE_FINGERPRINT_FOR_PROFILE = "USE_FINGERPRINT_FOR_PROFILE";

    public static String LOGINDATA = "LoginData_KEY";
    public static String LOGINUSERNAME = "LoginData_USERNAME";
    public static String LOGINAUTHTOKEN = "LOGINAUTHTOKEN";
    public static String LOGINLASTTIME = "LOGINLASTTIME";

    private static final long RELOGIN_PERIOD = 5 * 60 * 1000;

    public static final String CURRENT_DRAWER_ITEM = "CURRENT_DRAWER_ITEM";


    private static LoginManager mManager = null;
    private String mUserName = null;
    private String mUserAuthToken = null;
    private String mUserEncriptedCredetials = null;
    private ProfileCollection mProfileCollection;

    protected LoginManager() {
    }

    @NonNull
    public static LoginManager getInstance() {
        if (mManager == null) {
            mManager = new LoginManager();
        }
        return mManager;
    }

    public void setProfile(@NonNull ProfileCollection profile) {
        mProfileCollection = profile;
        set(EHI_PROFILE, (EHIProfileResponse) mProfileCollection);
    }

    public ProfileCollection getProfileCollection() {
        if (mProfileCollection != null) {
            return mProfileCollection;
        } else {
            return getProfileNoCache();
        }
    }

    public ProfileCollection getProfileNoCache() {
        return getEhiModelNoCache(EHI_PROFILE, EHIProfileResponse.class);
    }

    public void setLoginUserName(@NonNull String userName, boolean persist) {
        mUserName = userName;
        if (persist) {
            set(LOGINUSERNAME, userName);
        }
    }

    @Nullable
    public String getUserName() {
        return (mUserName == null) ? getString(LOGINUSERNAME, null) : mUserName;
    }

    public void setEncryptedCredentials(@NonNull String hash, boolean remember) {
        if (remember) {
            set(LOGINDATA, hash);
        }
        mUserEncriptedCredetials = hash;
    }

    public void login(@NonNull String userHash, @NonNull String userAuthToken, @NonNull String userName, ProfileCollection profile, boolean persist) {
        login(userHash, userAuthToken, userName, profile, persist, NavigationDrawerViewModel.RESET_MENU);
    }

    public void login(@NonNull String userHash, @NonNull String userAuthToken, @NonNull String userName, ProfileCollection profile, boolean persist, int drawerItemId) {
        setLoginUserName(userName, persist);
        setProfile(profile);
        setUserAuthToken(userAuthToken, persist);
        setEncryptedCredentials(userHash, persist);
        broadcastLoginStateChange(drawerItemId);
    }

    public void setEncryptedCredentials(@NonNull String hash) {
        setEncryptedCredentials(hash, savingData());
    }

    public void logOut() {
        mUserName = null;
        mUserAuthToken = null;
        mUserEncriptedCredetials = null;
        mProfileCollection = null;
        remove(EHI_PROFILE);
        remove(LOGINDATA);
        remove(LOGINUSERNAME);
        remove(LOGINAUTHTOKEN);
        remove(TERMS_CONDITIONS_VERSION);
        remove(LocalDataManager.GBO_REGION_COOKIE);
        broadcastLoginStateChange(NavigationDrawerViewModel.RESET_MENU);
    }


    /**
     * @return the user hash used to login user without user/password
     */
    @Nullable
    public String getEncryptedCredentials() {
        return (mUserEncriptedCredetials == null) ? getString(LOGINDATA, null) : mUserEncriptedCredetials;
    }

    public void setUserAuthToken(String authToken, boolean remember) {
        if (remember) {
            set(LOGINAUTHTOKEN, authToken);
        }
        mUserAuthToken = authToken;
    }

    public void setUserAuthToken(String authToken) {
        setUserAuthToken(authToken, savingData());
    }

    public String getUserAuthToken() {
        return mUserAuthToken == null ? getString(LOGINAUTHTOKEN, null) : mUserAuthToken;
    }

    public void setLastLoginTime(long time) {
        set(LOGINLASTTIME, time);
    }

    public long getLastLoginTime() {
        return getLong(LOGINLASTTIME, 0);
    }

    public boolean isNeedToRelogin() {
        return System.currentTimeMillis() - getLastLoginTime() > RELOGIN_PERIOD;
    }

    /**
     * used if user is logged in to know if you should save data based on past preferences
     *
     * @return
     */
    public boolean savingData() {
        return getBoolean(LOGINSAVEDATA, false);
    }

    public void setSavingData(boolean save) {
        set(LOGINSAVEDATA, save);
    }

    /**
     * wrapper to see if someone is logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return getUserAuthToken() != null;
    }

    private void broadcastLoginStateChange(int drawerItem) {
        Intent loginIntent = new Intent(LOGIN_EVENT);
        loginIntent.putExtra(LOGGED_IN, isLoggedIn());
        loginIntent.putExtra(CURRENT_DRAWER_ITEM, drawerItem);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(loginIntent);
    }

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        mManager = this;

        //clear default configurations
        remove(LocalDataManager.GBO_REGION_COOKIE);
    }

    @Override
    protected String getSharedPreferencesName() {
        return LOGINDATA;
    }

    public void setTermsConditionsVersion(String versionNumber) {
        set(TERMS_CONDITIONS_VERSION, versionNumber);
    }

    public String getTermsConditionsVersion() {
        return getString(TERMS_CONDITIONS_VERSION, "0");
    }

    public ProfileCollection smartUpdateProfileCollection(ProfileCollection newProfile) {
        ProfileCollection currentProfile = getProfileCollection();

        if (currentProfile != null && currentProfile.getProfile().getBasicProfile().getLoyaltyData() != null
                && newProfile.getProfile().getBasicProfile().getLoyaltyData() != null
                && newProfile.getProfile().getBasicProfile().getLoyaltyData().getActivityToNextTier() == null) {

            newProfile.getProfile().getBasicProfile().getLoyaltyData().setActivityToNextTier(currentProfile.getProfile().getBasicProfile().getLoyaltyData().getActivityToNextTier());
        }
        setProfile(newProfile);
        return newProfile;
    }

    public void setUseFingerprintForProfile(boolean useFingerprint) {
        set(USE_FINGERPRINT_FOR_PROFILE, useFingerprint);
    }

    public boolean shouldUseFingerprintForProfile() {
        return getBoolean(USE_FINGERPRINT_FOR_PROFILE, false);
    }
}
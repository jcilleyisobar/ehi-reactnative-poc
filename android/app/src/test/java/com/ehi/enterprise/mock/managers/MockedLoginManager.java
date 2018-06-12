package com.ehi.enterprise.mock.managers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.utils.manager.LoginManager;

public class MockedLoginManager extends LoginManager{
    private EHIProfile mProfile;
    private String mUserName;
    private String mHash;
    private EHIProfile mUserProfile;
    private String mAuthToken;
    private long mLastLoginTime;
    private boolean mSavingData;
    private String mVersionNumber;


    public MockedLoginManager(){
    }

    //todo do we actually need all this stuff? (Is there a case where it's not easier to just mock expected behaviour via mockito)

    @Override
    public void setLoginUserName(@NonNull String userName, boolean persist) {
        mUserName = userName;
    }

    @Nullable
    @Override
    public String getUserName() {
        return mUserName;
    }

    @Override
    public void setEncryptedCredentials(@NonNull String hash, boolean remember) {
        mHash = hash;
    }

    public void login(@NonNull String userHash, @NonNull String userAuthToken, @NonNull String userName, EHIProfile profile, boolean persist) {
        //todo add fake login data
        mUserProfile = new EHIProfile();
    }

    @Override
    public void setEncryptedCredentials(@NonNull String hash) {
        mHash = hash;
    }

    @Override
    public void logOut() {
        mUserProfile = null;
    }

    @Nullable
    @Override
    public String getEncryptedCredentials() {
        return mHash;
    }

    @Override
    public void setUserAuthToken(String authToken, boolean remember) {
        mAuthToken = authToken;
    }

    @Override
    public void setUserAuthToken(String authToken) {
        mAuthToken = authToken;
    }

    @Override
    public String getUserAuthToken() {
        return mAuthToken;
    }

    @Override
    public void setLastLoginTime(long time) {
        mLastLoginTime = time;
    }

    @Override
    public long getLastLoginTime() {
        return mLastLoginTime;
    }

    @Override
    public boolean isNeedToRelogin() {
        return super.isNeedToRelogin();
    }

    @Override
    public boolean savingData() {
        return mSavingData;
    }

    @Override
    public void setSavingData(boolean save) {
        mSavingData = save;
    }

    @Override
    public boolean isLoggedIn() {
        return mUserProfile != null;
    }

    @Override
    public void initialize(@NonNull Context context) {
    }

    @Override
    protected String getSharedPreferencesName() {
        return null;
    }

    @Override
    public void setTermsConditionsVersion(String versionNumber) {
        mVersionNumber = versionNumber;
    }

    @Override
    public String getTermsConditionsVersion() {
        return mVersionNumber;
    }
}

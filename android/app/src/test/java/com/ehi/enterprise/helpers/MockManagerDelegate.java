package com.ehi.enterprise.helpers;

import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;
import com.ehi.enterprise.android.utils.manager.ConfigFeedManager;
import com.ehi.enterprise.android.utils.manager.DateUtilManager;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.ehi.enterprise.android.utils.manager.ForeSeeSurveyManager;
import com.ehi.enterprise.android.utils.manager.IManagersDelegate;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LocationManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;
import com.ehi.enterprise.android.utils.manager.SettingsManager;
import com.ehi.enterprise.android.utils.manager.SupportInfoManager;
import com.ehi.enterprise.mock.managers.MockedDateUtilManager;
import com.ehi.enterprise.mock.managers.MockedEHINotificationManager;
import com.ehi.enterprise.mock.managers.MockedForeseeSurveyManager;
import com.ehi.enterprise.mock.managers.MockedGeofenceManager;
import com.ehi.enterprise.mock.managers.MockedLocalDataManager;
import com.ehi.enterprise.mock.managers.MockedLocationAPIManager;
import com.ehi.enterprise.mock.managers.MockedLocationManager;
import com.ehi.enterprise.mock.managers.MockedLoginManager;
import com.ehi.enterprise.mock.managers.MockedReservationManager;
import com.ehi.enterprise.mock.managers.MockedSettingsManager;
import com.ehi.enterprise.mock.managers.MockedSupportInfoManager;

public class MockManagerDelegate implements IManagersDelegate {

    private MockableObject<MockedLoginManager> mMockedLoginManager = null;
    private MockableObject<MockedLocationManager> mMockedLocationManager;
    private MockableObject<MockedLocationAPIManager> mMockedLocationApiManager;
    private MockableObject<MockedLocalDataManager> mMockedLocalDataManager;
    private MockableObject<MockedReservationManager> mMockedReservationManager;
    private MockableObject<MockedSupportInfoManager> mMockedSupportInfoManager;
    private MockableObject<MockedDateUtilManager> mMockedDateUtilManager;
    private MockableObject<MockedSettingsManager> mMockedSettingsManager;
    private MockableObject<MockedEHINotificationManager> mMockedNotificationManager;
    private MockableObject<MockedGeofenceManager> mMockedGeofenceManager;
    private MockableObject<MockedForeseeSurveyManager> mMockedForeseeSurveyManager;

    @Override
    public SupportInfoManager getSupportInfoManager() {
        return getMockedSupportInfoManager().getMockedObject();
    }

    @Override
    public ReservationManager getReservationManager() {
        return getMockedReservationManager().getMockedObject();
    }

    @Override
    public LoginManager getLoginManager() {
        return getMockedLoginManager().getMockedObject();
    }

    @Override
    public LocationManager getLocationManager() {
        return getMockedLocationManager().getMockedObject();
    }

    @Override
    public LocationApiManager getLocationApiManager() {
        return getMockedLocationApiManager().getMockedObject();
    }

    @Override
    public DateUtilManager getDateUtilManager() {
        return getMockedDateUtilManager().getMockedObject();
    }

    @Override
    public EHINotificationManager getNotificationManager() {
        return getMockedNotificationManager().getMockedObject();
    }

    @Override
    public GeofenceManager getGeofenceManger() {
        return getMockedGeofenceManager().getMockedObject();
    }

    @Override
    public ForeSeeSurveyManager getForeseeSurveyManager() {
        return getMockedForeseeSurveyManager().getMockedObject();
    }

    @Override
    public SettingsManager getSettingsManager() {
        return getMockedSettingsManager().getMockedObject();
    }

    @Override
    public LocalDataManager getLocalDataManager() {
        return getMockedLocalDataManager().getMockedObject();
    }

    @Override
    public ConfigFeedManager getConfigFeedManager() {
        return null;
    }

    public MockableObject<MockedLocalDataManager> getMockedLocalDataManager(){
        if(mMockedLocalDataManager == null){
            mMockedLocalDataManager = new MockableObject<>(MockedLocalDataManager.class);
        }
        return mMockedLocalDataManager;
    }

    public MockableObject<MockedLoginManager> getMockedLoginManager() {
        if(mMockedLoginManager == null){
            mMockedLoginManager = new MockableObject<>(MockedLoginManager.class);
        }
        return mMockedLoginManager;
    }

    public MockableObject<MockedLocationManager> getMockedLocationManager() {
        if(mMockedLocationManager == null){
            mMockedLocationManager = new MockableObject<>(MockedLocationManager.class);
        }
        return mMockedLocationManager;
    }

    public MockableObject<MockedLocationAPIManager> getMockedLocationApiManager() {
        if(mMockedLocationApiManager == null){
            mMockedLocationApiManager = new MockableObject<>(MockedLocationAPIManager.class);
        }
        return mMockedLocationApiManager;
    }

    public MockableObject<MockedReservationManager> getMockedReservationManager(){
        if(mMockedReservationManager == null){
            mMockedReservationManager = new MockableObject<>(MockedReservationManager.class);
        }

        return mMockedReservationManager;
    }

    public MockableObject<MockedSupportInfoManager> getMockedSupportInfoManager(){
        if(mMockedSupportInfoManager == null){
            mMockedSupportInfoManager = new MockableObject<>(MockedSupportInfoManager.class);
        }

        return mMockedSupportInfoManager;
    }

    public MockableObject<MockedDateUtilManager> getMockedDateUtilManager() {
        if(mMockedDateUtilManager == null){
            mMockedDateUtilManager = new MockableObject<>(MockedDateUtilManager.class);
        }

        return mMockedDateUtilManager;
    }

    public MockableObject<MockedSettingsManager> getMockedSettingsManager(){
        if(mMockedSettingsManager == null){
            mMockedSettingsManager = new MockableObject<>(MockedSettingsManager.class);
        }

        return mMockedSettingsManager;
    }

    public MockableObject<MockedEHINotificationManager> getMockedNotificationManager(){
        if(mMockedNotificationManager == null){
            mMockedNotificationManager = new MockableObject<>(MockedEHINotificationManager.class);
        }

        return mMockedNotificationManager;
    }

    public MockableObject<MockedGeofenceManager> getMockedGeofenceManager(){
        if(mMockedGeofenceManager == null){
            mMockedGeofenceManager = new MockableObject<>(MockedGeofenceManager.class);
        }

        return mMockedGeofenceManager;
    }

    public MockableObject<MockedForeseeSurveyManager> getMockedForeseeSurveyManager(){
        if(mMockedForeseeSurveyManager == null){
            mMockedForeseeSurveyManager = new MockableObject<>(MockedForeseeSurveyManager.class);
        }

        return mMockedForeseeSurveyManager;
    }
}
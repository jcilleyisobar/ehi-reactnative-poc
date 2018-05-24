package com.ehi.enterprise.android.utils.manager;

import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;

public class ManagerDelegateImpl implements IManagersDelegate {

    @Override
    public SupportInfoManager getSupportInfoManager() {
        return SupportInfoManager.getInstance();
    }

    @Override
    public ReservationManager getReservationManager() {
        return ReservationManager.getInstance();
    }

    @Override
    public LoginManager getLoginManager() {
        return LoginManager.getInstance();
    }

    @Override
    public LocationManager getLocationManager() {
        return LocationManager.getInstance();
    }

    @Override
    public SettingsManager getSettingsManager() {
        return SettingsManager.getInstance();
    }

    @Override
    public LocalDataManager getLocalDataManager() {
        return LocalDataManager.getInstance();
    }

    @Override
    public ConfigFeedManager getConfigFeedManager() {
        return ConfigFeedManager.getInstance();
    }

    @Override
    public LocationApiManager getLocationApiManager() {
        return LocationApiManager.getInstance();
    }

    @Override
    public DateUtilManager getDateUtilManager() {
        return DateUtilManager.getInstance();
    }

    @Override
    public EHINotificationManager getNotificationManager() {
        return EHINotificationManager.getInstance();
    }

    @Override
    public GeofenceManager getGeofenceManger() {
        return GeofenceManager.getInstance();
    }

    @Override
    public ForeSeeSurveyManager getForeseeSurveyManager() {
        return ForeSeeSurveyManager.getInstance();
    }
}

package com.ehi.enterprise.android.utils.manager;

import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;

public interface IManagersDelegate {

    SupportInfoManager getSupportInfoManager();

    ReservationManager getReservationManager();

    LoginManager getLoginManager();

    LocationManager getLocationManager();

    SettingsManager getSettingsManager();

    LocalDataManager getLocalDataManager();

    ConfigFeedManager getConfigFeedManager();

    LocationApiManager getLocationApiManager();

    DateUtilManager getDateUtilManager();

    EHINotificationManager getNotificationManager();

    GeofenceManager getGeofenceManger();

    ForeSeeSurveyManager getForeseeSurveyManager();
}

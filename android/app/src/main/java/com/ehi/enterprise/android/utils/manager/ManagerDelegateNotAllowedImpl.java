package com.ehi.enterprise.android.utils.manager;

import com.ehi.enterprise.android.utils.locations.GeofenceManager;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;

public class ManagerDelegateNotAllowedImpl implements IManagersDelegate {

	private static final String EXCEPTION_MESSAGE = "Can't access this manager from View's viewmodel. Pass all data you need from fragment/activity level. ";

	@Override
	public SupportInfoManager getSupportInfoManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public ReservationManager getReservationManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public LoginManager getLoginManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public LocationManager getLocationManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public SettingsManager getSettingsManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public LocalDataManager getLocalDataManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public ConfigFeedManager getConfigFeedManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

	@Override
	public LocationApiManager getLocationApiManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}

    @Override
    public DateUtilManager getDateUtilManager() {
        return DateUtilManager.getInstance();
    }

    @Override
    public EHINotificationManager getNotificationManager() {
        throw new IllegalStateException(EXCEPTION_MESSAGE);
    }

    @Override
    public GeofenceManager getGeofenceManger() {
        return null;
    }

	@Override
	public ForeSeeSurveyManager getForeseeSurveyManager() {
		throw new IllegalStateException(EXCEPTION_MESSAGE);
	}
}

package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.models.location.EHICityLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class SearchLocationsActivityViewModel extends ManagersAccessViewModel {

	private boolean mIsModify;

	public void addRecentLocation(EHISolrLocation location) {
		if (getManagers().getSettingsManager().isSearchHistoryEnabled()) {
			getManagers().getLocationManager().addRecentLocation(location);
		}
	}

	public String getValidAlamoReservationUrl() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getValidAlamoReservationUrl();
	}

	public String getValidNationalReservationUrl() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getValidNationalReservationUrl();
	}

	public boolean isSearchHistoryEnabled() {
		return getManagers().getSettingsManager().isSearchHistoryEnabled();
	}

	public void addRecentCitySearchLocation(EHICityLocation cityLocation) {
		getManagers().getLocationManager().addRecentCitySearchLocation(cityLocation);
	}

	public boolean isModify() {
		return mIsModify;
	}

	public void setIsModify(boolean isModify) {
		mIsModify = isModify;
	}
}

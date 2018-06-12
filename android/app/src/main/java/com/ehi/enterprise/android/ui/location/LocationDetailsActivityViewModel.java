package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class LocationDetailsActivityViewModel extends ManagersAccessViewModel {

	private boolean mIsModify;

	public void addRecentLocation(EHISolrLocation location) {
		if (getManagers().getSettingsManager().isSearchHistoryEnabled()) {
			getManagers().getLocationManager().addRecentLocation(location);
		}
	}

	public String getValidNationalReservationUrl() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getValidNationalReservationUrl();
	}

	public String getValidAlamoReservationUrl() {
		return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getValidAlamoReservationUrl();
	}

	public void setIsModify(boolean isModify) {
		mIsModify = isModify;
	}

	public boolean isModify() {
		return mIsModify;
	}

	public void setModify(boolean isModify) {
		mIsModify = isModify;
	}
}

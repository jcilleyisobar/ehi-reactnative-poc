package com.ehi.enterprise.android.ui.dashboard.interfaces;

import com.ehi.enterprise.android.models.location.EHILocation;

public interface OnActiveRentalEventsListener {

	void onReturnInstructionsClicked();

	void onGetDirectionsClicked();

	void onExtendRentalClicked();

	void onViewRentalDetailsClicked();

	void onFindGasStationsClicked(EHILocation returnLocation);

	void onLocationNameClicked(EHILocation location);

	void onRateMyRideButtonClicked(String rateUrl);

}
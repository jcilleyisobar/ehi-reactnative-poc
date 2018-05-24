package com.ehi.enterprise.android.network.request_params.reservation;

import com.google.gson.annotations.SerializedName;

public class InitiateModifyRequestParams {

	@SerializedName("vehicle_change_requested")
	private boolean mVehicleChangeRequested;

	@SerializedName("pickup_location_id")
	private String mPickupLocationId;

	@SerializedName("return_location_id")
	private String mReturnLocationid;

	@SerializedName("pickup_time")
	private String mPickupTime;

	@SerializedName("return_time")
	private String mReturnTime;

	@SerializedName("country_of_residence_code")
	private String mCountryOfResidenceCode;

	public InitiateModifyRequestParams(boolean vehicleChangeRequested,
	                                   String pickupLocationId,
	                                   String returnLocationid,
	                                   String pickupTime,
	                                   String returnTime,
	                                   String countryOfResidenceCode) {
		mVehicleChangeRequested = vehicleChangeRequested;
		mPickupLocationId = pickupLocationId;
		mReturnLocationid = returnLocationid;
		mPickupTime = pickupTime;
		mReturnTime = returnTime;
		mCountryOfResidenceCode = countryOfResidenceCode;
	}

}
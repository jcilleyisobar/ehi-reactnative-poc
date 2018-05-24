package com.ehi.enterprise.android.network.request_params.reservation;

import com.google.gson.annotations.SerializedName;

public class InitiateAlternateModifyRequestParams {

	@SerializedName("pickup_location_id")
	private String mPickupLocationId;

	@SerializedName("return_location_id")
	private String mReturnLocationid;

	@SerializedName("pickup_time")
	private String mPickupTime;

	@SerializedName("return_time")
	private String mReturnTime;

	@SerializedName("renter_age")
	private String mRenterAge;

	@SerializedName("country_of_residence_code")
	private String mCountryOfResidenceCode;

	public InitiateAlternateModifyRequestParams(String pickupLocationId,
	                                            String pickupTime,
	                                            String returnLocationid,
	                                            String returnTime,
	                                            String renterAge,
	                                            String contractNumber,
	                                            String countryOfResidenceCode) {

		mPickupLocationId = pickupLocationId;
		mPickupTime = pickupTime;
		mReturnLocationid = returnLocationid;
		mReturnTime = returnTime;
		mRenterAge = renterAge;
		mCountryOfResidenceCode = countryOfResidenceCode;
	}

}
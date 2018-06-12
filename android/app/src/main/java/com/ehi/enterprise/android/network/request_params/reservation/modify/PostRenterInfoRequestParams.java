package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.google.gson.annotations.SerializedName;

public class PostRenterInfoRequestParams {

	@SerializedName("renter_info")
	private EHIDriverInfo mRenterInfo;

	@SerializedName("airline_information")
	private EHIAirlineInformation mFlightInfo;

	public PostRenterInfoRequestParams(EHIDriverInfo renterInfo, EHIAirlineInformation flightInfo) {
		mRenterInfo = renterInfo;
		mRenterInfo.clearMaskedFields();
		mFlightInfo = flightInfo;
	}
}

package com.ehi.enterprise.android.network.request_params.reservation;

import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ModifyRequestParams {

	@SerializedName("driver_info")
	private EHIDriverInfo mDriverInfo;

	@SerializedName("additional_information")
	private EHIAdditionalInformation mAdditionalInformation;

	@SerializedName("payment_ids")
	private List mPaymentIds;

	@SerializedName("billing_info")
	private String mBillingInfo;

	@SerializedName("airline_information")
	private EHIAirlineInformation mAirlineInformation;

	public ModifyRequestParams(EHIDriverInfo driverInfo,
	                           EHIAdditionalInformation additionalInformation,
	                           List paymentIds,
	                           String billingInfo,
	                           EHIAirlineInformation airlineInformation) {
		mDriverInfo = driverInfo;
		mAdditionalInformation = additionalInformation;
		mPaymentIds = paymentIds;
		mBillingInfo = billingInfo;
		mAirlineInformation = airlineInformation;
	}
}

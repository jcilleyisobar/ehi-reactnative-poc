package com.ehi.enterprise.android.network.request_params.reservation;

import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.google.gson.annotations.SerializedName;

public class AdditionalInformationRequestParams {

	@SerializedName("pre_rates")
	private boolean mPreRates;

	@SerializedName("additional_information")
	private EHIAdditionalInformation mAdditionalInformation;

	public AdditionalInformationRequestParams(boolean preRates, EHIAdditionalInformation additionalInformation) {
		mAdditionalInformation = additionalInformation;
		mPreRates = preRates;
	}

}
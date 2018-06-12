package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostAdditionalInfoModifyRequestParams {

	@SerializedName("additional_information")
	private List<EHIAdditionalInformation> mAdditionalInformation;

	public PostAdditionalInfoModifyRequestParams(List<EHIAdditionalInformation> additionalInformation) {
		mAdditionalInformation = additionalInformation;
	}
}

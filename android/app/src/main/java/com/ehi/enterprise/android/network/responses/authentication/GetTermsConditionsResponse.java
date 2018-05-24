package com.ehi.enterprise.android.network.responses.authentication;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetTermsConditionsResponse extends BaseResponse {

	@SerializedName("eplus_terms_and_conditions")
	private String mTermsConditionsString;

	@SerializedName("eplus_terms_and_conditions_version")
	private String mTermsConditionsVersion;

	public String getTermsConditionsString() {
		return mTermsConditionsString;
	}

	public String getTermsConditionsVersion() {
		return mTermsConditionsVersion;
	}

}
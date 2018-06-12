package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class GetMorePrepayTermsConditionsResponse extends BaseResponse {

	@SerializedName("prepay_terms_and_conditions")
	String mContent;

	public String getContent() {
		return mContent;
	}

}
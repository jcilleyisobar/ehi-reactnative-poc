package com.ehi.enterprise.android.models.profile.payment.pangui;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHISecurityCredential extends EHIModel {

	@SerializedName("ServiceAccountToken")
	private String mServiceAccountToken;

	public EHISecurityCredential(String token) {
		mServiceAccountToken = token;
	}
}

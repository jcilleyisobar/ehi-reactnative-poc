package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIBusinessType extends EHIModel {

	@SerializedName("code")
	private String mBusinessTypeCode;

	public String getBusinessTypeCode() {
		return mBusinessTypeCode;
	}
}

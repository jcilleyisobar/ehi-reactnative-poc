package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHICategory extends EHIModel {

	@SerializedName("code")
	private String mCode;

	@SerializedName("name")
	private String mName;

	public String getCode() {
		return mCode;
	}

	public String getName() {
		return mName;
	}

}
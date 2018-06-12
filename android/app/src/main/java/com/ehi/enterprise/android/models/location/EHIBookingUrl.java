package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;


public class EHIBookingUrl extends EHIModel {

	@SerializedName("type")
	private String mType;

	@SerializedName("url")
	private String mUrl;

	public String getType() {
		return mType;
	}

	public String getUrl() {
		return mUrl;
	}

}

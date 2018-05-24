package com.ehi.enterprise.android.models.location;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;


public class EHIDriveDistance extends EHIModel {

	@SerializedName("unit")
	private String mUnit;

	@SerializedName("distance")
	private double mDistance;

	public String getUnit() {
		return mUnit;
	}

	public double getDistance() {
		return mDistance;
	}
}

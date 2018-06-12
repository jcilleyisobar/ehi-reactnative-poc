package com.ehi.enterprise.android.network.request_params.reservation;

import com.google.gson.annotations.SerializedName;

public class SelectUpgradeRequestParams {

	@SerializedName("car_class_code")
	private String mCarClassCode;

	public SelectUpgradeRequestParams(String carClassCode) {
		mCarClassCode = carClassCode;
	}

}
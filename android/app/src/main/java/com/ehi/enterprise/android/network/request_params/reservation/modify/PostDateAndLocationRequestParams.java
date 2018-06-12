package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class PostDateAndLocationRequestParams {

	@SerializedName("pickup_location_id")
	private String mPickupLocationId;

	@SerializedName("return_location_id")
	private String mReturnLocationId;

	@SerializedName("pickup_time")
	private Date mPickupTime;

	@SerializedName("return_time")
	private Date mReturnTime;

	public PostDateAndLocationRequestParams(String pickupLocationId,
	                                        String returnLocationId,
	                                        Date pickupTime,
	                                        Date returnTime) {

		mPickupLocationId = pickupLocationId;
		mReturnLocationId = returnLocationId;
		mPickupTime = pickupTime;
		mReturnTime = returnTime;
	}

}
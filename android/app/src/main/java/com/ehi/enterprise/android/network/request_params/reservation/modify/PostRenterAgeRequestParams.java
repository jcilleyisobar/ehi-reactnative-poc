package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.google.gson.annotations.SerializedName;

public class PostRenterAgeRequestParams {

	@SerializedName("renter_age")
	private int mRenterAge;

	public PostRenterAgeRequestParams(int renterAge) {
		mRenterAge = renterAge;
	}
}

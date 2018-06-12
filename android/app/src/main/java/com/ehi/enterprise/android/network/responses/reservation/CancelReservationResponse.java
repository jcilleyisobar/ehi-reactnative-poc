package com.ehi.enterprise.android.network.responses.reservation;

import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

public class CancelReservationResponse extends BaseResponse {

	@SerializedName("confirmation_number")
	private String mConfirmationNumber;

	public String getConfirmationNumber() {
		return mConfirmationNumber;
	}

}
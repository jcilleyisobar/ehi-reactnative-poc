package com.ehi.enterprise.android.network.request_params.reservation;

import com.google.gson.annotations.SerializedName;

public class CancelReservationRequestParams {

	@SerializedName("confirmation_number")
	private String mConfirmationNumber;

	public CancelReservationRequestParams(String confirmationNumber) {
		mConfirmationNumber = confirmationNumber;
	}

}
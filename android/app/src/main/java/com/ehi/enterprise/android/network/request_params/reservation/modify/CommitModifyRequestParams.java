package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.google.gson.annotations.SerializedName;

public class CommitModifyRequestParams {

	@SerializedName("prepay3_dspa_res")
	private String m3DsPaRes;

	@SerializedName("payment_id")
	private String mPaymentId;

	public CommitModifyRequestParams(String paymentId, String paRes) {
		mPaymentId = paymentId;
		m3DsPaRes = paRes;
	}

}
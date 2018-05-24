package com.ehi.enterprise.android.network.request_params.reservation.modify;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostPaymentMethodParams {

	@SerializedName("payment_ids")
	private List<String> mPaymentIds;

	@SerializedName("billing_account")
	private String mBillingAccount;

	@SerializedName("billing_account_type")
	private String mBillingAccountType;

	public PostPaymentMethodParams(List<String> paymentIds, String billingAccount, String billingAccountType) {
		mPaymentIds = paymentIds;
		mBillingAccount = billingAccount;
		mBillingAccountType = billingAccountType;
	}
}

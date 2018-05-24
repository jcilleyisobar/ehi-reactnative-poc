package com.ehi.enterprise.android.models.reservation;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIPayment extends EHIModel {

	@SerializedName("transaction_type") private String mTransactionType;
	@SerializedName("amount") private EHIPrice mAmount;
	@SerializedName("card_details") private EHICardDetails mCard;

	public EHIPayment(String mTransactionType, EHIPrice mAmount, EHICardDetails mCard) {
		this.mTransactionType = mTransactionType;
		this.mAmount = mAmount;
		this.mCard = mCard;
	}

	public String getTransactionType() {
		return mTransactionType;
	}

	public EHIPrice getAmount() {
		return mAmount;
	}

	public EHICardDetails getCard() {
		return mCard;
	}

	@Override
	public String toString() {
		return "EHIPayment{" +
				"mTransactionType='" + mTransactionType + '\'' +
				", mAmount=" + mAmount +
				", mCard=" + mCard +
				'}';
	}
}

package com.ehi.enterprise.android.models.profile.payment.pangui;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.google.gson.annotations.SerializedName;

public class EHIPaymentMedia extends EHIModel {

	@SerializedName("EncryptedPrimaryAccountNumber")
	private String mEncryptedPrimaryAccountNumber;

	@SerializedName("EncryptionKeyIdentifier")
	private String mEncryptionKeyIdentifier;

	@SerializedName("CardHolderName")
	private String mCardHolderName;

	@SerializedName("EncryptedCardCustomerIdentificationNumber")
	private String mCVV;

	// format MMYYYY
	@SerializedName("ExpirationMonthYearText")
	private String mExpirationMonthYearText;

	public EHIPaymentMedia(EHICreditCard card, String holderName) {
		mEncryptedPrimaryAccountNumber = card.getCreditCardNumber();
		mCardHolderName = holderName;
		mExpirationMonthYearText = card.getExpirationMonth() + card.getExpirationYear();
		mCVV = card.getCVV();
	}

}

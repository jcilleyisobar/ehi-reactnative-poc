package com.ehi.enterprise.android.network.request_params.reservation;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineInformation;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class CommitRequestParams {

	public static final String EXISTING = "EXISTING";
	public static final String NONE = "NONE";
	public static final String CUSTOM = "CUSTOM";

	@Retention(RetentionPolicy.SOURCE)
	@StringDef({EXISTING, NONE, CUSTOM})
	public @interface BillingTypes{}

	public static final String BILLING_ACCOUNT = "BILLING_ACCOUNT";
	public static final String PAY_AT_COUNTER = "PAY_AT_COUNTER";

	@Retention(RetentionPolicy.CLASS)
	@StringDef({BILLING_ACCOUNT, PAY_AT_COUNTER})
	public @interface BillingMethods{}

	@SerializedName("driver_info")
	private EHIDriverInfo mEHIDriverInfo;

	@SerializedName("additional_information")
	private List<EHIAdditionalInformation> mEHIAdditionalInformation;

	@SerializedName("payment_ids")
	private List<String> mPaymentIds;

	@SerializedName("billing_account")
	private String mBillingAccount;

	@SerializedName("prepay3_dspa_res")
	private final String mPaRes;

	@SerializedName("billing_account_type")
	private String mBillingType;

	@SerializedName("airline_information")
	private EHIAirlineInformation mEHIAirlineInformation;

	@SerializedName("trip_purpose")
	private String mTripPurpose;

	public CommitRequestParams(EHIDriverInfo driverInfo,
							   List<EHIAdditionalInformation> additionalInformation,
							   List<String> paymentIds,
							   String billingAccount,
							   String billingType,
							   EHIAirlineInformation airlineInformation,
							   String tripPurpose, String paRes) {
		if (driverInfo != null) {
			mEHIDriverInfo = driverInfo;
			mEHIDriverInfo.clearMaskedFields();
			mEHIDriverInfo.setSourceCode(Settings.SOURCE_CODE);
		}
		mEHIAdditionalInformation = additionalInformation;
		mPaymentIds = paymentIds;
		mBillingAccount = billingAccount;
		mEHIAirlineInformation = airlineInformation;
		mBillingType = billingType;
		mTripPurpose = tripPurpose;
		mPaRes = paRes;
	}

}
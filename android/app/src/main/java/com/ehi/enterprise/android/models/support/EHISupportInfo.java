package com.ehi.enterprise.android.models.support;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EHISupportInfo extends BaseResponse {

	@SerializedName("support_phone_numbers")
	private List<EHIPhone> mSupportPhoneNumbers;

	@SerializedName("forgot_password_url")
	private String mForgotPasswordURL;

	@SerializedName("prefill_url")
	private String mPrefillUrl;

	@SerializedName("join_eplus_url")
	private String mJoinEplusUrl;

	@SerializedName("activate_url")
	private String mActivateUrl;

	@SerializedName("national_reservation_url")
	private String mNationalReservationUrl;

	@SerializedName("alamo_reservation_url")
	private String mAlamoReservationUrl;

	@SerializedName("print_receipt_url")
	private String mPrintReceiptUrl;

	@SerializedName("support_send_message_url")
	private String mSupportSendMessageUrl;

	@SerializedName("support_answers_url")
	private String mSupportAnswerUrl;

    @SerializedName("feedback_url")
    private String mFeedbackUrl;

	@SerializedName("ec_forgot_password_url")
	private String mEcForgotPasswordUrl;

	public List<EHIPhone> getSupportPhoneNumbers() {
		return mSupportPhoneNumbers;
	}

	public String getForgotPasswordURL() {
		return mForgotPasswordURL;
	}

	public String getJoinEplusUrl() {
		return mJoinEplusUrl;
	}

	public String getPrefillUrl() {
		return mPrefillUrl;
	}

	public String getPrintReceiptUrl() {
		return mPrintReceiptUrl;
	}

	public String getActivateUrl() {
		return mActivateUrl;
	}

	public String getValidNationalReservationUrl() {
		if (!TextUtils.isEmpty(getNationalReservationUrl())) {
			return getNationalReservationUrl();
		}
		else {
			return Settings.NATIONAL_REDIRECT;
		}
	}

	public String getNationalReservationUrl() {
		return mNationalReservationUrl;
	}

	public String getValidAlamoReservationUrl() {
		if (!TextUtils.isEmpty(getAlamoReservationUrl())) {
			return getAlamoReservationUrl();
		}
		else {
			return Settings.ALAMO_REDIRECT;
		}
	}

	public String getAlamoReservationUrl() {
		return mAlamoReservationUrl;
	}

	public String getSupportSendMessageUrl() {
		return mSupportSendMessageUrl;
	}

	public String getSupportAnswerUrl() {
		return mSupportAnswerUrl;
	}

    public String getFeedbackUrl() {
        return mFeedbackUrl;
    }

    @Nullable
	public String getSupportPhoneNumber(EHIPhone.PhoneType type) {
		if (mSupportPhoneNumbers == null) {
			return null;
		}
		EHIPhone number;
		for (int i = 0; i < mSupportPhoneNumbers.size(); i++) {
			number = mSupportPhoneNumbers.get(i);

			if (!TextUtils.isEmpty(number.getPhoneNumber()) && number.getPhoneType().equals(type)) {
				return number.getPhoneNumber();
			}
		}
		return "";
	}

	@Nullable
	public EHIPhone getValidPhoneNumber() {
		if (mSupportPhoneNumbers == null) {
			return null;
		}
		for (int i = 0; i < mSupportPhoneNumbers.size(); i++) {
			if (!TextUtils.isEmpty(mSupportPhoneNumbers.get(i).getPhoneNumber())) {
				return mSupportPhoneNumbers.get(i);

			}
		}
		return null;
	}

	public String getEcForgotPasswordUrl() {
		return mEcForgotPasswordUrl;
	}
}
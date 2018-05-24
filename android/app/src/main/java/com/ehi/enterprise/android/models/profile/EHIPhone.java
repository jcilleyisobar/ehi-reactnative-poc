package com.ehi.enterprise.android.models.profile;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIPhone extends EHIModel {

	public enum PhoneType {
		OFFICE("OFFICE"),
		FAX("FAX"),
		AFTER_HOURS("AFTER HOURS"),
		HOME("HOME"),
		WORK("WORK"),
		MOBILE("MOBILE"),
		OTHER("OTHER"),
		CONTACT_US("CONTACT_US"),
		ROADSIDE_ASSISTANCE("ROADSIDE_ASSISTANCE"),
		EPLUS("EPLUS"),
		DISABILITES("DISABILITES"),
		DNR("DNR");

		private final String mValue;

		PhoneType(String value) {
			this.mValue = value;
		}

		public String getValue() {
			return mValue;
		}

		@NonNull
		public static PhoneType fromValue(String value) {
			if (value == null) {
				return OTHER;
			}
			if (value.equalsIgnoreCase(FAX.getValue())) {
				return FAX;
			}
			else if (value.equalsIgnoreCase(AFTER_HOURS.getValue())) {
				return AFTER_HOURS;
			}
			else if (value.equalsIgnoreCase(WORK.getValue())) {
				return WORK;
			}
			else if (value.equalsIgnoreCase(HOME.getValue())) {
				return HOME;
			}
			else if (value.equalsIgnoreCase(MOBILE.getValue())) {
				return MOBILE;
			}
			else if (value.equalsIgnoreCase(OTHER.getValue())) {
				return OTHER;
			}
			else if (value.equalsIgnoreCase(CONTACT_US.getValue())) {
				return CONTACT_US;
			}
			else if (value.equalsIgnoreCase(ROADSIDE_ASSISTANCE.getValue())) {
				return ROADSIDE_ASSISTANCE;
			}
			else if (value.equalsIgnoreCase(EPLUS.getValue())) {
				return EPLUS;
			}
			else if (value.equalsIgnoreCase(DISABILITES.getValue())) {
				return DISABILITES;
			}
			else if (value.equalsIgnoreCase(DNR.getValue())) {
				return DNR;
			}
			else if (value.equalsIgnoreCase(OFFICE.getValue())) {
				return OFFICE;
			}
			return OTHER;
		}
	}

	@SerializedName("phone_number")
	private String mPhoneNumber;

	@SerializedName("mask_phone_number")
	private String mMaskPhoneNumber;

	@SerializedName("phone_type")
	private String mPhoneType;

	@SerializedName("country_code")
	private String mCountryCode;

	@SerializedName("country_name")
	private String mCountryName;

	@SerializedName("priority")
	private String mPriority;

	@SerializedName("default_indicator")
	private boolean mDefaultIndicator;

	public EHIPhone() {
	}

	public EHIPhone(String phoneType) {
		mPhoneType = phoneType;
	}

	public EHIPhone(String number, String type) {
		mPhoneNumber = number;
		mPhoneType = type;
		mPriority = "1";
	}

	public String getPhoneNumber() {
		return mPhoneNumber;
	}

	@NonNull
	public PhoneType getPhoneType() {
		return PhoneType.fromValue(mPhoneType);
	}

	public static EHIPhone createEmpty(int priority) {
		EHIPhone number = new EHIPhone();
		number.setPhoneNumber("");
		number.setPhoneType(PhoneType.OTHER.getValue());
		return number;
	}

	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}

	public void setPhoneType(String phoneType) {
		mPhoneType = phoneType;
	}

	public String getPriority() {
		return mPriority;
	}

	public void setPriority(int priority) {
		mPriority = priority + "";
	}

	public boolean isDefaultIndicator() {
		return mDefaultIndicator;
	}

	@StringRes
	public int getTypeString() {
		switch (PhoneType.fromValue(mPhoneType)) {
			case MOBILE:
				return R.string.user_phone_type_mobile;
			case HOME:
				return R.string.user_phone_type_home;
			case FAX:
				return R.string.user_phone_type_fax;
			case OFFICE:
				return R.string.user_phone_type_office;
			case WORK:
				return R.string.user_phone_type_work;
			case OTHER:
			default:
				return R.string.user_phone_type_other;

		}
	}

	@Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EHIPhone)) return false;

        EHIPhone number = (EHIPhone) o;

        return !(mPhoneNumber != null ? !mPhoneNumber.equals(number.mPhoneNumber) : number.mPhoneNumber != null)
				&& !(mPhoneType != null ? !mPhoneType.equals(number.mPhoneType) : number.mPhoneType != null)
				&& !(mCountryCode != null ? !mCountryCode.equals(number.mCountryCode) : number.mCountryCode!= null)
				&& !(mCountryName != null ? !mCountryName.equals(number.mCountryName) : number.mCountryName!= null)
				&& !(mPriority != null ? !mPriority.equals(number.mPriority) : number.mPriority != null)
				&& !(mMaskPhoneNumber != null ? !mCountryName.equals(number.mMaskPhoneNumber) : number.mMaskPhoneNumber!= null);
    }

	@Override
	public int hashCode() {
		int result = mPhoneNumber != null ? mPhoneNumber.hashCode() : 0;
		result = 31 * result + (mPhoneType != null ? mPhoneType.hashCode() : 0);
		result = 31 * result + (mCountryName != null ? mCountryName.hashCode() : 0);
		result = 31 * result + (mCountryCode != null ? mCountryCode.hashCode() : 0);
		result = 31 * result + (mPriority != null ? mPriority.hashCode() : 0);
		result = 31 * result + (mMaskPhoneNumber != null ? mMaskPhoneNumber.hashCode() : 0);
		return result;
	}

	public String getMaskPhoneNumber() {
		return mMaskPhoneNumber;
	}

	public void setMaskPhoneNumber(String mMaskPhoneNumber) {
		this.mMaskPhoneNumber = mMaskPhoneNumber;
	}

	public String getCountryCode() {
		return mCountryCode;
	}

	public void setCountryCode(String mCountryCode) {
		this.mCountryCode = mCountryCode;
	}

	public String getCountryName() {
		return mCountryName;
	}

	public void setCountryName(String mCountryName) {
		this.mCountryName = mCountryName;
	}
}

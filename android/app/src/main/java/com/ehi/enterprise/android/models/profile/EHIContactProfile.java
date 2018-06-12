package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class EHIContactProfile extends EHIModel {

	private static final int PHONES_LINES_COUNT = 2;

	@SerializedName("email")
	private String mEmail;

	@SerializedName("mask_email")
	private String mMaskEmail;

	@SerializedName("phones")
	private List<EHIPhone> mPhones;

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
	}

	public List<EHIPhone> getPhones() {
		return mPhones;
	}

	public void setPhones(List<EHIPhone> phones) {
		mPhones = phones;
	}

	public EHIPhone getPhone(int position) {
		if (mPhones != null
				&& mPhones.size() > position) {
			return mPhones.get(position);
		}
		return EHIPhone.createEmpty(position + 1);
	}

	public void setPhoneNumber(int position, String number) {
		if (mPhones == null) {
			mPhones = new LinkedList<>();
		}
		if (mPhones.size() <= position) {
			for (int i = mPhones.size(); i <= position; i++) {
				mPhones.add(EHIPhone.createEmpty(i + 1));
			}
		}
		mPhones.get(position).setPhoneNumber(number);
		mPhones.get(position).setPriority(position + 1);
	}

	public void setPhoneType(int position, String type) {
		if (mPhones == null) {
			mPhones = new LinkedList<>();
		}

		if (mPhones.size() < PHONES_LINES_COUNT) {
			for (int i = mPhones.size(); i < PHONES_LINES_COUNT; i++) {
				mPhones.add(EHIPhone.createEmpty(i + 1));
			}
		}

		mPhones.get(position).setPhoneType(type);
		mPhones.get(position).setPriority(position + 1);
	}

	public String getMaskEmail() {
		return mMaskEmail;
	}

	public void setMaskEmail(String maskEmail) {
		this.mMaskEmail = maskEmail;
	}
}

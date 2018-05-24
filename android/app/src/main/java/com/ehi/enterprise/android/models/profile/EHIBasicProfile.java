package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIBasicProfile extends EHIModel {

	@SerializedName("first_name")
	private String mFirstName;

	@SerializedName("last_name")
	private String mLastName;

	@SerializedName("profile_id")
	private long mProfileId;

	@SerializedName("age")
	private int mAge;

	@SerializedName("loyalty_data")
	private EHILoyaltyData mLoyaltyData;

	public String getFirstName() {
		return mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public long getProfileId() {
		return mProfileId;
	}

	public String getFullName() {
		return mFirstName + " " + mLastName;
	}

	public void setFirstName(String firstName) {
		mFirstName = firstName;
	}

	public void setLastName(String lastName) {
		mLastName = lastName;
	}

	public EHILoyaltyData getLoyaltyData() {
		return mLoyaltyData;
	}

	public int getAge() {
		return mAge;
	}
}

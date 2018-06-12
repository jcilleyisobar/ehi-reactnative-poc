package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIToNextTierActivity extends EHIModel {

	@SerializedName("remaining_rental_count")
	private long mRemainingRentalCount;

	@SerializedName("remaining_rental_days")
	private long mRemainingRentalDays;

	@SerializedName("next_tier_rental_count")
	private long mNextTierRentalCount;

	@SerializedName("next_tier_rental_days")
	private long mNextTierRentalDays;

	@SerializedName("loyalty_tier")
	private String mLoyaltyTier;

	public long getRemainingRentalCount() {
		return mRemainingRentalCount;
	}

	public long getRemainingRentalDays() {
		return mRemainingRentalDays;
	}

	public long getNextTierRentalCount() {
		return mNextTierRentalCount;
	}

	public long getNextTierRentalDays() {
		return mNextTierRentalDays;
	}

	public String getLoyaltyTier() {
		return mLoyaltyTier;
	}

	public long getAccomplishedRentals() {
		return mNextTierRentalCount - mRemainingRentalCount;
	}

	public long getAccomplishedDays() {
		return mNextTierRentalDays - mRemainingRentalDays;
	}
}

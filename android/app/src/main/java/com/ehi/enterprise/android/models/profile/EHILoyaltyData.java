package com.ehi.enterprise.android.models.profile;

import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.NumberFormat;

public class EHILoyaltyData extends EHIModel {

    @SerializedName("id")
    private String mId;

    @SerializedName("loyalty_number")
    private String mLoyaltyNumber;

    @SerializedName("points_to_date")
    private long mPointsToDate;

    @SerializedName("year_to_date_rental_counts")
    private EHIYearToDateRentalCount mYearToDateRentalCount;

    @LoyaltyTier
    @SerializedName("loyalty_tier")
    private String mLoyaltyTier;

    @SerializedName("activity_to_next_tier")
    private EHIToNextTierActivity mActivityToNextTier;

    @LoyaltyProgramCode
    @SerializedName("loyalty_program_code")
    private int mLoyaltyProgramCode;

    public String getTierBonus() {
        switch (mLoyaltyTier.toLowerCase()) {
            case SILVER_STATUS:
                return "10%";
            case GOLD_STATUS:
                return "15%";
            case PLATINUM_STATUS:
                return "20%";
            default:
                return "0";
        }
    }

    public String getTierFreeUpgrades() {
        switch (mLoyaltyTier.toLowerCase()) {
            case SILVER_STATUS:
                return "1";
            case GOLD_STATUS:
                return "2";
            case PLATINUM_STATUS:
                return "4";
            default:
                return "0";
        }
    }

    public boolean hasTierUpgraded(String tier) {
        if (tier == null) {
             return !mLoyaltyTier.equalsIgnoreCase(PLUS_STATUS);
        }
        switch (tier.toLowerCase()) {
            case PLUS_STATUS:
                return !mLoyaltyTier.equalsIgnoreCase(tier);
            case SILVER_STATUS:
                return mLoyaltyTier.equalsIgnoreCase(GOLD_STATUS) || mLoyaltyTier.equalsIgnoreCase(PLATINUM_STATUS);
            case GOLD_STATUS:
                return mLoyaltyTier.equalsIgnoreCase(PLATINUM_STATUS);
            default:
                return false;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({GOLD_STATUS, PLATINUM_STATUS, SILVER_STATUS, PLUS_STATUS})
    public @interface LoyaltyTier {
    }

    public static final String GOLD_STATUS = "gold";
    public static final String SILVER_STATUS = "silver";
    public static final String PLUS_STATUS = "plus";
    public static final String PLATINUM_STATUS = "platinum";


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ENTERPRISE_PLUS, EMERALD_CLUB, NON_LOYALTY})
    public @interface LoyaltyProgramCode {
    }

    public static final int ENTERPRISE_PLUS = 1;
    public static final int EMERALD_CLUB = 2;
    public static final int NON_LOYALTY = 4;

    public String getId() {
        return mId;
    }

    public String getLoyaltyNumber() {
        return mLoyaltyNumber;
    }

    public long getPointsToDate() {
        return mPointsToDate;
    }

    public String getFormattedPointsToDate() {
        return NumberFormat.getNumberInstance().format(mPointsToDate);
    }

    public long getRentalsToDate() {
        if (mYearToDateRentalCount != null) {
            return mYearToDateRentalCount.getNumberOfRentals();
        }
        return 0;
    }

    public long getRentalDaysToDate() {
        if (mYearToDateRentalCount != null) {
            return mYearToDateRentalCount.getNumberOfRentalDays();
        }
        return 0;
    }

    public void setActivityToNextTier(EHIToNextTierActivity activityToNextTier) {
        mActivityToNextTier = activityToNextTier;
    }

    @LoyaltyTier
    public String getLoyaltyTier() {
        return mLoyaltyTier;
    }

    public void setTier(String tier) {
        mLoyaltyTier = tier;
    }

    public EHIToNextTierActivity getActivityToNextTier() {
        return mActivityToNextTier;
    }

    public CharSequence getFormattedRentalPercent(Resources resources) {
        long requiredRentalCount = getRentalsToDate()
                + getActivityToNextTier().getRemainingRentalCount();


        if (getLoyaltyTier().equalsIgnoreCase(PLATINUM_STATUS)) {
            requiredRentalCount = 0;
        }

        return new TokenizedString.Formatter<EHIStringToken>(resources)
                .addTokenAndValue(EHIStringToken.TO_DATE, getRentalsToDate() + "")
                .addTokenAndValue(EHIStringToken.NEEDED, requiredRentalCount + "")
                .formatString(R.string.rewards_rentals_completed_title)
                .format();
    }

    public CharSequence getFormattedDayPercent(Resources resources) {
        long requiredDaysCount = getRentalDaysToDate()
                + getActivityToNextTier().getRemainingRentalDays();

        if (getLoyaltyTier().equalsIgnoreCase(PLATINUM_STATUS)) {
            requiredDaysCount = 0;
        }

        return new TokenizedString.Formatter<EHIStringToken>(resources)
                .addTokenAndValue(EHIStringToken.TO_DATE, getRentalsToDate() + "")
                .addTokenAndValue(EHIStringToken.NEEDED, requiredDaysCount + "")
                .formatString(R.string.rewards_days_completed_title)
                .format();
    }

    @LoyaltyProgramCode
    public int getLoyaltyProgramCode() {
        return mLoyaltyProgramCode;
    }
}

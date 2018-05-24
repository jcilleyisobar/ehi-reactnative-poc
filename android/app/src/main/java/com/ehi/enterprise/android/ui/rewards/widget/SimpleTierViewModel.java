package com.ehi.enterprise.android.ui.rewards.widget;

import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.EHIToNextTierActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SimpleTierViewModel extends ManagersAccessViewModel {
    public static final int TOTAL_DAYS = 1;
    public static final int TOTAL_RENTAL = 2;

    private @ColorRes int mFillColor;
    private int mNumberOfSections;
    private boolean mAreSectionsVisible;
    private int mFilledSectionsNumber;

    final ReactorTextViewState gaugeLength = new ReactorTextViewState();
    final ReactorTextViewState gaugeType = new ReactorTextViewState();
    final ReactorTextViewState gaugeLengthDescription = new ReactorTextViewState();

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOTAL_DAYS, TOTAL_RENTAL})
    public @interface LoyaltyTotalType {
    }
    private @LoyaltyTotalType int mLoyaltyTotalType;

    public void setLoyaltyTotalType(int loyaltyTotalType) {
        this.mLoyaltyTotalType = loyaltyTotalType;
    }

    public void setupGaugeData(EHILoyaltyData loyaltyData) {
        final EHIToNextTierActivity nextTier = loyaltyData.getActivityToNextTier();
        @EHILoyaltyData.LoyaltyTier String loyaltyTier = loyaltyData.getLoyaltyTier();
        switch (loyaltyTier.toLowerCase()) {
            case EHILoyaltyData.PLUS_STATUS:
                mFillColor = R.color.rewards_silver_benefits_color;
                mAreSectionsVisible = true;
                break;
            case EHILoyaltyData.SILVER_STATUS:
                mFillColor = R.color.rewards_gold_benefits_color;
                mAreSectionsVisible = false;
                break;
            case EHILoyaltyData.GOLD_STATUS:
                mFillColor = R.color.ehi_black;
                mAreSectionsVisible = false;
                break;
        }

        if (mLoyaltyTotalType == TOTAL_DAYS) {
            mFilledSectionsNumber = (int) nextTier.getAccomplishedDays();
            mNumberOfSections = (int) nextTier.getNextTierRentalDays();
            gaugeType.setText(getResources().getString(R.string.rewards_welcome_days));
        } else {
            mFilledSectionsNumber = (int) nextTier.getAccomplishedRentals();
            mNumberOfSections = (int) nextTier.getNextTierRentalCount();
            gaugeType.setText(getResources().getString(R.string.rewards_welcome_rentals));
        }
        gaugeLength.setText(String.valueOf(mFilledSectionsNumber));
        gaugeLengthDescription.setText(
                new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .addTokenAndValue(EHIStringToken.NUMBER, String.valueOf(mNumberOfSections))
                        .formatString(R.string.rewards_welcome_of_total)
                        .format());
    }

    public @ColorRes int getFillColor() {
        return mFillColor;
    }

    public int getNumberOfSections() {
        return mNumberOfSections;
    }

    public boolean areSectionsVisible() {
        return mAreSectionsVisible;
    }

    public int getFilledSectionsNumber() {
        return mFilledSectionsNumber;
    }
}

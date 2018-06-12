package com.ehi.enterprise.android.ui.rewards;

import android.content.res.AssetManager;
import android.support.annotation.ColorRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.profile.EHIBasicProfile;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class RewardsViewModel extends EnterprisePlusTermsAndConditionsViewModel {

    private static final int LOT_OF_POINTS = 1000;
    private static final int ALMOST_NEXT_TIER_RENTALS = 1;
    private static final int ALMOST_NEXT_TIER_DAYS = 3;

    final ReactorTextViewState headerTitle = new ReactorTextViewState();
    final ReactorTextViewState gaugeTitle = new ReactorTextViewState();
    final ReactorTextViewState headerSubtitle = new ReactorTextViewState();
    final ReactorImageViewState headerImage = new ReactorImageViewState();
    final ReactorViewState legalCopyText = new ReactorViewState();

    private boolean mShouldUseDoubleTier;
    private boolean mShouldHideGaugeView;

    public void logOut() {
        getManagers().getLoginManager().logOut();
    }

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        setupTierStatus();
    }

    private void setupTierStatus() {
        final EHIBasicProfile profile = getUserProfileCollection().getBasicProfile();
        final EHILoyaltyData loyaltyData = profile.getLoyaltyData();
        if (loyaltyData != null) {
            @EHILoyaltyData.LoyaltyTier String loyaltyTier = loyaltyData.getLoyaltyTier();
            final String nextTier = loyaltyData.getActivityToNextTier().getLoyaltyTier();
            @ColorRes int gaugeTitleColor = R.color.rewards_silver_benefits_color;
            switch (loyaltyTier.toLowerCase()) {
                case EHILoyaltyData.SILVER_STATUS:
                    mShouldUseDoubleTier = true;
                    gaugeTitleColor = R.color.rewards_gold_benefits_color;
                    break;
                case EHILoyaltyData.GOLD_STATUS:
                    gaugeTitleColor = R.color.ehi_black;
                    mShouldUseDoubleTier = true;
                    break;
                case EHILoyaltyData.PLATINUM_STATUS:
                    mShouldHideGaugeView = true;
                    break;
                case EHILoyaltyData.PLUS_STATUS:
                default:
                    mShouldUseDoubleTier = false;
                    gaugeTitleColor = R.color.rewards_silver_benefits_color;
                    break;
            }
            setGaugeFormattedTitle(nextTier, gaugeTitleColor);
        }
    }

    private void setGaugeFormattedTitle(String nextTier, int gaugeTitleColor) {
        SpannableStringBuilder bld = new SpannableStringBuilder();
        SpannableString progressToReach = new SpannableString(getResources().getString(R.string.rewards_gauge_progress_reach));

        final AssetManager assets = getResources().getAssets();
        CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan("", ResourcesCompat.getFont(getContext(), R.font.source_sans_bold));
        progressToReach.setSpan(
                typefaceSpan,
                0,
                progressToReach.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        progressToReach.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_black)), 0, progressToReach.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bld.append(progressToReach);
        bld.append(" ");

        CharSequence gaugeFormattedTitle = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.rewards_welcome_current_tier)
                .addTokenAndValue(EHIStringToken.TIER, nextTier)
                .format();
        SpannableString tier = new SpannableString(gaugeFormattedTitle);
        tier.setSpan(
                typefaceSpan,
                0,
                tier.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        tier.setSpan(new ForegroundColorSpan(getResources().getColor(gaugeTitleColor)), 0, tier.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        bld.append(tier);

        gaugeTitle.setText(bld);
    }

    public void setUpHeader() {
        final EHIBasicProfile profile = getUserProfileCollection().getBasicProfile();
        final EHILoyaltyData loyaltyData = profile.getLoyaltyData();
        final String oldTier = getManagers().getLocalDataManager().getLoyaltyTier(loyaltyData.getLoyaltyNumber());
        final String currentTier = loyaltyData.getLoyaltyTier().toLowerCase();

        headerImage.setImageResource(R.drawable.rewards_default_header);
        legalCopyText.setVisibility(ReactorTextViewState.GONE);

        if (EHITextUtils.isEmpty(oldTier) && loyaltyData.getPointsToDate() == 0) {
            // new member
            headerTitle.setText(R.string.enroll_confirmation_title);
            headerSubtitle.setText(R.string.rewards_welcome_earn_points_toward_free_rental_day);
            headerImage.setImageResource(R.drawable.rewards_sun_up_header);
        } else if (loyaltyData.hasTierUpgraded(oldTier) && !EHITextUtils.isEmpty(oldTier) ) {
            // new tier
            headerTitle.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_welcome_you_made_tier)
                    .addTokenAndValue(EHIStringToken.TIER, currentTier)
                    .format());
            final int resId = currentTier.equalsIgnoreCase(EHILoyaltyData.SILVER_STATUS) ?
                    R.string.rewards_welcome_tier_info :
                    R.string.rewards_welcome_tier_info_plural;
            headerSubtitle.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(resId)
                    .addTokenAndValue(EHIStringToken.PERCENT, loyaltyData.getTierBonus())
                    .addTokenAndValue(EHIStringToken.NUMBER, loyaltyData.getTierFreeUpgrades())
                    .format());
            headerImage.setImageResource(R.drawable.rewards_fireworks_header);
            legalCopyText.setVisibility(ReactorTextViewState.VISIBLE);
        } else if (loyaltyData.getActivityToNextTier().getRemainingRentalCount() == ALMOST_NEXT_TIER_RENTALS) {
            // almost new tier
            headerTitle.setText(R.string.rewards_welcome_you_are_almost_there);
            headerSubtitle.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_welcome_almost_next_tier)
                    .addTokenAndValue(EHIStringToken.NUMBER, String.valueOf(loyaltyData.getActivityToNextTier().getRemainingRentalCount()))
                    .addTokenAndValue(EHIStringToken.TIER, loyaltyData.getActivityToNextTier().getLoyaltyTier())
                    .format());
        } else if ((currentTier.equalsIgnoreCase(EHILoyaltyData.SILVER_STATUS) || currentTier.equalsIgnoreCase(EHILoyaltyData.GOLD_STATUS))
                && loyaltyData.getActivityToNextTier().getRemainingRentalDays() <= ALMOST_NEXT_TIER_DAYS) {
            // almost gold or platinum
            headerTitle.setText(R.string.rewards_welcome_you_are_almost_there);
            headerSubtitle.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_welcome_almost_gold_platinum_tier)
                    .addTokenAndValue(EHIStringToken.DAY, String.valueOf(loyaltyData.getActivityToNextTier().getRemainingRentalDays()))
                    .format());
        } else if (loyaltyData.getPointsToDate() >= LOT_OF_POINTS) {
            // 1000+ points
            headerTitle.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_welcome_back)
                    .addTokenAndValue(EHIStringToken.NAME, profile.getFirstName())
                    .format());
            headerSubtitle.setText(getResources().getText(R.string.rewards_welcome_you_have_a_lot_of_points)
                    + "\n"
                    + getResources().getText(R.string.rewards_welcome_start_a_new_rental));
        } else {
            // default
            headerTitle.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_welcome_back)
                    .addTokenAndValue(EHIStringToken.NAME, profile.getFirstName())
                    .format());
            headerSubtitle.setVisibility(View.GONE);
            headerImage.setImageResource(R.drawable.rewards_sun_down_header);
        }

        getManagers().getLocalDataManager().setLoyaltyTier(loyaltyData.getLoyaltyNumber(), loyaltyData.getLoyaltyTier());
    }

    public boolean shouldUseDoubleTier() {
        return mShouldUseDoubleTier;
    }

    public boolean shouldHideGaugeView() {
        return mShouldHideGaugeView;
    }
}

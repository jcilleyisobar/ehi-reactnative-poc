package com.ehi.enterprise.android.ui.rewards.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.AboutTierHeaderViewBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class AboutTierHeaderView extends DataBindingViewModelView<ManagersAccessViewModel, AboutTierHeaderViewBinding> {

    public AboutTierHeaderView(Context context) {
        this(context, null, 0);
    }

    public AboutTierHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AboutTierHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_rewards_about_tier_header, null));
            return;
        }

        createViewBinding(R.layout.v_rewards_about_tier_header);
    }

    public void setupView(String currentTier) {
        CharSequence percent = getResources().getString(R.string.about_e_p_silver_bonus_points);
        CharSequence number = getResources().getString(R.string.about_e_p_silver_upgrades_per_year);

        if (currentTier.equalsIgnoreCase(EHILoyaltyData.PLUS_STATUS)) {
            getViewBinding().rewardsAboutTierMembersGet.setVisibility(View.GONE);
            getViewBinding().rewardsAboutTierMembersBonus.setVisibility(View.GONE);
            getViewBinding().rewardsAboutTierMembersPlusTier.setVisibility(View.VISIBLE);
        } else {
            CharSequence formattedGetString = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_about_tier_members_get)
                    .addTokenAndValue(EHIStringToken.TIER, currentTier)
                    .format();
            getViewBinding().rewardsAboutTierMembersGet.setText(formattedGetString);

            if (currentTier.equalsIgnoreCase(EHILoyaltyData.GOLD_STATUS)) {
                percent = getResources().getString(R.string.about_e_p_gold_bonus_points);
                number = getResources().getString(R.string.about_e_p_gold_upgrades_per_year);
            } else if (currentTier.equalsIgnoreCase(EHILoyaltyData.PLATINUM_STATUS)) {
                percent = getResources().getString(R.string.about_e_p_platinum_bonus_points);
                number = getResources().getString(R.string.about_e_p_platinum_upgrades_per_year);
            }
            CharSequence formattedBonusString = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.rewards_about_tier_members_bonus)
                    .addTokenAndValue(EHIStringToken.PERCENT, percent)
                    .addTokenAndValue(EHIStringToken.NUMBER, number)
                    .format();
            getViewBinding().rewardsAboutTierMembersBonus.setText(formattedBonusString);
        }
    }

}

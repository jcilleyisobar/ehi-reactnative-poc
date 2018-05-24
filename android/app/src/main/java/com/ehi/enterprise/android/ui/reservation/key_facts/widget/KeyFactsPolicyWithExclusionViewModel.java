package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.support.annotation.StringRes;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.List;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class KeyFactsPolicyWithExclusionViewModel extends ManagersAccessViewModel {
    final ReactorTextViewState policyName = new ReactorTextViewState();
    final ReactorTextViewState policyPrice = new ReactorTextViewState();
    final ReactorTextViewState exclusion = new ReactorTextViewState();
    private EHIKeyFactsPolicy mPolicy;
    private List<EHIKeyFactsPolicy> mExclusions;
    private EHIExtraItem mExtraItem;
    private KeyFactsPolicyWithExclusionCell.PolicyExtraPair mPolicyExtraPair;

    public void setPolicy(final EHIKeyFactsPolicy policy) {
        mPolicy = policy;
        mExclusions = mPolicy.getPolicyExclusions();
        if (ListUtils.isEmpty(mExclusions)) {
            exclusion.setVisibility(View.GONE);
        }
        else {
            exclusion.setText(R.string.key_facts_protections_exclusions);
            exclusion.setVisibility(View.VISIBLE);
        }
        policyName.setText(mPolicy.getDescription());
    }

    public EHIKeyFactsPolicy getPolicy() {
        return mPolicy;
    }

    public List<EHIKeyFactsPolicy> getExclusions() {
        return mExclusions;
    }

    public void setExtraItem(final EHIExtraItem extraItem) {
        mExtraItem = extraItem;
        exclusion.setVisibility(View.GONE);
        policyName.setText(mExtraItem.getName());
        setPolicyPrice(extraItem);
    }

    private void setPolicyPrice(final EHIExtraItem extraItem) {
        if(extraItem.getRateAmountView() == null){
            return;
        }
        policyPrice.setVisibility(View.VISIBLE);
        final TokenizedString.Formatter<EHIStringToken> ehiStringTokenFormatter = new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.reservation_line_item_rental_rate_title)
                .addTokenAndValue(EHIStringToken.PRICE, String.valueOf(extraItem.getRateAmountView().getFormattedPrice(false)));

        @StringRes int unitString = getUnitString(extraItem);

        if(unitString != 0) {
            ehiStringTokenFormatter.addTokenAndValue(EHIStringToken.UNIT, getResources().getString(unitString));
            policyPrice.setText(ehiStringTokenFormatter.format());
        }
        else {
            policyPrice.setText("("+mExtraItem.getRateAmountView().getFormattedPrice(false) +")");
        }
    }

    private int getUnitString(final EHIExtraItem extraItem) {
        @StringRes int unitString = 0;
        if(EHIExtraItem.HOURLY.equalsIgnoreCase(extraItem.getRateType())){
            unitString = R.string.reservation_rate_hourly_unit;
        }
        else if(EHIExtraItem.DAILY.equalsIgnoreCase(extraItem.getRateType())){
            unitString = R.string.reservation_rate_daily_unit;
        }
        else if(EHIExtraItem.WEEKLY.equalsIgnoreCase(extraItem.getRateType())){
            unitString = R.string.reservation_rate_weekly_unit;
        }
        else if(EHIExtraItem.RENTAL.equalsIgnoreCase(extraItem.getRateType())){
            unitString = R.string.reservation_rate_rental_unit;
        }
        else if(EHIExtraItem.GALLON.equalsIgnoreCase(extraItem.getRateType())){
            unitString = R.string.reservation_rate_gallon_unit;
        }
        return unitString;
    }

    public EHIExtraItem getExtraItem() {
        return mExtraItem;
    }

    public void setPolicyExtraPair(final KeyFactsPolicyWithExclusionCell.PolicyExtraPair policyExtraPair) {
        mPolicyExtraPair = policyExtraPair;
        setPolicy(mPolicyExtraPair.policy);
        if(mPolicyExtraPair.policyPrice != null) {
            setPolicyPrice(mPolicyExtraPair.policyPrice);
        }
    }
}

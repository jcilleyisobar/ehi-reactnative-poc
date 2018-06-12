package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ProtectionProductsViewModel extends ManagersAccessViewModel{
    final ReactorVar<List<KeyFactsPolicyWithExclusionCell.PolicyExtraPair>> mIncludedProtectionCells = new ReactorVar<>();
    final ReactorVar<List<KeyFactsPolicyWithExclusionCell.PolicyExtraPair>> mOptionalProtectionCells = new ReactorVar<>();
    final ReactorViewState includedProtectionsContainer = new ReactorViewState();
    final ReactorTextViewState includedProtectionsSubtitle = new ReactorTextViewState();
    final ReactorViewState optionalProtectionsContainer = new ReactorViewState();
    final ReactorTextViewState optionalProtectionsSubtitle = new ReactorTextViewState();

    public void setPoliciesAndExtras(final List<EHIKeyFactsPolicy> policies,
                                     final EHIExtras carClassExtras) {
        List<KeyFactsPolicyWithExclusionCell.PolicyExtraPair> includedProtectionCells = new ArrayList<>();
        List<KeyFactsPolicyWithExclusionCell.PolicyExtraPair> optionalProtectionCells = new ArrayList<>();

        for (EHIKeyFactsPolicy policy : policies) {
            EHIExtraItem carClassExtraItem = null;
            for (EHIExtraItem item : carClassExtras.getInsurance()) {
                if(item.getCode().equalsIgnoreCase(policy.getCode())){
                    carClassExtraItem = item;
                }
            }

            if(policy.isIncluded()){
                includedProtectionCells.add(new KeyFactsPolicyWithExclusionCell.PolicyExtraPair(policy, null));
            }
            else {
                optionalProtectionCells.add(new KeyFactsPolicyWithExclusionCell.PolicyExtraPair(policy, carClassExtraItem));
            }

        }

        if(ListUtils.isEmpty(includedProtectionCells)){
            includedProtectionsSubtitle.setText(R.string.key_facts_no_included_protections);
            includedProtectionsContainer.setVisibility(View.GONE);
        }
        else {
            includedProtectionsSubtitle.setText(R.string.key_facts_included_protections_subtitle);
            includedProtectionsContainer.setVisibility(View.VISIBLE);
            mIncludedProtectionCells.setValue(includedProtectionCells);
        }

        if(ListUtils.isEmpty(optionalProtectionCells)){
            optionalProtectionsSubtitle.setText(R.string.key_facts_no_protections);
            optionalProtectionsContainer.setVisibility(View.GONE);
        }
        else {
            optionalProtectionsSubtitle.setText(R.string.key_facts_optional_bookable_protections_subtitle);
            optionalProtectionsContainer.setVisibility(View.VISIBLE);
            mOptionalProtectionCells.setValue(optionalProtectionCells);
        }
    }

    public List<KeyFactsPolicyWithExclusionCell.PolicyExtraPair> getIncludedProtectionCells() {
        return mIncludedProtectionCells.getValue();
    }

    public List<KeyFactsPolicyWithExclusionCell.PolicyExtraPair> getOptionalProtectionCells() {
        return mOptionalProtectionCells.getValue();
    }

}

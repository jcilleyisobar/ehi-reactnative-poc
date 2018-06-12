package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class AdditionalRentalPoliciesViewModel extends ManagersAccessViewModel{
    final ReactorTextViewState title = new ReactorTextViewState();
    final ReactorTextViewState subtitle = new ReactorTextViewState();
    final ReactorViewState content = new ReactorViewState();
    final ReactorVar<List<EHIKeyFactsPolicy>> mPolicies = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setText(R.string.key_facts_additional_rental_policies_title);
        content.setVisibility(View.GONE);
    }

    public void cellTitleClicked() {
        content.setVisibility(content.visibility().getRawValue() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void setPolicies(final List<EHIKeyFactsPolicy> policies) {
        if(ListUtils.isEmpty(policies)){
            subtitle.setText(R.string.key_facts_no_additional_rental_policies);
        }
        else {
            subtitle.setText(R.string.key_facts_additional_rental_policies_subtitle);
            mPolicies.setValue(policies);
        }
    }

    public List<EHIKeyFactsPolicy> getPolicies() {
        return mPolicies.getValue();
    }
}

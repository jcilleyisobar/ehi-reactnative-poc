package com.ehi.enterprise.android.ui.reservation.key_facts;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class KeyFactsDetailsViewModel extends ManagersAccessViewModel{
    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorTextViewState keyFactsName = new ReactorTextViewState();
    final ReactorTextViewState content = new ReactorTextViewState();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.policy_details_title);
    }

    public void setPolicy(EHIKeyFactsPolicy policy){
        keyFactsName.setText(policy.getDescription());
        content.setText(policy.getPolicyText());
    }

    public void setExtra(final EHIExtraItem extra) {
        keyFactsName.setText(extra.getName());
        content.setText(extra.getDetailedDescription());
    }
}

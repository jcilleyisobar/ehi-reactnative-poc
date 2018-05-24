package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class KeyFactsPolicyCellViewModel extends ManagersAccessViewModel{
    final ReactorTextViewState policyName = new ReactorTextViewState();
    private EHIKeyFactsPolicy mPolicy;

    public void setPolicy(final EHIKeyFactsPolicy policy) {
        mPolicy = policy;
        policyName.setText(mPolicy.getDescription() + " >");
    }

    public EHIKeyFactsPolicy getPolicy() {
        return mPolicy;
    }
}

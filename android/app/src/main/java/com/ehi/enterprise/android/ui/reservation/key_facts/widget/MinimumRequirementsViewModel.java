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
public class MinimumRequirementsViewModel extends ManagersAccessViewModel{
    final ReactorTextViewState title = new ReactorTextViewState();
    final ReactorViewState content = new ReactorViewState();
    final ReactorTextViewState subtitle = new ReactorTextViewState();
    private ReactorVar<List<EHIKeyFactsPolicy>> mKeyFactsPolicies = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setText(R.string.key_facts_minimum_requirements_title);
        content.setVisibility(View.GONE);
    }

    public void setKeyFactsPolicies(final List<EHIKeyFactsPolicy> keyFactsPolicies) {
        mKeyFactsPolicies.setValue(keyFactsPolicies);
        if(ListUtils.isEmpty(mKeyFactsPolicies.getRawValue())){
            subtitle.setText(R.string.key_facts_no_minimum_requirements);
        }
    }

    public List<EHIKeyFactsPolicy> getKeyFactsPolicies() {
        return mKeyFactsPolicies.getValue();
    }

    public void cellTitleClicked() {
        content.setVisibility(content.visibility().getRawValue() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}

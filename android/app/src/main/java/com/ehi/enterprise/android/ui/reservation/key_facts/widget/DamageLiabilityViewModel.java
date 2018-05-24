package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class DamageLiabilityViewModel extends ManagersAccessViewModel{
    final ReactorTextViewState title = new ReactorTextViewState();
    final ReactorTextViewState content = new ReactorTextViewState();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setText(R.string.key_facts_damage_liability_title);
        content.setVisibility(View.GONE);
    }

    public void cellTitleClicked() {
        content.setVisibility(content.visibility().getRawValue() == View.VISIBLE ? View.GONE
                                                                                 : View.VISIBLE);
    }
}

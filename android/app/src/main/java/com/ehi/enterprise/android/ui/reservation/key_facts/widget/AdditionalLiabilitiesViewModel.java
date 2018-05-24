package com.ehi.enterprise.android.ui.reservation.key_facts.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class AdditionalLiabilitiesViewModel extends ManagersAccessViewModel{
    final ReactorTextViewState title = new ReactorTextViewState();
    final ReactorViewState content = new ReactorViewState();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setText(R.string.key_facts_additional_liabilites_title);
        content.setVisibility(View.GONE);
    }

    public void cellTitleClicked() {
        content.setVisibility(content.visibility().getRawValue() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }
}

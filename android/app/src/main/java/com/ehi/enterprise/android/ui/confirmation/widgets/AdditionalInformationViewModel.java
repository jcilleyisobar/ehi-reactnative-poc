package com.ehi.enterprise.android.ui.confirmation.widgets;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class AdditionalInformationViewModel extends ManagersAccessViewModel {

    final ReactorViewState greenArrow = new ReactorViewState();

    public void hideGreenArrow() {
        greenArrow.setVisibility(ReactorViewState.GONE);
    }

    public void showGreenArrow() {
        greenArrow.setVisibility(ReactorViewState.VISIBLE);
    }
}

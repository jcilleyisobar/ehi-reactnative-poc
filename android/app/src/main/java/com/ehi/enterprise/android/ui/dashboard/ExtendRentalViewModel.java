package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ExtendRentalViewModel extends ManagersAccessViewModel {
    final ReactorTextViewState callNumber = new ReactorTextViewState();
    final ReactorTextViewState confirmationNumber = new ReactorTextViewState();

    private String mConfirmationNumber;

    public String getConfirmationNumber() {
        return mConfirmationNumber;
    }

    public void setConfirmationNumber(String confirmationNumber) {
        mConfirmationNumber = confirmationNumber;
    }
}

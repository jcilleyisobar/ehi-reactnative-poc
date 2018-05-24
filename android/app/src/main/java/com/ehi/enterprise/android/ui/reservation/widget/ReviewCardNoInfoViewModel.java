package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewCardNoInfoViewModel extends ManagersAccessViewModel {

    final ReactorTextViewState addPaymentButton = new ReactorTextViewState();
    final ReactorTextViewState creditCardAddedButton = new ReactorTextViewState();

    private boolean mCreditCardAdded = false;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        setAddPaymentButtonVisible(!mCreditCardAdded);
        setCreditCardAddedButtonVisible(mCreditCardAdded);
    }

    public void setAddPaymentButtonVisible(boolean isVisible) {
        addPaymentButton.setVisible(isVisible);
    }

    public void setCreditCardAddedButtonVisible(boolean isVisible) {
        creditCardAddedButton.setVisible(isVisible);
    }

    public void setCreditCardAdded(boolean isAdded) {
        mCreditCardAdded = isAdded;
    }

    public boolean isCreditCardAdded() {
        return mCreditCardAdded;
    }

}
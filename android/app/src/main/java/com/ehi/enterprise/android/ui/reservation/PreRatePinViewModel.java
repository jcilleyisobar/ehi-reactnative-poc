package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class PreRatePinViewModel extends ManagersAccessViewModel {

    final ReactorTextViewState pinInput = new ReactorTextViewState();
    final ReactorViewState pinSubmitButton = new ReactorViewState();

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        pinSubmitButton.enabled().setValue(false);

        pinInput.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(String newValue) {

                if (newValue.length() == 3){
                    pinSubmitButton.enabled().setValue(true);
                } else {
                    pinSubmitButton.enabled().setValue(false);
                }
            }
        });
    }
}

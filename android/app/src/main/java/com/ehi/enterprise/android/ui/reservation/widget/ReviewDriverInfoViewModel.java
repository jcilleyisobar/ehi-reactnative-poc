package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewDriverInfoViewModel extends ManagersAccessViewModel {

    //region reactive vars/state
    final ReactorTextViewState driverName = new ReactorTextViewState();
    final ReactorTextViewState driverEmail = new ReactorTextViewState();
    final ReactorTextViewState driverPhone = new ReactorTextViewState();
    final ReactorViewState greenArrow = new ReactorViewState();
    //endregion

    public void setDriverInfo(EHIDriverInfo driverInfo) {
        if (driverInfo.getFirstName() != null
                && driverInfo.getLastName() != null) {
            driverName.setText(driverInfo.getFirstName() + " " + driverInfo.getLastName());
        }
        if (driverInfo.getMaskEmailAddress() != null) {
            driverEmail.setText(driverInfo.getMaskEmailAddress());
        } else {
            driverEmail.setText(driverInfo.getEmailAddress());
        }
        if (driverInfo.getPhone() != null) {
            if (driverInfo.getPhone().getMaskPhoneNumber() != null) {
                driverPhone.setText(driverInfo.getPhone().getMaskPhoneNumber());
            } else {
                driverPhone.setText(driverInfo.getPhone().getPhoneNumber());
            }
        }
    }

    public void hideGreenArrow() {
        greenArrow.setVisibility(ReactorViewState.GONE);
    }

    public void showGreenArrow() {
        greenArrow.setVisibility(ReactorViewState.VISIBLE);
    }
}

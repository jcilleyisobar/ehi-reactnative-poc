package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewDateTimeViewModel extends ManagersAccessViewModel {

    //region reactive states
    final ReactorTextViewState titleText = new ReactorTextViewState();
    final ReactorTextViewState labelText = new ReactorTextViewState();
    final ReactorViewState greenArrowView = new ReactorViewState();
    //endregion

    public void setTitleText(String title) {
        titleText.setText(title);
    }

    public void setLabelText(String label) {
        labelText.setText(label);
    }

    public void hideGreenArrow() {
        greenArrowView.setVisibility(ReactorViewState.GONE);
    }

    public void showGreenArrow() {
        greenArrowView.setVisibility(ReactorViewState.VISIBLE);
    }
}

package com.ehi.enterprise.android.ui.widget;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

public class ModifyPrePayBannerViewModel extends ManagersAccessViewModel {
    final ReactorTextViewState bannerText = new ReactorTextViewState();
    final ReactorViewState amountView = new ReactorViewState();

    private boolean mIsNorthAmericaAirport;

    public void setIsNorthAmericaAirport(boolean mIsNorthAmericaAirport) {
        this.mIsNorthAmericaAirport = mIsNorthAmericaAirport;
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        amountView.setVisibility(View.GONE);
        bannerText.setText(getResources().getString(R.string.modify_reservation_prepay_default_warning_text));
    }

    public void reservationUpdated() {
        if (mIsNorthAmericaAirport) {
            bannerText.setText(getResources().getString(R.string.modify_reservation_prepay_naa_warning_text));
            amountView.setVisibility(View.GONE);
        } else {
            bannerText.setText(getResources().getString(R.string.modify_reservation_prepay_warning_text));
            amountView.setVisibility(View.VISIBLE);
        }
    }
}

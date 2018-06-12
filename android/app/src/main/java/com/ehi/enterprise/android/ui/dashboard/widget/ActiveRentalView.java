package com.ehi.enterprise.android.ui.dashboard.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ActiveRentalsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHITripSummary;
import com.ehi.enterprise.android.ui.dashboard.interfaces.OnActiveRentalEventsListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ActiveRentalViewModel.class)
public class ActiveRentalView extends DataBindingViewModelView<ActiveRentalViewModel, ActiveRentalsViewBinding> {
    private OnActiveRentalEventsListener mOnDashboardEventsListener;

    //region onClickListener
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().returnInstructionsButton) {
                if (mOnDashboardEventsListener != null) {
                    mOnDashboardEventsListener.onReturnInstructionsClicked();
                }
            } else if (view == getViewBinding().getDirectionsButton) {
                if (mOnDashboardEventsListener != null) {
                    mOnDashboardEventsListener.onGetDirectionsClicked();
                }
            } else if (view == getViewBinding().extendRentalButton) {
                if (mOnDashboardEventsListener != null) {
                    mOnDashboardEventsListener.onExtendRentalClicked();
                }
            } else if (view == getViewBinding().findGasStationButton) {
                if (mOnDashboardEventsListener != null) {
                    mOnDashboardEventsListener.onFindGasStationsClicked(getViewModel().getReturnLocation());
                }
            } else if (view == getViewBinding().returnLocationName) {
                if (mOnDashboardEventsListener != null) {
                    mOnDashboardEventsListener.onLocationNameClicked(getViewModel().getReturnLocation());
                }
            } else if (view == getViewBinding().rateMyRideButton) {
                if (mOnDashboardEventsListener != null) {
                    mOnDashboardEventsListener.onRateMyRideButtonClicked(getViewModel().getTripSummary().getRateMyRideUrl());
                }
            }
        }
    };
    //endregion

    //region constructors
    public ActiveRentalView(Context context) {
        this(context, null);
    }

    public ActiveRentalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveRentalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_dashboard_active_rental);
        initViews();
    }
    //endregion

    private void initViews() {
        getViewBinding().returnInstructionsButton.setOnClickListener(mOnClickListener);
        getViewBinding().getDirectionsButton.setOnClickListener(mOnClickListener);
        getViewBinding().extendRentalButton.setOnClickListener(mOnClickListener);
        getViewBinding().findGasStationButton.setOnClickListener(mOnClickListener);
        getViewBinding().returnLocationName.setOnClickListener(mOnClickListener);
        getViewBinding().rateMyRideButton.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().headerContainer.visibility(), getViewBinding().headerContainer));

        bind(ReactorView.visibility(getViewModel().vehicleNameHeader.visibility(), getViewBinding().vehicleNameHeader));
        bind(ReactorView.visibility(getViewModel().vehicleNameHeader.visibility(), getViewBinding().vehicleNameDivider));
        bind(ReactorView.visibility(getViewModel().vehicleName.visibility(), getViewBinding().vehicleName));
        bind(ReactorTextView.text(getViewModel().vehicleName.text(), getViewBinding().vehicleName));

        bind(ReactorView.visibility(getViewModel().vehicleColorHeader.visibility(), getViewBinding().vehicleColorHeader));
        bind(ReactorView.visibility(getViewModel().vehicleColorHeader.visibility(), getViewBinding().vehicleColorDivider));
        bind(ReactorView.visibility(getViewModel().vehicleColor.visibility(), getViewBinding().vehicleColor));
        bind(ReactorTextView.text(getViewModel().vehicleColor.text(), getViewBinding().vehicleColor));

        bind(ReactorView.visibility(getViewModel().vehiclePlateNumberHeader.visibility(), getViewBinding().vehiclePlateHeader));
        bind(ReactorView.visibility(getViewModel().vehiclePlateNumber.visibility(), getViewBinding().vehiclePlate));
        bind(ReactorTextView.text(getViewModel().vehiclePlateNumber.text(), getViewBinding().vehiclePlate));

        bind(ReactorTextView.text(getViewModel().returnDateTime.text(), getViewBinding().returnDateTime));
        bind(ReactorTextView.text(getViewModel().returnLocation.text(), getViewBinding().returnLocationName));
        bind(ReactorTextView.drawableLeft(getViewModel().returnLocation.drawableLeft(), getViewBinding().returnLocationName));
        bind(ReactorView.visibility(getViewModel().returnLocation.visibility(), getViewBinding().returnLocationName));
        bind(ReactorView.visibility(getViewModel().findGasStationButton.visibility(), getViewBinding().findGasStationButton));
        bind(ReactorView.visibility(getViewModel().returnInstructionsButton.visibility(), getViewBinding().returnInstructionsButton));
        bind(ReactorView.visibility(getViewModel().getDirectionsButton.visibility(), getViewBinding().getDirectionsButton));
        bind(ReactorView.visibility(getViewModel().rateMyRideButton.visibility(), getViewBinding().rateMyRideButton));
    }

    public void setTripSummary(EHITripSummary tripSummary) {
        getViewModel().setTripSummary(tripSummary);
    }

    public void setOnActiveRentalEventsListener(OnActiveRentalEventsListener onActiveRentalEventsListener) {
        mOnDashboardEventsListener = onActiveRentalEventsListener;
    }

    public void setIsCurrentRentalAfterHours(boolean isCurrentRentalAfterHours) {
        getViewModel().setupReturnButtonInstructions(isCurrentRentalAfterHours);
    }
}
package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ConfirmationLocationsViewBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.location.interfaces.OnLocationDetailEventsListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class ConfirmationLocationsView extends DataBindingViewModelView<ManagersAccessViewModel, ConfirmationLocationsViewBinding> {

    private OnLocationDetailEventsListener onLocationDetailEventsListener;

    private OnLocationDetailEventsListener mOnLocationDetailEventsListener = new OnLocationDetailEventsListener() {
        @Override
        public void onFavoriteStateChanged() {
            if (onLocationDetailEventsListener != null) {
                onLocationDetailEventsListener.onFavoriteStateChanged();
            }
        }

        @Override
        public void onCallLocation(String phoneNumber) {
            if (onLocationDetailEventsListener != null) {
                onLocationDetailEventsListener.onCallLocation(phoneNumber);
            }
        }

        @Override
        public void onShowDirection() {
            if (onLocationDetailEventsListener != null) {
                onLocationDetailEventsListener.onShowDirection();
            }
        }

        @Override
        public void onShowDirectionFromTerminal() {
            if (onLocationDetailEventsListener != null) {
                onLocationDetailEventsListener.onShowDirectionFromTerminal();
            }
        }

        @Override
        public void onShowLocationDetails(EHILocation location) {
            if (onLocationDetailEventsListener != null) {
                onLocationDetailEventsListener.onShowLocationDetails(location);
            }
        }

        @Override
        public void onShowAfterHoursDialog() {
            if (onLocationDetailEventsListener != null) {
                onLocationDetailEventsListener.onShowAfterHoursDialog();
            }
        }
    };

    public ConfirmationLocationsView(Context context) {
        this(context, null);
    }

    public ConfirmationLocationsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmationLocationsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_confirmation_locations, null));
            return;
        }

        createViewBinding(R.layout.v_confirmation_locations);
        initViews();
    }

    private void initViews() {
        getViewBinding().pickupLocationDetailsView.setOnLocationDetailEventsListener(mOnLocationDetailEventsListener);
        getViewBinding().returnLocationDetails.setOnLocationDetailEventsListener(mOnLocationDetailEventsListener);
    }

    public void setReservation(EHIReservation ehiReservation) {
        if (ehiReservation == null || ehiReservation.getPickupLocation() == null) {
            getViewBinding().getRoot().setVisibility(GONE);
            return;
        }

        getViewBinding().getRoot().setVisibility(VISIBLE);

        final EHILocation pickupLocation = ehiReservation.getPickupLocation();
        getViewBinding().pickupLocationDetailsView.setLocation(pickupLocation);

        final EHILocation returnLocation = ehiReservation.getReturnLocation();
        if (returnLocation == null
                || returnLocation.getId().equals(pickupLocation.getId())) {
            getViewBinding().returnLocationDetails.setVisibility(GONE);
            getViewBinding().pickupLocationDetailsView.setTitle(R.string.reservation_confirmation_location_section_one_way_title);
            return;
        }

        getViewBinding().returnLocationDetails.setLocation(returnLocation);
        getViewBinding().returnLocationDetails.setVisibility(VISIBLE);

        getViewBinding().pickupLocationDetailsView.setTitle(R.string.reservation_confirmation_location_section_pickup_title);
        getViewBinding().returnLocationDetails.setTitle(R.string.reservation_confirmation_location_section_return_title);
    }

    public void setOnLocationDetailEventsListener(OnLocationDetailEventsListener listener) {
        onLocationDetailEventsListener = listener;
    }
}

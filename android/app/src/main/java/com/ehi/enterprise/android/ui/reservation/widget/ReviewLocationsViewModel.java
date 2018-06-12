package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorImageViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReviewLocationsViewModel extends ManagersAccessViewModel {

    private EHILocation mPickupLocation;
    private EHILocation mReturnLocation;

    //region reactive states
    public final ReactorTextViewState locationsTitle = new ReactorTextViewState();
    public final ReactorTextViewState pickupLocationName = new ReactorTextViewState();
    public final ReactorImageViewState pickupLocationIcon = new ReactorImageViewState();
    public final ReactorViewState viewSeparator = new ReactorViewState();
    public final ReactorViewState returnLocationContainer = new ReactorViewState();
    public final ReactorTextViewState returnLocationName = new ReactorTextViewState();
    public final ReactorImageViewState returnLocationIcon = new ReactorImageViewState();

    public final ReactorImageViewState pickupImageButton = new ReactorImageViewState();
    public final ReactorImageViewState returnImageButton = new ReactorImageViewState();
    //endregion


    public void setLocations(EHILocation pickupLocation, EHILocation returnLocation, boolean blockLocationChange) {
        mPickupLocation = pickupLocation;
        mReturnLocation = returnLocation;
        if (mPickupLocation != null
                && mReturnLocation != null) {
            if (mPickupLocation.getGrayLocationCellIconDrawable() > 0) {
                pickupLocationIcon.setImageResource(mPickupLocation.getGrayLocationCellIconDrawable());
                pickupLocationIcon.setVisibility(ReactorViewState.VISIBLE);
            } else {
                pickupLocationIcon.setVisibility(ReactorViewState.GONE);
            }

            pickupLocationName.setText(mPickupLocation.getName());

            if (blockLocationChange) {
                pickupImageButton.setImageResource(R.drawable.icon_lock);
            }

            if (!mReturnLocation.getId().equalsIgnoreCase(mPickupLocation.getId())) {
                viewSeparator.setVisibility(ReactorViewState.VISIBLE);
                returnLocationContainer.setVisibility(ReactorViewState.VISIBLE);
                returnLocationName.setText(mReturnLocation.getName());
                if (mReturnLocation.getGrayLocationCellIconDrawable() > 0) {
                    returnLocationIcon.setImageResource(mReturnLocation.getGrayLocationCellIconDrawable());
                    returnLocationIcon.setVisibility(ReactorViewState.VISIBLE);
                } else {
                    returnLocationIcon.setVisibility(ReactorViewState.GONE);
                }
                if (blockLocationChange) {
                    returnImageButton.setImageResource(R.drawable.icon_lock);
                }
                locationsTitle.setText(R.string.reservation_confirmation_location_section_one_way_title);
            } else {
                viewSeparator.setVisibility(ReactorViewState.GONE);
                returnLocationContainer.setVisibility(ReactorViewState.GONE);

                locationsTitle.setText(R.string.reservation_review_location_section_title);
            }
        }
    }

    public void hideImageButtons() {
        pickupImageButton.setVisibility(ReactorViewState.GONE);
        returnImageButton.setVisibility(ReactorViewState.GONE);
    }

    public void showImageButtons() {
        pickupImageButton.setVisibility(ReactorViewState.VISIBLE);
        returnImageButton.setVisibility(ReactorViewState.VISIBLE);
    }
}

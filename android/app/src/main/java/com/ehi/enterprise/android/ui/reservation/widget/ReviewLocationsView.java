package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewLocationsViewBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorImageView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewLocationsViewModel.class)
public class ReviewLocationsView extends DataBindingViewModelView<ReviewLocationsViewModel, ReviewLocationsViewBinding> {

    //region constructors
    public ReviewLocationsView(Context context) {
        this(context, null, 0);
    }

    public ReviewLocationsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewLocationsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_locations_cell, null));
            return;
        }

        createViewBinding(R.layout.v_locations_cell);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().pickupLocationName.text(), getViewBinding().pickupCellText));
        bind(ReactorImageView.imageResource(getViewModel().pickupLocationIcon.imageResource(), getViewBinding().pickupCellIcon));
        bind(ReactorView.visibility(getViewModel().pickupLocationIcon.visibility(), getViewBinding().pickupCellIcon));
        bind(ReactorView.visibility(getViewModel().returnLocationContainer.visibility(), getViewBinding().returnCellContainer));
        bind(ReactorTextView.text(getViewModel().returnLocationName.text(), getViewBinding().returnCellText));
        bind(ReactorImageView.imageResource(getViewModel().returnLocationIcon.imageResource(), getViewBinding().returnCellIcon));
        bind(ReactorView.visibility(getViewModel().returnLocationIcon.visibility(), getViewBinding().returnCellIcon));

        bind(ReactorView.visibility(getViewModel().pickupImageButton.visibility(), getViewBinding().pickupRightIcon));
        bind(ReactorImageView.imageResource(getViewModel().pickupImageButton.imageResource(), getViewBinding().pickupRightIcon));
        bind(ReactorView.visibility(getViewModel().returnImageButton.visibility(), getViewBinding().returnRightIcon));
        bind(ReactorImageView.imageResource(getViewModel().returnImageButton.imageResource(), getViewBinding().returnRightIcon));
    }

    public void setLocations(EHILocation pickupLocation, EHILocation returnLocation, boolean blockLocationChange) {
        getViewModel().setLocations(pickupLocation, returnLocation, blockLocationChange);
    }

    public void hideGreenArrow() {
        getViewModel().hideImageButtons();
    }

    public void showGreenArrow() {
        getViewModel().showImageButtons();
    }

}
package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewDcDetailViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIVehicleLogistic;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorImageView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewDCDetailsViewModel.class)
public class ReviewDCDetailView extends DataBindingViewModelView<ReviewDCDetailsViewModel, ReviewDcDetailViewBinding> {

    private static final String TAG = "ReviewDCDetailView";

    //region constructors
    public ReviewDCDetailView(Context context) {
        this(context, null, 0);
    }

    public ReviewDCDetailView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewDCDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_dc_details, null));
            return;
        }

        createViewBinding(R.layout.v_review_dc_details);
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().deliveryArea.visibility(), getViewBinding().deliveryArea));
        bind(ReactorView.visibility(getViewModel().deliveryNotAvailableView.visibility(), getViewBinding().deliveryNotAvailableView));
        bind(ReactorView.visibility(getViewModel().collectionArea.visibility(), getViewBinding().collectionArea));
        bind(ReactorView.visibility(getViewModel().collectionNotAvailableView.visibility(), getViewBinding().collectionNotAvailableView));
        bind(ReactorView.visibility(getViewModel().collectionSameAsDeliveryView.visibility(), getViewBinding().collectionSameAsDeliveryView));

        bind(ReactorImageView.imageResource(getViewModel().useDcButtonArrow.imageResource(), getViewBinding().useDcButtonArrow));

        bind(ReactorView.visibility(getViewModel().deliveryIconView.visibility(), getViewBinding().deliveryIconImage));
        bind(ReactorImageView.imageResource(getViewModel().deliveryIconView.imageResource(), getViewBinding().deliveryIconImage));

        bind(ReactorView.visibility(getViewModel().collectionIconView.visibility(), getViewBinding().collectionIconImage));
        bind(ReactorImageView.imageResource(getViewModel().collectionIconView.imageResource(), getViewBinding().collectionIconImage));

        bind(ReactorView.visibility(getViewModel().headerText.visibility(), getViewBinding().headerView));
        bind(ReactorView.visibility(getViewModel().useDcButton.visibility(), getViewBinding().useDcButton));
        bind(ReactorView.visibility(getViewModel().dcView.visibility(), getViewBinding().getRoot()));
        bind(ReactorView.visibility(getViewModel().dcInformationBody.visibility(), getViewBinding().dcInformationBody));

        bind(ReactorView.visibility(getViewModel().deliveryAddressView.visibility(), getViewBinding().deliveryAddressView));
        bind(ReactorTextView.text(getViewModel().deliveryAddressView.text(), getViewBinding().deliveryAddressView));

        bind(ReactorView.visibility(getViewModel().deliveryPhoneView.visibility(), getViewBinding().deliveryPhoneView));
        bind(ReactorTextView.text(getViewModel().deliveryPhoneView.text(), getViewBinding().deliveryPhoneView));

        bind(ReactorView.visibility(getViewModel().deliveryCommentView.visibility(), getViewBinding().deliveryCommentView));
        bind(ReactorTextView.text(getViewModel().deliveryCommentView.text(), getViewBinding().deliveryCommentView));

        bind(ReactorView.visibility(getViewModel().collectionAddressView.visibility(), getViewBinding().collectionAddressView));
        bind(ReactorTextView.text(getViewModel().collectionAddressView.text(), getViewBinding().collectionAddressView));

        bind(ReactorView.visibility(getViewModel().collectionPhoneView.visibility(), getViewBinding().collectionPhoneView));
        bind(ReactorTextView.text(getViewModel().collectionPhoneView.text(), getViewBinding().collectionPhoneView));

        bind(ReactorView.visibility(getViewModel().collectionCommentView.visibility(), getViewBinding().collectionCommentView));
        bind(ReactorTextView.text(getViewModel().collectionCommentView.text(), getViewBinding().collectionCommentView));
    }

    public void populateWithData(boolean deliveryAllowed,
                                 boolean collectionAllowed,
                                 boolean requiresTravelPurpose,
                                 boolean isBusinessTravelPurpose,
                                 EHIVehicleLogistic vehicleLogistic,
                                 boolean displayOnly) {
        getViewModel().populateWithData(deliveryAllowed,
                collectionAllowed,
                requiresTravelPurpose,
                isBusinessTravelPurpose,
                vehicleLogistic,
                displayOnly);
    }

    public void setIconsVisibility(int visibility) {
        getViewModel().setIconVisibility(visibility);
    }

    public void setIconsRes(int resId) {
        getViewModel().setIconResId(resId);
    }

    @Override
    public void setAlpha(float alpha) {
        getViewBinding().dcInformationBody.setAlpha(alpha);
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }
}

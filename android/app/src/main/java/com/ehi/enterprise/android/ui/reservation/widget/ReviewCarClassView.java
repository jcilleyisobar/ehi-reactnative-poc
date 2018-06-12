package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewCarClassViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.widget.ArrowViewGroup;
import com.ehi.enterprise.android.utils.DisplayUtils;
import com.ehi.enterprise.android.utils.image.EHIImageLoader;
import com.ehi.enterprise.android.utils.image.EHIImageUtils;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewCarClassViewModel.class)
public class ReviewCarClassView extends DataBindingViewModelView<ReviewCarClassViewModel, ReviewCarClassViewBinding> {

    private static final String TAG = "ReviewCarClassView";
    private CarReviewCallback mCallback;

    //region constructors
    public ReviewCarClassView(Context context) {
        this(context, null, 0);
    }

    public ReviewCarClassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewCarClassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_car_class, null));
            return;
        }

        createViewBinding(R.layout.v_review_car_class);
    }
    //endregion

    //region listener
    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCallback == null) {
                return;
            }
            if (getViewBinding().upgradeCarButton == v) {
                mCallback.carUpgradeClicked(getViewModel().getUpgradeCarId());
            } else {
                mCallback.carChangeClicked();
            }
        }
    };

    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().carClassType.text(), getViewBinding().carClassType));
        bind(ReactorView.visibility(getViewModel().carUpgradeContainer.visibility(), getViewBinding().arrowViewgroup));
        bind(ReactorTextView.text(getViewModel().carModelText.text(), getViewBinding().carModelText));
        bind(ReactorTextView.text(getViewModel().carClassTransmission.text(), getViewBinding().carTransmissionText));
        bind(ReactorTextView.drawableLeft(getViewModel().carClassTransmission.drawableLeft(), getViewBinding().carTransmissionText));
        bind(EHIImageLoader.imageByType(getViewModel().classImages, getViewBinding().carClassImage, getViewModel().getImageType()));
        bind(ReactorTextView.text(getViewModel().carUpgradeTextInformation, getViewBinding().pricingInformationText));
        bind(EHIImageLoader.imageByType(getViewModel().upgradeImages, getViewBinding().carUpgradeImage, EHIImageUtils.IMAGE_TYPE_SIDE_PROFILE));
        bind(ReactorView.visibility(getViewModel().carChangeArrow.visibility(), getViewBinding().carChangeArrow));
        getViewBinding().arrowViewgroup
                .setArrowExtensionLength(ViewGroup.LayoutParams.MATCH_PARENT)
                .setArrowPosition(ArrowViewGroup.TOP)
                .setArrowPointedIn(false)
                .setArrowWidth(DisplayUtils.dipToPixels(getContext(), 25f))
                .setArrowHeight(getResources().getDimension(R.dimen.upgrade_arrow_height) - 1)
                .setExteriorColor(getResources().getColor(R.color.white))
                .setArrowColor(R.color.section_divider)
                .setStrokeWidth(DisplayUtils.dipToPixels(getContext(), 2f))
                .setExteriorPadding(getResources().getDimension(R.dimen.upgrade_arrow_height))
                .invalidate();

        getViewBinding().upgradeCarButton.setOnClickListener(mOnClickListener);
        getViewBinding().carChangeContainer.setOnClickListener(mOnClickListener);
    }

    public void setCarClassDetails(EHIReservation currentReservation, boolean shouldShowUpgradeOption) {
        getViewModel().setUpReservation(currentReservation, shouldShowUpgradeOption);
    }

    public void setCarClassChangeCarListener(CarReviewCallback callback) {
        mCallback = callback;
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }

    public interface CarReviewCallback {
        void carChangeClicked();

        void carUpgradeClicked(String carId);
    }
}
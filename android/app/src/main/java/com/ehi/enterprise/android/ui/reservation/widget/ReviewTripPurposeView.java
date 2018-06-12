package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewTripPurposeViewBinding;
import com.ehi.enterprise.android.ui.reservation.TripPurposeFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnReviewTripPurposeListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ReviewTripPurposeViewModel.class)
public class ReviewTripPurposeView extends DataBindingViewModelView<ReviewTripPurposeViewModel, ReviewTripPurposeViewBinding> {

    private static final String TAG = "ReviewTripPurposeView";

    private OnReviewTripPurposeListener mTripPurposeChangedListener;

    private RadioGroup.OnCheckedChangeListener mRadioClickListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == getViewBinding().tripPurposeBusinessButton.getId()) {
                mTripPurposeChangedListener.onTripPurposeChanged(TripPurposeFragment.TRIP_TYPE_BUSINESS);
            } else if (checkedId == getViewBinding().tripPurposeLeisureButton.getId()) {
                mTripPurposeChangedListener.onTripPurposeChanged(TripPurposeFragment.TRIP_TYPE_LEISURE);
            }
        }
    };

    //region constructor
    public ReviewTripPurposeView(Context context) {
        this(context, null, 0);
    }

    public ReviewTripPurposeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewTripPurposeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_trip_purpose, null));
            return;
        }

        createViewBinding(R.layout.v_review_trip_purpose);
        initViews();
    }
    //endregion

    private void initViews() {
        getViewBinding().tripPurposeSelectorRadioGroup.setOnCheckedChangeListener(mRadioClickListener);
        getViewBinding().tripPurposeBusinessButton.setText(getResources().getString(R.string.review_travel_purpose_segmented_control_first_title));
        getViewBinding().tripPurposeLeisureButton.setText(getResources().getString(R.string.review_travel_purpose_segmented_control_second_title));
    }

    public void setTripPurposeTitle(CharSequence tripPurposeTitle) {
        getViewBinding().titleTextView.setText(tripPurposeTitle);
    }

    public void setOnReviewTripPurposeListener(OnReviewTripPurposeListener tripPurposeChangedListener) {
        mTripPurposeChangedListener = tripPurposeChangedListener;
    }

}
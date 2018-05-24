package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewPointsViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewPointsViewModel.class)
public class ReviewPointsView extends DataBindingViewModelView<ReviewPointsViewModel, ReviewPointsViewBinding> {

    private static final String TAG = "ReviewPointsView";

    private OnActionClickListener mOnActionClickListener;

    //region onClickListener
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().savePointsExpandedButton) {
                if (mOnActionClickListener != null) {
                    mOnActionClickListener.onSavePoints();
                }
            } else if (view == getViewBinding().redeemPointsButton
                    || view == getViewBinding().redeemPointsButtonExpanded
                    || view == getViewBinding().daysRowView) {
                if (mOnActionClickListener != null) {
                    mOnActionClickListener.onRedeemPoints();
                }
            } else if (view == getViewBinding().removePointsFromRentalButton) {
                if (mOnActionClickListener != null) {
                    mOnActionClickListener.onRemovePoints();
                }
            }
        }
    };
    //endregion

    //region constructors
    public ReviewPointsView(Context context) {
        this(context, null, 0);
    }

    public ReviewPointsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewPointsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_review_points);
            initViews();
        }
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().redemptionEnabledTitle.visibility(), getViewBinding().redemptionEnabledTitle));
        bind(ReactorView.visibility(getViewModel().redemptionEnabledTriangle.visibility(), getViewBinding().redemptionEnabledTriangle));
        bind(ReactorView.visibility(getViewModel().notEnoughtPointsView.visibility(), getViewBinding().notEnoughtPointsView));
        bind(ReactorView.visibility(getViewModel().enoughtPointsTitleView.visibility(), getViewBinding().enoughtPointsTitleView));
        bind(ReactorView.visibility(getViewModel().redeemPointsButton.visibility(), getViewBinding().redeemPointsButton));
        bind(ReactorView.visibility(getViewModel().pointsExpandedView.visibility(), getViewBinding().pointsExpandedView));
        bind(ReactorView.visibility(getViewModel().removePointsFromRentalButton.visibility(), getViewBinding().removePointsFromRentalButton));
        bind(ReactorView.visibility(getViewModel().pointsPriceDetailsView.visibility(), getViewBinding().pointsPriceDetailsView));
        bind(ReactorView.visibility(getViewModel().enoughtPointsValueView.visibility(), getViewBinding().enoughtPointsValueView));

        bind(ReactorTextView.text(getViewModel().enoughtPointsValueView.text(), getViewBinding().enoughtPointsValueView));
        bind(ReactorTextView.text(getViewModel().priceDetailsDaysValue.text(), getViewBinding().priceDetailsDaysValue));
        bind(ReactorTextView.text(getViewModel().priceDetailsPointsValue.text(), getViewBinding().priceDetailsPointsValue));
    }

    private void initViews() {
        getViewBinding().removePointsFromRentalButton.setOnClickListener(mOnClickListener);
        getViewBinding().redeemPointsButton.setOnClickListener(mOnClickListener);
        getViewBinding().redeemPointsButtonExpanded.setOnClickListener(mOnClickListener);
        getViewBinding().savePointsExpandedButton.setOnClickListener(mOnClickListener);
        getViewBinding().daysRowView.setOnClickListener(mOnClickListener);
    }

    public void setShowPoint(boolean showPoints) {
        getViewModel().setShowPoints(showPoints);
    }

    public void setCurrentPoints(String points) {
        getViewBinding().currentPointsTextView.setText(points);
    }

    public void setCarClassDetails(EHICarClassDetails details) {
        getViewModel().setCarClassDetails(details);
    }

    public void setRedeemingInformations(int pointsUsed, int daysUsed) {
        getViewModel().setRedeemInformation(pointsUsed, daysUsed);
    }

    public void setOnActionClickListener(OnActionClickListener listener) {
        mOnActionClickListener = listener;
    }

    public interface OnActionClickListener {
        void onSavePoints();

        void onRedeemPoints();

        void onRemovePoints();
    }
}

package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewPrepayOrSaveViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewPrepayOrSaveViewModel.class)
public class ReviewPrepayOrSaveView extends DataBindingViewModelView<ReviewPrepayOrSaveViewModel, ReviewPrepayOrSaveViewBinding> {

    //region constructors
    public ReviewPrepayOrSaveView(Context context) {
        this(context, null);
    }

    public ReviewPrepayOrSaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewPrepayOrSaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_prepay_or_save, null));
            return;
        }

        createViewBinding(R.layout.v_review_prepay_or_save);
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().saveIfPayLaterText.textCharSequence(), getViewBinding().orSaveIfPayLater));
        bind(ReactorTextView.text(getViewModel().saveIfPayNowText.textCharSequence(), getViewBinding().orSaveIfPayNow));
        bind(ReactorView.visibility(getViewModel().saveIfPayLaterText.visibility(), getViewBinding().orSaveIfPayLater));
        bind(ReactorView.visibility(getViewModel().saveIfPayNowText.visibility(), getViewBinding().orSaveIfPayNow));
    }

    public void toggleVisibility() {
        getViewModel().toggleVisibility();
    }

    public void populateView(EHICarClassDetails carClassDetails) {
        getViewModel().setPrepayOrSaveText(carClassDetails);
    }

    public boolean isPayingLater() {
        Integer visibility = getViewModel().saveIfPayNowText.visibility().getValue();
        return visibility == null || visibility == View.VISIBLE;
    }

    public void setPayState(ReservationFlowListener.PayState payState) {
        getViewModel().setPayState(payState);
    }

}
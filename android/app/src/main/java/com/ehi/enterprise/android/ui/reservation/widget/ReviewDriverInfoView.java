package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ReviewDriverInfoViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ReviewDriverInfoViewModel.class)
public class ReviewDriverInfoView extends DataBindingViewModelView<ReviewDriverInfoViewModel, ReviewDriverInfoViewBinding> {

    private static final String TAG = "ReviewDriverInfoView";

    //region constructors
    public ReviewDriverInfoView(Context context) {
        this(context, null, 0);
    }

    public ReviewDriverInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReviewDriverInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_review_driver_info, null));
            return;
        }

        createViewBinding(R.layout.v_review_driver_info);
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().driverName.text(), getViewBinding().driverNameView));
        bind(ReactorTextView.text(getViewModel().driverEmail.text(), getViewBinding().driverEmailView));
        bind(ReactorTextView.text(getViewModel().driverPhone.text(), getViewBinding().driverPhoneView));
        bind(ReactorView.visibility(getViewModel().greenArrow.visibility(), getViewBinding().greenArrow));
    }

    public void setDriverInfo(EHIDriverInfo driverInfo) {
        getViewModel().setDriverInfo(driverInfo);
    }

    public void hideGreenArrow() {
        getViewModel().hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewModel().showGreenArrow();
    }
}

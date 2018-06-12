package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ConfirmationCarClassViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(ConfirmationCarClassViewModel.class)
public class ConfirmationCarClassView extends DataBindingViewModelView<ConfirmationCarClassViewModel, ConfirmationCarClassViewBinding> {

    private static final String TAG = "ConfirmationCarClassView";

    public ConfirmationCarClassView(Context context) {
        this(context, null, 0);
    }

    public ConfirmationCarClassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmationCarClassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_confirmation_car_class, null));
            return;
        }

        createViewBinding(R.layout.v_confirmation_car_class);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.text(getViewModel().carClassTypeText.text(), getViewBinding().carClassType));
        bind(ReactorTextView.text(getViewModel().carClassModelText.textCharSequence(), getViewBinding().carClassModel));
        bind(ReactorTextView.text(getViewModel().carClassTransmissionTextView.text(), getViewBinding().carClassTransmission));
        bind(ReactorTextView.drawableLeft(getViewModel().carClassTransmissionTextView.drawableLeft(), getViewBinding().carClassTransmission));
        bind(ReactorTextView.compoundDrawablePaddingInDp(getViewModel().carClassTransmissionTextView.compoundDrawablePaddingInDp(), getViewBinding().carClassTransmission));
    }

    public void setCarClassDetails(EHICarClassDetails carClassDetails) {
        getViewModel().setCarClassDetails(carClassDetails);
    }
}
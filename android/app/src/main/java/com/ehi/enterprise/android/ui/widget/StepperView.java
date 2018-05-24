package com.ehi.enterprise.android.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.StepperViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(ManagersAccessViewModel.class)
public class StepperView extends DataBindingViewModelView<ManagersAccessViewModel, StepperViewBinding> {

    private CharSequence mStepperText;
    private StepperListener mStepperListener;
    private boolean mMinusEnabled;
    private boolean mPlusEnabled;

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().minusButton) {
                mStepperListener.onMinusClicked();
            } else if (v == getViewBinding().plusButton) {
                mStepperListener.onPlusClicked();
            }
        }
    };

    public StepperView(Context context) {
        this(context, null, 0);
    }

    public StepperView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_stepper);
        initViews();
    }

    private void initViews() {
        getViewBinding().minusButton.setOnClickListener(mOnClickListener);
        getViewBinding().plusButton.setOnClickListener(mOnClickListener);
        setStepperText(mStepperText);
    }

    public void setStepperText(CharSequence stepperText) {
        mStepperText = stepperText;
        if (getViewBinding().text != null) {
            getViewBinding().text.setText(mStepperText);
        }
    }

    public void setStepperListener(StepperListener stepperListener) {
        mStepperListener = stepperListener;
    }

    public void setMinusButtonEnabled(boolean minusEnabled) {
        mMinusEnabled = minusEnabled;
        getViewBinding().minusButton.setBackgroundResource(mMinusEnabled ? R.drawable.green_button_touch_overlay
                : R.drawable.disabled_button);
    }

    public void setPlusButtonEnabled(boolean enabled) {
        mPlusEnabled = enabled;

        getViewBinding().plusButton.setBackgroundResource(mPlusEnabled ? R.drawable.green_button_touch_overlay
                : R.drawable.disabled_button);
    }

    public interface StepperListener {
        void onMinusClicked();

        void onPlusClicked();
    }

    public static ReactorComputationFunction text(final ReactorVar<? extends CharSequence> source, final StepperView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                target.setStepperText(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction plusButton(final ReactorVar<Boolean> enabled, final StepperView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                target.setPlusButtonEnabled(enabled.getValue());
            }
        };
    }

    public static ReactorComputationFunction minusButton(final ReactorVar<Boolean> enabled, final StepperView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                target.setMinusButtonEnabled(enabled.getValue());
            }
        };
    }
}

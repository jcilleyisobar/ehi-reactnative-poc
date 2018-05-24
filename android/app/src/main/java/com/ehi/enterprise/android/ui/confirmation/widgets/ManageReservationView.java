package com.ehi.enterprise.android.ui.confirmation.widgets;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ManageReservationViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(ManageReservationViewModel.class)
public class ManageReservationView extends DataBindingViewModelView<ManageReservationViewModel, ManageReservationViewBinding> {

    private int mExpandedHeight = 0;
    private ManageReservationListener mListener;

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().containerLayout) {
                onCollapseExpandClicked();
            } else if (v == getViewBinding().cancelButton) {
                mListener.onCancelClick();
            } else if (v == getViewBinding().modifyButton) {
                mListener.onModifyClick();
            } else if (v == getViewBinding().addToCalendarButton) {
                mListener.onAddToCalendarClick();
            }
        }
    };

    public ManageReservationView(Context context) {
        this(context, null);
    }

    public ManageReservationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ManageReservationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_manage_reservation, null));
            return;
        }

        createViewBinding(R.layout.v_manage_reservation);
        initViews();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().buttonsContainer.visibility(), getViewBinding().buttonsContainer));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().rotateArrow.getValue()) {
                    rotate(getViewBinding().arrowView, getViewModel().getArrowInitialPosition(), getViewModel().getArrowFinalPosition());
                    getViewModel().rotateArrow.setValue(false);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().expandButtonsContainer.getValue()) {
                    expand(getViewBinding().buttonsContainer);
                    getViewModel().expandButtonsContainer.setValue(false);
                } else if (getViewModel().collapseButtonsContainer.getValue()) {
                    collapse(getViewBinding().buttonsContainer);
                    getViewModel().collapseButtonsContainer.setValue(false);
                }
            }
        });

    }

    private void rotate(final View v, final int initialPos, final int finalPos) {
        ValueAnimator animator = ObjectAnimator.ofFloat(v, "rotation", initialPos, finalPos);
        animator.setDuration(300);
        animator.start();
    }

    private void expand(final View v) {
        if (mExpandedHeight == 0) {
            v.measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mExpandedHeight = v.getMeasuredHeight();
        }

        getViewModel().buttonsContainer.setVisibility(View.VISIBLE);

        ValueAnimator anim = ValueAnimator.ofInt(0, mExpandedHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);
            }
        });

        anim.setDuration(300);
        anim.start();
    }

    public void resetInitialState() {
        getViewModel().resetInitialState();
    }

    private void collapse(final View v) {
        ValueAnimator anim = ValueAnimator.ofInt(mExpandedHeight, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);
            }
        });

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                getViewModel().buttonsContainer.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        anim.setDuration(300);
        anim.start();
    }

    public void initViews() {
        getViewBinding().containerLayout.setOnClickListener(onClickListener);
        getViewBinding().cancelButton.setOnClickListener(onClickListener);
        getViewBinding().modifyButton.setOnClickListener(onClickListener);
        getViewBinding().addToCalendarButton.setOnClickListener(onClickListener);
    }

    private void onCollapseExpandClicked() {
        getViewModel().toggleContainer();
    }

    public void setListener(ManageReservationListener listener) {
        mListener = listener;
    }

    public interface ManageReservationListener {
        void onAddToCalendarClick();
        void onModifyClick();
        void onCancelClick();
    }
}

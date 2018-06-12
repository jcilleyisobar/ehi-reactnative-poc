package com.ehi.enterprise.android.ui.reservation.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CollapsibleViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DisplayUtils;

public abstract class CollapsibleView<T extends ManagersAccessViewModel> extends DataBindingViewModelView<T, CollapsibleViewBinding> {

    private int childViewHeight = 0;

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            toggleContent();
        }
    };

    public CollapsibleView(Context context) {
        this(context, null);
    }

    public CollapsibleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_collapsible, null));
            return;
        }

        createViewBinding(R.layout.v_collapsible);

        setUpCollapsibleAction();
    }

    public void showContent() {
        if (getViewBinding().childContainer.getChildCount() == 0
                || getViewBinding().childContainer.getLayoutParams().height != 0) {
            return;
        }

        toggleContent();
    }

    public void hideContent() {
        if (getViewBinding().childContainer.getChildCount() == 0
                || getViewBinding().childContainer.getLayoutParams().height == 0) {
            return;
        }

        toggleContent();
    }

    private void setUpCollapsibleAction() {
        getViewBinding().mainArea.setOnClickListener(onClickListener);
    }

    public void toggleContent() {
        if (getViewBinding().childContainer.getChildCount() == 0) {
            return;
        }

        final ViewGroup.LayoutParams childContainerParams = getViewBinding().childContainer.getLayoutParams();

        if (childViewHeight == 0 && childContainerParams.height == 0) {
            getViewBinding().childContainer.measure(
                    MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenWidth(getContext()), View.MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(DisplayUtils.getScreenHeight(getContext()), MeasureSpec.AT_MOST)
            );
            childViewHeight = getViewBinding().childContainer.getMeasuredHeight();
        }

        ValueAnimator valueAnimator;
        int rotation;
        if (childContainerParams.height == 0) {
            valueAnimator = ValueAnimator.ofInt(0, childViewHeight);
            rotation = -90;
        } else {
            valueAnimator = ValueAnimator.ofInt(childViewHeight, 0);
            rotation = 90;
        }

        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                childContainerParams.height = (int) valueAnimator.getAnimatedValue();
                getViewBinding().childContainer.requestLayout();
            }
        });
        valueAnimator.start();

        getViewBinding().mainToggle.animate()
                .setDuration(300)
                .setInterpolator(new LinearInterpolator())
                .rotation(rotation);
    }
}

package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PointsContainerViewBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EPointsContainerViewModel.class)
public class EPointsContainer extends DataBindingViewModelView<EPointsContainerViewModel, PointsContainerViewBinding> {

    private boolean mInstantiated = false;
    private Handler mHandler;
    private Runnable mToggleRunnable = new Runnable() {
        @Override
        public void run() {
            toggleView(getViewModel().isExpanded());
        }
    };
    private int mExpandedHeight = 0;

    //region constructors
    public EPointsContainer(Context context) {
        this(context, null);
    }

    public EPointsContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EPointsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_points_container_layout);
            mHandler = new Handler();
        }
    }
    //endregion

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().root.visibility(), getViewBinding().getRoot()));
        bind(ReactorTextView.text(getViewModel().pointsPerDay.text(), getViewBinding().pointsPerDay));
        bind(ReactorTextView.text(getViewModel().title.text(), getViewBinding().title),
                ReactorView.visibility(getViewModel().title.visibility(), getViewBinding().title));
        bind(ReactorTextView.text(getViewModel().subtitle.textCharSequence(), getViewBinding().subtitle),
                ReactorView.visibility(getViewModel().subtitle.visibility(), getViewBinding().subtitle));
    }

    private void toggleView(final boolean expand) {
        getViewModel().setExpanded(expand);
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null) {
            mHandler.post(mToggleRunnable);
            return;
        }
        mHandler.removeCallbacks(mToggleRunnable);

        if (mExpandedHeight <= 0 && expand) {
            return;
        }

        params.height = expand ? mExpandedHeight : 0;
        setLayoutParams(params);
    }

    @Override
    public void setVisibility(int visibility) {
        getViewModel().setRootVisibility(visibility);
    }

    public void setCarClassDetail(EHICarClassDetails details, boolean showPoints) {
        getViewModel().setCarClassDetails(details);
        mInstantiated = true;
        toggleView(showPoints);
    }

    public void setText(EHICarClassDetails details) {
        getViewModel().parseCarClassDetails(details);

        getViewBinding().actualPointsWrapper.measure(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mExpandedHeight = getViewBinding().actualPointsWrapper.getMeasuredHeight();

        requestLayout();
    }

    public boolean isInstantiated() {
        return mInstantiated;
    }

    public void animateShowPoints(boolean showPoints) {
        if (getViewModel().shouldPreventAnimations()) {
            toggleView(showPoints);
            return;
        }

        setVisibility(VISIBLE);
        ViewGroup.LayoutParams params = getLayoutParams();

        final int pointsHeight = mExpandedHeight;
        final int desiredHeight = showPoints ? pointsHeight : 0;
        final int startingHeight = getHeight();

        if (desiredHeight == 0 && startingHeight == 0) { // if the view is just being inflated, no animation needed
            params.height = desiredHeight;
            setLayoutParams(params);
        } else if (pointsHeight != desiredHeight || startingHeight != pointsHeight) {
            final float difference = (showPoints ? 1 : -1) * pointsHeight;

            Animation collapseAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    super.applyTransformation(interpolatedTime, t);
                    ViewGroup.LayoutParams pointParams = getLayoutParams();

                    pointParams.height = startingHeight + (int) (interpolatedTime * difference);
                    if (pointParams.height < 0) {
                        pointParams.height = 0;
                    } else if (pointParams.height > mExpandedHeight) {
                        pointParams.height = mExpandedHeight;
                    }

                    setLayoutParams(pointParams);
                }
            };

            collapseAnimation.setDuration(300);
            startAnimation(collapseAnimation);
        }
    }

    public void preventAnimation(boolean preventAnimations) {
        getViewModel().setPreventAnimations(preventAnimations);
    }
}

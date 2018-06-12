package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EPointsHeaderBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EPointsHeaderViewModel.class)
public class EPointsHeaderView extends DataBindingViewModelView<EPointsHeaderViewModel, EPointsHeaderBinding> {

    private boolean mShouldShowPoints;

    public EPointsHeaderView(Context context) {
        this(context, null);
    }

    public EPointsHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EPointsHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_points_header);
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().topRightText, getViewBinding().topRightText));
        bind(ReactorTextView.text(getViewModel().topLeftPointsText, getViewBinding().topLeftPoints),
                ReactorView.visible(getViewModel().topLeftPointsVisibility, getViewBinding().topLeftPoints));
        bind(ReactorTextView.text(getViewModel().topLeftHeaderText, getViewBinding().topLeftHeader),
                ReactorView.visible(getViewModel().topLeftHeaderVisibility, getViewBinding().topLeftHeader));
        bind(ReactorView.visible(getViewModel().dividerVisibility, getViewBinding().vPointsHeaderDivider));
    }

    /**
     * Only use if this is wrapped in a {@link android.support.design.widget.AppBarLayout}
     *
     * @param flags
     * @return
     */
    public EPointsHeaderView setScrollFlags(int flags) {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
        setLayoutParams(params);
        return this;
    }

    /**
     * @param text
     * @param clickListener null if you don't want to set it
     * @return
     */
    public EPointsHeaderView setTopRightText(String text, OnClickListener clickListener) {
        getViewModel().setTopRightText(text);
        if (clickListener != null) {
            getViewBinding().showHidePoints.setOnClickListener(clickListener);
        }
        return this;
    }

    public EPointsHeaderView setTopLeftPointsText(long points) {
        getViewModel().setTopLeftPointsText(points);
        return this;
    }

    public EPointsHeaderView setTopLeftHeaderText(String text) {
        getViewModel().setTopLeftHeaderText(text);
        return this;
    }

    public void setDividerVisible(final boolean visible) {
        getViewModel().setDividerVisibility(visible);
    }

    /**
     * method used to disable the show/hide of the view
     *
     * @param showPoints
     */
    public void shouldShowPoints(boolean showPoints) {
        mShouldShowPoints = showPoints;
    }

    @Override
    public void setVisibility(int visibility) {
        if (!mShouldShowPoints) {
            visibility = GONE;
        }
        super.setVisibility(visibility);
    }

    public static ReactorComputationFunction points(final ReactorVar<Long> points, final EPointsHeaderView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(points, points.getValue(), target)) {
                    target.setTopLeftPointsText(points.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction showPoints(final ReactorVar<Boolean> source, final EPointsHeaderView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    target.shouldShowPoints(source.getValue());
                }
            }
        };
    }

    public static ReactorComputationFunction visibility(final ReactorVar<Integer> source, final EPointsHeaderView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source, source.getValue(), target)) {
                    //noinspection WrongConstant
                    target.setVisibility(source.getValue());
                }
            }
        };
    }


}

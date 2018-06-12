package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorEPointsHeaderState extends ReactorViewState {
    ReactorVar<Long> mPoints;
    private ReactorPropertyChangedListener<Long> mPointsChangedListener;
    ReactorVar<Long> mShowPoints;
    private ReactorPropertyChangedListener<Long> mShowPointsChangedListener;

    public void setPointsChangedListener(final ReactorPropertyChangedListener<Long> pointsChangedListener) {
        mPointsChangedListener = pointsChangedListener;
    }

    public ReactorVar<Long> points() {
        if(mPoints == null){
            mPoints = new ReactorVar<Long>(){
                @Override
                public void setValue(final Long value) {
                    super.setValue(value);
                    if(mPointsChangedListener != null){
                        mPointsChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mPoints;
    }

    public void setPoints(Long points){
        points().setValue(points);
    }

    public void setShowPointsChangedListener(final ReactorPropertyChangedListener<Long> showPointsChangedListener) {
        mShowPointsChangedListener = showPointsChangedListener;
    }

    public ReactorVar<Long> showPoints() {
        if(mShowPoints == null){
            mShowPoints = new ReactorVar<Long>(){
                @Override
                public void setValue(final Long value) {
                    super.setValue(value);
                    if(mShowPointsChangedListener != null){
                        mShowPointsChangedListener.onPropertyChanged(value);
                    }
                }
            };
        }

        return mShowPoints;
    }

    public void setShowPoints(Long showPoints){
        showPoints().setValue(showPoints);
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorEPointsHeaderState$$Unbinder.unbind(this);
        mPointsChangedListener = null;
        mShowPointsChangedListener = null;
    }
}

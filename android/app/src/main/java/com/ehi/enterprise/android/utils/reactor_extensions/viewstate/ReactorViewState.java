package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import android.graphics.drawable.Drawable;
import android.view.View;

import io.dwak.reactor.ReactorDependency;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorViewState extends ReactorVar{
	public static int VISIBLE = View.VISIBLE;
	public static int INVISIBLE = View.INVISIBLE;
	public static int GONE = View.GONE;

	ReactorVar<Boolean> mVisible;
    private ReactorPropertyChangedListener<Boolean> mVisibleChangedListener;

	ReactorVar<Integer> mVisibility;
    private ReactorPropertyChangedListener<Integer> mVisibilityChangedListener;

	ReactorVar<Boolean> mEnabled;
    private ReactorPropertyChangedListener<Boolean> mEnabledChangedListener;

	ReactorVar<Boolean> mSelected;
    private ReactorPropertyChangedListener<Boolean> mSelectedChangedListener;

	ReactorVar<Integer> mBackgroundRes;
    private ReactorPropertyChangedListener<Integer> mBackgroundResChangedListener;

	ReactorVar<Drawable> mBackgroundDrawable;
    private ReactorPropertyChangedListener<Drawable> mBackgroundDrawableChangedListener;

	ReactorVar<Integer> mBackgroundColor;
    private ReactorPropertyChangedListener<Integer> mBackgroundColorChangedListener;

    ReactorVar<Float> mAlpha;
    private ReactorPropertyChangedListener<Float> mAlphaChangedListener;

    ReactorVar<Float> mRotation;
    private ReactorPropertyChangedListener<Float> mRotationChangedListener;

    public ReactorViewState() {
        super.setDependency(null);
    }

    public void setVisibleChangedListener(final ReactorPropertyChangedListener<Boolean> visibleChangedListener) {
        mVisibleChangedListener = visibleChangedListener;
    }

    public ReactorVar<Boolean> visible() {
		if (mVisible == null) mVisible = new ReactorVar<Boolean>(){
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if(mVisibleChangedListener != null){
                    mVisibleChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mVisible;
	}

	public void setVisible(boolean visibility) {
		visible().setValue(visibility);
	}

    public void setVisibilityChangedListener(final ReactorPropertyChangedListener<Integer> visibilityChangedListener) {
        mVisibilityChangedListener = visibilityChangedListener;
    }

    public ReactorVar<Integer> visibility() {
		if (mVisibility == null) mVisibility = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mVisibilityChangedListener != null){
                    mVisibilityChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mVisibility;
	}

	public void setVisibility(int visibility) {
		visibility().setValue(visibility);
	}

    public void setEnabledChangedListener(final ReactorPropertyChangedListener<Boolean> enabledChangedListener) {
        mEnabledChangedListener = enabledChangedListener;
    }

    public ReactorVar<Boolean> enabled() {
		if (mEnabled == null) mEnabled = new ReactorVar<Boolean>(){
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if(mEnabledChangedListener != null){
                    mEnabledChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		enabled().setValue(enabled);
	}

    public void setSelectedChangedListener(final ReactorPropertyChangedListener<Boolean> selectedChangedListener) {
        mSelectedChangedListener = selectedChangedListener;
    }

    public ReactorVar<Boolean> selected() {
		if (mSelected == null) mSelected = new ReactorVar<Boolean>(){
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if(mSelectedChangedListener != null){
                    mSelectedChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mSelected;
	}

	public void setSelected(final boolean selected) {
		selected().setValue(selected);
	}

    public void setBackgroundResChangedListener(final ReactorPropertyChangedListener<Integer> backgroundResChangedListener) {
        mBackgroundResChangedListener = backgroundResChangedListener;
    }

    public ReactorVar<Integer> backgroundResource() {
		if (mBackgroundRes == null) mBackgroundRes = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mBackgroundResChangedListener != null){
                    mBackgroundResChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mBackgroundRes;
	}

	public void setBackgroundResource(int backgroundRes) {
		backgroundResource().setValue(backgroundRes);
	}

    public void setBackgroundDrawableChangedListener(final ReactorPropertyChangedListener<Drawable> backgroundDrawableChangedListener) {
        mBackgroundDrawableChangedListener = backgroundDrawableChangedListener;
    }

    public ReactorVar<Drawable> background() {
		if (mBackgroundDrawable == null) mBackgroundDrawable = new ReactorVar<Drawable>(){
            @Override
            public void setValue(final Drawable value) {
                super.setValue(value);
                if(mBackgroundDrawableChangedListener != null){
                    mBackgroundDrawableChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mBackgroundDrawable;
	}

	public void setBackground(final Drawable backgroundDrawable) {
		background().setValue(backgroundDrawable);
	}

    public void setBackgroundColorChangedListener(final ReactorPropertyChangedListener<Integer> backgroundColorChangedListener) {
        mBackgroundColorChangedListener = backgroundColorChangedListener;
    }

    public ReactorVar<Integer> backgroundColor() {
		if (mBackgroundColor == null) mBackgroundColor = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mBackgroundColorChangedListener != null){
                    mBackgroundColorChangedListener.onPropertyChanged(value);
                }
            }
        };

		return mBackgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		backgroundColor().setValue(backgroundColor);
	}

    public void setAlphaColorChangedListener(final ReactorPropertyChangedListener<Float> backgroundColorChangedListener) {
        mAlphaChangedListener = backgroundColorChangedListener;
    }

    public ReactorVar<Float> alpha() {
        if (mAlpha == null) mAlpha = new ReactorVar<Float>(){
            @Override
            public void setValue(final Float value) {
                super.setValue(value);
                if(mAlphaChangedListener != null){
                    mAlphaChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mAlpha;
    }

    public void setRotationChangedListener(final ReactorPropertyChangedListener<Float> rotationChangedListener) {
        mAlphaChangedListener = rotationChangedListener;
    }

    public void setRotation(float rotationValue) {
        rotation().setValue(rotationValue);
    }

    public ReactorVar<Float> rotation() {
        if (mRotation == null) mRotation = new ReactorVar<Float>(){
            @Override
            public void setValue(final Float value) {
                super.setValue(value);
                if(mRotationChangedListener != null){
                    mRotationChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mRotation;
    }

    public void setAlpha(float alpha) {
        alpha().setValue(alpha);
    }

    @Override
    public void unbindDependency() {
        ReactorViewState$$Unbinder.unbind(this);
        mEnabledChangedListener = null;
        mSelectedChangedListener = null;
        mVisibilityChangedListener = null;
        mVisibleChangedListener = null;
        mBackgroundColorChangedListener = null;
        mBackgroundDrawableChangedListener = null;
        mBackgroundResChangedListener = null;
        mRotationChangedListener = null;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(final Object value) {
    }

    @Override
    public ReactorDependency getDependency() {
        return null;
    }

    @Override
    public void setDependency(final ReactorDependency dependency) {
    }

    @Override
    public Object getRawValue() {
        return null;
    }

    @Override
    public void setRawValue(final Object value) {
    }
}

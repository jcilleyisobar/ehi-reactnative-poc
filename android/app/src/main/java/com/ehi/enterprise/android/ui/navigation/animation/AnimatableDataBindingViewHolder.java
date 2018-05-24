package com.ehi.enterprise.android.ui.navigation.animation;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;

public abstract class AnimatableDataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    AnimateState mState = null;
    float mStart;
    float mEnd;

    private T mViewBinding;

    protected static ViewDataBinding createViewBinding(@NonNull Context context, @LayoutRes int layoutResId, @NonNull ViewGroup parent) {
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResId, parent, false);
    }

    public T getViewBinding() {
        return mViewBinding;
    }

    public AnimatableDataBindingViewHolder(View itemView) {
        super(itemView);
    }

    public AnimatableDataBindingViewHolder(T viewBinding) {
        this(viewBinding, 0.0f, 1.0f);
        mViewBinding = viewBinding;
    }

    public AnimatableDataBindingViewHolder(final T viewBinding, float start, float end) {
        super(viewBinding.getRoot());
        mViewBinding = viewBinding;
        mStart = start;
        mEnd = end;
    }

    /**
     * @param state state required to animate a animateableviewholder
     */
    public void setState(AnimateState state) {
        mState = state;
    }

    public abstract LinearLayout getLayout();

    public void update(float current) {
        if (mState == null) {
            return;
        }
        float percentage = (current == 0) ? 0 : current / (mEnd - mStart);
        mState.update(percentage);
    }

    public void animationOverride(float percent, int time) {
        if (mState == null) {
            return;
        }
        float percentage = (percent == 0) ? 0 : percent / (mEnd - mStart);
        mState.animateOveride(percentage, time);
    }

    public void resetAnimation() {
        getLayout().setAlpha(mState.getStartAlpha());
        getLayout().setTranslationX(mState.getStartX());
        getLayout().setTranslationY(mState.getStartY());
    }

    /**
     * a class based on a LINEAR progression of a state, y = x
     */
    public static class LinearLeftFadeInAnimateState extends AnimateState {
        private float mPercentage = 0;

        public LinearLeftFadeInAnimateState(final LinearLayout layout, final float offSet) {
            super(layout);
            layout.post(new Runnable() {
                @Override
                public void run() {
                    setStart_X((offSet * layout.getWidth()) * -1);
                }
            });
            getLayout().setLayerType(View.LAYER_TYPE_HARDWARE, null);
            setStartAlpha((-1f * offSet))
                    .setEndAlpha(1)
                    .setEnd_X(0);
        }

        @Override
        public void update(float percentage) {
            final float computeX = computeX(percentage);
            final float computeAlpha = computeAlpha(percentage);
            if (!Float.isInfinite(computeX) && !Float.isInfinite(computeAlpha)) {
                getLayout().animate()
                        .setDuration((long) Math.abs(10f * (percentage - mPercentage)))
                        .translationX(computeX)
                        .alpha(computeAlpha);

                mPercentage = percentage;
            }
        }

        @Override
        public float computeAlpha(float percent) {
            float computed = mStart_Alpha + ((mEnd_Alpha - mStart_Alpha) * percent);
            return (computed < 0) ? 0 : computed;
        }

        @Override
        public void animateOveride(float percentage, int time) {
            final float computeX = computeX(percentage);
            final float computeAlpha = computeAlpha(percentage);
            if (!Float.isInfinite(computeX) && !Float.isInfinite(computeAlpha)) {
                getLayout().animate().setDuration((long) time)
                        .translationX(computeX)
                        .alpha(computeAlpha);
            }
        }
    }

    /**
     * a class used to manage the start to finish progression of a viewholder animation based on a
     * linear progression opposed to a time based progression
     */
    public static abstract class AnimateState {
        protected float mStart_Alpha = 0f;
        protected float mEnd_Alpha = 1f;
        protected float mStart_X = 0;
        protected float mEnd_X = 0;
        protected float mStart_Y = 0;
        protected float mEnd_Y = 0;
        protected LinearLayout mLayout;

        public AnimateState(LinearLayout layout) {
            mLayout = layout;
        }

        public void animateOveride(float percentage, int time) {
            throw new NotImplementedException();
        }

        public LinearLayout getLayout() {
            return mLayout;
        }

        public abstract void update(float percentage);

        public float getStartAlpha() {
            return mStart_Alpha;
        }

        public AnimateState setStartAlpha(float start_Alpha) {
            mStart_Alpha = start_Alpha;
            return this;
        }

        public float getEnd_Alpha() {
            return mEnd_Alpha;
        }

        public AnimateState setEndAlpha(float end_Alpha) {
            mEnd_Alpha = end_Alpha;
            return this;
        }

        public float getStartX() {
            return mStart_X;
        }

        public AnimateState setStart_X(float start_X) {
            mStart_X = start_X;
            return this;
        }

        public float getEndX() {
            return mEnd_X;
        }

        public AnimateState setEnd_X(float end_X) {
            mEnd_X = end_X;
            return this;
        }

        public float getStartY() {
            return mStart_Y;
        }

        public AnimateState setStart_Y(float start_Y) {
            mStart_Y = start_Y;
            return this;
        }

        public float getEndY() {
            return mEnd_Y;
        }

        public AnimateState setEnd_Y(float end_Y) {
            mEnd_Y = end_Y;
            return this;
        }

        public float computeAlpha(float percent) {
            return mStart_Alpha + ((mEnd_Alpha - mStart_Alpha) * percent);
        }

        public float computeX(float percent) {
            return mStart_X + ((mEnd_X - mStart_X) * percent);
        }

        public float computeY(float percent) {
            return mStart_Y + ((mEnd_Y - mStart_Y) * percent);
        }
    }

}
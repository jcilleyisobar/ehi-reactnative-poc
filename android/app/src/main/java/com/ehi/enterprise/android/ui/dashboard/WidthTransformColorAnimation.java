package com.ehi.enterprise.android.ui.dashboard;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class WidthTransformColorAnimation extends Animation {
        private final View mParent;
        private final float mNewPosX;
        private float mChildOffset;
        private float mParentStartX;
        private int mWidth;
        private int mStartWidth;
        private View mView;

        public WidthTransformColorAnimation(@Nullable View parent, float posX, View view, int width) {
            mView = view;
            mParent = parent;
            mNewPosX = posX;
            if (parent != null) {
                mParentStartX = parent.getX();
                mChildOffset = mView.getX();
            }
            mWidth = width;
            mStartWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
            if (mParent != null) {
                float updatedPosX = mParentStartX + ((mNewPosX - mParentStartX) * interpolatedTime);
                mParent.setX(updatedPosX);
            }
            mView.getLayoutParams().width = newWidth;
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
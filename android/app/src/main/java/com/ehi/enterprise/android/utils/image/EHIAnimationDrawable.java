package com.ehi.enterprise.android.utils.image;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;

public class EHIAnimationDrawable extends AnimationDrawable {


    private Runnable mRunnable;
    private AnimationDrawable mInternalDrawable;
    private Handler mHandler;

    public EHIAnimationDrawable(AnimationDrawable internalDrawable, Runnable runnable) {
        this(internalDrawable, null, runnable);
    }

    public EHIAnimationDrawable(AnimationDrawable internalDrawable, Handler handler, Runnable runnable) {
        mRunnable = runnable;
        mInternalDrawable = internalDrawable;
        mHandler = handler == null ? new Handler() : handler;
    }

    public void addFinishCallback(Runnable runnable){
        mRunnable = runnable;
    }

    @Override
    public Drawable getFrame(int index) {
        if(index == getNumberOfFrames() - 1 && mRunnable != null){
            mHandler.postDelayed(mRunnable, getDuration(index));
        }
        return super.getFrame(index);
    }
}

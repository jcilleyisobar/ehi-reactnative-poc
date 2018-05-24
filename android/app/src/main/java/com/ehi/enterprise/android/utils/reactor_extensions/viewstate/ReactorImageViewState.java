package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorImageViewState extends ReactorViewState {

    ReactorVar<Integer> mSrcRes;
    ReactorVar<Drawable> mDrawableResource;

    public ReactorVar<Integer> imageResource(){
        if(mSrcRes == null) mSrcRes = new ReactorVar<>();

        return mSrcRes;
    }

    public void setImageResource(@DrawableRes int textRes) {
        if(mSrcRes == null) mSrcRes = new ReactorVar<>();

        mSrcRes.setValue(textRes);
    }

    public ReactorVar<Drawable> imageDrawable(){
        if(mDrawableResource == null) mDrawableResource = new ReactorVar<>();

        return mDrawableResource;
    }

    public void setImageDrawable(Drawable imageDrawable){
        if(mDrawableResource == null) mDrawableResource = new ReactorVar<>();

        mDrawableResource.setValue(imageDrawable);
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorImageViewState$$Unbinder.unbind(this);
    }
}

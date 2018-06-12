package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorBookRentalButtonState extends ReactorViewState {
    ReactorVar<String> mTitle;
    private ReactorPropertyChangedListener<String> mTitleChangedListener;

    ReactorVar<String> mSubtitle;
    private ReactorPropertyChangedListener<String> mSubtitleChangedListener;

    ReactorVar<CharSequence> mPrice;
    private ReactorPropertyChangedListener<CharSequence> mPriceChangedListener;

    ReactorVar<String> mPriceSubtitle;
    private ReactorPropertyChangedListener<String> mPriceSubtitleChangedListener;

    ReactorVar<Integer> mPriceVisibility;
    private ReactorPropertyChangedListener<Integer> mPriceVisibilityChangedListener;

    ReactorVar<Integer> mNetRateVisibility;

    ReactorVar<Boolean> mProgress;
    private ReactorPropertyChangedListener<Boolean> mProgressChangedListener;

    public void setTitleChangedListener(final ReactorPropertyChangedListener<String> titleChangedListener) {
        mTitleChangedListener = titleChangedListener;
    }

    public ReactorVar<String> title(){
        if(mTitle == null) mTitle = new ReactorVar<String>(){
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if(mTitleChangedListener != null){
                    mTitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mTitle;
    }

    public void setTitle(String title){
        title().setValue(title);
    }

    public void setSubtitleChangedListener(final ReactorPropertyChangedListener<String> subtitleChangedListener) {
        mSubtitleChangedListener = subtitleChangedListener;
    }

    public ReactorVar<String> subtitle(){
        if(mSubtitle == null) mSubtitle = new ReactorVar<String>(){
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if(mSubtitleChangedListener != null){
                    mSubtitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mSubtitle;
    }

    public void setSubtitle(String subtitle){
        subtitle().setValue(subtitle);
    }

    public void setPriceChangedListener(final ReactorPropertyChangedListener<CharSequence> priceChangedListener) {
        mPriceChangedListener = priceChangedListener;
    }

    public ReactorVar<CharSequence> price(){
        if(mPrice == null) mPrice = new ReactorVar<CharSequence>(){
            @Override
            public void setValue(final CharSequence value) {
                super.setValue(value);
                if(mPriceChangedListener != null){
                    mPriceChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mPrice;
    }

    public void setPrice(CharSequence price){
        price().setValue(price);
        setPriceVisibility(VISIBLE);
        setNetRateVisibility(GONE);
    }

    public void showNetRate() {
        setPriceVisibility(GONE);
        setNetRateVisibility(VISIBLE);
    }

    public void setPriceSubtitleChangedListener(final ReactorPropertyChangedListener<String> priceSubtitleChangedListener) {
        mPriceSubtitleChangedListener = priceSubtitleChangedListener;
    }

    public ReactorVar<String> priceSubtitle(){
        if(mPriceSubtitle == null) mPriceSubtitle = new ReactorVar<String>(){
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if(mPriceSubtitleChangedListener != null){
                    mPriceSubtitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mPriceSubtitle;
    }

    public void setPriceSubtitle(String priceSubtitle){
        priceSubtitle().setValue(priceSubtitle);
    }

    public void setProgressChangedListener(final ReactorPropertyChangedListener<Boolean> progressChangedListener) {
        mProgressChangedListener = progressChangedListener;
    }

    public ReactorVar<Boolean> progress(){
        if(mProgress == null) mProgress = new ReactorVar<Boolean>(){
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if(mProgressChangedListener != null){
                    mProgressChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mProgress;
    }

    public void setProgress(boolean progress){
        progress().setValue(progress);
        if (progress) {
            setNetRateVisibility(GONE);
        }
    }

    public void setPriceVisibilityChangedListener(final ReactorPropertyChangedListener<Integer> priceVisibilityChangedListener) {
        mPriceVisibilityChangedListener = priceVisibilityChangedListener;
    }

    public ReactorVar<Integer> priceVisibility(){
        if(mPriceVisibility == null) mPriceVisibility = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mPriceVisibilityChangedListener != null){
                    mPriceVisibilityChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mPriceVisibility;
    }

    public ReactorVar<Integer> netRateVisibility(){
        if (mNetRateVisibility == null) mNetRateVisibility = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
            }
        };
        return mNetRateVisibility;
    }

    public void setPriceVisibility(int visibility){
        priceVisibility().setValue(visibility);
    }

    public void setNetRateVisibility(int visibility){
        netRateVisibility().setValue(visibility);
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorBookRentalButtonState$$Unbinder.unbind(this);
        mTitleChangedListener = null;
        mSubtitleChangedListener = null;
        mPriceChangedListener = null;
        mPriceSubtitleChangedListener = null;
        mProgressChangedListener = null;
        mPriceVisibilityChangedListener = null;
    }
}

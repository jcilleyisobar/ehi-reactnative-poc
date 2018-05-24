package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import android.support.annotation.StringRes;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorTextViewState extends ReactorViewState {
    ReactorVar<CharSequence> mTextCharSequence;
    private ReactorPropertyChangedListener<CharSequence> mTextCharSequenceChangedListener;

    ReactorVar<String> mText;
    private ReactorPropertyChangedListener<String> mTextChangedListener;

    ReactorVar<Integer> mTextRes;
    private ReactorPropertyChangedListener<Integer> mTextResChangedListener;

    ReactorVar<Integer> mDrawableLeft;
    private ReactorPropertyChangedListener<Integer> mDrawableLeftChangedListener;

    ReactorVar<Integer> mDrawableRight;
    private ReactorPropertyChangedListener<Integer> mDrawableRightChangedListener;

    ReactorVar<Integer> mDrawableTop;
    private ReactorPropertyChangedListener<Integer> mDrawableTopChangedListener;

    ReactorVar<Integer> mDrawableBottom;
    private ReactorPropertyChangedListener<Integer> mDrawableBottomChangedListener;

    ReactorVar<Integer> mCompoundDrawablePadding;
    private ReactorPropertyChangedListener<Integer> mCompoundDrawablePaddingInDpChangedListener;

    ReactorVar<Integer> mTextColor;
    private ReactorPropertyChangedListener<Integer> mTextColorChangedListener;

    public ReactorTextViewState() {
        super();
    }

    public void setTextCharSequenceChangedListener(final ReactorPropertyChangedListener<CharSequence> textCharSequenceChangedListener) {
        mTextCharSequenceChangedListener = textCharSequenceChangedListener;
    }

    public ReactorVar<CharSequence> textCharSequence(){
        if(mTextCharSequence == null) mTextCharSequence = new ReactorVar<CharSequence>(){
            @Override
            public void setValue(final CharSequence value) {
                super.setValue(value);
                if(mTextCharSequenceChangedListener != null){
                    mTextCharSequenceChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mTextCharSequence;
    }

    public void setText(CharSequence text){
        textCharSequence().setValue(text);
    }

    public void setTextChangedListener(final ReactorPropertyChangedListener<String> changedListener) {
        mTextChangedListener = changedListener;
    }

    public ReactorVar<String> text() {
        if (mText == null) mText = new ReactorVar<String>(){
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if(mTextChangedListener != null){
                    mTextChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mText;
    }

    public void setText(String text) {
        text().setValue(text);
    }

    public void setTextResChangedListener(final ReactorPropertyChangedListener<Integer> textResChangedListener) {
        mTextResChangedListener = textResChangedListener;
    }

    public ReactorVar<Integer> textRes(){
        if(mTextRes == null) mTextRes = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mTextResChangedListener != null){
                    mTextResChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mTextRes;
    }

    public void setText(@StringRes int textRes) {
        textRes().setValue(textRes);
    }

    public void setDrawableLeftChangedListener(final ReactorPropertyChangedListener<Integer> drawableLeftChangedListener) {
        mDrawableLeftChangedListener = drawableLeftChangedListener;
    }

    public ReactorVar<Integer> drawableLeft() {
        if(mDrawableLeft == null) mDrawableLeft = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mDrawableLeftChangedListener != null){
                    mDrawableLeftChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mDrawableLeft;
    }

    public void setDrawableLeft(final int drawableLeft) {
        drawableLeft().setValue(drawableLeft);
    }

    public void setDrawableRightChangedListener(final ReactorPropertyChangedListener<Integer> drawableRightChangedListener) {
        mDrawableRightChangedListener = drawableRightChangedListener;
    }

    public ReactorVar<Integer> drawableRight() {
        if(mDrawableRight== null) mDrawableRight= new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mDrawableRightChangedListener != null){
                    mDrawableRightChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mDrawableRight;
    }

    public void setDrawableRight(final int drawableRight) {
        drawableRight().setValue(drawableRight);
    }

    public void setDrawableTopChangedListener(final ReactorPropertyChangedListener<Integer> drawableTopChangedListener) {
        mDrawableTopChangedListener = drawableTopChangedListener;
    }

    public ReactorVar<Integer> drawableTop() {
        if(mDrawableTop == null) mDrawableTop= new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mDrawableTopChangedListener != null){
                    mDrawableTopChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mDrawableTop;
    }

    public void setDrawableTop(final int drawableTop) {
        drawableTop().setValue(drawableTop);
    }

    public void setDrawableBottomChangedListener(final ReactorPropertyChangedListener<Integer> drawableBottomChangedListener) {
        mDrawableBottomChangedListener = drawableBottomChangedListener;
    }

    public ReactorVar<Integer> drawableBottom() {
        if(mDrawableBottom == null) mDrawableBottom = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mDrawableBottomChangedListener != null){
                    mDrawableTopChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mDrawableBottom;
    }

    public void setDrawableBottom(final int drawableBottom) {
        drawableBottom().setValue(drawableBottom);
    }

    public void setCompoundDrawablePaddinginDpChangedListener(final ReactorPropertyChangedListener<Integer> compoundDrawablePaddingChangedListener) {
        mCompoundDrawablePaddingInDpChangedListener = compoundDrawablePaddingChangedListener;
    }

    public ReactorVar<Integer> compoundDrawablePaddingInDp() {
        if(mCompoundDrawablePadding == null) mCompoundDrawablePadding = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mCompoundDrawablePaddingInDpChangedListener != null){
                    mCompoundDrawablePaddingInDpChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mCompoundDrawablePadding;
    }

    public void setCompoundDrawablePaddingInDp(final int paddingInDp) {
        compoundDrawablePaddingInDp().setValue(paddingInDp);
    }

    public void setTextColor(final int textColor) {
        textColor().setValue(textColor);
    }

    public ReactorVar<Integer> textColor() {
        if(mTextColor == null) mTextColor = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mTextColorChangedListener!= null){
                    mTextColorChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mTextColor;
    }

    public void setTextColorChangedListener(ReactorPropertyChangedListener<Integer> textColorChangedListener) {
        mTextColorChangedListener = textColorChangedListener;
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorTextViewState$$Unbinder.unbind(this);
        mTextCharSequenceChangedListener = null;
        mTextChangedListener = null;
        mTextResChangedListener = null;
        mDrawableBottomChangedListener = null;
        mDrawableTopChangedListener = null;
        mDrawableLeftChangedListener = null;
        mDrawableRightChangedListener = null;
        mCompoundDrawablePaddingInDpChangedListener = null;
        mTextColorChangedListener = null;
    }
}

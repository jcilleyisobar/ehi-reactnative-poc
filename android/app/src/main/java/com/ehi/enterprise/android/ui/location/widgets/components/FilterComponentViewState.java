package com.ehi.enterprise.android.ui.location.widgets.components;

import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FilterComponentViewState extends ReactorViewState {
    ReactorVar<String> mTitle;
    private ReactorPropertyChangedListener<String> mTitleChangedListener;

    ReactorVar<Integer> mResetButtonVisibility;
    private ReactorPropertyChangedListener<Integer> mResetButtonChangedListener;

    ReactorVar<Boolean> mDefaultState;
    private ReactorPropertyChangedListener<Boolean> mDefaultStateListener;


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

    public ReactorVar<Boolean> resetDefaultState(){
        if(mDefaultState == null) mDefaultState = new ReactorVar<Boolean>(){
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if(mDefaultStateListener != null){
                    mDefaultStateListener.onPropertyChanged(value);
                }
            }
        };

        return mDefaultState;
    }

    public void setTitle(String title){
        title().setValue(title);
    }

    public void setResetButtonVisibility(int visibility) {
        resetButtonVisibility().setValue(visibility);
    }

    public ReactorVar<Integer> resetButtonVisibility(){
        if(mResetButtonVisibility == null) mResetButtonVisibility = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mResetButtonChangedListener != null){
                    mResetButtonChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mResetButtonVisibility;
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        mTitleChangedListener = null;
        mResetButtonChangedListener = null;
        mDefaultStateListener = null;
    }
}


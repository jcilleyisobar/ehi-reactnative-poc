package com.ehi.enterprise.android.ui.location.widgets.components;

import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FilterTextMapComponentViewState extends ReactorViewState {
    ReactorVar<String> mTitle;
    private ReactorPropertyChangedListener<String> mTitleChangedListener;

    ReactorVar<Integer> mFilterIconVisibility;
    private ReactorPropertyChangedListener<Integer> mFilterIconVisibilityChangedListener;

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

    public void setFilterIconVisibility(int visibility) {
        filterIconVisibility().setValue(visibility);
    }

    public ReactorVar<Integer> filterIconVisibility(){
        if(mFilterIconVisibility == null) mFilterIconVisibility = new ReactorVar<Integer>(){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mFilterIconVisibilityChangedListener != null){
                    mFilterIconVisibilityChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mFilterIconVisibility;
    }

    public void setDateTitle(String title){
        title().setValue(title);
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        mTitleChangedListener = null;
        mFilterIconVisibility = null;
    }
}

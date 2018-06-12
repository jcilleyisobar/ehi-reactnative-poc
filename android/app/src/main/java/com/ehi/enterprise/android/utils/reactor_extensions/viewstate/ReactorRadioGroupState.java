package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import android.support.annotation.IdRes;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorRadioGroupState extends ReactorViewState{
    ReactorVar<Integer> mCheckedId;
    private ReactorPropertyChangedListener<Integer> mCheckedIdChangedListener;

    public ReactorRadioGroupState() {
        super();
    }

    public void setCheckedIdChangedListener(final ReactorPropertyChangedListener<Integer> checkedIdChangedListener) {
        mCheckedIdChangedListener = checkedIdChangedListener;
    }

    public ReactorVar<Integer> checkedId() {
        if (mCheckedId == null) mCheckedId = new ReactorVar<Integer>(0){
            @Override
            public void setValue(final Integer value) {
                super.setValue(value);
                if(mCheckedIdChangedListener != null){
                    mCheckedIdChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mCheckedId;
    }

    public void setCheckedId(@IdRes int checkedId) {
        checkedId().setValue(checkedId);
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorRadioGroupState$$Unbinder.unbind(this);
        mCheckedIdChangedListener = null;
    }
}

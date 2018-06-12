package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorCompoundButtonState extends ReactorTextViewState {
    ReactorVar<Boolean> mChecked;
    private ReactorPropertyChangedListener<Boolean> mCheckedChangedListener;

    public ReactorCompoundButtonState() {
        super();
    }

    public void setCheckedChangedListener(final ReactorPropertyChangedListener<Boolean> checkedChangedListener) {
        mCheckedChangedListener = checkedChangedListener;
    }

    public ReactorVar<Boolean> checked() {
        if (mChecked == null) mChecked = new ReactorVar<Boolean>(false) {
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if (mCheckedChangedListener != null) {
                    mCheckedChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mChecked;
    }

    public void setChecked(boolean checked) {
        checked().setValue(checked);
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorCompoundButtonState$$Unbinder.unbind(this);
        mCheckedChangedListener = null;
    }
}

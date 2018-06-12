package com.ehi.enterprise.android.ui.location.widgets.components;

import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FilterMapComponentViewState extends ReactorViewState {
    ReactorVar<String> mPickupDateTitle;
    ReactorVar<String> mPickupTimeTitle;
    ReactorVar<String> mReturnDateTitle;
    ReactorVar<String> mReturnTimeTitle;
    ReactorVar<Boolean> mPickupDefaultState;
    ReactorVar<Boolean> mDropoffDefaultState;
    private ReactorPropertyChangedListener<String> mPickupDateTitleChangedListener;
    private ReactorPropertyChangedListener<String> mPickupTimeTitleChangedListener;
    private ReactorPropertyChangedListener<String> mReturnDateTitleChangedListener;
    private ReactorPropertyChangedListener<String> mReturnTimeTitleChangedListener;
    private ReactorPropertyChangedListener<Boolean> mPickupDefaultStateListener;
    private ReactorPropertyChangedListener<Boolean> mDropoffDefaultStateListener;

    public ReactorVar<String> pickupDateTitle() {
        if (mPickupDateTitle == null) mPickupDateTitle = new ReactorVar<String>() {
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if (mPickupDateTitleChangedListener != null) {
                    mPickupDateTitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mPickupDateTitle;
    }

    public void setPickupDateTitle(String title) {
        pickupDateTitle().setValue(title);
    }

    public ReactorVar<String> pickupTimeTitle() {
        if (mPickupTimeTitle == null) mPickupTimeTitle = new ReactorVar<String>() {
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if (mPickupTimeTitleChangedListener != null) {
                    mPickupTimeTitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mPickupTimeTitle;
    }

    public void setPickupTimeTitle(String title) {
        pickupTimeTitle().setValue(title);
    }

    public ReactorVar<String> dropoffDateTitle() {
        if (mReturnDateTitle == null) mReturnDateTitle = new ReactorVar<String>() {
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if (mReturnDateTitleChangedListener != null) {
                    mReturnDateTitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mReturnDateTitle;
    }

    public void setDropoffDateTitle(String title) {
        dropoffDateTitle().setValue(title);
    }

    public ReactorVar<String> dropoffTimeTitle() {
        if (mReturnTimeTitle == null) mReturnTimeTitle = new ReactorVar<String>() {
            @Override
            public void setValue(final String value) {
                super.setValue(value);
                if (mReturnTimeTitleChangedListener != null) {
                    mReturnTimeTitleChangedListener.onPropertyChanged(value);
                }
            }
        };

        return mReturnTimeTitle;
    }

    public void setDropoffTimeTitle(String title) {
        dropoffTimeTitle().setValue(title);
    }

    public ReactorVar<Boolean> pickupDefaultState() {
        if (mPickupDefaultState == null) mPickupDefaultState = new ReactorVar<Boolean>() {
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if (mPickupDefaultStateListener != null) {
                    mPickupDefaultStateListener.onPropertyChanged(value);
                }
            }
        };

        return mPickupDefaultState;
    }

    public ReactorVar<Boolean> dropoffDefaultState() {
        if (mDropoffDefaultState == null) mDropoffDefaultState = new ReactorVar<Boolean>() {
            @Override
            public void setValue(final Boolean value) {
                super.setValue(value);
                if (mDropoffDefaultStateListener != null) {
                    mDropoffDefaultStateListener.onPropertyChanged(value);
                }
            }
        };

        return mDropoffDefaultState;
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        mPickupDateTitleChangedListener = null;
        mPickupTimeTitleChangedListener = null;
        mReturnDateTitleChangedListener = null;
        mReturnTimeTitleChangedListener = null;
        mPickupDefaultStateListener = null;
        mDropoffDefaultStateListener = null;
    }
}
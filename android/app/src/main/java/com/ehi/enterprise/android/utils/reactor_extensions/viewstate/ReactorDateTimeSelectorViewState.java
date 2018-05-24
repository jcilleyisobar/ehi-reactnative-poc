package com.ehi.enterprise.android.utils.reactor_extensions.viewstate;

import java.util.Date;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ReactorDateTimeSelectorViewState extends ReactorViewState {

    ReactorVar<Date> mSelectedDate;
    private ReactorPropertyChangedListener<Date> mSelectedDateListener;

    ReactorVar<Date> mSelectedTime;
    private ReactorPropertyChangedListener<Date> mSelectedTimeListener;

    ReactorVar<Integer> mPickerBackgroundDrawableRes;
    private ReactorPropertyChangedListener<Integer> mPickerBackgroundDrawableResListener;

    ReactorVar<Float> mPickerIconTextAlpha;
    private ReactorPropertyChangedListener<Float> mPickerIconTextAlphaListener;

    ReactorVar<Boolean> mTimeEnabled;
    private ReactorPropertyChangedListener<Boolean> mTimeEnabledListener;


    public ReactorVar<Date> selectedDate() {
        if (mSelectedDate == null) mSelectedDate = new ReactorVar<Date>() {
            @Override
            public void setValue(final Date value) {
                super.setValue(value);
                if (mSelectedDateListener != null) {
                    mSelectedDateListener.onPropertyChanged(value);
                }
            }
        };

        return mSelectedDate;
    }

    public void setSelectedDate(Date date) {
        selectedDate().setValue(date);
    }

    public void setSelectedDateListener(final ReactorPropertyChangedListener<Date> selectedDateChangedListener) {
        mSelectedDateListener = selectedDateChangedListener;
    }

    public ReactorVar<Date> selectedTime() {
        if (mSelectedTime == null) mSelectedTime = new ReactorVar<Date>() {
            @Override
            public void setValue(final Date value) {
                super.setValue(value);
                if (mSelectedTimeListener != null) {
                    mSelectedTimeListener.onPropertyChanged(value);
                }
            }
        };

        return mSelectedTime;
    }

    public void setSelectedTime(Date date) {
        selectedTime().setValue(date);
    }

    public void setSelectedTimeListener(final ReactorPropertyChangedListener<Date> selectedTimeChangedListener) {
        mSelectedTimeListener = selectedTimeChangedListener;
    }

    public ReactorVar<Integer> pickerBackgroundDrawableRes() {
        if (mPickerBackgroundDrawableRes == null)
            mPickerBackgroundDrawableRes = new ReactorVar<Integer>() {
                @Override
                public void setValue(final Integer value) {
                    super.setValue(value);
                    if (mPickerBackgroundDrawableResListener != null) {
                        mPickerBackgroundDrawableResListener.onPropertyChanged(value);
                    }
                }
            };

        return mPickerBackgroundDrawableRes;
    }

    public void setPickerBackgroundDrawableRes(int resId) {
        pickerBackgroundDrawableRes().setValue(resId);
    }

    public void setPickerBackgroundDrawableResListener(final ReactorPropertyChangedListener<Integer> pickerBackgroundDrawableResListener) {
        mPickerBackgroundDrawableResListener = pickerBackgroundDrawableResListener;
    }

    public ReactorVar<Float> pickerIconTextAlpha() {
        if (mPickerIconTextAlpha == null)
            mPickerIconTextAlpha = new ReactorVar<Float>() {
                @Override
                public void setValue(final Float value) {
                    super.setValue(value);
                    if (mPickerIconTextAlphaListener != null) {
                        mPickerIconTextAlphaListener.onPropertyChanged(value);
                    }
                }
            };

        return mPickerIconTextAlpha;
    }

    public void setPickerIconTextAlpha(float alpha) {
        pickerIconTextAlpha().setValue(alpha);
    }

    public void setPickerIconTextAlphaListener(final ReactorPropertyChangedListener<Float> pickerIconTextAlphaListener) {
        mPickerIconTextAlphaListener = pickerIconTextAlphaListener;
    }

    public ReactorVar<Boolean> timeEnabled() {
        if (mTimeEnabled == null)
            mTimeEnabled = new ReactorVar<Boolean>() {
                @Override
                public void setValue(final Boolean value) {
                    super.setValue(value);
                    if (mTimeEnabledListener != null) {
                        mTimeEnabledListener.onPropertyChanged(value);
                    }
                }
            };

        return mTimeEnabled;
    }

    public void setTimeEnabled(boolean enabled) {
        timeEnabled().setValue(enabled);
    }

    public void setTimeEnabledListener(final ReactorPropertyChangedListener<Boolean> timeEnabledListener) {
        mTimeEnabledListener = timeEnabledListener;
    }

    @Override
    public void unbindDependency() {
        super.unbindDependency();
        ReactorDateTimeSelectorViewState$$Unbinder.unbind(this);
        mSelectedDateListener = null;
        mSelectedTimeListener = null;
        mPickerBackgroundDrawableResListener = null;
        mPickerIconTextAlphaListener = null;
        mTimeEnabledListener = null;
    }

}

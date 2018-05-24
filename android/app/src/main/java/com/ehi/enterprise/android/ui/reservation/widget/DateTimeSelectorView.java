package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DateSelectorViewBinding;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorDateTimeSelectorViewState;
import com.isobar.android.viewmodel.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.Preconditions;

@ViewModel(DateTimeSelectorViewModel.class)
public class DateTimeSelectorView extends DataBindingViewModelView<DateTimeSelectorViewModel, DateSelectorViewBinding> {

    private static final String TAG = DateTimeSelectorView.class.getSimpleName();
    private Date mDate;
    private Date mTime;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({UNSELECTED, DATE_SELECTED, DATE_TIME_SELECTED})
    public @interface ViewState {
    }

    public static final int UNSELECTED = 0;
    public static final int DATE_SELECTED = 1;
    public static final int DATE_TIME_SELECTED = 2;

    @ViewState
    private int mViewState;

    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("EEE, MMM dd", Locale.getDefault());

    private String mHeaderString;
    private InteractionListener mInteractionListener;
    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().unselectedContainer) {
                mInteractionListener.onSelectDateClicked();
            } else if (view == getViewBinding().dateTimeSelectedDateText || view == getViewBinding().dateSelectedDateText) {
                mInteractionListener.onSelectDateClicked();
            } else if (view == getViewBinding().dateTimeSelectedTimeText || view == getViewBinding().dateSelectedTimeText) {
                mInteractionListener.onSelectTimeClicked();
            }
        }
    };

    public DateTimeSelectorView(Context context) {
        this(context, null, 0);
    }

    public DateTimeSelectorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateTimeSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        createViewBinding(R.layout.v_date_selector);
        defineTitleFromAttributes(context, attrs);
        initViews();
        mViewState = UNSELECTED;
    }

    private void defineTitleFromAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DateTimeSelectorTitle,
                0, 0);

        try {
            mHeaderString = array.getString(R.styleable.DateTimeSelectorTitle_dateTimeTitle);
        } finally {
            array.recycle();
        }
    }

    public void setTitle(String title) {
        getViewBinding().titleTextView.setText(title);
    }

    private void initViews() {
        getViewBinding().dateSelectedDateText.setOnClickListener(mOnClickListener);
        getViewBinding().dateSelectedTimeText.setOnClickListener(mOnClickListener);
        getViewBinding().dateTimeSelectedDateText.setOnClickListener(mOnClickListener);
        getViewBinding().dateTimeSelectedTimeText.setOnClickListener(mOnClickListener);

        setTitle(mHeaderString);
    }

    private void setDateSelected(boolean selected) {
        getViewBinding().unselectedContainer.setVisibility(selected ? View.GONE : View.VISIBLE);
    }

    private void updateViewState() {
        if (mDate == null && mTime == null) {
            mViewState = UNSELECTED;
        } else if (mDate != null && mTime == null) {
            mViewState = DATE_SELECTED;
        } else {
            mViewState = DATE_TIME_SELECTED;
        }

        switch (mViewState) {
            case UNSELECTED:
                getViewBinding().dateSelectedContainer.setVisibility(View.GONE);
                getViewBinding().dateTimeSelectedContainer.setVisibility(View.GONE);
                getViewBinding().unselectedContainer.setVisibility(View.VISIBLE);
                break;
            case DATE_SELECTED:
                getViewBinding().dateTimeSelectedContainer.setVisibility(View.GONE);
                getViewBinding().unselectedContainer.setVisibility(View.GONE);
                getViewBinding().dateSelectedContainer.setVisibility(View.VISIBLE);
                break;
            case DATE_TIME_SELECTED:
                getViewBinding().dateSelectedContainer.setVisibility(View.GONE);
                getViewBinding().unselectedContainer.setVisibility(View.GONE);
                getViewBinding().dateTimeSelectedContainer.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setPickerBackgroundDrawable(int id) {
        getViewBinding().unselectedContainer.setBackgroundResource(id);
    }

    public void setPickerIconTextAlpha(float alpha) {
        getViewBinding().selectContainerText.setAlpha(alpha);
    }

    public void setSelectedDate(Date date) {
        mDate = date;
        updateViewState();
        if (date == null) {
            return;
        }

        switch (mViewState) {
            case DATE_SELECTED:
                getViewBinding().dateSelectedDateText.setText(sDateFormat.format(mDate));
                break;
            case DATE_TIME_SELECTED:
                getViewBinding().dateTimeSelectedDateText.setText(sDateFormat.format(mDate));
                break;
        }
    }

    public void setSelectedTime(Date time) {
        mTime = time;
        updateViewState();
        if (time == null) {
            return;
        }

        SimpleDateFormat localizedTime = (SimpleDateFormat) DateFormat.getTimeFormat(getContext());
        switch (mViewState) {
            case DATE_SELECTED:
                break;
            case DATE_TIME_SELECTED:
                if (mDate != null) {
                    getViewBinding().dateTimeSelectedDateText.setText(sDateFormat.format(mDate));
                }
                getViewBinding().dateTimeSelectedTimeText.setText(localizedTime.format(mTime));
                break;
        }
    }

    public void setInteractionListener(InteractionListener interactionListener) {
        mInteractionListener = interactionListener;
    }

    public void setTimeCalloutText(int resId) {
        getViewBinding().dateTimeSelectedTimeText.setText(resId);
    }

    public void setTimeEnabled(boolean enabled) {
        getViewBinding().dateTimeSelectedTimeText.setEnabled(enabled);
        getViewBinding().dateSelectedTimeText.setEnabled(enabled);
    }

    public static ReactorComputationFunction selectedDate(final ReactorVar<Date> source, final DateTimeSelectorView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(target))
                    target.setSelectedDate(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction selectedTime(final ReactorVar<Date> source, final DateTimeSelectorView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(target))
                    target.setSelectedTime(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction pickerBackgroundDrawableRes(final ReactorVar<Integer> source, final DateTimeSelectorView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setPickerBackgroundDrawable(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction pickerIconTextAlpha(final ReactorVar<Float> source, final DateTimeSelectorView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setPickerIconTextAlpha(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction timeEnabled(final ReactorVar<Boolean> source, final DateTimeSelectorView target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (Preconditions.checkNotNull(source.getValue(), target))
                    target.setTimeEnabled(source.getValue());
            }
        };
    }

    public static ReactorComputationFunction[] bind(final ReactorDateTimeSelectorViewState source, final DateTimeSelectorView target) {
        ReactorComputationFunction[] functions = new ReactorComputationFunction[5];

        functions[0] = selectedDate(source.selectedDate(), target);
        functions[1] = selectedTime(source.selectedTime(), target);
        functions[2] = pickerBackgroundDrawableRes(source.pickerBackgroundDrawableRes(), target);
        functions[3] = pickerIconTextAlpha(source.pickerIconTextAlpha(), target);
        functions[4] = timeEnabled(source.timeEnabled(), target);

        return functions;
    }

    public interface InteractionListener {
        void onSelectDateClicked();

        void onSelectTimeClicked();
    }
}

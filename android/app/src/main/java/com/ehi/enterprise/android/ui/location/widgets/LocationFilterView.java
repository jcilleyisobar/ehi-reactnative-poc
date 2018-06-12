package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationFilterViewBinding;
import com.ehi.enterprise.android.ui.location.LocationsOnMapActivity;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterComponentView;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(LocationFilterViewModel.class)
public class LocationFilterView extends DataBindingViewModelView<LocationFilterViewModel, LocationFilterViewBinding> {
    private static final int DATE_FORMAT_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_ABBREV_MONTH;

    private OnClickListener resetPickupDateListener;
    private OnClickListener resetPickupTimeListener;
    private OnClickListener resetReturnDateListener;
    private OnClickListener resetReturnTimeListener;
    private ArrayList<Integer> filterTypes;

    public LocationFilterView(Context context) {
        this(context, null);
    }

    public LocationFilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createViewBinding(R.layout.v_location_filter);
        initViews();
    }

    private void initViews() {
        getViewBinding().pickupDateFilter.setResetOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackAction(EHIAnalytics.Action.ACTION_DATE_UNSELECT, EHIAnalytics.LocationFilterType.PICKUP_DATE);
                resetPickupDateListener.onClick(v);
                getViewModel().resetPickupDate();
            }
        });
        getViewBinding().returnDateFilter.setResetOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackAction(EHIAnalytics.Action.ACTION_DATE_UNSELECT, EHIAnalytics.LocationFilterType.DROPOFF_DATE);
                resetReturnDateListener.onClick(v);
                getViewModel().resetReturnDate();
            }
        });

        getViewBinding().pickupTimeFilter.setResetOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackAction(EHIAnalytics.Action.ACTION_TIME_UNSELECT, EHIAnalytics.LocationFilterType.PICKUP_TIME);
                resetPickupTimeListener.onClick(v);
                getViewModel().resetPickupTime();
            }
        });
        getViewBinding().returnTimeFilter.setResetOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                trackAction(EHIAnalytics.Action.ACTION_TIME_UNSELECT, EHIAnalytics.LocationFilterType.DROPOFF_TIME);
                resetReturnTimeListener.onClick(v);
                getViewModel().resetReturnTime();
            }
        });
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorView.visibility(getViewModel().pickupTimeViewState.visibility(), getViewBinding().pickupTimeFilter));
        bind(FilterComponentView.resetDefaultState(getViewModel().pickupTimeViewState.resetDefaultState(), getViewBinding().pickupTimeFilter));
        bind(FilterComponentView.title(getViewModel().pickupTimeViewState.title(), getViewBinding().pickupTimeFilter));

        bind(ReactorView.visibility(getViewModel().returnTimeViewState.visibility(), getViewBinding().returnTimeFilter));
        bind(FilterComponentView.resetDefaultState(getViewModel().returnTimeViewState.resetDefaultState(), getViewBinding().returnTimeFilter));
        bind(FilterComponentView.title(getViewModel().returnTimeViewState.title(), getViewBinding().returnTimeFilter));

        bind(FilterComponentView.resetDefaultState(getViewModel().pickupDateViewState.resetDefaultState(), getViewBinding().pickupDateFilter));
        bind(FilterComponentView.title(getViewModel().pickupDateViewState.title(), getViewBinding().pickupDateFilter));

        bind(FilterComponentView.resetDefaultState(getViewModel().returnDateViewState.resetDefaultState(), getViewBinding().returnDateFilter));
        bind(FilterComponentView.title(getViewModel().returnDateViewState.title(), getViewBinding().returnDateFilter));
    }

    public void setPickupDateListener(OnClickListener onClickListener) {
        getViewBinding().pickupDateFilter.setOnFilterClickListener(onClickListener);
    }

    public void setReturnDateListener(OnClickListener onClickListener) {
        getViewBinding().returnDateFilter.setOnFilterClickListener(onClickListener);
    }

    public void setPickupTimeListener(OnClickListener onClickListener) {
        getViewBinding().pickupTimeFilter.setOnFilterClickListener(onClickListener);
    }

    public void setReturnTimeListener(OnClickListener onClickListener) {
        getViewBinding().returnTimeFilter.setOnFilterClickListener(onClickListener);
    }

    public void setPickupDateText(@Nullable Date pickupDateText) {
        if (pickupDateText != null) {
            getViewModel().setPickupDate(getFormattedDate(pickupDateText));
        } else {
            getViewModel().setDefaultPickupDate();
        }
    }

    public void setReturnDateText(@Nullable Date returnDateText) {
        if (returnDateText != null) {
            getViewModel().setReturnDate(getFormattedDate(returnDateText));
        } else {
            getViewModel().setDefaultReturnDate();
        }
    }

    public void setPickupTimeText(@Nullable Date pickupDateText) {
        if (pickupDateText != null) {
            getViewModel().setPickupTimeText(getFormattedTime(pickupDateText));
        } else {
            getViewModel().setDefaultPickupTimeText();
        }
    }

    public void setReturnTimeText(@Nullable Date returnDateText) {
        if (returnDateText != null) {
            getViewModel().setReturnTimeText(getFormattedTime(returnDateText));
        } else {
            getViewModel().setDefaultReturnTimeText();
        }
    }

    private String getFormattedTime(Date time) {
        SimpleDateFormat localizedTime = (SimpleDateFormat) DateFormat.getTimeFormat(getContext());
        return localizedTime.format(time);
    }

    private String getFormattedDate(@Nullable Date pickupDateText) {
        return DateUtils.formatDateTime(getContext(),
                pickupDateText.getTime(),
                DATE_FORMAT_FLAGS);
    }

    public void setResetPickupDateListener(OnClickListener resetPickupDateListener) {
        this.resetPickupDateListener = resetPickupDateListener;
    }

    public void setResetReturnDateListener(OnClickListener resetReturnDateListener) {
        this.resetReturnDateListener = resetReturnDateListener;
    }

    public void setResetPickupTimeListener(OnClickListener resetPickupTimeListener) {
        this.resetPickupTimeListener = resetPickupTimeListener;
    }

    public void setResetReturnTimeListener(OnClickListener resetReturnTimeListener) {
        this.resetReturnTimeListener = resetReturnTimeListener;
    }

    private void trackAction(EHIAnalytics.Action actionType, EHIAnalytics.LocationFilterType locationFilterType) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, LocationsOnMapActivity.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SEARCH_FILTER.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, actionType.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(filterTypes, getResources(), locationFilterType))
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_FILTER_DATE_TIME_UNSELECTED.value)
                .tagScreen()
                .tagEvent()
                .tagMacroEvent();
    }

    public void setFilterTypes(ArrayList<Integer> filterTypes) {
        this.filterTypes = filterTypes;
    }
}

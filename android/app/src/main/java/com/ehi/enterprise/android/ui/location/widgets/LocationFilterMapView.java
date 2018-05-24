package com.ehi.enterprise.android.ui.location.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.LocationFilterReadOnlyViewBinding;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterMapComponentView;
import com.ehi.enterprise.android.ui.location.widgets.components.FilterTextMapComponentView;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.isobar.android.viewmodel.ViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(LocationFilterMapViewModel.class)
public class LocationFilterMapView extends DataBindingViewModelView<LocationFilterMapViewModel, LocationFilterReadOnlyViewBinding> implements View.OnTouchListener{

    private static final int DATE_FORMAT_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_ABBREV_MONTH;

    public LocationFilterMapView(Context context) {
        this(context, null);
    }

    public LocationFilterMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LocationFilterMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            createViewBinding(R.layout.v_location_filter_read_only);
        } else {
            addView(inflate(context, R.layout.v_location_filter_read_only, null));
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorView.visibility(getViewModel().filterViewState.visibility(), getViewBinding().filterView));
        bind(FilterMapComponentView.pickupDateTitle(getViewModel().filterViewState.pickupDateTitle(), getViewBinding().filterView));
        bind(FilterMapComponentView.pickupTimeTitle(getViewModel().filterViewState.pickupTimeTitle(), getViewBinding().filterView));
        bind(FilterMapComponentView.dropoffDateTitle(getViewModel().filterViewState.dropoffDateTitle(), getViewBinding().filterView));
        bind(FilterMapComponentView.dropoffTimeTitle(getViewModel().filterViewState.dropoffTimeTitle(), getViewBinding().filterView));
        bind(FilterMapComponentView.resetPickupDefaultState(getViewModel().filterViewState.pickupDefaultState(), getViewBinding().filterView));
        bind(FilterMapComponentView.resetDropoffDefaultState(getViewModel().filterViewState.dropoffDefaultState(), getViewBinding().filterView));
        bind(ReactorView.visibility(getViewModel().filterTextViewState.visibility(), getViewBinding().filtersTextView));
        bind(FilterTextMapComponentView.title(getViewModel().filterTextViewState.title(), getViewBinding().filtersTextView));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewModel().updateViewsVisibility();
            }
        });
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

    public void setPickupTimeText(Date pickupTimeText) {
        if (pickupTimeText != null) {
            getViewModel().setPickupTimeText(getFormattedTime(pickupTimeText));
        } else {
            getViewModel().setDefaultPickupTimeText();
        }
    }

    public void setReturnTimeText(Date returnTimeText) {
        if (returnTimeText != null) {
            getViewModel().setReturnTimeText(getFormattedTime(returnTimeText));
        } else {
            getViewModel().setDefaultReturnTimeText();
        }
    }

    private String getFormattedDate(@Nullable Date pickupDateText) {
        return DateUtils.formatDateTime(getContext(),
                pickupDateText.getTime(),
                DATE_FORMAT_FLAGS);
    }

    private String getFormattedTime(Date time) {
        SimpleDateFormat localizedTime = (SimpleDateFormat) DateFormat.getTimeFormat(getContext());
        return localizedTime.format(time);
    }

    public void setFiltersText(String filtersText) {
        getViewModel().setFiltersText(filtersText);
    }

    public void setOnClearFilterClickListener(OnClickListener onClickListener) {
        getViewBinding().closeImage.setOnClickListener(onClickListener);
    }

    public void setFilterViewClickListener(FilterMapComponentView.FilterMapViewListener filterListener) {
        getViewBinding().filterView.setFilterViewClickListener(filterListener);
    }

    public String getFilterTypes() {
        final StringBuilder bld = new StringBuilder();
        addFilterType(bld, getViewModel().filterViewState.pickupDateTitle(), R.string.locations_map_any_day_label, EHIAnalytics.LocationFilterType.PICKUP_DATE);
        addFilterType(bld, getViewModel().filterViewState.dropoffDateTitle(), R.string.locations_map_any_day_label, EHIAnalytics.LocationFilterType.DROPOFF_DATE);
        addFilterType(bld, getViewModel().filterViewState.pickupTimeTitle(), R.string.locations_map_any_time_label, EHIAnalytics.LocationFilterType.PICKUP_TIME);
        addFilterType(bld, getViewModel().filterViewState.dropoffTimeTitle(), R.string.locations_map_any_time_label, EHIAnalytics.LocationFilterType.DROPOFF_TIME);
        if (bld.length() == 0) {
            return EHIAnalytics.LocationFilterType.NONE.value;
        }
        return bld.toString();
    }

    private void addFilterType(StringBuilder bld, ReactorVar<String> title, int resource, EHIAnalytics.LocationFilterType filterType) {
        if (!title.getRawValue().equalsIgnoreCase(getResources().getString(resource))) {
            if (bld.toString().trim().length() != 0) {
                bld.append("|");
            }
            bld.append(filterType.value);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}

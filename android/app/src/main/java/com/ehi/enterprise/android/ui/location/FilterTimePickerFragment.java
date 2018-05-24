package com.ehi.enterprise.android.ui.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterTimePickerViewBinding;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnTimeSelectListener;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(FilterTimePickerViewModel.class)
public class FilterTimePickerFragment extends DataBindingViewModelFragment<FilterTimePickerViewModel, FilterTimePickerViewBinding> {

    private static final String SCREEN_NAME = "FilterTimePickerFragment";

    @Extra(value = Boolean.class, required = true)
    public static final String EXTRA_IS_PICKUP = "EXTRA_IS_PICKUP";
    @Extra(value = ArrayList.class, type = Integer.class)
    public static final String EXTRA_FILTERS = "ehi.EXTRA_FILTERS";

    private ArrayList<Integer> filterTypes;
    private EHIAnalytics.LocationFilterType locationFilterType = EHIAnalytics.LocationFilterType.NONE;

    private OnTimeSelectListener onTimeSelectListener = new OnTimeSelectListener() {
        @Override
        public void onTimeSelected(Date time) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_SEARCH_FILTER.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_TIME_SELECT.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(filterTypes, getResources(), locationFilterType))
                    .macroEvent(EHIAnalytics.MacroEvent.MACRO_FILTER_DATE_TIME_SELECTED.value)
                    .tagScreen()
                    .tagEvent()
                    .tagMacroEvent();
            getViewModel().selectTime(time);
        }

        @Override
        public void onAfterHoursTitleClicked() {

        }

        @Override
        public void onLastPickupTimeClicked() {

        }

        @Override
        public void onLastReturnTimeClicked() {

        }

        @Override
        public void onSearchOpenLocationsInMapClicked(int selectionMode, Date selectedTime) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FilterTimePickerFragmentHelper.Extractor extractor = new FilterTimePickerFragmentHelper.Extractor(this);
        if (extractor.extraIsPickup() != null) {
            getViewModel().setIsPickup(extractor.extraIsPickup());
            locationFilterType = extractor.extraIsPickup() ? EHIAnalytics.LocationFilterType.PICKUP_TIME : EHIAnalytics.LocationFilterType.DROPOFF_TIME;
        }
        filterTypes = extractor.extraFilters();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_filter_time_picker, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().timePickerView.setWorkingDayInfo(EHISolrWorkingDayInfo.createOpenAllDay());
        getViewBinding().timePickerView.setOnTimeSelectListener(onTimeSelectListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.title(getViewModel().titleText, getActivity()));
        bind(ReactorTextView.text(getViewModel().headerText, getViewBinding().headerTextView));
        bind(ReactorTextView.text(getViewModel().subHeaderText, getViewBinding().subHeaderTextView));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final Date date = getViewModel().getTime();
                if (date != null) {
                    finishActivity(date);
                }
            }
        });
    }

    private void finishActivity(Date date) {
        Intent resultData = new Intent();
        Bundle extras = new Bundle();
        extras.putSerializable(SearchLocationsFilterFragment.EXTRA_TIME_SELECT, date);
        resultData.putExtras(extras);
        getActivity().setResult(Activity.RESULT_OK, resultData);
        getActivity().finish();
    }
}

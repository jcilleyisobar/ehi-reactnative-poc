package com.ehi.enterprise.android.ui.location;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.FilterDatePickerViewBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.ModalFragment;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(FilterDatePickerFragmentViewModel.class)
public class FilterDatePickerFragment extends DataBindingViewModelFragment<FilterDatePickerFragmentViewModel, FilterDatePickerViewBinding> implements ModalFragment {

    private static final String SCREEN_NAME = "FilterDatePickerFragment";

    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_DATE = "ehi.EXTRA_RETURN_DATE";
    @Extra(value = Boolean.class, required = false)
    public static final String EXTRA_IS_SELECT_PICKUP = "ehi.EXTRA_SELECT_PICKUP_DATE";
    @Extra(value = ArrayList.class, type = Integer.class)
    public static final String EXTRA_FILTERS = "ehi.EXTRA_FILTERS";

    private ArrayList<Integer> filterTypes;
    private EHIAnalytics.LocationFilterType locationFilterType;

    private CalendarPickerView.OnDateSelectedListener onDateSelectedListener = new CalendarPickerView.OnDateSelectedListener() {
        @Override
        public void onDateSelected(Date date) {
            getViewModel().selectDate(date);
        }

        @Override
        public void onDateUnselected(Date date) {
        }
    };

    private View.OnClickListener continueButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_SEARCH_FILTER.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DATE_SELECT.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(filterTypes, getResources(), locationFilterType))
                    .macroEvent(EHIAnalytics.MacroEvent.MACRO_FILTER_DATE_TIME_SELECTED.value)
                    .tagScreen()
                    .tagEvent()
                    .tagMacroEvent();
            getViewModel().continueButtonClicked();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FilterDatePickerFragmentHelper.Extractor extractor = new FilterDatePickerFragmentHelper.Extractor(this);
        if (extractor.extraPickupDate() != null) {
            getViewModel().setPickupDate(extractor.extraPickupDate());
        }
        if (extractor.extraReturnDate() != null) {
            getViewModel().setReturnDate(extractor.extraReturnDate());
        }
        final boolean isSelectPickupMode = extractor.extraIsSelectPickup() != null && extractor.extraIsSelectPickup();
        if (isSelectPickupMode) {
            getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_PICKUP);
            locationFilterType = EHIAnalytics.LocationFilterType.PICKUP_DATE;
        } else {
            getViewModel().setDateSelectState(FilterDatePickerFragmentViewModel.DATE_SELECT_STATE_RETURN);
            locationFilterType = EHIAnalytics.LocationFilterType.DROPOFF_DATE;
        }
        filterTypes = extractor.extraFilters();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_filter_date_picker, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().continueButton.setOnClickListener(continueButtonClickListener);
        initCalendar();
    }

    private void initCalendar() {
        getViewBinding().calendarView.setOnDateSelectedListener(onDateSelectedListener);

        final Date startDate = getViewModel().getCalendarStartDate();
        getViewBinding().calendarView.init(startDate,
                getViewModel().getCalendarEndDate(),
                getResources().getString(R.string.reservation_calendar_closed_dates))
                .withHighlightedDate(new Date())
                .inMode(CalendarPickerView.SelectionMode.SINGLE);

        getViewBinding().calendarView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_light));
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().closeDatePicker.getValue()) {
                    finishWithResult();
                }
            }
        });

        // Continue button bindings
        bind(ReactorView.background(getViewModel().continueButtonTextViewState.background(), getViewBinding().continueButton));
        bind(ReactorView.backgroundColor(getViewModel().continueButtonTextViewState.backgroundColor(), getViewBinding().continueButton));
        bind(ReactorTextView.textColor(getViewModel().continueButtonTextViewState.textColor(), getViewBinding().continueButton));
        bind(ReactorView.visibility(getViewModel().continueButtonTextViewState.visibility(), getViewBinding().continueButton));

        bind(ReactorTextView.text(getViewModel().subHeaderTextView, getViewBinding().subHeaderTextView));

        bind(ReactorView.visibility(getViewModel().toastView.visibility(), getViewBinding().invalidDatesView));
        bind(ReactorTextView.text(getViewModel().toastView.textCharSequence(), getViewBinding().invalidDatesView));
        bind(ReactorActivity.title(getViewModel().title, getActivity()));

    }

    private void finishWithResult() {
        Intent resultData = new Intent();
        Bundle extras = new Bundle();
        if (getViewModel().shouldPersistPickupDate()) {
            extras.putSerializable(EXTRA_PICKUP_DATE, getViewModel().getPickupDate());
        }
        if (getViewModel().shouldPersistReturnDate()) {
            extras.putSerializable(EXTRA_RETURN_DATE, getViewModel().getReturnDate());
        }
        resultData.putExtras(extras);
        getActivity().setResult(Activity.RESULT_OK, resultData);
        getActivity().finish();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}

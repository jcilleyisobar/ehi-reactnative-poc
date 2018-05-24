package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DatePickerFragmentBinding;
import com.ehi.enterprise.android.models.location.EHILatLng;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.ui.activity.ModalDialogActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.LocationsOnMapActivityHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ModalFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnTimeSelectListener;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.example.reactorbinding_support_v4.view.ReactorViewPager;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(DatePickerViewModel.class)
public class DatePickerFragment extends DataBindingViewModelFragment<DatePickerViewModel, DatePickerFragmentBinding> implements ModalFragment {

    public static final String TAG = "DatePickerFragment";
    public static final String SCREEN_NAME = "DatePickerFragment";
    private TimePagerAdapter mTimePagerAdapter;
    private SimpleDateFormat mLocalizedTimeFormat;

    @Extra(EHISolrLocation.class)
    public static final String EXTRA_PICKUP_LOCATION = "EXTRA_PICKUP_LOCATION";
    @Extra(EHISolrLocation.class)
    public static final String EXTRA_RETURN_LOCATION = "EXTRA_RETURN_LOCATION";
    @Extra(value = int.class, required = false)
    public static final String EXTRA_DATE_SELECT_STATE = "EXTRA_DATE_SELECT_STATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_DATE = "ehi.EXTRA_PICKUP_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_DATE = "ehi.EXTRA_RETURN_DATE";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_PICKUP_TIME = "ehi.EXTRA_PICKUP_TIME";
    @Extra(value = Date.class, required = false)
    public static final String EXTRA_RETURN_TIME = "ehi.EXTRA_RETURN_TIME";
    @Extra(value = boolean.class, required = false)
    public static final String EXTRA_EDIT = "EXTRA_EDIT";


    private final int DATE_FORMAT_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY
            | DateUtils.FORMAT_ABBREV_WEEKDAY
            | DateUtils.FORMAT_ABBREV_MONTH;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().continueButton) {
                getViewModel().calendarContinueClicked();
            } else if (v == getViewBinding().pickupDateSelectionDisplay) {
                getViewModel().pickupDateTextClicked();
            } else if (v == getViewBinding().returnDateSelectionDisplay) {
                getViewModel().returnDateTextClicked();
            } else if (v == getViewBinding().pickupTimeSelectionDisplay) {
                getViewModel().pickupTimeTextClicked();
            } else if (v == getViewBinding().returnTimeSelectionDisplay) {
                getViewModel().returnTimeTextClicked();
            }
        }
    };


    private OnTimeSelectListener mOnTimeSelectListener = new OnTimeSelectListener() {
        @Override
        public void onTimeSelected(Date time) {
            getViewModel().selectTime(time);
        }

        @Override
        public void onAfterHoursTitleClicked() {
            Fragment fragment = new ModalTextDialogFragmentHelper.Builder()
                    .title(getString(R.string.time_selection_after_hours_info_title))
                    .text(getString(R.string.time_selection_after_hours_details))
                    .buttonText(getString(R.string.modal_default_dismiss_title))
                    .build();

            Intent intent = new ModalDialogActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivity(intent);
        }

        @Override
        public void onLastPickupTimeClicked() {
            Fragment fragment = new ModalTextDialogFragmentHelper.Builder()
                    .title(getString(R.string.time_selection_pickup_close_time_info_title))
                    .text(getString(R.string.time_selection_pickup_close_time_details))
                    .buttonText(getString(R.string.modal_default_dismiss_title))
                    .build();

            Intent intent = new ModalDialogActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivity(intent);
        }

        @Override
        public void onLastReturnTimeClicked() {
            Fragment fragment = new ModalTextDialogFragmentHelper.Builder()
                    .title(getString(R.string.time_selection_return_close_time_info_title))
                    .text(getString(R.string.time_selection_return_close_time_details))
                    .buttonText(getString(R.string.modal_default_dismiss_title))
                    .build();

            Intent intent = new ModalDialogActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivity(intent);
        }

        @Override
        public void onSearchOpenLocationsInMapClicked(int selectionMode, Date selectedTime) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_DATE_TIME.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_TIME_SELECT.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_DIFFERENT_LOCATION.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.locationsFilter(null, getResources()))
                    .tagScreen()
                    .tagEvent();

            final LocationsOnMapActivityHelper.Builder builder = new LocationsOnMapActivityHelper.Builder();
            builder.extraFlow(getViewModel().getFlow(selectionMode))
                    .extraDropoffDate(getViewModel().getDropoffDate())
                    .extraPickupDate(getViewModel().getPickupDate());

            if (getViewModel().shouldSendConflictingPickupTimeSelectedInTimePicker(selectionMode)) {
               builder.extraPickupTime(selectedTime);
               builder.extraDropoffTime(getViewModel().getDropoffTime());
            } else {
                builder.extraDropoffTime(selectedTime);
                builder.extraPickupTime(getViewModel().getPickupTime());
            }

            final EHISolrLocation location;
            if (getViewModel().shouldSendPickupLocation(selectionMode)) {
                location = getViewModel().getPickupLocation();
            } else {
                location = getViewModel().getDropoffLocation();
            }
            builder.extraLatLng(new EHILatLng(location.getLatLng()))
                    .extraName(location.getDefaultLocationName());

            startActivity(builder.build(getActivity()));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mLocalizedTimeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_date_picker, container);
        initViews();
        initCalendar();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().frDatePickerTimeViewsPager.setOffscreenPageLimit(2);
        getViewBinding().frDatePickerTimeViewsPager.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mTimePagerAdapter = new TimePagerAdapter(mOnTimeSelectListener);
        getViewBinding().frDatePickerTimeViewsPager.setAdapter(mTimePagerAdapter);
        mTimePagerAdapter.notifyDataSetChanged();
        getViewBinding().pickupDateSelectionDisplay.setOnClickListener(mOnClickListener);
        getViewBinding().pickupTimeSelectionDisplay.setOnClickListener(mOnClickListener);
        getViewBinding().returnDateSelectionDisplay.setOnClickListener(mOnClickListener);
        getViewBinding().returnTimeSelectionDisplay.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
    }

    @SuppressWarnings("ResourceType")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            final DatePickerFragmentHelper.Extractor extractor = new DatePickerFragmentHelper.Extractor(this);
            getViewModel().setPickupLocation(extractor.extraPickupLocation());
            getViewModel().setReturnLocation(extractor.extraReturnLocation());
            getViewModel().requestAvailableDatesInfo();

            if (extractor.extraDateSelectState() != null
                    && extractor.extraPickupDate() != null) {
                //noinspection ResourceType,ConstantConditions
                getViewModel().setDateSelectState(extractor.extraDateSelectState(), false);
                if (extractor.extraPickupDate() != null) {
                    getViewModel().setPickupDate(extractor.extraPickupDate());
                }

                if (extractor.extraReturnDate() != null) {
                    getViewModel().setReturnDate(extractor.extraReturnDate());
                }

                if (extractor.extraPickupTime() != null) {
                    getViewModel().setPickUpTime(extractor.extraPickupTime());
                }
                if (extractor.extraReturnTime() != null) {
                    getViewModel().setReturnTime(extractor.extraReturnTime());
                }

                if (extractor.extraEdit() != null) {
                    //noinspection ConstantConditions
                    getViewModel().setEditMode(extractor.extraEdit());
                }

                getViewModel().update();
            }
        } else {
            DLog.e(TAG, new NoArgumentsFoundException());
            getActivity().finish();
            return;
        }

        getViewModel().requestWorkingDaysInfo();
    }

    private void initCalendar() {
        initCalendarView();

        if (getViewModel().getDateSelectState() == DatePickerViewModel.DATE_SELECT_STATE_RETURN_DATE) {
            getViewBinding().calendarView.selectDate(getViewModel().getPickupDate(), true);
        }

        if (getViewModel().getDateSelectState() == DatePickerViewModel.DATE_SELECT_STATE_PICKUP_TIME
                || getViewModel().getDateSelectState() == DatePickerViewModel.DATE_SELECT_STATE_RETURN_TIME) {
            if (getViewModel().getPickupDate() != null) {
                getViewBinding().calendarView.selectDate(getViewModel().getPickupDate(), true);
            }
            if (getViewModel().getDropoffDate() != null) {
                getViewBinding().calendarView.selectDate(getViewModel().getDropoffDate(), false);
            }
        }

        getViewBinding().calendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {

            @Override
            public void onDateSelected(Date date) {
                getViewModel().selectDate(date);
            }

            @Override
            public void onDateUnselected(Date date) {
                getViewModel().reset();
            }
        });
    }

    private void initCalendarView() {
        final Date startDate = getViewModel().getCalendarStartDate();

        final String[] shortWeekdays = getViewModel().getShortWeekdays();

        getViewBinding().calendarView.init(startDate,
                getViewModel().getCalendarEndDate(),
                getResources().getString(R.string.reservation_calendar_closed_dates))
                .withHighlightedDate(new Date())
                .setShortWeekdays(shortWeekdays)
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        getViewBinding().calendarView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.source_sans_light));

        final List<CalendarCellDecorator> decoratorList = new ArrayList<>();
        decoratorList.add(new CalendarCellDecoratorImpl(getViewModel()));

        getViewBinding().calendarView.setDecorators(decoratorList);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(ToastUtils.toast(getViewModel().errorToast, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorActivity.title(getViewModel().title, getActivity()));

        // Calendar and Time Picker Bindings
        bind(ReactorView.visible(getViewModel().calendarVisible, getViewBinding().calendarView));
        bind(ReactorViewPager.currentItem(getViewModel().timeSelectorCurrentPage, getViewBinding().frDatePickerTimeViewsPager, true));

        // Pickup bindings
        bind(ReactorTextView.text(getViewModel().pickupDateText, getViewBinding().pickupDateSelectionDisplay));
        bind(ReactorView.selected(getViewModel().pickupDateTextSelected, getViewBinding().pickupDateSelectionDisplay));
        bind(ReactorTextView.text(getViewModel().pickupTimeText, getViewBinding().pickupTimeSelectionDisplay));
        bind(ReactorView.selected(getViewModel().pickupTimeTextSelected, getViewBinding().pickupTimeSelectionDisplay));
        bind(ReactorView.visible(getViewModel().pickupTimeTextVisible, getViewBinding().pickupTimeSelectionDisplay));
        bind(ReactorView.visible(getViewModel().pickupTriangleVisible, getViewBinding().frDatePickerPickupTriangle));

        // Return bindings
        bind(ReactorTextView.text(getViewModel().returnDateText, getViewBinding().returnDateSelectionDisplay));
        bind(ReactorView.selected(getViewModel().returnDateTextSelected, getViewBinding().returnDateSelectionDisplay));
        bind(ReactorView.visible(getViewModel().returnDateTextVisible, getViewBinding().returnDateSelectionDisplay, View.INVISIBLE));
        bind(ReactorTextView.text(getViewModel().returnTimeText, getViewBinding().returnTimeSelectionDisplay));
        bind(ReactorView.selected(getViewModel().returnTimeTextSelected, getViewBinding().returnTimeSelectionDisplay));
        bind(ReactorView.visible(getViewModel().returnTimeTextVisible, getViewBinding().returnTimeSelectionDisplay));
        bind(ReactorView.visible(getViewModel().returnTriangleVisible, getViewBinding().frDatePickerReturnTriangle));

        // Continue button bindings
        bind(ReactorView.background(getViewModel().continueButtonBackgroundDrawable, getViewBinding().continueButton));
        bind(ReactorView.backgroundColor(getViewModel().continueButtonBackgroundColor, getViewBinding().continueButton));
        bind(ReactorTextView.textColor(getViewModel().continueButtonTextColor, getViewBinding().continueButton));
        bind(ReactorView.visible(getViewModel().continueButtonVisible, getViewBinding().continueButton));

        // Divider bindings
        bind(ReactorView.visible(getViewModel().dividerVisibility, getViewBinding().pickupTimeDivider));
        bind(ReactorView.visible(getViewModel().dividerVisibility, getViewBinding().returnTimeDivider));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().closeDatePicker.getValue()) {
                    // TODO make sure that during the edit flow we close the modal after the edit is made
                    finishWithResult();
                }
            }
        });

        addReaction("CLOSED_DATES_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getPickupClosedDates() != null
                        && getViewModel().getReturnClosedDates() != null) {
                    runNonReactive(new ReactorComputationFunction() {
                        @Override
                        public void react(ReactorComputation reactorComputation) {
                            initCalendar();
                        }
                    });
                }
            }
        });

        // The following 4 reactions can't occur in the viewmodel because the formatter requires Context :(
        addReaction("PICKUP_DATE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getPickupDate() != null) {
                    getViewModel().setFormattedPickupDate(DateUtils.formatDateTime(getActivity(),
                            getViewModel().getPickupDate().getTime(),
                            DATE_FORMAT_FLAGS));
                }
            }
        });

        addReaction("PICKUP_TIME_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getPickupTime() != null) {
                    getViewModel().setFormattedPickupTime(mLocalizedTimeFormat.format(getViewModel().getPickupTime()));
                }
            }
        });

        addReaction("RETURN_DATE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getDropoffDate() != null) {
                    getViewModel().setFormattedReturnDate(DateUtils.formatDateTime(getActivity(),
                            getViewModel().getDropoffDate().getTime(),
                            DATE_FORMAT_FLAGS));
                }
            }
        });

        addReaction("RETURN_TIME_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getDropoffTime() != null) {
                    getViewModel().setFormattedReturnTime(mLocalizedTimeFormat.format(getViewModel().getDropoffTime()));
                }
            }
        });

        addReaction("SELECTION_STATE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                @DatePickerViewModel.DateSelectState int mDateSelectionState = getViewModel().getDateSelectState();
                switch (mDateSelectionState) {
                    case DatePickerViewModel.DATE_SELECT_STATE_PICKUP_DATE:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_DATE_TIME.value, DatePickerFragment.SCREEN_NAME)
                                .state(EHIAnalytics.State.STATE_CALENDAR.value)
                                .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickupLocation(),
                                        getViewModel().getDropoffLocation(),
                                        getViewModel().getPickupDateAndTime(),
                                        getViewModel().getReturnDateAndTime()))
                                .tagScreen()
                                .tagEvent();
                        getViewBinding().calendarView.clearHighlightedDates();
                        break;
                    case DatePickerViewModel.DATE_SELECT_STATE_PICKUP_TIME:
                        EHIAnalyticsEvent.create()
                                .screen(EHIAnalytics.Screen.SCREEN_DATE_TIME.value, DatePickerFragment.SCREEN_NAME)
                                .state(EHIAnalytics.State.STATE_TIME_SELECT.value)
                                .addDictionary(EHIAnalyticsDictionaryUtils.dateTime(getViewModel().getPickupLocation(),
                                        getViewModel().getDropoffLocation(),
                                        getViewModel().getPickupDateAndTime(),
                                        getViewModel().getReturnDateAndTime()))
                                .tagScreen()
                                .tagEvent();

                        if (getViewModel().getFirstDateWorkingInfo() != null) {
                            mTimePagerAdapter.setPickupWorkingDayInfo(getViewModel().getFirstDateWorkingInfo());
                        }
                        break;

                    case DatePickerViewModel.DATE_SELECT_STATE_RETURN_TIME:
                        if (getViewModel().getSecondDateWorkingInfo() != null) {
                            mTimePagerAdapter.setReturnWorkingDayInfo(getViewModel().getSecondDateWorkingInfo());
                        }
                        break;

                    case DatePickerViewModel.DATE_SELECT_STATE_TIME_COMPLETE:
                        finishWithResult();
                        break;
                }
            }
        });
    }

    private void finishWithResult() {
        Intent resultData = new Intent();
        Bundle extras = new Bundle();
        extras.putSerializable(EXTRA_PICKUP_DATE, getViewModel().getPickupDate());
        extras.putSerializable(EXTRA_RETURN_DATE, getViewModel().getDropoffDate());
        extras.putSerializable(EXTRA_PICKUP_TIME, getViewModel().getPickupTime());
        extras.putSerializable(EXTRA_RETURN_TIME, getViewModel().getDropoffTime());
        resultData.putExtras(extras);
        getActivity().setResult(Activity.RESULT_OK, resultData);
        getActivity().finish();
    }

    private void finishWithCancel() {
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    @Override
    public boolean onBackPressed() {
        switch (getViewModel().getDateSelectState()) {
            case DatePickerViewModel.DATE_SELECT_STATE_PICKUP_DATE:
            case DatePickerViewModel.DATE_SELECT_STATE_RETURN_DATE:
                finishWithCancel();
                return true;
            case DatePickerViewModel.DATE_SELECT_STATE_DATE_COMPLETE:
                List<Date> selectedDates = getViewBinding().calendarView.getSelectedDates();
                if (selectedDates.size() > 1) {
                    // setting the date here will trigger a reset
                    getViewBinding().calendarView.selectDate(selectedDates.get(0));
                    // but will not trigger the set date
                    getViewModel().selectDate(selectedDates.get(0));
                }
                return true;
            case DatePickerViewModel.DATE_SELECT_STATE_PICKUP_TIME:
                getViewModel().returnDateTextClicked();
                return true;
            case DatePickerViewModel.DATE_SELECT_STATE_RETURN_TIME:
                getViewModel().pickupTimeTextClicked();
                return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cancel, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_cancel) {
            showCancelDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCancelDialog(){
        DialogUtils.showDiscardReservationDialog(getActivity(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ItineraryActivity.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ABANDON.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_YES.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                // clean any promotions applied
                getViewModel().getManagers().getReservationManager().setWeekendSpecial(false);

                getActivity().overridePendingTransition(R.anim.modal_stay, R.anim.modal_slide_out);
                IntentUtils.goToHomeScreen(getContext());
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

}
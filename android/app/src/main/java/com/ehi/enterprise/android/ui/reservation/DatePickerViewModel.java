package com.ehi.enterprise.android.ui.reservation;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrWorkingDayInfo;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.location.solr.GetSolrHoursByLocationIdRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.solr.GetSolrHoursResponse;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.reservation.widget.time_selection.TimeSelectionView;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.reactor_extensions.ReactorCalendar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class DatePickerViewModel extends ManagersAccessViewModel {

    private static final String TAG = DatePickerViewModel.class.getSimpleName();

    // DatePickerFragment uses the values of the states for int comparisons
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DATE_SELECT_STATE_PICKUP_DATE,
            DATE_SELECT_STATE_RETURN_DATE,
            DATE_SELECT_STATE_DATE_COMPLETE,
            DATE_SELECT_STATE_PICKUP_TIME,
            DATE_SELECT_STATE_RETURN_TIME,
            DATE_SELECT_STATE_TIME_COMPLETE})
    public @interface DateSelectState {
    }

    public static final int DATE_SELECT_STATE_PICKUP_DATE = 0;
    public static final int DATE_SELECT_STATE_RETURN_DATE = 1;
    public static final int DATE_SELECT_STATE_DATE_COMPLETE = 2;
    public static final int DATE_SELECT_STATE_PICKUP_TIME = 3;
    public static final int DATE_SELECT_STATE_RETURN_TIME = 4;
    public static final int DATE_SELECT_STATE_TIME_COMPLETE = 5;

    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    final ReactorVar<Set<Date>> mPickupClosedDates = new ReactorVar<>();
    final ReactorVar<Set<Date>> mReturnClosedDates = new ReactorVar<>();

    final ReactorVar<Integer> mDateSelectState = new ReactorVar<>(0);

    public final ReactorVar<String> errorToast = new ReactorVar<>();
    public final ReactorVar<ResponseWrapper> errorResponse = new ReactorVar<>();

    public final ReactorVar<Boolean> calendarVisible = new ReactorVar<>(true);

    public final ReactorVar<Boolean> timeSelectorVisible = new ReactorVar<>(false);
    public final ReactorVar<Integer> timeSelectorCurrentPage = new ReactorVar<>(0);

    public final ReactorVar<String> pickupDateText = new ReactorVar<>();
    public final ReactorVar<Boolean> pickupDateTextSelected = new ReactorVar<>(true);
    public final ReactorVar<Boolean> pickupDateTextVisible = new ReactorVar<>(true);
    public final ReactorVar<Boolean> pickupTriangleVisible = new ReactorVar<>(true);

    public final ReactorVar<String> pickupTimeText = new ReactorVar<>();
    public final ReactorVar<Boolean> pickupTimeTextSelected = new ReactorVar<>(false);
    public final ReactorVar<Boolean> pickupTimeTextVisible = new ReactorVar<>(false);

    public final ReactorVar<String> returnDateText = new ReactorVar<>();
    public final ReactorVar<Boolean> returnDateTextSelected = new ReactorVar<>(false);
    public final ReactorVar<Boolean> returnDateTextVisible = new ReactorVar<>(false);
    public final ReactorVar<Boolean> returnTriangleVisible = new ReactorVar<>(false);

    public final ReactorVar<String> returnTimeText = new ReactorVar<>();
    public final ReactorVar<Boolean> returnTimeTextVisible = new ReactorVar<>(false);
    public final ReactorVar<Boolean> returnTimeTextSelected = new ReactorVar<>(false);


    public final ReactorVar<Boolean> dividerVisibility = new ReactorVar<>(false);
    public final ReactorVar<Drawable> continueButtonBackgroundDrawable = new ReactorVar<>();
    public final ReactorVar<Integer> continueButtonBackgroundColor = new ReactorVar<>(0);
    public final ReactorVar<Integer> continueButtonTextColor = new ReactorVar<>();
    public final ReactorVar<Boolean> continueButtonVisible = new ReactorVar<>(true);

    public final ReactorVar<Boolean> closeDatePicker = new ReactorVar<>(false);

    public final ReactorVar<String> title = new ReactorVar<>();

    private ReactorCalendar mPickupDateCalendar = new ReactorCalendar();
    private ReactorCalendar mPickupTimeCalendar = new ReactorCalendar();

    private ReactorCalendar mReturnDateCalendar = new ReactorCalendar();
    private ReactorCalendar mReturnTimeCalendar = new ReactorCalendar();

    private EHISolrWorkingDayInfo mFirstDateInfo;
    private EHISolrWorkingDayInfo mSecondDateInfo;

    private EHISolrLocation mPickupLocation;
    private EHISolrLocation mReturnLocation;

    private Map<String, EHISolrWorkingDayInfo> mPickupLocationDatesInfo = new HashMap<>();
    private Map<String, EHISolrWorkingDayInfo> mReturnLocationDatesInfo = new HashMap<>();

    private boolean mEditMode;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        title.setValue(getResources().getString(R.string.reservation_calendar_title));
        pickupDateText.setValue(getResources().getString(R.string.reservation_scheduler_pickup_date_callout));
        returnDateText.setValue(getResources().getString(R.string.reservation_scheduler_return_date_callout));
        continueButtonBackgroundColor.setValue(getResources().getColor(R.color.ehi_disabled_gray));
        continueButtonTextColor.setValue(getResources().getColor(R.color.white40));
    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        mPickupDateCalendar.unbind();
        mPickupTimeCalendar.unbind();
        mReturnDateCalendar.unbind();
        mReturnTimeCalendar.unbind();
    }

    public String[] getShortWeekdays() {
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(Locale.getDefault());
        String[] shortWeekdays = dateFormatSymbols.getShortWeekdays();
        for (int i = 0; i < shortWeekdays.length; i++) {
            if (!(TextUtils.isEmpty(shortWeekdays[i]))) {
                shortWeekdays[i] = String.valueOf(shortWeekdays[i].charAt(0));
            }
        }
        return shortWeekdays;
    }

    public void update() {
        final int state = mDateSelectState.getRawValue();
        pickupDateTextVisible.setValue(state >= DATE_SELECT_STATE_PICKUP_DATE);
        pickupDateTextSelected.setValue(state == DATE_SELECT_STATE_PICKUP_DATE);

        returnDateTextVisible.setValue(state >= DATE_SELECT_STATE_RETURN_DATE);
        returnDateTextSelected.setValue(state == DATE_SELECT_STATE_RETURN_DATE);

        continueButtonVisible.setValue(state >= DATE_SELECT_STATE_DATE_COMPLETE);
        calendarVisible.setValue(state <= DATE_SELECT_STATE_DATE_COMPLETE);

        pickupTimeTextVisible.setValue(state >= DATE_SELECT_STATE_PICKUP_TIME);
        pickupTimeTextSelected.setValue(state == DATE_SELECT_STATE_PICKUP_TIME);

        returnTimeTextVisible.setValue(state >= DATE_SELECT_STATE_RETURN_TIME);
        returnTimeTextSelected.setValue(state == DATE_SELECT_STATE_RETURN_TIME);

        pickupTriangleVisible.setValue(state == DATE_SELECT_STATE_PICKUP_DATE || state == DATE_SELECT_STATE_PICKUP_TIME);
        returnTriangleVisible.setValue(state == DATE_SELECT_STATE_RETURN_DATE || state == DATE_SELECT_STATE_RETURN_TIME);

        timeSelectorVisible.setValue(
                (state == DATE_SELECT_STATE_PICKUP_TIME && getFirstDateWorkingInfo() != null)
                        || (state == DATE_SELECT_STATE_RETURN_TIME && getSecondDateWorkingInfo() != null));

        title.setValue(getResources().getString(R.string.reservation_calendar_title));
        switch (state) {
            case DATE_SELECT_STATE_PICKUP_DATE:
                title.setValue(getResources().getString(R.string.reservation_calendar_title));

                continueButtonVisible.setValue(true);
                continueButtonBackgroundColor.setValue(getResources().getColor(R.color.ehi_disabled_gray));
                continueButtonTextColor.setValue(getResources().getColor(R.color.white40));
                break;

            case DATE_SELECT_STATE_RETURN_DATE:
                title.setValue(getResources().getString(R.string.reservation_calendar_title));

                continueButtonVisible.setValue(true);
                continueButtonBackgroundColor.setValue(getResources().getColor(R.color.ehi_disabled_gray));
                continueButtonTextColor.setValue(getResources().getColor(R.color.white40));
                break;

            case DATE_SELECT_STATE_DATE_COMPLETE:
                title.setValue(getResources().getString(R.string.reservation_calendar_title));

                continueButtonBackgroundColor.setValue(getResources().getColor(R.color.ehi_primary));
                continueButtonTextColor.setValue(getResources().getColor(R.color.white));
                break;

            case DATE_SELECT_STATE_PICKUP_TIME:
                title.setValue(getResources().getString(R.string.time_picker_screen_title));
                pickupTimeText.setValue(getResources().getString(R.string.reservation_scheduler_pickup_time_callout));

                dividerVisibility.setValue(true);
                timeSelectorCurrentPage.setValue(0);

                if (getFirstDateWorkingInfo() == null) {
                    requestWorkingTimeInfo();
                } else {
                    continueButtonVisible.setValue(false);
                }
                break;

            case DATE_SELECT_STATE_RETURN_TIME:
                title.setValue(getResources().getString(R.string.time_picker_screen_title));
                returnTimeText.setValue(getResources().getString(R.string.reservation_scheduler_return_time_callout));

                dividerVisibility.setValue(true);
                timeSelectorCurrentPage.setValue(1);

                if (getSecondDateWorkingInfo() == null) {
                    requestWorkingTimeInfo();
                } else {
                    continueButtonVisible.setValue(false);
                }
                break;

            case DATE_SELECT_STATE_TIME_COMPLETE:
                continueButtonVisible.setValue(false);
                break;
        }
    }

    public void setDateSelectState(@DateSelectState int state, final boolean update) {
        mDateSelectState.setValue(state);

        if (update) {
            update();
        }
    }

    public
    @DateSelectState
    int getDateSelectState() {
        //noinspection ResourceType
        return mDateSelectState.getValue();
    }

    public Date getFirstSelectedDate() {
        return mPickupDateCalendar.getTime();
    }

    public Date getPickupDateAndTime() {
        long dateTime = 0;
        if (getPickupDate() != null) {
            dateTime += getPickupDate().getTime();
        }
        if (getPickupTime() != null) {
            dateTime += getPickupTime().getTime();
        }
        if (dateTime == 0) {
            return null;
        } else {
            return new Date(dateTime);
        }
    }

    public Date getReturnDateAndTime() {
        long dateTime = 0;
        if (getDropoffDate() != null) {
            dateTime += getDropoffDate().getTime();
        }
        if (getDropoffTime() != null) {
            dateTime += getDropoffTime().getTime();
        }
        if (dateTime == 0) {
            return null;
        } else {
            return new Date(dateTime);
        }
    }

    public void setPickupTime(Date time) {
        mPickupTimeCalendar.setTime(time);
    }

    public void setFormattedPickupDate(String formattedPickupDate) {
        if (isEditingState(DATE_SELECT_STATE_PICKUP_DATE)) {
            pickupDateText.setValue(getResources().getString(R.string.reservation_scheduler_pickup_date_callout));
        } else {
            pickupDateText.setValue(formattedPickupDate);
        }
    }

    public void setFormattedPickupTime(String formattedPickupTime) {
        if (isEditingState(DATE_SELECT_STATE_PICKUP_TIME)) {
            pickupTimeText.setValue(getResources().getString(R.string.reservation_scheduler_pickup_time_callout));
        } else {
            pickupTimeText.setValue(formattedPickupTime);
        }
    }

    public void setFormattedReturnDate(String formattedReturnDate) {
        if (isEditingState(DATE_SELECT_STATE_RETURN_DATE)) {
            returnDateText.setValue(getResources().getString(R.string.reservation_scheduler_return_date_callout));
        } else {
            returnDateText.setValue(formattedReturnDate);
        }
    }

    public void setFormattedReturnTime(String formattedReturnTime) {
        if (isEditingState(DATE_SELECT_STATE_RETURN_TIME)) {
            returnTimeText.setValue(getResources().getString(R.string.reservation_scheduler_return_time_callout));
        } else {
            returnTimeText.setValue(formattedReturnTime);
        }
    }

    public boolean isEditingState(@DateSelectState int currentState) {
        //noinspection ResourceType
        return isEditMode() && currentState == mDateSelectState.getRawValue();
    }

    public void reset() {
        setDateSelectState(DATE_SELECT_STATE_PICKUP_DATE, true);
        mPickupDateCalendar.clear();
        mReturnDateCalendar.clear();
        if (!mEditMode) {
            mPickupTimeCalendar.clear();
            mReturnTimeCalendar.clear();
        }
    }

    @NonNull
    public Date getCalendarStartDate() {
        return new Date();
    }

    @NonNull
    public Date getCalendarEndDate() {
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        nextYear.add(Calendar.DAY_OF_YEAR, -5);
        return nextYear.getTime();
    }

    public void selectTime(@NonNull Date time) {
        switch (getDateSelectState()) {
            case DATE_SELECT_STATE_PICKUP_TIME:
                setPickupTime(time);
                finishIfNecessary();
                if (getDropoffDate() != null) {
                    setDateSelectState(DATE_SELECT_STATE_RETURN_TIME, !isEditMode());
                } else {
                    setDateSelectState(DATE_SELECT_STATE_RETURN_DATE, !isEditMode());
                }
                break;
            case DATE_SELECT_STATE_RETURN_TIME:
                setReturnTime(time);
                finishIfNecessary();
                setDateSelectState(DATE_SELECT_STATE_TIME_COMPLETE, !isEditMode());
                break;

        }
    }

    public void selectDate(@NonNull Date date) {
        if (getDateSelectState() == DATE_SELECT_STATE_RETURN_DATE
                && mReturnDateCalendar.getRawTime() != null
                && date.before(mPickupDateCalendar.getRawTime())) {
            setDateSelectState(DATE_SELECT_STATE_PICKUP_DATE, false);
        }
        switch (getDateSelectState()) {
            case DATE_SELECT_STATE_PICKUP_DATE:
                setPickupDate(date);
                if (mEditMode) {
                    mReturnDateCalendar.clear();
                    returnDateText.setValue(getResources().getString(R.string.reservation_scheduler_return_date_callout));
                }
                setDateSelectState(DATE_SELECT_STATE_RETURN_DATE, true);
                break;
            case DATE_SELECT_STATE_RETURN_DATE:
                setReturnDate(date);
                setDateSelectState(DATE_SELECT_STATE_DATE_COMPLETE, true);
                break;
            case DATE_SELECT_STATE_DATE_COMPLETE:
                setDateSelectState(DATE_SELECT_STATE_PICKUP_DATE, true);
                reset();
                selectDate(date);
                break;
        }
    }

    private void finishIfNecessary() {
        if (isEditMode()) {
            closeDatePicker.setValue(true);
        }
    }

    public void requestWorkingTimeInfo() {
        showProgress(true);

        Date firstDate = mPickupDateCalendar.getTime();
        if (firstDate != null) {
            String firstDateKey = sDateFormatter.format(firstDate);
            if (mPickupLocationDatesInfo != null
                    && mPickupLocationDatesInfo.containsKey(firstDateKey)) {
                saveWorkingDaysInfo(mPickupLocationDatesInfo.get(firstDateKey), null);
            } else {
                performRequest(new GetSolrHoursByLocationIdRequest(mPickupLocation.getPeopleSoftId(), firstDate),
                        new IApiCallback<GetSolrHoursResponse>() {

                            @Override
                            public void handleResponse(ResponseWrapper<GetSolrHoursResponse> response) {
                                if (response.isSuccess()) {
                                    try {
                                        Map<String, EHISolrWorkingDayInfo> map = response.getData().getDaysInfo();
                                        EHISolrWorkingDayInfo info = new ArrayList<>(map.values()).get(0);
                                        saveWorkingDaysInfo(info, null);
                                    } catch (NullPointerException | IndexOutOfBoundsException e) {
                                        DLog.w(TAG, e);
                                        saveWorkingDaysInfo(EHISolrWorkingDayInfo.createOpenAllDay(), null);
                                    }
                                } else {
                                    setErrorResponse(response);
                                    saveWorkingDaysInfo(EHISolrWorkingDayInfo.createOpenAllDay(), null);
                                }
                            }
                        });
            }
        }

        Date secondDate = mReturnDateCalendar.getTime();
        if (secondDate != null) {
            String firstDateKey = sDateFormatter.format(secondDate);
            if (mReturnLocationDatesInfo != null
                    && mReturnLocationDatesInfo.containsKey(firstDateKey)) {
                saveWorkingDaysInfo(null, mReturnLocationDatesInfo.get(firstDateKey));
            } else {
                performRequest(new GetSolrHoursByLocationIdRequest(mReturnLocation.getPeopleSoftId(), secondDate),
                        new IApiCallback<GetSolrHoursResponse>() {

                            @Override
                            public void handleResponse(ResponseWrapper<GetSolrHoursResponse> response) {
                                if (response.isSuccess()) {
                                    try {
                                        Map<String, EHISolrWorkingDayInfo> map = response.getData().getDaysInfo();
                                        EHISolrWorkingDayInfo info = new ArrayList<>(map.values()).get(0);
                                        saveWorkingDaysInfo(null, info);
                                    } catch (NullPointerException | IndexOutOfBoundsException e) {
                                        DLog.w(TAG, e);
                                        saveWorkingDaysInfo(null, EHISolrWorkingDayInfo.createOpenAllDay());
                                    }
                                } else {
                                    setErrorResponse(response);
                                    saveWorkingDaysInfo(null, EHISolrWorkingDayInfo.createOpenAllDay());
                                }
                            }
                        });
            }
        }
    }

    public void requestWorkingDaysInfo() {
        showProgress(true);
        final boolean sameLocation = mPickupLocation.getPeopleSoftId().equals(mReturnLocation.getPeopleSoftId());
        performRequest(new GetSolrHoursByLocationIdRequest(mPickupLocation.getPeopleSoftId(),
                        new Date(),
                        getCalendarEndDate()),
                new IApiCallback<GetSolrHoursResponse>() {
                    @Override
                    public void handleResponse(final ResponseWrapper<GetSolrHoursResponse> response) {
                        if (response.isSuccess()) {
                            new AsyncTask<Void, Void, Pair<Set<Date>, Set<Date>>>() {
                                @Override
                                protected Pair<Set<Date>, Set<Date>> doInBackground(Void... params) {
                                    if (response.getData().getDaysInfo() == null) {
                                        mPickupLocationDatesInfo = new HashMap<>();
                                    } else {
                                        mPickupLocationDatesInfo = new HashMap<>(response.getData().getDaysInfo());
                                    }

                                    final Set<Date> pickupClosedDates = new HashSet<>();
                                    Set<Date> returnClosedDates = null;
                                    if (sameLocation) {
                                        returnClosedDates = new HashSet<>();
                                    }

                                    if (mPickupLocationDatesInfo == null) {
                                        return new Pair<>(pickupClosedDates, returnClosedDates);
                                    }

                                    for (String s : mPickupLocationDatesInfo.keySet()) {
                                        try {
                                            Date date = sDateFormatter.parse(s);

                                            if (mPickupLocationDatesInfo.get(s).getStandardTime() != null
                                                    && mPickupLocationDatesInfo.get(s).getStandardTime().isClosed()) {
                                                pickupClosedDates.add(date);
                                            }

                                            if (sameLocation) {
                                                mReturnLocationDatesInfo = new HashMap<>(mPickupLocationDatesInfo);
                                                if (mPickupLocationDatesInfo.get(s).getStandardTime() != null
                                                        && mPickupLocationDatesInfo.get(s).getStandardTime().isClosed()) {
                                                    if (mPickupLocationDatesInfo.get(s).getDropTime() != null) {
                                                        if (mPickupLocationDatesInfo.get(s).getDropTime().isClosed()) {
                                                            returnClosedDates.add(date);
                                                        }
                                                    } else {
                                                        returnClosedDates.add(date);
                                                    }
                                                }

                                            }
                                        } catch (ParseException e) {
                                            DLog.e(TAG, "GetSolrHoursByLocationIdRequest date parse error", e);
                                        }
                                    }

                                    return new Pair<>(pickupClosedDates, returnClosedDates);
                                }

                                @Override
                                protected void onPostExecute(Pair<Set<Date>, Set<Date>> setSetPair) {
                                    super.onPostExecute(setSetPair);
                                    setPickupClosedDates(setSetPair.first);
                                    setReturnClosedDates(setSetPair.second);
                                    showProgress(false);
                                }
                            }.execute();
                        } else {
                            setErrorResponse(response);
                        }
                    }
                });

        if (!sameLocation) {
            performRequest(new GetSolrHoursByLocationIdRequest(mReturnLocation.getPeopleSoftId(),
                            new Date(),
                            getCalendarEndDate()),
                    new IApiCallback<GetSolrHoursResponse>() {
                        @Override
                        public void handleResponse(final ResponseWrapper<GetSolrHoursResponse> response) {
                            if (response.isSuccess()) {
                                new AsyncTask<Void, Void, Set<Date>>() {
                                    @Override
                                    protected Set<Date> doInBackground(Void... params) {
                                        mReturnLocationDatesInfo = new HashMap<>(response.getData().getDaysInfo());
                                        final Set<Date> returnClosedDates = new HashSet<>();

                                        if (mReturnLocationDatesInfo.keySet() != null) {
                                            for (String s : mReturnLocationDatesInfo.keySet()) {
                                                try {
                                                    Date date = sDateFormatter.parse(s);
                                                    //in closed in standard time
                                                    if (mReturnLocationDatesInfo.get(s).getStandardTime() != null
                                                            && mReturnLocationDatesInfo.get(s).getStandardTime().isClosed()) {
                                                        if (mReturnLocationDatesInfo.get(s).getDropTime() != null) {
                                                            if (mReturnLocationDatesInfo.get(s).getDropTime().isClosed()) {
                                                                //if after hours not supported
                                                                returnClosedDates.add(date);
                                                            }
                                                        } else {
                                                            //if no after hours info provided
                                                            returnClosedDates.add(date);
                                                        }
                                                    }
                                                } catch (ParseException e) {
                                                    DLog.e(TAG, "GetSolrHoursByLocationIdRequest date parse error", e);
                                                }
                                            }
                                        }

                                        return returnClosedDates;
                                    }

                                    @Override
                                    protected void onPostExecute(Set<Date> returnClosedDates) {
                                        super.onPostExecute(returnClosedDates);
                                        setReturnClosedDates(returnClosedDates);
                                    }
                                }.execute();

                            } else {
                                showProgress(false);
                                setErrorResponse(response);
                            }
                        }
                    });
        }

    }

    boolean isDateClosed(Date date, Set<Date> dateSet) {
        if (dateSet == null) {
            return true;
        }

        Calendar dateToSelect = Calendar.getInstance();
        dateToSelect.setTime(date);
        return dateSet.contains(date);
    }

    public Set<Date> getPickupClosedDates() {
        return mPickupClosedDates.getValue() == null ? null
                : Collections.unmodifiableSet(mPickupClosedDates.getValue());
    }

    public Set<Date> getReturnClosedDates() {
        return mReturnClosedDates.getValue() == null ? null
                : Collections.unmodifiableSet(mReturnClosedDates.getValue());
    }

    void setPickupClosedDates(Set<Date> closedDates) {
        mPickupClosedDates.setValue(closedDates);
    }

    void setReturnClosedDates(Set<Date> closedDates) {
        mReturnClosedDates.setValue(closedDates);
    }

    private void saveWorkingDaysInfo(EHISolrWorkingDayInfo pickup, EHISolrWorkingDayInfo ret) {
        if (pickup != null) {
            mFirstDateInfo = pickup;
        }

        if (ret != null) {
            mSecondDateInfo = ret;
        }

        if (isEditMode()) { //If we're editing we already are in the state we expect to be, just need to update the screen
            //noinspection ResourceType
            setDateSelectState(mDateSelectState.getRawValue(), true);
            showProgress(false);
            return;
        }

        if (mFirstDateInfo != null
                && mPickupTimeCalendar.getTime() == null) {
            setDateSelectState(DATE_SELECT_STATE_PICKUP_TIME, true);
        } else if (mSecondDateInfo != null
                && mReturnTimeCalendar.getTime() == null) {
            setDateSelectState(DATE_SELECT_STATE_RETURN_TIME, true);
        }

        showProgress(false);
    }

    public EHISolrWorkingDayInfo getFirstDateWorkingInfo() {
        return mFirstDateInfo;
    }

    public EHISolrWorkingDayInfo getSecondDateWorkingInfo() {
        return mSecondDateInfo;
    }

    public void setPickupLocation(EHISolrLocation pickupLocation) {
        mPickupLocation = pickupLocation;
    }

    public void setReturnLocation(EHISolrLocation returnLocation) {
        mReturnLocation = returnLocation;
    }

    public EHISolrLocation getPickupLocation() {
        return mPickupLocation;
    }

    public EHISolrLocation getDropoffLocation() {
        return mReturnLocation;
    }

    public void requestAvailableDatesInfo() {
        setDateSelectState(DATE_SELECT_STATE_PICKUP_DATE, true);
    }

    public void setPickupDate(Date date) {
        mPickupDateCalendar.setTime(date);
    }

    public Date getPickupDate() {
        return mPickupDateCalendar.getTime();
    }

    public void setPickUpTime(Date date) {
        mPickupTimeCalendar.setTime(date);
    }

    public Date getPickupTime() {
        return mPickupTimeCalendar.getTime();
    }

    public void setReturnDate(Date date) {
        mReturnDateCalendar.setTime(date);
    }

    public Date getDropoffDate() {
        return mReturnDateCalendar.getTime();
    }

    public void setReturnTime(Date time) {
        mReturnTimeCalendar.setTime(time);
    }

    public Date getDropoffTime() {
        return mReturnTimeCalendar.getTime();
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
    }

    public boolean isEditMode() {
        return mEditMode
                && mPickupDateCalendar.getRawTime() != null
                && mPickupTimeCalendar.getRawTime() != null
                && mReturnDateCalendar.getRawTime() != null
                && mReturnTimeCalendar.getRawTime() != null;
    }

    public void setErrorResponse(ResponseWrapper errorResponse) {
        showProgress(false);
        setError(errorResponse);
    }

    public void pickupDateTextClicked() {
        if (getFirstSelectedDate() == null) {
            errorToast.setValue(getResources().getString(R.string.reservation_pickup_date_toast));
            return;
        }
        mPickupDateCalendar.clear();
        mReturnDateCalendar.clear();
        mPickupTimeCalendar.clear();
        mReturnTimeCalendar.clear();
        setDateSelectState(DATE_SELECT_STATE_PICKUP_DATE, true);
    }

    public void returnDateTextClicked() {
        if (getFirstSelectedDate() == null) {
            errorToast.setValue(getResources().getString(R.string.reservation_return_date_toast));
            return;
        }
        mPickupTimeCalendar.clear();
        mReturnTimeCalendar.clear();
        setDateSelectState(DATE_SELECT_STATE_DATE_COMPLETE, true);
    }

    public void pickupTimeTextClicked() {
        if (getPickupTime() == null) {
            errorToast.setValue(getResources().getString(R.string.reservation_pickup_time_toast));
            return;
        }
        mPickupTimeCalendar.clear();
        mReturnTimeCalendar.clear();
        setDateSelectState(DATE_SELECT_STATE_PICKUP_TIME, true);
    }

    public void returnTimeTextClicked() {
        if (getDropoffDate() == null) {
            errorToast.setValue(getResources().getString(R.string.reservation_return_time_toast));
            return;
        }
        mReturnTimeCalendar.clear();
        setDateSelectState(DATE_SELECT_STATE_RETURN_TIME, true);
    }

    public void calendarContinueClicked() {
        if (getFirstSelectedDate() == null) {
            errorToast.setValue(getResources().getString(R.string.reservation_pickup_date_toast));
            return;
        }
        if (getDropoffDate() == null) {
            errorToast.setValue(getResources().getString(R.string.reservation_return_date_toast));
            return;
        }
        finishIfNecessary();
        requestWorkingTimeInfo();
    }

    public boolean shouldSendPickupLocation(int selectionMode) {
        return selectionMode == TimeSelectionView.MODE_PICKUP_TIME ||
                (selectionMode == TimeSelectionView.MODE_RETURN_TIME && mReturnLocation == null);
    }

    public @SearchLocationsActivity.Flow int getFlow(int selectionMode) {
        if (mReturnLocation == null
                ||  mReturnLocation.getPeopleSoftId() == null
                || mPickupLocation == null
                || mPickupLocation.getPeopleSoftId() == null) {
            return SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP;
        }

        if (mReturnLocation.getPeopleSoftId().equalsIgnoreCase(mPickupLocation.getPeopleSoftId())) {
            return SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP;
        } else if (selectionMode == TimeSelectionView.MODE_PICKUP_TIME) {
            return SearchLocationsActivity.FLOW_PICKUP_LOCATION_ONE_WAY;
        } else {
            return SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY;
        }
    }

    public boolean shouldSendConflictingPickupTimeSelectedInTimePicker(int selectionMode) {
        return selectionMode == TimeSelectionView.MODE_PICKUP_TIME;
    }
}

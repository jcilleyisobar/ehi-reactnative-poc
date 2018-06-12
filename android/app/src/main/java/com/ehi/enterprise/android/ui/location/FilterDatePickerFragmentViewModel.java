package com.ehi.enterprise.android.ui.location;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.reactor_extensions.ReactorCalendar;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;
import java.util.Date;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class FilterDatePickerFragmentViewModel extends ManagersAccessViewModel {

    private static final String TAG = "FILTER_DATE_PICKER_FRAGMENT_VIEW_MODEL";

    private static final int DATE_FORMAT_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY
            | DateUtils.FORMAT_ABBREV_WEEKDAY
            | DateUtils.FORMAT_ABBREV_MONTH;

    public static final int DATE_SELECT_STATE_PICKUP = 0;
    public static final int DATE_SELECT_STATE_RETURN = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DATE_SELECT_STATE_PICKUP,
            DATE_SELECT_STATE_RETURN})
    public @interface DateSelectState {

    }

    final ReactorTextViewState toastView = new ReactorTextViewState();
    public final ReactorTextViewState continueButtonTextViewState = new ReactorTextViewState();

    public final ReactorVar<Boolean> closeDatePicker = new ReactorVar<>(false);
    public final ReactorVar<String> subHeaderTextView = new ReactorVar<>();
    public ReactorVar<String> title = new ReactorVar<>();
    final ReactorVar<Integer> dateSelectState = new ReactorVar<>(0);

    private ReactorCalendar mPickupDateCalendar = new ReactorCalendar();
    private ReactorCalendar mReturnDateCalendar = new ReactorCalendar();

    private boolean mShouldPersistPickupDate = true;
    private boolean mShouldPersistReturnDate = true;

    public ReactorCalendar getPickupDateCalendar() {
        return mPickupDateCalendar;
    }

    public ReactorCalendar getReturnDateCalendar() {
        return mReturnDateCalendar;
    }

    public boolean shouldPersistPickupDate() {
        return mShouldPersistPickupDate;
    }

    public boolean shouldPersistReturnDate() {
        return mShouldPersistReturnDate;
    }

    @Override
    public void prepareToAttachToView() {
        super.prepareToAttachToView();
        toastView.setVisibility(View.GONE);
        continueButtonTextViewState.setVisibility(View.VISIBLE);
        continueButtonTextViewState.setBackgroundColor(getResources().getColor(R.color.ehi_disabled_gray));
        continueButtonTextViewState.setTextColor(getResources().getColor(R.color.white40));
    }

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        switch (dateSelectState.getValue()) {
            case DATE_SELECT_STATE_PICKUP:
                title.setValue(getResources().getString(R.string.date_select_pickup_title));
                subHeaderTextView.setValue(getResources().getString(R.string.reservation_scheduler_pickup_date_callout));
                break;
            case DATE_SELECT_STATE_RETURN:
                title.setValue(getResources().getString(R.string.date_select_return_title));
                subHeaderTextView.setValue(getResources().getString(R.string.reservation_scheduler_return_date_callout));
                break;
            default:
                DLog.e(TAG, "There are no state set");
                break;
        }
    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        mPickupDateCalendar.unbind();
        mReturnDateCalendar.unbind();
    }

    public void setPickupDateCalendar(ReactorCalendar mPickupDateCalendar) {
        this.mPickupDateCalendar = mPickupDateCalendar;
    }

    public void setReturnDateCalendar(ReactorCalendar mReturnDateCalendar) {
        this.mReturnDateCalendar = mReturnDateCalendar;
    }

    public void setPickupDate(Date date) {
        mPickupDateCalendar.setTime(date);
    }

    public void setReturnDate(Date date) {
        mReturnDateCalendar.setTime(date);
    }

    public void selectDate(Date date) {
        boolean isDateValid = true;
        switch (dateSelectState.getValue()) {
            case DATE_SELECT_STATE_PICKUP:
                isDateValid = isDateValid(date, getReturnDate());
                mPickupDateCalendar.setTime(date);
                break;
            case DATE_SELECT_STATE_RETURN:
                isDateValid = isDateValid(getPickupDate(), date);
                mReturnDateCalendar.setTime(date);
                break;
            default:
                DLog.e(TAG, "There are no state set");
                break;
        }
        setSubHeaderTitle(date);
        updateToastVisibility(isDateValid);
        updateContinueButton();
    }

    public void setSubHeaderTitle(Date date) {
        final String dateFormatted = DateUtils.formatDateTime(null, date.getTime(),
                DATE_FORMAT_FLAGS);
        subHeaderTextView.setValue(dateFormatted);
    }

    private void updateContinueButton() {
        continueButtonTextViewState.setBackgroundColor(getResources().getColor(R.color.ehi_primary));
        continueButtonTextViewState.setTextColor(getResources().getColor(R.color.white));
        continueButtonTextViewState.setVisibility(View.VISIBLE);
    }

    public void updateToastVisibility(boolean isDateValid) {
        if (!isDateValid) {
            final String dateFormatted;
            final CharSequence toastText;
            switch (dateSelectState.getValue()) {
                case DATE_SELECT_STATE_PICKUP:
                    dateFormatted = DateUtils.formatDateTime(null, getReturnDate().getTime(),
                            DATE_FORMAT_FLAGS);
                    toastText = new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.date_select_invalid_pickup_date)
                            .addTokenAndValue(EHIStringToken.DATE, dateFormatted)
                            .format();

                    break;
                case DATE_SELECT_STATE_RETURN:
                    dateFormatted = DateUtils.formatDateTime(null, getPickupDate().getTime(),
                            DATE_FORMAT_FLAGS);
                    toastText = new TokenizedString.Formatter<EHIStringToken>(getResources())
                            .formatString(R.string.date_select_invalid_return_date)
                            .addTokenAndValue(EHIStringToken.DATE, dateFormatted)
                            .format();
                    break;
                default:
                    toastText = "";
                    DLog.e(TAG, "There are no state set");
                    break;
            }
            toastView.setText(toastText);
        }

        if (isDateValid) {
            toastView.setVisibility(View.GONE);
        } else {
            toastView.setVisibility(View.VISIBLE);
        }
    }

    public Date getPickupDate() {
        return mPickupDateCalendar.getTime();
    }

    public Date getReturnDate() {
        return mReturnDateCalendar.getTime();
    }

    public void setDateSelectState(@DateSelectState int state) {
        dateSelectState.setValue(state);
    }

    public @DateSelectState int getDateSelectState() {
        //noinspection ResourceType
        return dateSelectState.getValue();
    }

    public boolean isDateValid(Date pickupDate, Date returnDate) {
        return pickupDate == null
                || returnDate == null
                || pickupDate.before(returnDate);
    }

    public void continueButtonClicked() {
        if (!isDateValid(getPickupDate(), getReturnDate())) {
            setDatesPersistency();
        }
        closeDatePicker.setValue(true);
    }

    public void setDatesPersistency() {
        switch (dateSelectState.getValue()) {
            case DATE_SELECT_STATE_PICKUP:
                mShouldPersistReturnDate = false;
                break;
            case DATE_SELECT_STATE_RETURN:
            default:
                mShouldPersistPickupDate = false;
                break;
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

}

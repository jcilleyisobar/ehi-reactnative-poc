package com.ehi.enterprise.android.ui.reservation;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;

import java.util.Calendar;
import java.util.Date;

class CalendarCellDecoratorImpl implements CalendarCellDecorator {
    private final DatePickerViewModel mViewModel;
    private final Date mToday = new Date();

    public CalendarCellDecoratorImpl(DatePickerViewModel viewModel) {
        mViewModel = viewModel;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void decorate(CalendarCellView cellView, Date date) {
        final Resources resources = mViewModel.getResources();

        if (mViewModel.getPickupClosedDates() == null || mViewModel.getReturnClosedDates() == null) {
            return;
        }

        if (!cellView.isCurrentMonth()) {
            cellView.setVisibility(View.INVISIBLE);
            return;
        } else {
            cellView.setVisibility(View.VISIBLE);
        }

        Date firstSelectedDate = mViewModel.getPickupDate();
        Date secondSelectedDate = mViewModel.getDropoffDate();

        Calendar dayToDecorate = Calendar.getInstance();
        dayToDecorate.setTime(date);

        if (mViewModel.isEditingState(DatePickerViewModel.DATE_SELECT_STATE_PICKUP_DATE)  //The calendar needs to look like there's no date selected, even though we've got the users original date choice saved
                || (firstSelectedDate == null && secondSelectedDate == null)) {
            if (mViewModel.isDateClosed(date, mViewModel.getPickupClosedDates())) {
                setCalendarCellClosed(cellView);
                return;
            }

            if (!date.before(mToday) || isSameDay(mToday, date)) {
                cellView.setTextColor(resources.getColor(R.color.ehi_black));
            } else {
                cellView.setTextColor(resources.getColor(R.color.ehi_dark_rule));
            }

            cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_background));
            if (cellView.isHighlighted() && cellView.isToday()) {
                cellView.setBackground(resources.getDrawable(R.drawable.white_background_black_border));
            }

        } else {
            boolean isBeginningOfMonth = dayToDecorate.getActualMinimum(Calendar.DAY_OF_MONTH) == dayToDecorate.get(Calendar.DAY_OF_MONTH);
            boolean isEndOfMonth = dayToDecorate.getActualMaximum(Calendar.DAY_OF_MONTH) == dayToDecorate.get(Calendar.DAY_OF_MONTH);

            if ((firstSelectedDate != null && mViewModel.isEditingState(DatePickerViewModel.DATE_SELECT_STATE_RETURN_DATE)
                    || firstSelectedDate != null && secondSelectedDate == null)) {

                // Reset all dates after today
                if (!date.before(mToday) || isSameDay(mToday, date)) {
                    cellView.setSelectable(true);
                    cellView.setEnabled(true);
                    cellView.setTextColor(resources.getColor(R.color.ehi_black));
                } else {
                    cellView.setSelectable(false);
                    cellView.setEnabled(false);
                    cellView.setTextColor(resources.getColor(R.color.ehi_dark_rule));
                }

                // Mark dates that aren't selected, and are closed
                if (!cellView.isSelected()
                        && !date.before(mToday)
                        && firstSelectedDate != date
                        && mViewModel.isDateClosed(date, mViewModel.getReturnClosedDates())) {
                    setCalendarCellClosed(cellView);
                    cellView.setTextColor(resources.getColor(R.color.ehi_dark_rule));
                    return;
                }

                // Mark dates that aren't selected, before the pickup date as closed if they are
                if (!cellView.isSelected()
                        && date.before(firstSelectedDate)
                        && mViewModel.isDateClosed(date, mViewModel.getPickupClosedDates())) {
                    setCalendarCellClosed(cellView);
                    cellView.setTextColor(resources.getColor(R.color.ehi_dark_rule));
                    return;
                }

                Calendar firstSelectedDateCalendar = Calendar.getInstance();
                firstSelectedDateCalendar.setTime(firstSelectedDate);

                Calendar nextDay = Calendar.getInstance();
                nextDay.setTime(firstSelectedDate);
                nextDay.add(Calendar.DAY_OF_YEAR, 1);

                boolean isDayAfterFirstSelection = isSameDay(dayToDecorate, nextDay);

                if (isSameDay(firstSelectedDateCalendar, dayToDecorate)) {

                    // Pickup date gets a green background
                    cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_highlighted_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (cellView.isHighlighted() && cellView.isToday()) {

                    //Make sure today stays highlighted
                    cellView.setBackground(resources.getDrawable(R.drawable.white_background_black_border));
                    cellView.setTextColor(resources.getColor(R.color.ehi_black));
                } else if (isDayAfterFirstSelection && !isBeginningOfMonth) {

                    // The day after the pickup date gets a right facing arrow, this makes it look like the pickup
                    // date has the arrow pointing out of it
                    cellView.setBackground(resources.getDrawable(R.drawable.day_after_pickup_selection_background));
                    cellView.setTextColor(resources.getColor(R.color.ehi_black));
                } else {

                    //All other selectable cells get white background
                    cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_background));
                }
            } else if (firstSelectedDate != null && secondSelectedDate != null) {
                if (!date.before(mToday) || isSameDay(mToday, date)) {
                    cellView.setSelectable(true);
                    cellView.setEnabled(true);
                    cellView.setTextColor(resources.getColor(R.color.ehi_black));
                } else {
                    cellView.setSelectable(false);
                    cellView.setEnabled(false);
                    cellView.setTextColor(resources.getColor(R.color.ehi_dark_rule));
                }

                Calendar firstSelectedDateCalendar = Calendar.getInstance();
                firstSelectedDateCalendar.setTime(firstSelectedDate);

                Calendar secondSelectedDateCalendar = Calendar.getInstance();
                secondSelectedDateCalendar.setTime(secondSelectedDate);

                Calendar nextDay = Calendar.getInstance();
                nextDay.setTime(firstSelectedDate);
                nextDay.add(Calendar.DAY_OF_YEAR, 1);

                Calendar previousDay = Calendar.getInstance();
                previousDay.setTime(secondSelectedDate);
                previousDay.add(Calendar.DAY_OF_YEAR, -1);

                boolean isDayAfterFirstSelection = isSameDay(dayToDecorate, nextDay);
                boolean isDayBeforeLastSelection = isSameDay(dayToDecorate, previousDay);
                boolean isFirstSelectedDay = isSameDay(dayToDecorate, firstSelectedDateCalendar);
                boolean isLastSelectedDay = isSameDay(dayToDecorate, secondSelectedDateCalendar);
                boolean isSameDay = isSameDay(firstSelectedDateCalendar, secondSelectedDateCalendar);

                if (isDayAfterFirstSelection
                        && isDayBeforeLastSelection
                        && !isBeginningOfMonth
                        && !isEndOfMonth
                        && !isSameDay
                        && cellView.isSelectable()) {

                    // 3 day rental, middle day shows two arrows pointing inwards

                    cellView.setBackground(resources.getDrawable(R.drawable.first_and_last_non_selected_range_day_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (isDayAfterFirstSelection
                        && !isBeginningOfMonth
                        && !isLastSelectedDay
                        && !isSameDay
                        && cellView.isSelectable()) {

                    // Arrow on the day after the pickup selection, points to the right

                    cellView.setBackground(resources.getDrawable(R.drawable.first_non_selected_range_day_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (isDayBeforeLastSelection
                        && !isEndOfMonth
                        && !isFirstSelectedDay
                        && !isSameDay
                        && cellView.isSelectable()) {

                    // Arrow on the day before the return selection, points to the left

                    cellView.setBackground(resources.getDrawable(R.drawable.last_non_selected_range_day_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (isFirstSelectedDay
                        && !isLastSelectedDay
                        && cellView.isSelectable()) {

                    // Pickup date, primary green

                    cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_highlighted_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (!isFirstSelectedDay
                        && isLastSelectedDay
                        && cellView.isSelectable()) {

                    // Return date, primary green

                    cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_highlighted_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (isFirstSelectedDay
                        && isLastSelectedDay
                        && cellView.isSelectable()
                        && isSameDay) {

                    // Pickup date and return date are the same, use double border green background

                    cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_highlighted_background_same_day));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else if (cellView.isSelected()
                        && cellView.isSelectable()) {

                    // Dates inside the range, use dark primary green

                    cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_middle_background));
                    cellView.setTextColor(resources.getColor(R.color.white));
                } else {
                    if (cellView.isHighlighted() && cellView.isToday()) {

                        // Highlight today with a black border

                        cellView.setBackground(resources.getDrawable(R.drawable.white_background_black_border));
                    } else {

                        // All other selectable cells have white backgrounds
                        cellView.setBackground(resources.getDrawable(R.drawable.calendar_day_cell_background));
                    }
                }

                if (mViewModel.isDateClosed(date, mViewModel.getPickupClosedDates())) {
                    if (!cellView.isSelected()) {
                        cellView.setTextColor(resources.getColor(R.color.ehi_dark_rule));
                    }
                    setCalendarCellClosed(cellView);
                }

            }
        }
    }

    private void setCalendarCellClosed(CalendarCellView cellView) {
        cellView.setSelectable(false);
        cellView.setEnabled(false);

        if (!cellView.isSelected()) {
            cellView.setBackgroundColor(mViewModel.getResources().getColor(R.color.ehi_table_cell_color));
            cellView.setTextColor(mViewModel.getResources().getColor(R.color.ehi_dark_rule));
        } else {
            cellView.setTextColor(mViewModel.getResources().getColor(R.color.white));
        }

    }

    public boolean isSameDay(@NonNull Calendar calendar1, @NonNull Calendar calendar2) {
        return calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
                && calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR);
    }

    public boolean isSameDay(@NonNull Date date1, @NonNull Date date2) {
        final Calendar calendar1 = Calendar.getInstance();
        final Calendar calendar2 = Calendar.getInstance();

        calendar1.setTime(date1);
        calendar2.setTime(date2);

        return isSameDay(calendar1, calendar2);
    }

}

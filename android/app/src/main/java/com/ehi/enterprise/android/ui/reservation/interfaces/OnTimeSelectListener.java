package com.ehi.enterprise.android.ui.reservation.interfaces;

import java.util.Date;

public interface OnTimeSelectListener {

	void onTimeSelected(Date time);

	void onAfterHoursTitleClicked();

	void onLastPickupTimeClicked();

	void onLastReturnTimeClicked();

	void onSearchOpenLocationsInMapClicked(int selectionMode, Date selectedTime);
}

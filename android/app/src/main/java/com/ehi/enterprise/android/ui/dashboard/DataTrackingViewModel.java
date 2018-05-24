package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;

import java.util.Calendar;

public class DataTrackingViewModel extends ManagersAccessViewModel {

    public void setDataCollectionWarningAsShow() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 12);

        getManagers().getLocalDataManager().setDataCollectionReminderNextShowTimestamp(
                calendar.getTimeInMillis()
        );
    }

    public String getRentalState() {
        return isUserLoggedIn() ? EHIAnalytics.State.STATE_NONE.value : EHIAnalytics.State.STATE_UNAUTH.value;
    }
}

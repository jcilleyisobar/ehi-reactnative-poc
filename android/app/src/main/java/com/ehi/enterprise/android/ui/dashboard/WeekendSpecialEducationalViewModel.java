package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class WeekendSpecialEducationalViewModel extends ManagersAccessViewModel {
    public void markWeekendSpecialModalAsSeen() {
        getManagers().getLocalDataManager().setShouldShowWeekendSpecialModal(false);
    }
}

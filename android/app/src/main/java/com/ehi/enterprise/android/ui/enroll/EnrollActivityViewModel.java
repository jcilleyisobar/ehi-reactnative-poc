package com.ehi.enterprise.android.ui.enroll;

import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class EnrollActivityViewModel extends ManagersAccessViewModel {

    public void startEnrollProfile() {
        getManagers().getLocalDataManager().setEnrollmentProfile(
                new EHIEnrollProfile()
        );
    }

    public void clearEnrollProfile() {
        getManagers().getLocalDataManager().clearEnrollmentProfile();
    }
}

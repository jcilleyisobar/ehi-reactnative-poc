package com.ehi.enterprise.android.ui.splash;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SplashActivityViewModel extends ManagersAccessViewModel {

    public void refreshPreferredRegionWeekendSpecialContract() {
        if (getManagers().getLocalDataManager().havePreferredRegion()) {
            retrievePreferredRegionWeekendSpecialContract();
        } else {
            mIsWeekendSpecialContractRequestDone.setValue(true);
        }
    }

    public boolean shouldShowOnboarding() {
        return getManagers().getLocalDataManager().getFirstStartFlag();
    }

    public void setOnboardingWasShown() {
        getManagers().getLocalDataManager().setFirstStartFlag(false);
    }

}

package com.ehi.enterprise.android.ui.reservation.promotions;

import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

public class WeekendSpecialDetailsViewModel extends ManagersAccessViewModel {

    public void enableWeekendSpecial() {
        getManagers().getReservationManager().setWeekendSpecial(true);
    }

    public boolean needShowContractDialog() {
        return (isUserLoggedIn()
                && getUserProfileCollection() != null
                && getUserProfileCollection().getProfile() != null
                && getUserProfileCollection().getProfile().getCorporateAccount() != null)
                || (isLoggedIntoEmeraldClub()
                && getEmeraldClubProfile() != null
                && getEmeraldClubProfile().getProfile() != null
                && getEmeraldClubProfile().getProfile().getCorporateAccount() != null);

    }
}

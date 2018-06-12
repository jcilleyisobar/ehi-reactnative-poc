package com.ehi.enterprise.android.ui.enroll;


import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.ListUtils;

public class EnrollAddressFragmentViewModel extends ManagersAccessViewModel {

    private boolean mIsEmeraldClub;

    public String getStreetAddress() {
        final EHIEnrollProfile profile = getManagers().getLocalDataManager().getEnrollProfile();
        if (profile.getEhiAddressProfile() != null) {
            if (!ListUtils.isEmpty(profile.getEhiAddressProfile().getStreetAddresses())) {
                return profile.getEhiAddressProfile().getStreetAddresses().get(0);
            }
        }
        return null;
    }

    public boolean isEmeraldClub() {
        return mIsEmeraldClub;
    }

    public void setEmeraldClub(boolean value) {
        mIsEmeraldClub = value;
    }
}

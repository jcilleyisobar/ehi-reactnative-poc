package com.ehi.enterprise.android.ui.rewards;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.support.EHISupportInfo;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class AboutPointsViewModel extends ManagersAccessViewModel {

//    final ReactorViewState pointsHistoryContainer= new ReactorViewState();
//    final ReactorViewState transferPointsContainer= new ReactorViewState();
    final ReactorViewState lostPointsContainer= new ReactorViewState();

    final ReactorTextViewState pointsBalance = new ReactorTextViewState();
    final ReactorTextViewState eplusPhoneNumber = new ReactorTextViewState();


    @Override
    public void onAttachToView() {
        super.onAttachToView();
        fillUpPointsInfo();
    }

    private void fillUpPointsInfo() {
        ProfileCollection profile = getManagers().getLoginManager().getProfileCollection();
        if (profile != null
                && profile.getBasicProfile() != null
                && profile.getBasicProfile().getLoyaltyData() != null) {
            pointsBalance.setText(profile.getBasicProfile().getLoyaltyData().getFormattedPointsToDate());
        }
        String phoneNumber = getEplusPhoneNumber();
        if (phoneNumber != null) {
            eplusPhoneNumber.setText(getResources().getString(R.string.profile_edit_member_info_non_editable_action_title));
        } else {
//            pointsHistoryContainer.setVisibility(View.GONE);
//            transferPointsContainer.setVisibility(View.GONE);
            lostPointsContainer.setVisibility(View.GONE);
        }
    }

    public String getEplusPhoneNumber() {
        EHISupportInfo supportInfo = getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale();
        if (supportInfo != null) {
            String phoneNumber = supportInfo.getSupportPhoneNumber(EHIPhone.PhoneType.EPLUS);
            if (phoneNumber != null) {
                return phoneNumber;
            }
        }
        return null;
    }
}

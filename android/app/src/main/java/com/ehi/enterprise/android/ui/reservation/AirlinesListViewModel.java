package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.BaseAppUtils;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class AirlinesListViewModel extends ManagersAccessViewModel {

    private ReactorVar<List<EHIAirlineDetails>> airlinesList = new ReactorVar<>();

    private EHIAirlineDetails mOther;

    public List<EHIAirlineDetails> getAirlines() {
        return airlinesList.getValue();
    }

    public void setAirlines(List<EHIAirlineDetails> airlineDetails) {
       int index = getOtherIndex(airlineDetails);
        if (index != -1){
            mOther = airlineDetails.get(index);
            airlineDetails.remove(index);
        }
        airlinesList.setValue(airlineDetails);
    }

    public int getOtherIndex(List<EHIAirlineDetails> airlineDetails){
        return BaseAppUtils.indexOf(
                EHIAirlineDetails.OTHER_CODE,
                airlineDetails,
                new BaseAppUtils.CompareTwo<String, EHIAirlineDetails>() {
                    @Override
                    public boolean equals(String first, EHIAirlineDetails second) {
                        return first.equalsIgnoreCase(second.getCode());
                    }
                });
    }

    public EHIAirlineDetails getOther() {
        return mOther;
    }
}

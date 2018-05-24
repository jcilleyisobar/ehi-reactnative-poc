package com.ehi.enterprise.android.ui.reservation;

import com.ehi.enterprise.android.models.reservation.EHIAvailableCarFilters;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class CarFilterViewModel extends ManagersAccessViewModel {

    ReactorVar<List<EHICarClassDetails>> mCarClasses;
    private List<EHIAvailableCarFilters> mFilters;
    private List<EHICarClassDetails> mResetCars;

    public void applyFilters() {
        mCarClasses.setValue(EHICarClassDetails.applyFilters(mCarClasses.getValue(), mResetCars, mFilters));
    }

    public void setListOfCars(List<EHICarClassDetails> carClasses) {
        if (mResetCars == null) {
            mResetCars = carClasses;
        }

        if (mCarClasses == null) {
            mCarClasses = new ReactorVar<>(carClasses);
        }
        else {
            mCarClasses.setValue(carClasses);
        }

        if (mFilters == null) {
            mFilters = getManagers().getReservationManager().getFilters();
            applyFilters();
        }
    }

    public void clearFilters() {
        mCarClasses.setValue(mResetCars);
        for (int i = 0; i < getFilters().size(); i++) {
            getFilters().get(i).clearFilters();
        }
    }

    public List<EHICarClassDetails> getListOfCars() {
        if (mCarClasses == null) {
            mCarClasses = new ReactorVar<>((List<EHICarClassDetails>) new ArrayList<EHICarClassDetails>());
        }
        return mCarClasses.getValue();
    }

    public List<EHIAvailableCarFilters> getFilters() {
        return mFilters;
    }


    public void commitFilters() {
        getManagers().getReservationManager().setFilters(mFilters);
    }
}


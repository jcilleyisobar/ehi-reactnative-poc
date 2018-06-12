package com.ehi.enterprise.android.ui.enroll;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;

import java.util.List;

public interface CountryContract {

    interface CountryListener {

        void onCountryClick();

        void onRegionClick();
    }

    interface CountryView {
        void setCountry(EHICountry ehiCountry);

        EHICountry getCountry();

        String getCountryCode();

        void setRegion(EHIRegion ehiRegion);

        void setRegionList(List<EHIRegion> regionList);

        List<EHIRegion> getRegionList();
    }
}

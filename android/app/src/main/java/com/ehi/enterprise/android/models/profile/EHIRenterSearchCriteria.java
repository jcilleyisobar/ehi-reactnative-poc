package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.google.gson.annotations.SerializedName;

public class EHIRenterSearchCriteria extends EHIModel {

    @SerializedName("country_subdivision")
    private String mCountrySubdivision;

    @SerializedName("country")
    private String mCountry;

    @SerializedName("date_of_birth")
    private String mDateOfBirth;

    @SerializedName("driver_license_number")
    private String mDriverLicenseNumber;

    @SerializedName("last_name")
    private String mLastName;

    @SerializedName("issuing_authority")
    private String mIssuingAuthority;

    public EHIRenterSearchCriteria( String country,
                                    String countrySubdivision,
                                    String issuingAuthority,
                                    String driverLicenseNumber,
                                    String lastName){
        mCountry = country;
        if (!EHITextUtils.isEmpty(countrySubdivision)) {
            mCountrySubdivision = countrySubdivision;
        }
        if (!EHITextUtils.isEmpty(issuingAuthority)) {
            mIssuingAuthority = issuingAuthority;
        }
        mDriverLicenseNumber = driverLicenseNumber;
        mLastName = lastName;
    }
}

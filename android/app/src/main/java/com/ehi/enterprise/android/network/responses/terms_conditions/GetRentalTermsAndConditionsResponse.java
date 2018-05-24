package com.ehi.enterprise.android.network.responses.terms_conditions;

import com.ehi.enterprise.android.models.terms_conditions.EHIRentalTermsAndConditions;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetRentalTermsAndConditionsResponse extends BaseResponse {

    @SerializedName("rental_terms_and_conditions")
    private List<EHIRentalTermsAndConditions> mEHIRentalTermsAndConditions;

    public List<EHIRentalTermsAndConditions> getEHIRentalTermsAndConditions() {
        return mEHIRentalTermsAndConditions;
    }

}
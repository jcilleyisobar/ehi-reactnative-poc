package com.ehi.enterprise.android.models.terms_conditions;


import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

public class EHIRentalTermsAndConditions extends EHIModel {

    @SerializedName("rental_terms_and_conditions_text")
    private String mRentalTermsAndConditionsText;

    @SerializedName("locale")
    private String mLocale;

    @SerializedName("locale_label")
    private String mLocaleLabel;

    public String getRentalTermsAndConditionsText() {
        return mRentalTermsAndConditionsText;
    }

    public String getLocale() {
        return mLocale;
    }

    public String getLocaleLabel() {
        return mLocaleLabel;
    }

}
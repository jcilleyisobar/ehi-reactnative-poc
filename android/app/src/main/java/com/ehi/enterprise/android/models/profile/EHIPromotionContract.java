package com.ehi.enterprise.android.models.profile;

import android.support.annotation.StringDef;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EHIPromotionContract extends EHIModel {

    @SerializedName("contract_number")
    private String contractNumber;

    @SerializedName("contract_type")
    private String contractType;

    @SerializedName("contract_sub_type")
    private String contractSubType;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            WEEKEND_SPECIAL, MON_THURS_START_END, SATURDAY_NIGHT_STAY, LAST_MINUTE_SPECIAL
    })
    public @interface PromotionType {}
    private static final String WEEKEND_SPECIAL = "WEEKEND_SPECIAL";
    private static final String MON_THURS_START_END = "MON_THURS_START_END";
    private static final String SATURDAY_NIGHT_STAY = "SATURDAY_NIGHT_STAY";
    private static final String LAST_MINUTE_SPECIAL = "LMS" ;


    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getContractSubType() {
        return contractSubType;
    }

    public void setContractSubType(String contractSubType) {
        this.contractSubType = contractSubType;
    }

    public boolean isWeekendSpecial(){
        return WEEKEND_SPECIAL.equalsIgnoreCase(this.contractSubType);
    }

}

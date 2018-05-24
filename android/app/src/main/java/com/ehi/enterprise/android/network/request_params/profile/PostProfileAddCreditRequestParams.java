package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.profile.EHICreditCard;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.google.gson.annotations.SerializedName;

public class PostProfileAddCreditRequestParams {

    @SerializedName("payment_method")
    private EHIPaymentMethod mCard;

    public PostProfileAddCreditRequestParams(String referenceId, EHICreditCard card) {
        mCard = new EHIPaymentMethod(referenceId, card);
    }
}

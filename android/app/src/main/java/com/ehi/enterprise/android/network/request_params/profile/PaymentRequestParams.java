package com.ehi.enterprise.android.network.request_params.profile;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.google.gson.annotations.SerializedName;

public class PaymentRequestParams {

    @SerializedName("payment_method")
    private EHIPaymentMethod paymentMethod;

    public PaymentRequestParams(EHIPaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}

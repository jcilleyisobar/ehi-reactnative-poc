package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentResponse extends BaseResponse {

    @SerializedName("payment_methods")
    private List<EHIPaymentMethod> paymentMethods;

    public List<EHIPaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }
}

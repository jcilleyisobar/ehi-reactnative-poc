package com.ehi.enterprise.android.network.responses.profile;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentProfileResponse extends BaseResponse {

    @SerializedName("payment_profile")
    private EHIPaymentProfile paymentProfile;

    public List<EHIPaymentMethod> getPaymentMethods() {
        return paymentProfile.getAllPaymentMethods();
    }
}

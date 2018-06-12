package com.ehi.enterprise.android.utils.payment.interfaces;

import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;

import java.util.List;

public interface IOnSaveCreditCardCallback {

    void onSuccess(List<EHIPaymentMethod> paymentMethods, String addedPaymentReferenceId);

    void onPaymentReferenceIdObtained(String paymentReferenceId);

    void onFailure(ResponseWrapper response);

    void onPaymentProxyError(String errorMessage);

    void onDebitCardEntered();
}

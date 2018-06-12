package com.ehi.enterprise.android.models.profile;

import com.ehi.enterprise.android.models.EHIModel;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class EHIPaymentProfile extends EHIModel {

    @SerializedName("payment_methods")
    private List<EHIPaymentMethod> mPaymentMethods;

    public EHIPaymentProfile() {
    }

    public EHIPaymentProfile(List<EHIPaymentMethod> paymentMethods) {
        mPaymentMethods = paymentMethods;
    }

    public List<EHIPaymentMethod> getAllPaymentMethods() {
        return mPaymentMethods;
    }

    public List<EHIPaymentMethod> getBillingPaymentMethods() {
        List<EHIPaymentMethod> billing = new LinkedList<>();
        if (mPaymentMethods != null
                && mPaymentMethods.size() > 0) {
            for (EHIPaymentMethod method : mPaymentMethods) {
                if (!method.getPaymentType().equalsIgnoreCase(EHIPaymentMethod.TYPE_CREDIT_CARD)) {
                    //TODO R1.1update this statement when you gona know exact billing code type value
                    billing.add(method);
                }
            }
        }
        return billing;
    }

    public List<EHIPaymentMethod> getCardPaymentMethods() {
        List<EHIPaymentMethod> cards = new LinkedList<>();
        if (mPaymentMethods != null
                && mPaymentMethods.size() > 0) {
            for (EHIPaymentMethod method : mPaymentMethods) {
                if (method.getPaymentType().equalsIgnoreCase(EHIPaymentMethod.TYPE_CREDIT_CARD)) {
                    cards.add(method);
                }
            }
        }
        return cards;
    }

    public void setPaymentMethods(List<EHIPaymentMethod> paymentMethods) {
        mPaymentMethods = paymentMethods;
    }

    public EHIPaymentMethod getPreferred(){
        for (EHIPaymentMethod method : mPaymentMethods) {
            if (method.isPreferred()) {
                return method;
            }
        }
        return null;
    }
}

package com.ehi.enterprise.android.network.services;

public class EHINetworkFailure extends IllegalStateException {

    public EHINetworkFailure(String detailMessage) {
        super(detailMessage);
    }
}

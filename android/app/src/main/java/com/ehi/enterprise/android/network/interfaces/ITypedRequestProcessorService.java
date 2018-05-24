package com.ehi.enterprise.android.network.interfaces;

import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;

public interface ITypedRequestProcessorService {
    <R> void performRequest(AbstractRequestProvider<R> requestProvider, IApiCallback<R> callback);
}

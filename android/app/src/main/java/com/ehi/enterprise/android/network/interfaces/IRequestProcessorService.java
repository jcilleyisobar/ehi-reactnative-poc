package com.ehi.enterprise.android.network.interfaces;

import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;

public interface IRequestProcessorService {

	void performRequest(AbstractRequestProvider requestProvider, IApiCallback callback);
}

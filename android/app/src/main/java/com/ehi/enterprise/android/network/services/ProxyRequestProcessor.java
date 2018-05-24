package com.ehi.enterprise.android.network.services;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;

import java.util.LinkedList;

/**
 * This class helps to sole problem with dead callback object. Object can be collected by GC
 * since only weak reference is passed to real executor service.
 */
public class ProxyRequestProcessor implements IRequestProcessorService {


	private LinkedList<IApiCallback> mCallbackList = new LinkedList<>();
	private IRequestProcessorService mRealProcessorService;

	public ProxyRequestProcessor(IRequestProcessorService realProcessorService) {
		mRealProcessorService = realProcessorService;
	}

	@Override
	public void performRequest(AbstractRequestProvider requestProvider, IApiCallback callback) {
		mCallbackList.add(callback);
		mRealProcessorService.performRequest(requestProvider, callback);
	}

	public void clearCallbacks() {
		mCallbackList.clear();
	}

    public IRequestProcessorService getRealProcessorService() {
        return mRealProcessorService;
    }
}

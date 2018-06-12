package com.ehi.enterprise.mock.network;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;

public class MockRequestProcessor implements IRequestProcessorService {

    private Map<Class<? extends AbstractRequestProvider>, ResponseWrapper<?>> mMockedResponses;
    private Map<Class<? extends AbstractRequestProvider>, IApiCallback> mPendingRequestCallbacks;
    private Map<Class<? extends AbstractRequestProvider>, AbstractRequestProvider> mPendingRequests;

    public MockRequestProcessor() {
        mMockedResponses = new HashMap<>(1);
        mPendingRequestCallbacks = new HashMap<>(1);
        mPendingRequests = new HashMap<>(1);
    }

    /**
     * A method that will add a key value pair to the repsonse hashmap
     * @param klass
     * @param mockResponse
     */
    public void addMockResponse(Class<? extends AbstractRequestProvider> klass, ResponseWrapper<?> mockResponse){
        mMockedResponses.put(klass, mockResponse);
    }

    public void addAndExecuteMockResponse(Class<? extends AbstractRequestProvider> klass, ResponseWrapper<?> mockResponse){
        addMockResponse(klass, mockResponse);
        executedPendingMockResponse(klass);
    }

    public void executedPendingMockResponse(Class<? extends AbstractRequestProvider> klass){
        IApiCallback callback = mPendingRequestCallbacks.get(klass);
        ResponseWrapper<?> mockResponse = mMockedResponses.get(klass);
        if(callback != null && mockResponse != null){
            mPendingRequestCallbacks.remove(klass);
            callback.handleResponse(mockResponse);
        }
    }

    @Override
    public void performRequest(AbstractRequestProvider requestProvider, IApiCallback callback) {
        ResponseWrapper<?> response = mMockedResponses.get(requestProvider.getClass());
        if(response != null) {
            callback.handleResponse(response);
        }
        else{
            mPendingRequestCallbacks.put(requestProvider.getClass(), callback);
            mPendingRequests.put(requestProvider.getClass(), requestProvider);
        }
    }

    public void assertHasRequest(Class<? extends AbstractRequestProvider> klass){
        Assert.assertTrue(mPendingRequestCallbacks.containsKey(klass));
    }

    public Map<Class<? extends AbstractRequestProvider>, ResponseWrapper<?>> getMockedResponses() {
        return mMockedResponses;
    }

    public Map<Class<? extends AbstractRequestProvider>, IApiCallback> getPendingRequestCallbacks() {
        return mPendingRequestCallbacks;
    }

    public Map<Class<? extends AbstractRequestProvider>, AbstractRequestProvider> getPendingRequests() {
        return mPendingRequests;
    }
}

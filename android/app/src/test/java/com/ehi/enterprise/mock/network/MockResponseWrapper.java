package com.ehi.enterprise.mock.network;

import com.ehi.enterprise.android.network.responses.ResponseWrapper;

public class MockResponseWrapper<T> extends ResponseWrapper<T> {

    public MockResponseWrapper(T result, boolean success, String message){
        setData(result);
        setStatus(success ? OK : GENERIC_ERROR);
        setMessage(message);
    }

}

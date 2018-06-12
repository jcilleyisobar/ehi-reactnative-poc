package com.ehi.enterprise.android.network.interfaces;

import com.ehi.enterprise.android.network.responses.ResponseWrapper;

public interface IApiCallback<T> {

	void handleResponse(ResponseWrapper<T> response);
}

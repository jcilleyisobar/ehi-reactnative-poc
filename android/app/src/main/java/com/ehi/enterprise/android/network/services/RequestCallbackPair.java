package com.ehi.enterprise.android.network.services;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;

import java.lang.ref.WeakReference;

public class RequestCallbackPair {

	public final AbstractRequestProvider provider;
	public final WeakReference<IApiCallback> callback;

	public RequestCallbackPair(AbstractRequestProvider provider, IApiCallback callback) {
		this.provider = provider;
		this.callback = new WeakReference<>(callback);
	}
}

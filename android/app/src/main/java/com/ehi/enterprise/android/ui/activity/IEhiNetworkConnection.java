package com.ehi.enterprise.android.ui.activity;

import com.ehi.enterprise.android.network.interfaces.IApiServiceListener;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;

public interface IEhiNetworkConnection {
    IRequestProcessorService getApiService();

    boolean isApiServiceConnected();

    void addServiceConnectionObservable(IApiServiceListener apiUserDelegate);
}

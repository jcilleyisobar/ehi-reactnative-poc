package com.ehi.enterprise.android.ui.viewmodel;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;

public class ViewViewModel extends BaseViewModel {

    @Override
    public void performRequest(AbstractRequestProvider requestProvider, IApiCallback callback) {
        throw new IllegalStateException("Performing requests from a custom view viewmodel is not permitted");
    }
}

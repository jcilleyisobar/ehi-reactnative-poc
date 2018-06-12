package com.ehi.enterprise.android.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.interfaces.IApiServiceListener;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.network.services.EnterpriseNetworkService;
import com.ehi.enterprise.android.network.services.ProxyRequestProcessor;
import com.ehi.enterprise.android.utils.BaseAppUtils;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public abstract class ApiServiceActivity extends EHIBaseActivity implements IEhiNetworkConnection {

    private static final String TAG = "ApiServiceActivity";

    private ProxyRequestProcessor mApiService;
    private boolean mBound = false;
    private ServiceConnection mApiServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            EnterpriseNetworkService.ApiServiceBinder binder = (EnterpriseNetworkService.ApiServiceBinder) service;
            mApiService = new ProxyRequestProcessor(binder.getService());
            mBound = true;
            onApiServiceConnected();
            notifyObservers();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mApiService = null;
        }
    };
    private LinkedList<WeakReference<IApiServiceListener>> mObserversList = new LinkedList<>();

    private void notifyObservers() {
        for (WeakReference<IApiServiceListener> weakUser : mObserversList) {
            IApiServiceListener user = weakUser.get();
            if (user != null) {
                user.onApiServiceConnected(mApiService);
            }
        }
        mObserversList.clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindApiService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        BaseAppUtils.hideKeyboard(this);
        if (mBound) {
            unbindService(mApiServiceConnection);
            mApiService.clearCallbacks();
            mApiService = null;
            mBound = false;
        }
    }

    protected void onApiServiceConnected() {
    }

    @Override
    public IRequestProcessorService getApiService() {
        return mApiService;
    }

    @Override
    public boolean isApiServiceConnected() {
        return mApiService != null;
    }

    private void bindApiService() {
        startService(new Intent(this, Settings.API_SERVICE));
        Intent intent = new Intent(this, Settings.API_SERVICE);
        bindService(intent, mApiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void addServiceConnectionObservable(IApiServiceListener apiUserDelegate) {
        mObserversList.add(new WeakReference<>(apiUserDelegate));
    }

}

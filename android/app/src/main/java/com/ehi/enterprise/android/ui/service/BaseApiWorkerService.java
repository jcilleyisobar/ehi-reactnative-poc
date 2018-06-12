package com.ehi.enterprise.android.ui.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.interfaces.IApiServiceListener;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.network.services.EnterpriseNetworkService;
import com.ehi.enterprise.android.network.services.ProxyRequestProcessor;
import com.ehi.enterprise.android.ui.activity.IEhiNetworkConnection;

public abstract class BaseApiWorkerService extends Service
        implements IEhiNetworkConnection, ServiceConnection {

    public static final int SERVICE_LIFE_LENGTH = 300000;
    private ProxyRequestProcessor mApiService;
    private boolean mBound = false;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Message mMessage;
    private boolean mMessageSent;
    private ServiceBinder mBinder = new ServiceBinder();

    public BaseApiWorkerService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(getTag());
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        bindApiService();

        mServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSelf();
            }
        }, SERVICE_LIFE_LENGTH);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBound){
            unbindService(this);
            mApiService.clearCallbacks();
            mBound = false;
        }

        mServiceLooper.quit();
    }

    @Override
    public IRequestProcessorService getApiService() {
        return mApiService;
    }

    @Override
    public boolean isApiServiceConnected() {
        return mBound;
    }

    @Override
    public void addServiceConnectionObservable(final IApiServiceListener apiUserDelegate) {

    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        EnterpriseNetworkService.ApiServiceBinder binder = (EnterpriseNetworkService.ApiServiceBinder) service;
        mApiService = new ProxyRequestProcessor(binder.getService());
        onApiServiceConnected();
        mBound = true;
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        mBound = false;
        onApiServiceDisconnected();
    }

    private void bindApiService() {
        startService(new Intent(this, Settings.API_SERVICE));
        Intent intent = new Intent(this, Settings.API_SERVICE);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    protected void onApiServiceConnected(){
        if(!mMessageSent && mMessage != null){
            mServiceHandler.sendMessage(mMessage);
            mMessageSent = true;
        }
    }

    protected void onApiServiceDisconnected(){

    }

    protected final void sendMessage(Object object){
        mMessage = mServiceHandler.obtainMessage();
        mMessage.obj = object;
        if(isApiServiceConnected()) {
            mServiceHandler.sendMessage(mMessage);
            mMessageSent = true;
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(final Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            onMessage(msg);
        }
    }

    private final class ServiceBinder extends Binder {
        public ServiceBinder getService() {
            return ServiceBinder.this;
        }
    }

    protected abstract String getTag();
    protected abstract void onMessage(Message message);
}

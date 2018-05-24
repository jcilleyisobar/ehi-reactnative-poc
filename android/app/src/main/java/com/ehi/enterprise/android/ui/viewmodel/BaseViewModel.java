package com.ehi.enterprise.android.ui.viewmodel;

import android.content.res.Resources;
import android.support.annotation.CallSuper;
import android.support.v4.util.Pair;

import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.interfaces.IRequestProcessorService;
import com.ehi.enterprise.android.network.interfaces.ITypedRequestProcessorService;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;

import java.util.LinkedList;
import java.util.Queue;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.ReactorUnbinder;
import io.dwak.reactor.unbinder.annotation.AutoUnbind;

public class BaseViewModel implements ITypedRequestProcessorService {

    private IRequestProcessorService mApiService;
    private Queue<Pair<AbstractRequestProvider<?>, IApiCallback<?>>> mRequestQueue = new LinkedList<>();
    private boolean mAttachedToView = false;
    //Child viewmodels should use {@link #showProgress}, do not mutate this value directly
    @AutoUnbind
    public final ReactorVar<Boolean> progress = new ReactorVar<>(false);
    //Child viewmodels should use {@link #setError}, do not mutate this value directly
    @AutoUnbind
    public final ReactorVar<ResponseWrapper> errorResponse = new ReactorVar<>();
    @AutoUnbind
    public final ReactorVar<Boolean> mIsWeekendSpecialContractRequestDone = new ReactorVar<>(false);

    private Resources mResources;

    public void setResources(Resources resources) {
        mResources = resources;
    }

    public Resources getResources() {
        return mResources;
    }

    @CallSuper
    public void prepareToAttachToView() {
    }

    @CallSuper
    public void onAttachToView() {
        mAttachedToView = true;
    }

    /**
     * When implementing this, call super last
     * It's necessary to NULL out all Reactor dependencies here
     */
    @CallSuper
    public void onDetachFromView() {
        ReactorUnbinder.unbind(this);
        mAttachedToView = false;
    }

    public void onApiServiceConnected() {
        while (!mRequestQueue.isEmpty()) {
            Pair<AbstractRequestProvider<?>, IApiCallback<?>> apiCallbackPair = mRequestQueue.poll();
            mApiService.performRequest(apiCallbackPair.first, apiCallbackPair.second);
        }
    }

    protected boolean isApiServiceConnected() {
        return mApiService != null;
    }

    @Override
    public <R> void performRequest(AbstractRequestProvider<R> requestProvider, IApiCallback<R> callback) {
        if (isApiServiceConnected()) {
            mApiService.performRequest(requestProvider, callback);
        } else {
            mRequestQueue.add(new Pair<AbstractRequestProvider<?>, IApiCallback<?>>(requestProvider, callback));
        }
    }

    public void setApiService(IRequestProcessorService apiService) {
        mApiService = apiService;
    }

    protected boolean isAttached() {
        return mAttachedToView;
    }

    protected void showProgress(final boolean show) {
        progress.setValue(show);
    }

    protected void setError(final ResponseWrapper error) {
        errorResponse.setValue(error);
    }
}

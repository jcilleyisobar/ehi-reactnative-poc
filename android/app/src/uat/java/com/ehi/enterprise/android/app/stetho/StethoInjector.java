package com.ehi.enterprise.android.app.stetho;

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.Interceptor;

public class StethoInjector {
    private static final String TAG = "StethoInjector";
    public static void injectStetho(final Application application) {
        Log.d(TAG, "injectStetho() called with: " + "application = [" + application + "]");
        Stetho.initializeWithDefaults(application);
    }

    public static Interceptor getStethoNetworkInterceptor() {
        Log.d(TAG, "getStethoNetworkInterceptor() called with: " + "");
        return new StethoInterceptor();
    }
}

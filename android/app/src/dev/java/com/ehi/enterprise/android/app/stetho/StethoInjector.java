package com.ehi.enterprise.android.app.stetho;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.Interceptor;


public class StethoInjector {
    public static void injectStetho(final Application application) {
        Stetho.initializeWithDefaults(application);
    }

    public static Interceptor getStethoNetworkInterceptor() {
        return new StethoInterceptor();
    }

}

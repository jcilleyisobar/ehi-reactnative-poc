package com.ehi.enterprise.android.app.stetho;

import android.app.Application;

import okhttp3.Interceptor;

public class StethoInjector {
    public static void injectStetho(final Application application) {
    }

    public static Interceptor getStethoNetworkInterceptor() {
        return null;
    }
}

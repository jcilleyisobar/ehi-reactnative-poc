package com.ehi.enterprise.android.network.cookies;

import android.support.annotation.NonNull;

import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {

    private static final String GBO_REGION = "gbo_region";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        final List<String> cookies = originalResponse.headers("Set-Cookie");
        if (cookies != null) {
            for (String header : cookies) {
                if (header.contains(GBO_REGION)) {
                    LocalDataManager.getInstance().setGboRegionCookie(header);
                    return originalResponse;
                }
            }
        }
        // in case we didn't get the region cookie back we need to erase previous saved data
        if (isGboHost(originalResponse.request().url().toString())) {
            LocalDataManager.getInstance().setGboRegionCookie(null);
        }
        return originalResponse;
    }

    private boolean isGboHost(String url) {
        return url.contains(Settings.EHI_GBO_RENTAL) ||
                url.contains(Settings.EHI_GBO_PROFILE) ||
                url.contains(Settings.EHI_GBO_LOCATION);
    }
}

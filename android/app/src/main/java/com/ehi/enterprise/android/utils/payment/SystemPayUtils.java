package com.ehi.enterprise.android.utils.payment;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import io.dwak.reactor.BuildConfig;

public class SystemPayUtils {

    private static final String BILLING_IAP_PACKAGE_NAME = "com.sec.android.app.billing";

    public static void checkIfAndroidPayIsEnabled(FragmentActivity activity) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_PRODUCTION).build())
                .enableAutoManage(activity, null)
                .build();

        Wallet.Payments.isReadyToPay(googleApiClient, IsReadyToPayRequest.newBuilder().build())
                .setResultCallback(
                        new ResultCallback<BooleanResult>() {
                            @Override
                            public void onResult(@NonNull BooleanResult booleanResult) {
                                if (booleanResult.getStatus().isSuccess()) {
                                    if (booleanResult.getValue()) {
                                        LocalDataManager.getInstance().setHasPaySystem(true);
                                    }
                                }
                            }
                        });
    }

    // sample code from http://developer.samsung.com/iap#samples
    public static void checkIfSamsungPayIsEnabled(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getApplicationInfo(BILLING_IAP_PACKAGE_NAME,
                    PackageManager.GET_META_DATA);

            PackageInfo packageInfo = pm.getPackageInfo(BILLING_IAP_PACKAGE_NAME, PackageManager.GET_META_DATA);
            if (packageInfo.versionCode >= 400000000) {
                LocalDataManager.getInstance().setHasPaySystem(true);
            }
        } catch (PackageManager.NameNotFoundException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }
}

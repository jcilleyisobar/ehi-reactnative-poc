package com.ehi.enterprise.android.ui.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.services.EnterpriseNetworkService;
import com.ehi.enterprise.android.network.services.ProxyRequestProcessor;
import com.ehi.enterprise.android.ui.activity.ViewModelActivity;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.isobar.android.viewmodel.ViewModel;

import java.lang.ref.WeakReference;

@ViewModel(ManagersAccessViewModel.class)
public class AlertCallUsContinueWrapperActivity extends ViewModelActivity<ManagersAccessViewModel> {

    public static final String ALERT_TITLE = "ALERT_TITLE";
    public static final String ALERT_MESSAGE = "ALERT_MESSAGE";
    public static final String ALERT_POSITIVE_BUTTON = "ALERT_POSITIVE_BUTTON";
    public static final String ALERT_NEGATIVE_BUTTON = "ALERT_NEGATIVE_BUTTON";
    public static final String RESPONSE_WRAPPER = "RESPONSE_WRAPPER";

    public static Intent getIntent(Context context, String alertMessage) {
        Intent intent = new Intent(context, AlertCallUsContinueWrapperActivity.class);
        intent.putExtra(ALERT_TITLE, R.string.alert_service_error_title);
        intent.putExtra(ALERT_MESSAGE, alertMessage);
        intent.putExtra(ALERT_POSITIVE_BUTTON, context.getResources().getString(R.string.alert_service_error_callus));
        intent.putExtra(ALERT_NEGATIVE_BUTTON, context.getResources().getString(R.string.alert_service_error_continue));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_alert_wrapper);
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        final String alertTitle = args.getString(ALERT_TITLE, "");
        final String alertMessage = args.getString(ALERT_MESSAGE, "");
        final String alertPositiveButton = args.getString(ALERT_POSITIVE_BUTTON, "");
        final String alertNegativeButton = args.getString(ALERT_NEGATIVE_BUTTON, "");

        new AlertDialog.Builder(this)
                .setMessage(alertMessage)
                .setTitle(alertTitle)
                .setPositiveButton(alertPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IntentUtils.callNumber(AlertCallUsContinueWrapperActivity.this,
                                getViewModel().getSupportPhoneNumber());
                    }
                })
                .setNegativeButton(alertNegativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final EnterpriseNetworkService apiService = (EnterpriseNetworkService) ((ProxyRequestProcessor) getApiService()).getRealProcessorService();
                        final WeakReference<IApiCallback> callUsContinueCallback = apiService.getCallUsContinueCallback();
                        final ResponseWrapper responseWrapper = apiService.getCallUsContinueResponseWrapper();
                        if (callUsContinueCallback != null && responseWrapper != null) {
                            callUsContinueCallback.get().handleResponse(responseWrapper);
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        AlertCallUsContinueWrapperActivity.this.finish();
                    }
                })
                .show();

    }
}

package com.ehi.enterprise.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.services.EHIServicesError;
import com.ehi.enterprise.android.utils.manager.SupportInfoManager;

import java.util.Calendar;
import java.util.Date;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public final class DialogUtils {
    public static final String TAG = "DialogUtils";

    /**
     * Allows binding a {@link ReactorVar} directly to display the error dialog
     *
     * @param source {@link ResponseWrapper} wrapped in a {@link ReactorVar} that contains the error response from the network
     * @param target Context from which to display the dialog
     * @return {@link ReactorComputationFunction} to use in {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see #showErrorDialog(Context, ResponseWrapper)
     */
    public static ReactorComputationFunction errorDialog(@NonNull final ReactorVar<ResponseWrapper> source, @NonNull final Context target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getValue() != null) {
                    showErrorDialog(target, source.getValue());
                    source.setValue(null);
                }
            }
        };
    }

    /**
     * Display an error dialog when an unexpected response occurs
     *
     * @param context       Context to display in
     * @param errorResponse Response object that contains the error
     */
    public static void showErrorDialog(final Context context, ResponseWrapper errorResponse) {
        if (context == null) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (EHIServicesError.ErrorCode.INVALID_API_KEY.equals(errorResponse.getErrorCode())) {
            builder.setMessage(R.string.alert_service_error_invalid_api_key_message)
                    .setPositiveButton(context.getString(R.string.alert_service_error_update), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IntentUtils.openUrlViaExternalApp(context, Settings.FORCE_UPGRADE_URL);
                        }
                    })
                    .setCancelable(false);

        } else if (errorResponse.getDisplayAs() != null) {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")
                    || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
                builder.setMessage(errorResponse.getCodes() + ":" + errorResponse.getMessage());
            } else {
                builder.setMessage(errorResponse.getMessage());
            }
            switch (errorResponse.getDisplayAs()) {
                case ALERT:
                    builder.setTitle(R.string.alert_service_error_title)
                            .setPositiveButton(R.string.alert_service_error_okay, null);
                    break;
                case CALL_US:
                    builder.setTitle(R.string.alert_service_error_callus)
                            .setPositiveButton(R.string.alert_service_error_callus, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    IntentUtils.callNumber(context, SupportInfoManager.getInstance().getContactUsPhoneNumberForCurrentLocale());
                                }
                            })
                            .setNegativeButton(R.string.alert_service_error_cancel, null);
                    break;
                case CALL_US_CONTINUE:
                    builder.setTitle(R.string.alert_service_error_callus)
                            .setPositiveButton(R.string.alert_service_error_callus, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    IntentUtils.callNumber(context, SupportInfoManager.getInstance().getContactUsPhoneNumberForCurrentLocale());
                                }
                            })
                            .setNegativeButton(R.string.alert_service_error_continue, null);
                    break;
                case SILENT:
                    return;
                default:
                    builder.setMessage(errorResponse.getMessage())
                            .setTitle(R.string.alert_service_error_title)
                            .setPositiveButton(context.getString(R.string.alert_okay_title), null);
            }
        } else {
            if (BuildConfig.FLAVOR.equalsIgnoreCase("dev")
                    || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
                String errorCodes = errorResponse.getCodes();
                //noinspection ResourceType
                if (TextUtils.isEmpty(errorCodes)) {
                    errorCodes = errorResponse.getStatus() + "";
                }
                builder.setMessage(errorCodes + ":" + errorResponse.getMessage());
            } else {
                builder.setMessage(errorResponse.getMessage());
            }
            builder.setTitle(R.string.alert_service_error_title)
                    .setPositiveButton(context.getString(R.string.alert_okay_title), null);
        }
        if ("dev".equals(BuildConfig.FLAVOR)) {
            DLog.d(TAG + "#showErrorDialog", Thread.currentThread().getStackTrace()[3].toString());
        }
        builder.show();
    }

    public static void showDialogWithTitleAndText(@NonNull Context context, @NonNull String text, @NonNull String title) {
        new AlertDialog.Builder(context)
                .setMessage(text)
                .setTitle(title)
                .setPositiveButton(context.getString(R.string.alert_okay_title), null)
                .create()
                .show();
    }

    public static void showOkCancelDialog(@NonNull Context context, @NonNull String title, @NonNull String text, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setMessage(text)
                .setTitle(title)
                .setPositiveButton(context.getString(R.string.alert_okay_title), listener)
                .setNegativeButton(context.getString(R.string.alert_cancel_title), null)
                .create()
                .show();
    }

    public static void showDialogLongTitle(@NonNull Context context, @NonNull String title, @NonNull String message, int yesRid, int noRid, DialogInterface.OnClickListener listener) {
        View textView = LayoutInflater.from(context).inflate(R.layout.v_right_to_be_forgotten_dialog_title, null);
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCustomTitle(textView)
                .setPositiveButton(context.getString(yesRid), listener)
                .setNegativeButton(context.getString(noRid), listener)
                .create()
                .show();
    }

    public static ReactorComputationFunction bindMessage(final ReactorVar<Integer> source, final Context context) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                Integer res = source.getValue();
                if (res != null) {
                    new AlertDialog.Builder(context)
                            .setMessage(context.getResources().getString(source.getValue()))
                            .setPositiveButton(context.getString(R.string.alert_okay_title), null)
                            .create()
                            .show();
                    source.setValue(null);
                }
            }
        };
    }

    public static void showDatePicker(Context context, @StringRes int title, Date initialDate, final OnDateSelectedListener onDateSelectedListener) {
        if (initialDate == null) {
            initialDate = new Date(System.currentTimeMillis());
        }

        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);


        EHIDatePickerDialog dialog = new EHIDatePickerDialog(
                context,
                R.style.DatePickerDialogCustom,
                new EHIDatePickerDialog.OnDateSetListener() {
                    private boolean mFired = false;

                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        try {
                            if (!mFired) {
                                calendar.set(i, i2, i3);
                                onDateSelectedListener.onDateSelected(calendar.getTime());
                                mFired = true;
                            }
                        } catch (Exception e) {
                            DLog.w(TAG, e);
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dialog.setTitle(title);
        dialog.show();
    }


    public static void showDiscardReservationDialog(final Activity context, DialogInterface.OnClickListener positiveClick, DialogInterface.OnClickListener negativeClick) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.reservation_cancel_discard_confirm_message)
                .setPositiveButton(R.string.reservation_cancel_discard_confirm_button, positiveClick)
                .setNegativeButton(R.string.reservation_cancel_discard_return_button, negativeClick)
                .show();
    }


    public interface OnDateSelectedListener {
        void onDateSelected(Date selectedDate);
    }
}

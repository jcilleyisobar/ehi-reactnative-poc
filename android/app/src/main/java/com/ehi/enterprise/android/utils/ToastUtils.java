package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

public final class ToastUtils {

    /**
     * Allows binding to the toast util using the provided string
     * @param source IntegerRes string message
     * @param target Context to display in
     * @return {@link ReactorComputationFunction} to use with {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see #showToast(Context, CharSequence)
     */
    public static ReactorComputationFunction toastRes(final ReactorVar<Integer> source, final Context target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getValue() != null) {
                    showToast(target, source.getValue());
                    source.setValue(null);
                }
            }
        };
    }
    /**
     * Allows binding to the toast util using the provided string
     * @param source Boolean true to show the toast
     * @param target Context to display in
     * @param message String to display
     * @return {@link ReactorComputationFunction} to use with {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see #showToast(Context, CharSequence)
     */
    public static ReactorComputationFunction toast(final ReactorVar<Boolean> source, final Context target, final @StringRes int message) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getValue()) {
                    showToast(target, message);
                }
            }
        };
    }

    /**
     * Allows binding to the toast util using the provided string
     * @param source Boolean true to show the toast
     * @param target Context to display in
     * @param message String to display
     * @return {@link ReactorComputationFunction} to use with {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see #showLongToast(Context, int)
     */
    public static ReactorComputationFunction longToast(final ReactorVar<Boolean> source, final Context target, final @StringRes int message) {
        return new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (source.getValue()) {
                    showLongToast(target, message);
                }
            }
        };
    }

    /**
     * Allows binding to the toast util
     * @param source String message
     * @param target Context to display in
     * @return {@link ReactorComputationFunction} to use with {@link io.dwak.reactor.Reactor#autoRun(ReactorComputationFunction)}
     * @see #showToast(Context, CharSequence)
     */
    public static ReactorComputationFunction toast(final ReactorVar<String> source, final Context target) {
        return new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (!TextUtils.isEmpty(source.getValue())) {
                    showToast(target, source.getValue());
                    source.setValue(null);
                }
            }
        };
    }

    /**
     * Shows a short toast message
     * @param context Context to show the toast from
     * @param message message to display
     * @see Toast#makeText(Context, CharSequence, int)
     */
    public static void showToast(Context context, @StringRes int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * @see #showToast(Context, int)
     * @see Toast#makeText(Context, CharSequence, int)
     */
    public static void showToast(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Shows a long toast message
     */
    public static void showLongToast(Context context, @StringRes int message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a long toast message
     */
    public static void showLongToast(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

}

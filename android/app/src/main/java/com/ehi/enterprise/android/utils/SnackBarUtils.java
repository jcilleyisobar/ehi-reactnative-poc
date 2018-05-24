package com.ehi.enterprise.android.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.ehi.enterprise.android.R;

public class SnackBarUtils {
    public static void showLocationPermissionSnackBar(final Context context, View view){
        Snackbar.make(view, R.string.snackbar_location_disabled_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(context, R.color.ehi_primary))
                .setAction(R.string.snackbar_location_disable_action, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        IntentUtils.goToAppSettings(context);
                    }
                })
                .show();
    }
}

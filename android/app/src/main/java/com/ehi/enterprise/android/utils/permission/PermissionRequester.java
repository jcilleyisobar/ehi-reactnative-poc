package com.ehi.enterprise.android.utils.permission;

import android.support.annotation.NonNull;

public interface PermissionRequester {
    void onRequestPermissionResult(final int requestCode,
                                   @NonNull final String[] permissions,
                                   @NonNull final int[] grantResults);
}

package com.ehi.enterprise.android.utils.permission;

public interface PermissionRequestHandler {
    void requestPermissions(final int permissionRequestCode,
                            final PermissionRequester requester,
                            String... permissions);
}

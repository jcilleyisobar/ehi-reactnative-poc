package com.ehi.enterprise.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public final class PermissionUtils {

    /**
     * This allows checking of multiple permissions and creating a single list of permissions that are not granted
     * @param context Context to use for checking permissions
     * @param permissions Permissions to request
     * @return {@link List} of the permissions that haven't been granted
     */
    public static List<String> checkPermissions(Context context, String... permissions){
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                permissionsToRequest.add(permission);
            }
        }

        return permissionsToRequest;
    }

    /**
     * Use this to request all permissions that haven't been granted. As returned from {@link #checkPermissions(Context, String...)}
     * @param activity Activity from which to request these permissions
     * @param permissions Permissions returned from {@link #checkPermissions(Context, String...)}
     * @param requestCode Request code to callback to
     */
    public static void requestCheckedPermissions(Activity activity, List<String> permissions, int requestCode){
        String[] permissionArray = new String[]{};
        ActivityCompat.requestPermissions(activity, permissions.toArray(permissionArray), requestCode);
    }

    /**
     * Check that all expected permissions have been granted
     * @param grantResults grant results returned from {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     * @return true if all permissions have been granted
     */
    public static boolean areAllPermissionsGranted(final int[] grantResults){
        boolean allPermissionsGranted = true;

        for (int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED){
                allPermissionsGranted = false;
                break;
            }
        }

        return allPermissionsGranted;
    }
}

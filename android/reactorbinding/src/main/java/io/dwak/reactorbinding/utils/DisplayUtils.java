package io.dwak.reactorbinding.utils;

import android.content.Context;
import android.util.TypedValue;

public class DisplayUtils {

    public static float dipToPx(Context context, float dipValue) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                context.getResources().getDisplayMetrics());
    }
}

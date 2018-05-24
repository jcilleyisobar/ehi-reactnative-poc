package com.ehi.enterprise.android.utils;


import android.support.annotation.Nullable;

public class EHITextUtils {

    public static final String MASK_CHAR = "•";

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns true if any of the parameters are null or empty
     *
     * @param str varargs of strings to check
     * @return true if any of the parameters are null or empty
     */
    public static boolean isEmpty(CharSequence... str) {
        boolean isEmpty = false;
        for (int i = 0; i < str.length; i++) {
            isEmpty = isEmpty(str[i]);
        }

        return isEmpty;
    }

    public static boolean areAllEmpty(CharSequence... str) {
        for (int i = 0; i < str.length; i++) {
            if (!isEmpty(str[i])) {
                return false;
            }
        }
        return true;
    }

    public static String getFirstN(String value, int n) {
        return value.substring(0, n);
    }

    public static String getLastN(String value, int n) {
        int stringLength = value.length();
        if (stringLength < n) {
            return value;
        }

        return value.substring(stringLength - n);
    }

    public static boolean isMaskedField(String field) {
        return !isEmpty(field) && field.contains(MASK_CHAR);
    }
}

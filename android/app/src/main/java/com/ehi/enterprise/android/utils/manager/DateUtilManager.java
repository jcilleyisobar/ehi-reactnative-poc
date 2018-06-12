package com.ehi.enterprise.android.utils.manager;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import com.ehi.enterprise.android.utils.EHITextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.Locale;

public class DateUtilManager extends BaseDataManager {
    public static final String NAME = "DATE_UTIL_MANAGER";
    private static DateUtilManager sManager;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FORMAT_SHOW_TIME, FORMAT_SHOW_WEEKDAY, FORMAT_NO_YEAR, FORMAT_SHOW_YEAR,
            FORMAT_SHOW_DATE, FORMAT_NO_MONTH_DAY, FORMAT_ABBREV_TIME, FORMAT_ABBREV_WEEKDAY,
            FORMAT_ABBREV_MONTH, FORMAT_NUMERIC_DATE, FORMAT_ABBREV_RELATIVE, FORMAT_ABBREV_ALL})
    public @interface DateFlags {
    }

    public static final int FORMAT_SHOW_TIME = 0x00001;
    public static final int FORMAT_SHOW_WEEKDAY = 0x00002;
    public static final int FORMAT_SHOW_YEAR = 0x00004;
    public static final int FORMAT_NO_YEAR = 0x00008;
    public static final int FORMAT_SHOW_DATE = 0x00010;
    public static final int FORMAT_NO_MONTH_DAY = 0x00020;
    public static final int FORMAT_ABBREV_TIME = 0x04000;
    public static final int FORMAT_ABBREV_WEEKDAY = 0x08000;
    public static final int FORMAT_ABBREV_MONTH = 0x10000;
    public static final int FORMAT_NUMERIC_DATE = 0x20000;
    public static final int FORMAT_ABBREV_RELATIVE = 0x40000;
    public static final int FORMAT_ABBREV_ALL = 0x80000;

    protected DateUtilManager(){}

    @Override
    public void initialize(@NonNull Context context) {
        super.initialize(context);
        sManager = this;
    }

    @NonNull
    public static DateUtilManager getInstance() {
        if (sManager == null) {
            sManager = new DateUtilManager();
        }
        return sManager;
    }

    public String formatDateTime(Date date, int... dateFlags) {
        int dateFlag = 0;
        for (int flag : dateFlags) {
            dateFlag |= flag;
        }
        return DateUtils.formatDateTime(getContext(), date.getTime(), dateFlag);
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String formatMaskedDate(String masked) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), "dMMyyyy");
            return replaceChars(masked, pattern);
        }
        return masked;
    }

    public String replaceChars(String masked, String pattern) {
        final String day = masked.replaceAll("\\D", "");
        return pattern
                .replace("d", day)
                .replace("M", EHITextUtils.MASK_CHAR)
                .replace("y", EHITextUtils.MASK_CHAR);
    }

    @Override
    protected String getSharedPreferencesName() {
        return NAME;
    }
}

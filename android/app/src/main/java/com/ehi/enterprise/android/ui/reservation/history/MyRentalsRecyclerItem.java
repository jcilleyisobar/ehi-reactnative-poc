package com.ehi.enterprise.android.ui.reservation.history;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MyRentalsRecyclerItem<T> {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SELECTOR,
            INFO_MESSAGE,
            PAST_TRIP,
            FOOTER,
            UPCOMING_TRIP,
            CURRENT_TRIP,
            LOAD_MORE,
            EMPTY_CELL,
            MISSING_PAST_RENTAL,
            SPACER})
    public @interface ViewType {
    }

    public static final int SELECTOR = 0;
    public static final int INFO_MESSAGE = 1;
    public static final int PAST_TRIP = 2;
    public static final int FOOTER = 3;
    public static final int UPCOMING_TRIP = 4;
    public static final int CURRENT_TRIP = 5;
    public static final int LOAD_MORE = 6;
    public static final int EMPTY_CELL = 7;
    public static final int MISSING_PAST_RENTAL = 8;
    public static final int SPACER = 9;

    public final T object;
    public final
    @ViewType
    int viewType;

    public MyRentalsRecyclerItem(T object, @ViewType int viewType) {
        this.object = object;
        this.viewType = viewType;
    }


}

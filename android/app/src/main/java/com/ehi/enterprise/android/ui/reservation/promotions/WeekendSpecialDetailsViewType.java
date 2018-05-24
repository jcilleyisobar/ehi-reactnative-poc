package com.ehi.enterprise.android.ui.reservation.promotions;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class WeekendSpecialDetailsViewType {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IMAGE, TITLE, BULLET_ITEM, BUTTON, CLICKABLE_ITEM, DIVIDER})
    public @interface ViewType {
    }

    public static final int IMAGE = 0;
    public static final int TITLE = 1;
    public static final int BULLET_ITEM = 2;
    public static final int BUTTON = 3;
    public static final int CLICKABLE_ITEM = 4;
    public static final int DIVIDER = 5;

    @ViewType
    public abstract int getViewType();

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, PromotionDetailsDatum data);

    public interface PromotionDetailsDatum {
        @ViewType
        int getViewType();
    }
}

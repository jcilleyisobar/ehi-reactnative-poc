package com.ehi.enterprise.android.ui.location;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class FavoriteRecentItem<T> {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({HEADER, ITEM})
    public @interface ViewType {
    }

    public static final int HEADER = 10;
    public static final int ITEM = 11;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FAVORITE, RECENT})
    public @interface ItemType {
    }

    public static final int FAVORITE = 0;
    public static final int RECENT = 1;

    private T mObject;
    private
    @ViewType
    int mViewType;
    private int mItemType;

    public FavoriteRecentItem(T object, @ViewType int viewType) {
        mObject = object;
        mViewType = viewType;
        mItemType = -1;
    }

    public FavoriteRecentItem(T object, @ViewType int viewType, @ItemType int itemType) {
        mObject = object;
        mViewType = viewType;
        mItemType = itemType;
    }

    public T getObject() {
        return mObject;
    }

    @ViewType
    public int getViewType() {
        return mViewType;
    }

    @ItemType
    public int getItemType() {
        return mItemType;
    }
}

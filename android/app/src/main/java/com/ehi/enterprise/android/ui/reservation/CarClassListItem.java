package com.ehi.enterprise.android.ui.reservation;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class CarClassListItem<T> {
	@Retention(RetentionPolicy.SOURCE)
	@IntDef({HEADER, ITEM, FOOTER, INFO_ITEM})
	public @interface ViewType {
	}

	public static final int HEADER = 0;
	public static final int ITEM = 1;
	public static final int FOOTER = 2;
    public static final int INFO_ITEM = 3;

	T mObject;
	@ViewType int mViewType;

	public CarClassListItem(T object, @ViewType int viewType) {
		mObject = object;
		mViewType = viewType;
	}

    public CarClassListItem(@ViewType int viewType){
        mObject = null;
        mViewType = viewType;
    }

	public T getObject() {
		return mObject;
	}

	public @ViewType int getViewType() {
		return mViewType;
	}
}

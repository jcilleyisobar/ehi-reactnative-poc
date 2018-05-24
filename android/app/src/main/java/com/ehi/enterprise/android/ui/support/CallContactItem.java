package com.ehi.enterprise.android.ui.support;

import android.support.annotation.IntDef;

import com.ehi.enterprise.android.models.profile.EHIPhone;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CallContactItem<T> {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({HEADER, MAIN_HEADER, CALL_ITEM, MESSAGE_ITEM, SEARCH_ITEM})
	public @interface ViewType {
	}

	public static final int HEADER = 10;
	public static final int MAIN_HEADER = 11;
	public static final int CALL_ITEM = 12;
	public static final int MESSAGE_ITEM = 13;
	public static final int SEARCH_ITEM = 14;

	private T mObject;
	private @ViewType int mViewType;
	private String mStringUrl;
	private EHIPhone mEhiPhone;
	private String mTitle;
	private String mDesc;

	public CallContactItem(T object, @ViewType int viewType) {
		mObject = object;
		mViewType = viewType;
	}

	public CallContactItem(EHIPhone phone, String title, String desc, @ViewType int viewType) {
		mEhiPhone = phone;
		mTitle = title;
		mDesc = desc;
		mViewType = viewType;
	}

	public CallContactItem(String stringUrl, @ViewType int viewType) {
		mStringUrl = stringUrl;
		mViewType = viewType;
	}

	public T getObject() {
		return mObject;
	}

	public String getStringUrl() {
		return mStringUrl;
	}

	public String getNumber() {
		return mEhiPhone.getPhoneNumber();
	}

	public EHIPhone getPhoneNumber() {
		return mEhiPhone;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getDesc() {
		return mDesc;
	}

	@ViewType
	public int getViewType() {
		return mViewType;
	}

}
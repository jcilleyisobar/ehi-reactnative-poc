package com.ehi.enterprise.android.ui.location;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import com.ehi.enterprise.android.utils.filters.EHIFilterList;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SearchLocationsFilterItem {

	private String mTitle;
	private @DrawableRes int mIconId;
	private boolean mChecked;
	private @EHIFilterList.EHILocationFilterTypes
	int mFilterType;

	@SearchLocationsItemType
	private int mType;

	@Retention(RetentionPolicy.CLASS)
	@IntDef({TYPE_PRIMARY_ITEM, TYPE_SECONDARY_ITEM})
	public @interface SearchLocationsItemType {
	}

	public static final int TYPE_PRIMARY_ITEM = 1;
	public static final int TYPE_SECONDARY_ITEM = 2;
	private static final int mTypeCount = 2; //returns count of the interface above!

	public SearchLocationsFilterItem(String title, int icon, boolean checked, @SearchLocationsItemType int type) {
		this(title, icon, checked, type, EHIFilterList.FILTER_NAN);
	}

	public SearchLocationsFilterItem(String title, boolean checked, @SearchLocationsItemType int type) {
		this(title, 0, checked, type, EHIFilterList.FILTER_NAN);
	}

	public SearchLocationsFilterItem(String title, int icon, boolean checked, @SearchLocationsItemType int type,
	                                 @EHIFilterList.EHILocationFilterTypes int filterType) {

		mTitle = title;
		mChecked = checked;
		mType = type;
		mFilterType = filterType;
		mIconId = icon;
	}

	public String getTitle() {
		return mTitle;
	}

	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean bool) {
		mChecked = bool;
	}

	@SearchLocationsItemType
	public int getType() {
		return mType;
	}

	public static int getTypeCount() {
		return mTypeCount;
	}

	@EHIFilterList.EHILocationFilterTypes
	public int getFilterType() {
		return mFilterType;
	}

	@DrawableRes
	public int getIconId() {
		return mIconId;
	}
}

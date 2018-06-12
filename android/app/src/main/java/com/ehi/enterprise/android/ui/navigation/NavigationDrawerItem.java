package com.ehi.enterprise.android.ui.navigation;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NavigationDrawerItem {

    public static final int ID_HOME = 94;
    public static final int ID_MY_REWARDS = 93;
    public static final int ID_MY_PROFILE = 91;
    public static final int ID_MY_RENTALS = 96;
    public static final int ID_LOCATIONS = 97;
    public static final int ID_START_RENTAL = 98;
    public static final int ID_CUSTOMER_SUPPORT = 92;
    public static final int ID_SETTINGS = 99;
    public static final int ID_SIGN_IN = 100;
    public static final int ID_SIGN_OUT = 33;
    public static final int ID_SHARE_FEEDBACK = 123;
    public static final int ID_WEEKEND_SPECIAL = 124;
    public static final int ID_EC_SIGN_OUT = 125;
    public static final int ID_HEADER = 126;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_HEADER,
            TYPE_PRIMARY_ITEM,
            TYPE_SECONDARY_ITEM,
            TYPE_BUTTON_ITEM,
            TYPE_SEPARATOR,
            TYPE_LOCATION_ITEM,
            TYPE_TOGGLE,
            TYPE_TOGGLE_LANGUAGE,
            TYPE_DEBUG_MENU,
            TYPE_WEEKEND_SPECIAL_VIEW,
            TYPE_SIGN_IN,
            TYPE_SIGN_OUT,
            TYPE_TOGGLE_SOLR
    })

    public @interface NavigationDrawerItemType {
    }

    public static final int TYPE_HEADER = 9999;
    public static final int TYPE_PRIMARY_ITEM = 1;
    public static final int TYPE_SECONDARY_ITEM = 2;
    public static final int TYPE_BUTTON_ITEM = 3;
    public static final int TYPE_SEPARATOR = 999;
    public static final int TYPE_LOCATION_ITEM = 4;
    public static final int TYPE_TOGGLE = 5;
    public static final int TYPE_TOGGLE_LANGUAGE = 6;
    public static final int TYPE_DEBUG_MENU = 7;
    public static final int TYPE_WEEKEND_SPECIAL_VIEW = 8;
    public static final int TYPE_TOGGLE_SOLR = 9;
    public static final int TYPE_SIGN_IN = 123;
    public static final int TYPE_SIGN_OUT = 124;

    private String mTitle;
    private
    @DrawableRes
    int mIcon;
    private Fragment mFragment;
    private boolean mSelected = false;
    private int mId = 0;

    private final
    @NavigationDrawerItemType
    int mType;
    int mBottomColor;

    public NavigationDrawerItem(String title, @DrawableRes int icon, Fragment fragment, @NavigationDrawerItemType int type) {
        mTitle = title;
        mIcon = icon;
        mFragment = fragment;
        mType = type;
    }

    public NavigationDrawerItem(String title, @DrawableRes int icon, @ColorInt int bottomColor, Fragment fragment, @NavigationDrawerItemType int type, int id) {
        this(title, icon, fragment, type);
        mId = id;
        mBottomColor = bottomColor;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public
    @NavigationDrawerItemType
    int getType() {
        return mType;
    }

    public String getTitle() {
        return mTitle;
    }

    @DrawableRes
    public int getIcon() {
        return mIcon;
    }

    public int getBottomColor() {
        return mBottomColor;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

}
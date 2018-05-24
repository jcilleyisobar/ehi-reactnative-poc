package com.ehi.enterprise.android.ui.navigation.animation;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ehi.enterprise.android.ui.navigation.EHIActionBarDrawerToggle;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerAdapter;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerFragment;

public class EHIDrawerToggle extends EHIActionBarDrawerToggle {

    private final NavigationDrawerAdapter mDrawerAdapter;

	@NavigationDrawerFragment.StatusType
	protected int mStatus = 0;

	public EHIDrawerToggle(Activity activity,
	                       Toolbar toolbar,
	                       DrawerLayout drawerLayout,
	                       EHIDrawerToggle slider,
	                       @StringRes int openDrawerContentDescRes,
	                       @StringRes int closeDrawerContentDescRes,
                           NavigationDrawerAdapter adapter) {
		super(activity, toolbar, drawerLayout, slider, openDrawerContentDescRes, closeDrawerContentDescRes);
        mDrawerAdapter = adapter;
	}

	@Override
	public void onDrawerSlide(View drawerView, float offset) {
		super.onDrawerSlide(drawerView, offset);
		if (mStatus >= 5) {
			if (offset == 0 && mStatus == NavigationDrawerFragment.NAV_CLOSING) {
				mStatus = NavigationDrawerFragment.NAV_CLOSED;

			}
			else if (offset == 1 && mStatus == NavigationDrawerFragment.NAV_OPENING) {
				mStatus = NavigationDrawerFragment.NAV_OPENED;
			}
			return;
		}
		for (int a = 0; a < mDrawerAdapter.getViewHolders().size(); a++) {
            mDrawerAdapter.getViewHolders().get(a).update(offset);
		}
	}

	@Override
	public void onDrawerClosed(View drawerView) {
		super.onDrawerClosed(drawerView);

	}

	@Override
	public void onDrawerOpened(View drawerView) {
		super.onDrawerOpened(drawerView);
	}

}
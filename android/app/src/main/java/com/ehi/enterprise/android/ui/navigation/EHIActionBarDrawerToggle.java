/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ehi.enterprise.android.ui.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class EHIActionBarDrawerToggle implements DrawerLayout.DrawerListener {

	public interface Delegate {

		/**
		 * Set the Action Bar's up indicator drawable and content description.
		 *
		 * @param upDrawable     - Drawable to set as up indicator
		 * @param contentDescRes - Content description to set
		 */
		void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes);

		/**
		 * Set the Action Bar's up indicator content description.
		 *
		 * @param contentDescRes - Content description to set
		 */
		void setActionBarDescription(@StringRes int contentDescRes);

		/**
		 * Returns the drawable to be set as up button when DrawerToggle is disabled
		 */
		Drawable getThemeUpIndicator();

		/**
		 * Returns the context of ActionBar
		 */
		Context getActionBarThemedContext();
	}

	private final Delegate mActivityImpl;
	private final DrawerLayout mDrawerLayout;

	private EHIDrawerToggle mSlider;
	private Drawable mHomeAsUpIndicator;
	private boolean mDrawerIndicatorEnabled = true;
	private boolean mHasCustomUpIndicator;
	private final int mOpenDrawerContentDescRes;
	private final int mCloseDrawerContentDescRes;
	// used in toolbar mode when DrawerToggle is disabled
	private View.OnClickListener mToolbarNavigationClickListener;

	/**
	 * In the future, we can make this constructor public if we want to let developers customize
	 * the
	 * animation.
	 */
	public EHIActionBarDrawerToggle(Activity activity, Toolbar toolbar,
	                                DrawerLayout drawerLayout, EHIDrawerToggle slider,
	                                @StringRes int openDrawerContentDescRes,
	                                @StringRes int closeDrawerContentDescRes) {
		if (toolbar != null) {
			mActivityImpl = new ToolbarCompatDelegate(toolbar);
			toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mDrawerIndicatorEnabled) {
						toggle();
					}
					else if (mToolbarNavigationClickListener != null) {
						mToolbarNavigationClickListener.onClick(v);
					}
				}
			});
		}
		else {
			mActivityImpl = new DummyDelegate(activity);
		}

		mDrawerLayout = drawerLayout;
		mOpenDrawerContentDescRes = openDrawerContentDescRes;
		mCloseDrawerContentDescRes = closeDrawerContentDescRes;
		if (slider == null) {
			mSlider = new DrawerXDrawableToggle(activity,
					mActivityImpl.getActionBarThemedContext());
		}
		else {
			mSlider = slider;
		}

		mHomeAsUpIndicator = getThemeUpIndicator();
	}

	/**
	 * Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
	 * <p/>
	 * <p>This should be called from your <code>Activity</code>'s
	 * {@link android.app.Activity#onPostCreate(android.os.Bundle) onPostCreate} method to synchronize after
	 * the DrawerLayout's instance state has been restored, and any other time when the state
	 * may have diverged in such a way that the ActionBarDrawerToggle was not notified.
	 * (For example, if you stop forwarding appropriate drawer events for a period of time.)</p>
	 */
	public void syncState() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mSlider.setPosition(1);
		}
		else {
			mSlider.setPosition(0);
		}
		if (mDrawerIndicatorEnabled) {
			setActionBarUpIndicator((Drawable) mSlider,
					mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
							mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
		}
	}

	/**
	 * This method should always be called by your <code>Activity</code>'s
	 * {@link android.app.Activity#onConfigurationChanged(android.content.res.Configuration)
	 * onConfigurationChanged}
	 * method.
	 *
	 * @param newConfig The new configuration
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		// Reload drawables that can change with configuration
		if (!mHasCustomUpIndicator) {
			mHomeAsUpIndicator = getThemeUpIndicator();
		}
		syncState();
	}

	/**
	 * This method should be called by your <code>Activity</code>'s
	 * {@link android.app.Activity#onOptionsItemSelected(android.view.MenuItem) onOptionsItemSelected} method.
	 * If it returns true, your <code>onOptionsItemSelected</code> method should return true and
	 * skip further processing.
	 *
	 * @param item the MenuItem instance representing the selected menu item
	 * @return true if the event was handled and further processing should not occur
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item != null && item.getItemId() == android.R.id.home && mDrawerIndicatorEnabled) {
			toggle();
			return true;
		}
		return false;
	}

	private void toggle() {
		if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
			closingDrawer();
			mDrawerLayout.closeDrawer(GravityCompat.START);
		}
		else {
			openingDrawer();
			mDrawerLayout.openDrawer(GravityCompat.START);
		}
	}

	protected void closingDrawer() {

	}

	protected void openingDrawer() {

	}

	/**
	 * Set the up indicator to display when the drawer indicator is not
	 * enabled.
	 * <p/>
	 * If you pass <code>null</code> to this method, the default drawable from
	 * the theme will be used.
	 *
	 * @param indicator A drawable to use for the up indicator, or null to use
	 *                  the theme's default
	 * @see #setDrawerIndicatorEnabled(boolean)
	 */
	public void setHomeAsUpIndicator(Drawable indicator) {
		if (indicator == null) {
			mHomeAsUpIndicator = getThemeUpIndicator();
			mHasCustomUpIndicator = false;
		}
		else {
			mHomeAsUpIndicator = indicator;
			mHasCustomUpIndicator = true;
		}

		if (!mDrawerIndicatorEnabled) {
			setActionBarUpIndicator(mHomeAsUpIndicator, 0);
		}
	}

	/**
	 * Set the up indicator to display when the drawer indicator is not
	 * enabled.
	 * <p/>
	 * If you pass 0 to this method, the default drawable from the theme will
	 * be used.
	 *
	 * @param resId Resource ID of a drawable to use for the up indicator, or 0
	 *              to use the theme's default
	 * @see #setDrawerIndicatorEnabled(boolean)
	 */
	public void setHomeAsUpIndicator(int resId) {
		Drawable indicator = null;
		if (resId != 0) {
			indicator = mDrawerLayout.getResources().getDrawable(resId);
		}
		setHomeAsUpIndicator(indicator);
	}

	/**
	 * @return true if the enhanced drawer indicator is enabled, false otherwise
	 * @see #setDrawerIndicatorEnabled(boolean)
	 */
	public boolean isDrawerIndicatorEnabled() {
		return mDrawerIndicatorEnabled;
	}

	/**
	 * Enable or disable the drawer indicator. The indicator defaults to enabled.
	 * <p/>
	 * <p>When the indicator is disabled, the <code>ActionBar</code> will revert to displaying
	 * the home-as-up indicator provided by the <code>Activity</code>'s theme in the
	 * <code>android.R.attr.homeAsUpIndicator</code> attribute instead of the animated
	 * drawer glyph.</p>
	 *
	 * @param enable true to enable, false to disable
	 */
	public void setDrawerIndicatorEnabled(boolean enable) {
		if (enable != mDrawerIndicatorEnabled) {
			if (enable) {
				setActionBarUpIndicator((Drawable) mSlider,
						mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
								mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
			}
			else {
				setActionBarUpIndicator(mHomeAsUpIndicator, 0);
			}
			mDrawerIndicatorEnabled = enable;
		}
	}


	/**
	 * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
	 * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
	 * through to this method from your own listener object.
	 *
	 * @param drawerView  The child view that was moved
	 * @param slideOffset The new offset of this drawer within its range, from 0-1
	 */
	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		mSlider.setPosition(Math.min(1f, Math.max(0, slideOffset)));
	}

	/**
	 * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
	 * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
	 * through to this method from your own listener object.
	 *
	 * @param drawerView Drawer view that is now open
	 */
	@Override
	public void onDrawerOpened(View drawerView) {
		mSlider.setPosition(1);
		if (mDrawerIndicatorEnabled) {
			setActionBarDescription(mCloseDrawerContentDescRes);
		}
	}

	/**
	 * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
	 * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
	 * through to this method from your own listener object.
	 *
	 * @param drawerView Drawer view that is now closed
	 */
	@Override
	public void onDrawerClosed(View drawerView) {
		mSlider.setPosition(0);
		if (mDrawerIndicatorEnabled) {
			setActionBarDescription(mOpenDrawerContentDescRes);
		}
	}

	/**
	 * {@link android.support.v4.widget.DrawerLayout.DrawerListener} callback method. If you do not use your
	 * ActionBarDrawerToggle instance directly as your DrawerLayout's listener, you should call
	 * through to this method from your own listener object.
	 *
	 * @param newState The new drawer motion state
	 */
	@Override
	public void onDrawerStateChanged(int newState) {
	}

	/**
	 * Returns the fallback listener for Navigation icon click events.
	 *
	 * @return The click listener which receives Navigation click events from Toolbar when
	 * drawer indicator is disabled.
	 * @see #setToolbarNavigationClickListener(android.view.View.OnClickListener)
	 * @see #setDrawerIndicatorEnabled(boolean)
	 * @see #isDrawerIndicatorEnabled()
	 */
	public View.OnClickListener getToolbarNavigationClickListener() {
		return mToolbarNavigationClickListener;
	}

	/**
	 * When DrawerToggle is constructed with a Toolbar, it sets the click listener on
	 * the Navigation icon. If you want to listen for clicks on the Navigation icon when
	 * DrawerToggle is disabled ({@link #setDrawerIndicatorEnabled(boolean)}, you should call this
	 * method with your listener and DrawerToggle will forward click events to that listener
	 * when drawer indicator is disabled.
	 *
	 * @see #setDrawerIndicatorEnabled(boolean)
	 */
	public void setToolbarNavigationClickListener(
			View.OnClickListener onToolbarNavigationClickListener) {
		mToolbarNavigationClickListener = onToolbarNavigationClickListener;
	}

	void setActionBarUpIndicator(Drawable upDrawable, int contentDescRes) {
		mActivityImpl.setActionBarUpIndicator(upDrawable, contentDescRes);
	}

	void setActionBarDescription(int contentDescRes) {
		mActivityImpl.setActionBarDescription(contentDescRes);
	}

	Drawable getThemeUpIndicator() {
		return mActivityImpl.getThemeUpIndicator();
	}

	public static class DrawerXDrawableToggle extends DrawerXDrawable
			implements EHIDrawerToggle {

		private final Activity mActivity;

		public DrawerXDrawableToggle(Activity activity, Context themedContext) {
			super(themedContext);
			mActivity = activity;
		}

		public void setPosition(float position) {
			if (position == 1f) {
				setVerticalMirror(true);
			}
			else if (position == 0f) {
				setVerticalMirror(false);
			}
			super.setProgress(position);
		}

		@Override boolean isLayoutRtl() {
			return ViewCompat.getLayoutDirection(mActivity.getWindow().getDecorView())
					== ViewCompat.LAYOUT_DIRECTION_RTL;
		}

		public float getPosition() {
			return super.getProgress();
		}
	}

	/**
	 * Interface for toggle drawables. Can be public in the future
	 */
	public interface EHIDrawerToggle {

		void setPosition(float position);

		float getPosition();
	}

	/**
	 * Used when DrawerToggle is initialized with a Toolbar
	 */
	static class ToolbarCompatDelegate implements Delegate {

		final Toolbar mToolbar;

		ToolbarCompatDelegate(Toolbar toolbar) {
			mToolbar = toolbar;
		}

		@Override
		public void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes) {
			mToolbar.setNavigationIcon(upDrawable);
			mToolbar.setNavigationContentDescription(contentDescRes);
		}

		@Override
		public void setActionBarDescription(@StringRes int contentDescRes) {
			mToolbar.setNavigationContentDescription(contentDescRes);
		}

		@Override
		public Drawable getThemeUpIndicator() {
			final TypedArray a = mToolbar.getContext()
					.obtainStyledAttributes(new int[]{android.R.id.home});
			final Drawable result = a.getDrawable(0);
			a.recycle();
			return result;
		}

		@Override
		public Context getActionBarThemedContext() {
			return mToolbar.getContext();
		}
	}

	/**
	 * Fallback delegate
	 */
	static class DummyDelegate implements Delegate {
		final Activity mActivity;

		DummyDelegate(Activity activity) {
			mActivity = activity;
		}

		@Override
		public void setActionBarUpIndicator(Drawable upDrawable, @StringRes int contentDescRes) {

		}

		@Override
		public void setActionBarDescription(@StringRes int contentDescRes) {

		}

		@Override
		public Drawable getThemeUpIndicator() {
			return null;
		}

		@Override
		public Context getActionBarThemedContext() {
			return mActivity;
		}
	}
}

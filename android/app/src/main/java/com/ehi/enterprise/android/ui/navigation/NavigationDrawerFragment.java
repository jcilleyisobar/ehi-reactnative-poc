package com.ehi.enterprise.android.ui.navigation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.NavigationDrawerFragmentBinding;
import com.ehi.enterprise.android.ui.dashboard.MainActivity;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.navigation.animation.EHIDrawerToggle;
import com.ehi.enterprise.android.ui.widget.drawer.FullScreenNavigationDrawer;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;
import com.isobar.android.viewmodel.ViewModel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(NavigationDrawerViewModel.class)
public class NavigationDrawerFragment
        extends DataBindingViewModelFragment<NavigationDrawerViewModel, NavigationDrawerFragmentBinding> {

    public static final String SCREEN_NAME = "NavigationDrawerFragment";

    public static final String DRAWER_ITEMS_REACTION = "DRAWER_ITEMS_REACTION";
    public static final String SELECTED_ITEM_REACTION = "SELECTED_ITEM_REACTION";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    private FullScreenNavigationDrawer mDrawerLayout;
    private EHIDrawerToggle mDrawerToggle;
    private View mFragmentContainerView;
    private NavigationDrawerCallbacks mCallbacks;
    private NavigationDrawerAdapter mAdapter;
    private BroadcastReceiver mBroadcastReceiver;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NAV_OPENED, NAV_CLOSED, NAV_OPENING, NAV_CLOSING})
    public @interface StatusType {
    }

    public final static int NAV_OPENED = 1;
    public final static int NAV_CLOSED = 0;
    public final static int NAV_OPENING = 5;
    public final static int NAV_CLOSING = 6;

    public NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks mDrawerCallback = new NavigationDrawerAdapter.NavigationDrawerAdapterCallbacks() {

        @Override
        public void onNavigationItemSelected(int position) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);

            if (mAdapter.getItemViewType(position) != NavigationDrawerItem.TYPE_PRIMARY_ITEM) {
                NavigationDrawerItem item = getViewModel().getDrawerItems().get(position);
                trackAnalyticsEvent(item);
                if (mCallbacks != null) {
                    mCallbacks.onNavigationDrawerItemSelected(item);
                }
                getViewModel().setNeedToTrackScreenChange(false);
            } else if (mAdapter.getItemViewType(position) == NavigationDrawerItem.TYPE_PRIMARY_ITEM &&
                    !mAdapter.isCurrentlySelected(position)) {
                getViewModel().getCurrentItem().setSelected(false);
                selectItem(position);
                getViewModel().getCurrentItem().setSelected(true);
                mAdapter.notifyDataSetChanged();
                trackAnalyticsEvent(getViewModel().getCurrentItem());
                getViewModel().setNeedToTrackScreenChange(false);
            }

        }
    };

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getViewModel().resetPosition();
                getViewModel().populateDrawerItems(intent.getIntExtra(LoginManager.CURRENT_DRAWER_ITEM, NavigationDrawerViewModel.RESET_MENU));
           }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(LoginManager.LOGIN_EVENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(ReservationManager.EC_REFRESH_EVENT));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(LocalDataManager.COUNTRY_CHANGE_EVENT));
    }

    @Override
    public void onResume() {
        super.onResume();
        initDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = createViewBinding(inflater, R.layout.fr_navigation_drawer, container);
        initViews();
        return rootView;
    }

    private void initViews() {
        getViewBinding().recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new NavigationDrawerAdapter(new ArrayList<NavigationDrawerItem>(), mDrawerCallback);

        getViewBinding().recyclerView.setItemAnimator(null);
        getViewBinding().recyclerView.setAdapter(mAdapter);
    }

    private void trackAnalyticsEvent(NavigationDrawerItem currentItem) {
        String action;
        switch (currentItem.getId()) {
            case NavigationDrawerItem.ID_MY_REWARDS:
                action = EHIAnalytics.Action.ACTION_MENU_EPLUS_REWARDS.value;
                break;
            case NavigationDrawerItem.ID_MY_PROFILE:
                action = EHIAnalytics.Action.ACTION_MENU_MY_PROFILE.value;
                break;
            case NavigationDrawerItem.ID_MY_RENTALS:
                action = EHIAnalytics.Action.ACTION_MENU_MY_RENTALS.value;
                break;
            case NavigationDrawerItem.ID_LOCATIONS:
                action = EHIAnalytics.Action.ACTION_MENU_LOCATIONS.value;
                break;
            case NavigationDrawerItem.ID_SIGN_IN:
                action = EHIAnalytics.Action.ACTION_MENU_SIGNIN.value;
                break;
            case NavigationDrawerItem.ID_CUSTOMER_SUPPORT:
                action = EHIAnalytics.Action.ACTION_MENU_CUSTOMER_SUPPORT.value;
                break;
            case NavigationDrawerItem.ID_SETTINGS:
                action = EHIAnalytics.Action.ACTION_MENU_SETTING.value;
                break;
            case NavigationDrawerItem.ID_SIGN_OUT:
                action = EHIAnalytics.Action.ACTION_MENU_SIGN_OUT.value;
                break;
            case NavigationDrawerItem.ID_START_RENTAL:
                action = EHIAnalytics.Action.ACTION_MENU_START_RENTAL.value;
                break;
            case NavigationDrawerItem.ID_SHARE_FEEDBACK:
                action = EHIAnalytics.Action.ACTION_MENU_SHARE_FEEDBACK.value;
                break;
            case NavigationDrawerItem.ID_WEEKEND_SPECIAL:
                action = EHIAnalytics.Action.ACTION_MENU_WKND_PROMO_GET_STARTED.value;
                break;
            case NavigationDrawerItem.ID_HOME:
            default:
                action = EHIAnalytics.Action.ACTION_MENU_HOME.value;
                break;
        }

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MENU.value, NavigationDrawerFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_HOME.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    public void revertSelection() {
        int resetPosition = getViewModel().getResetPosition();
        getViewModel().resetPosition();
        if (getViewModel().getCurrentItem() != null) {
            getViewModel().getCurrentItem().setSelected(false);
            getViewModel().setCurrentItem(resetPosition);
            getViewModel().getCurrentItem().setSelected(true);
        }
    }

    public void selectDrawerItem(int itemId) {
        for (int i = 0; i < getViewModel().getDrawerItems().size(); i++) {
            NavigationDrawerItem item = getViewModel().getDrawerItems().get(i);
            if (item.getId() == itemId) {
                mDrawerCallback.onNavigationItemSelected(i);
                break;
            }
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        addReaction(DRAWER_ITEMS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getDrawerItems() != null) {
                    mAdapter.setDrawerItems(getViewModel().getDrawerItems());
                }
            }
        });
        addReaction(SELECTED_ITEM_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final NavigationDrawerItem currentItem = getViewModel().getCurrentItem();
                if (currentItem.getId() != NavigationDrawerItem.ID_WEEKEND_SPECIAL
                        && currentItem.getId() != NavigationDrawerItem.ID_START_RENTAL) {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            final NavigationDrawerItem currentItem = getViewModel().getCurrentItem();
                            if (mCallbacks != null
                                    && getViewModel().shouldSelectItem()) {
                                getViewModel().updateLastSelectedItem();
                                mCallbacks.onNavigationDrawerItemSelected(currentItem);
                            }
                        }
                    }, 300);
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, FullScreenNavigationDrawer drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        // mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new EHIDrawerToggle(getActivity(),
                ((MainActivity) getActivity()).getToolbar(),
                mDrawerLayout,
                new EHIActionBarDrawerToggle.DrawerXDrawableToggle(getActivity(), actionBar.getThemedContext()),
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close,
                mAdapter) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                ((MainActivity) getActivity()).setToolbarTitleVisible(true);
                if (getViewModel().isNeedToTrackScreenChange()) {
                    ((MainActivity) getActivity()).trackCurrentScreenAnalytics();
                }

                getViewModel().setNeedToTrackScreenChange(true);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getViewModel().setNeedToTrackScreenChange(true);
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_MENU.value, NavigationDrawerFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                ((MainActivity) getActivity()).setToolbarTitleVisible(false);
                ((MainActivity) getActivity()).updateMenuVisibility();

            }

            @Override
            protected void closingDrawer() {
                mStatus = NAV_CLOSING;
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_MENU.value, NavigationDrawerFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_HIDE_MENU.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();
            }

            @Override
            protected void openingDrawer() {
                mStatus = NAV_OPENING;

                EHIAnalyticsEvent.create()
                        .smartTrackAction(true)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SHOW_MENU.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                ((MainActivity) getActivity()).setToolbarTitleVisible(false);
                ((MainActivity) getActivity()).updateMenuVisibility();

                //this is used to reset all animation starting points after someone closes the slide using the button
                for (int a = 0; a < mAdapter.getViewHolders().size(); a++) {
                    mAdapter.getViewHolders().get(a).resetAnimation();
                }
                for (int a = 0; a < mAdapter.getViewHolders().size(); a++) {
                    mAdapter.getViewHolders().get(a).animationOverride(1f, 512);
                }
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
    }

    private void selectItem(int position) {
        getViewModel().setCurrentItem(position);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         *
         * @param drawerItem
         */
        void onNavigationDrawerItemSelected(NavigationDrawerItem drawerItem);

        void selectDrawerItem(int itemId);
    }

}
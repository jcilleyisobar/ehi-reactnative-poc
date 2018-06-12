package com.ehi.enterprise.android.ui.navigation;

import android.support.annotation.NonNull;
import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.ui.dashboard.DashboardFragmentHelper;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsFragmentHelper;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.history.MyRentalsUnauthFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.promotions.WeekendSpecialDetailsFragmentHelper;
import com.ehi.enterprise.android.ui.rewards.RewardsFragmentHelper;
import com.ehi.enterprise.android.ui.settings.SettingsFragmentHelper;
import com.ehi.enterprise.android.ui.support.CustomerSupportFragmentHelper;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class NavigationDrawerViewModel extends CountrySpecificViewModel {

    ReactorVar<List<NavigationDrawerItem>> mDrawerItems = new ReactorVar<>();
    ReactorVar<NavigationDrawerItem> mCurrentItem = new ReactorVar<>();
    private int mCurrentItemPosition = 0;
    private int mResetPosition = -1;
    private int mLastSelectedItem = -1;

    private boolean isNavDrawerUsingWeekendSpecial = false;
    public static final int RESET_MENU = -1;

    /**
     * This flag is for analytics purpose only, we don't need to manually track screen change
     * in case user switched root screen since the new screen will track itself.
     * <p>
     * This flag should be set to @true only in case the manual tracking of visible screen should
     * happen.
     */
    private boolean mNeedToTrackScreenChange = true;

    public NavigationDrawerViewModel() {
        mDrawerItems.setValue(new ArrayList<NavigationDrawerItem>());
    }


    public void populateDrawerItems(){
        populateDrawerItems(NavigationDrawerViewModel.RESET_MENU);
    }

    public void populateDrawerItems(int navigationDrawerItemId) {
        int firstIndex = 1;

        final ArrayList<NavigationDrawerItem> listOfItems = new ArrayList<>();
        listOfItems.add(new NavigationDrawerItem("", 0, null, NavigationDrawerItem.TYPE_SEPARATOR));

        if (isWeekendSpecialAvailable()) {
            NavigationDrawerItem weekendSpecial = new NavigationDrawerItem(
                    getWeekendSpecialContract() == null ? "" : getWeekendSpecialContract().getContractName(),
                    0,
                    0,
                    new WeekendSpecialDetailsFragmentHelper.Builder().build(),
                    NavigationDrawerItem.TYPE_WEEKEND_SPECIAL_VIEW,
                    NavigationDrawerItem.ID_WEEKEND_SPECIAL
            );
            listOfItems.add(weekendSpecial);
            firstIndex++;
        }

        //home
        listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_home), R.drawable.icon_home, getResources().getColor(R.color.ehi_primary_light),
                new DashboardFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_PRIMARY_ITEM, NavigationDrawerItem.ID_HOME));

        //header enterprise plus
        listOfItems.add(new NavigationDrawerItem(
                isUserLoggedIn() ? getResources().getString(R.string.menu_section_my_enterprise_plus_auth) : getResources().getString(R.string.menu_section_my_enterprise_plus_unauth), 0, 0,
                null, NavigationDrawerItem.TYPE_HEADER, NavigationDrawerItem.ID_HEADER));
        if (isUserLoggedIn() || isLoggedIntoEmeraldClub()) {
            //my rentals
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_my_rentals), R.drawable.icon_rental, getResources().getColor(R.color.navigation_light_divider),
                    new MyRentalsFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_PRIMARY_ITEM, NavigationDrawerItem.ID_MY_RENTALS));
        }

        if (isUserLoggedIn()) {
            //my rewards and benefits
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_enterprise_plus_rewards), R.drawable.icon_rewards, getResources().getColor(R.color.navigation_light_divider),
                    new RewardsFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_PRIMARY_ITEM, NavigationDrawerItem.ID_MY_REWARDS));

            //profile
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_profile), R.drawable.icon_profile, getResources().getColor(R.color.ehi_primary_light),
                    null, NavigationDrawerItem.TYPE_PRIMARY_ITEM, NavigationDrawerItem.ID_MY_PROFILE));
        } else {
            //learn about rewards
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_learn_about_rewards), R.drawable.icon_rewards, getResources().getColor(R.color.navigation_light_divider),
                    new RewardsFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_PRIMARY_ITEM, NavigationDrawerItem.ID_MY_REWARDS));

            //enterprise plus sign in
            if (!isLoggedIntoEmeraldClub()) {
                listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.login_title), R.drawable.icon_signout_1, getResources().getColor(R.color.ehi_primary_light),
                        new LoginFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_SIGN_IN, NavigationDrawerItem.ID_SIGN_IN));
            }
        }


        //header reservations and locations search
        listOfItems.add(new NavigationDrawerItem(
                getResources().getString(R.string.menu_section_reservations_location_search), 0, 0,
                null, NavigationDrawerItem.TYPE_HEADER, NavigationDrawerItem.ID_HEADER));

        if (!isUserLoggedIn() && !isLoggedIntoEmeraldClub()) {
            //look up rentals
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_look_up_rentals), R.drawable.icon_rental, getResources().getColor(R.color.navigation_light_divider),
                    new MyRentalsUnauthFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_PRIMARY_ITEM, NavigationDrawerItem.ID_MY_RENTALS));
        }

        //start a reservation
        listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_start_a_reservation), R.drawable.icon_reservation, getResources().getColor(R.color.navigation_light_divider),
                new SearchLocationsFragmentHelper.Builder().extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP).build(), NavigationDrawerItem.TYPE_LOCATION_ITEM, NavigationDrawerItem.ID_START_RENTAL));

        //look up a location
        listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_look_up_a_location), R.drawable.icon_lookup, getResources().getColor(R.color.ehi_primary_light),
                new SearchLocationsFragmentHelper.Builder().extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP).build(), NavigationDrawerItem.TYPE_LOCATION_ITEM, NavigationDrawerItem.ID_LOCATIONS));


        //header support and tools
        listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_section_support_tools), 0, 0,
                null, NavigationDrawerItem.TYPE_HEADER, NavigationDrawerItem.ID_HEADER));

        //send us app feedback
        if (needShowFeedbackMenu()) {
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_send_feedback), 0, getResources().getColor(R.color.navigation_light_divider),
                    null, NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.ID_SHARE_FEEDBACK));
        }

        //help and customer support
        listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_customer_support), 0, getResources().getColor(R.color.navigation_light_divider),
                new CustomerSupportFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.ID_CUSTOMER_SUPPORT));

        //app settings
        listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_settings), 0, getResources().getColor(R.color.navigation_light_divider),
                new SettingsFragmentHelper.Builder().build(), NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.ID_SETTINGS));

        //sign out / sign in to enterprise plus
        if (isUserLoggedIn()) {
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_sign_out), 0, getResources().getColor(R.color.navigation_light_divider),
                    null, NavigationDrawerItem.TYPE_SIGN_OUT, NavigationDrawerItem.ID_SIGN_OUT));
        } else {
            if (isLoggedIntoEmeraldClub()) {
                listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.menu_emerald_club_sign_out), 0, getResources().getColor(R.color.navigation_light_divider),
                        null, NavigationDrawerItem.TYPE_SIGN_OUT, NavigationDrawerItem.ID_EC_SIGN_OUT));
            }
        }

        //development options
        if (BuildConfig.FLAVOR.equalsIgnoreCase("dev") || BuildConfig.FLAVOR.equalsIgnoreCase("uat")) {
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.navigation_toggle_environment), 0, getResources().getColor(R.color.navigation_light_divider),
                    null, NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.TYPE_TOGGLE));
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.navigation_toogle_solr_environment), 0, getResources().getColor(R.color.navigation_light_divider),
                    null, NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.TYPE_TOGGLE_SOLR));
            listOfItems.add(new NavigationDrawerItem(getResources().getString(R.string.navigation_toggle_language), 0, getResources().getColor(R.color.navigation_light_divider),
                    null, NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.TYPE_TOGGLE_LANGUAGE));
            listOfItems.add(new NavigationDrawerItem("Debug Menu", 0, getResources().getColor(R.color.navigation_light_divider),
                    null, NavigationDrawerItem.TYPE_SECONDARY_ITEM, NavigationDrawerItem.TYPE_DEBUG_MENU));
        }

        setDrawerItems(listOfItems);
        if (navigationDrawerItemId == -1) {
            setCurrentItem(firstIndex);
            listOfItems.get(firstIndex).setSelected(true);
        } else {
            for (int i = 0; i < listOfItems.size(); i++){
                if (listOfItems.get(i).getId() == navigationDrawerItemId){
                    setCurrentItem(i);
                    listOfItems.get(i).setSelected(true);
                }
            }
        }

    }


    public void pushDrawerItem(@NonNull NavigationDrawerItem navigationDrawerItem) {
        addDrawerItem(navigationDrawerItem, -1);
    }

    public int getDrawerSize() {
        return mDrawerItems.getValue().size();
    }

    public void addDrawerItem(@NonNull NavigationDrawerItem navigationDrawerItem, int position) {
        position = (position == -1) ? getDrawerSize() : position;
        mDrawerItems.getValue().add(position, navigationDrawerItem);
        mDrawerItems.getDependency().changed();
    }

    public void setDrawerItems(ArrayList<NavigationDrawerItem> items) {
        mDrawerItems.setValue(items);
    }

    public NavigationDrawerItem getItemAt(int position) {
        return (getDrawerSize() > position) ? mDrawerItems.getValue().get(position) : null;
    }

    public List<NavigationDrawerItem> getDrawerItems() {
        if (mDrawerItems != null) {
            if (mDrawerItems.getValue() == null) {
                mDrawerItems.setValue(new ArrayList<NavigationDrawerItem>());
            }
            if (mDrawerItems.getValue().isEmpty() || shouldRePopulateNavDrawer()) {
                //repopulation of drawer in case of WES contract not available
                isNavDrawerUsingWeekendSpecial = isWeekendSpecialAvailable();
                populateDrawerItems();
            }
            return mDrawerItems.getValue();
        }
        return null;
    }

    public NavigationDrawerItem getCurrentItem() {
        if (mCurrentItem != null) {
            return mCurrentItem.getValue();
        }
        return null;
    }

    public void setCurrentItem(@NonNull NavigationDrawerItem currentItem) {
        mCurrentItem.setValue(currentItem);
    }

    public void setCurrentItem(int position) {
        mResetPosition = mCurrentItemPosition;
        mCurrentItemPosition = position;
        mCurrentItem.setValue(mDrawerItems.getValue().get(position));
    }

    public int getCurrentItemPosition() {
        return mCurrentItemPosition;
    }

    public int getResetPosition() {
        return mResetPosition;
    }

    public void resetPosition() {
        mResetPosition = -1;
    }

    private boolean shouldRePopulateNavDrawer() {
        return isNavDrawerUsingWeekendSpecial != isWeekendSpecialAvailable()
                || !isSameWESContractName();
    }

    private boolean isSameWESContractName() {
        List<NavigationDrawerItem> items = mDrawerItems.getRawValue();
        if (items != null) {
            for (NavigationDrawerItem menuItem : items) {
                //repopulating of drawer items in case of different country for WES (potentially different title)
                if (menuItem.getId() == NavigationDrawerItem.ID_WEEKEND_SPECIAL) {
                    return menuItem.getTitle().trim().equals(getWeekendSpecialContract().getContractName().trim());
                }
            }
        }
        return true;
    }

    public boolean shouldSelectItem() {
        return mCurrentItemPosition != mLastSelectedItem;
    }

    public void updateLastSelectedItem() {
        mLastSelectedItem = mCurrentItemPosition;
    }

    public boolean isNeedToTrackScreenChange() {
        return mNeedToTrackScreenChange;
    }

    public void setNeedToTrackScreenChange(boolean needToTrackScreenChange) {
        mNeedToTrackScreenChange = needToTrackScreenChange;
    }

}
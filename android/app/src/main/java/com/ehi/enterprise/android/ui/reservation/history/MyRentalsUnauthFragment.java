package com.ehi.enterprise.android.ui.reservation.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyRentalsUnauthFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.login.LoginFragmentHelper;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerItem;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class MyRentalsUnauthFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, MyRentalsUnauthFragmentBinding>
        implements IRootMenuScreen {

    public static final String SCREEN_NAME = "MyRentalsUnauthFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().signInButton) {
                trackClick(EHIAnalytics.Action.ACTION_SIGN_IN);
                showModal(getActivity(), new LoginFragmentHelper.Builder()
                        .menuPosition(NavigationDrawerItem.ID_MY_RENTALS)
                        .build());
            } else if (v == getViewBinding().lookUpRentalButton) {
                trackClick(EHIAnalytics.Action.ACTION_LOOKUP);
                showModal(getActivity(), new LookupRentalFragmentHelper.Builder().build());
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.rentals_navigation_title);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_my_rentals_unauth, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.rentals_navigation_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreenChange();
        if (getViewModel().isUserLoggedIn() || getViewModel().isLoggedIntoEmeraldClub()) {
            ((MyRentalsUnauthFragmentListener) getActivity()).showAuthMyRentals();
        }
    }

    @Override
    public void trackScreenChange() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsUnauthFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_UNAUTH.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().signInButton.setOnClickListener(mOnClickListener);
        getViewBinding().lookUpRentalButton.setOnClickListener(mOnClickListener);
    }

    public interface MyRentalsUnauthFragmentListener {
        void showAuthMyRentals();
    }

    private void trackClick(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_MY_RENTALS.value, MyRentalsUnauthFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_UNAUTH.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }
}

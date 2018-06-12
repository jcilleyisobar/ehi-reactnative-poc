package com.ehi.enterprise.android.ui.rewards;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AboutPointsFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivityHelper;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;


@NoExtras
@ViewModel(AboutPointsViewModel.class)
public class AboutPointsFragment extends DataBindingViewModelFragment<AboutPointsViewModel, AboutPointsFragmentBinding> {

    public static final String SCREEN_NAME = "AboutPointsFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String action = "";
            if (view == getViewBinding().startReservationButton) {
                action = EHIAnalytics.Action.ACTION_START_RESERVATION.value;
                startActivity(new SearchLocationsActivityHelper.Builder()
                        .extraFlow(SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP)
                        .isModify(false)
                        .build(getActivity()));
//            } else if (view == getViewBinding().viewPointsHistoryButton) {
//                action = EHIAnalytics.Action.ACTION_VIEW_POINTS_HISTORY.value;
//                callEplusSupportNumber();
//            } else if (view == getViewBinding().transferPointsButton) {
//                action = EHIAnalytics.Action.ACTION_TRANSFER_POINTS.value;
//                callEplusSupportNumber();
            } else if (view == getViewBinding().requestLostPointsButton) {
                action = EHIAnalytics.Action.ACTION_CALL_REQUEST_POINTS.value;
                callEplusSupportNumber();
            } else if (view == getViewBinding().programDetailsButton) {
                showModal(getActivity(), new RewardsAboutEnterprisePlusFragmentHelper.Builder().build());
            }
            if (!EHITextUtils.isEmpty(action)) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_ABOUT_POINTS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, action)
                        .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                        .tagScreen()
                        .tagEvent();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_about_points, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_REWARDS_AUTH.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_ABOUT_POINTS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.rewardsDict())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().startReservationButton.setOnClickListener(mOnClickListener);
//        getViewBinding().viewPointsHistoryButton.setOnClickListener(mOnClickListener);
//        getViewBinding().transferPointsButton.setOnClickListener(mOnClickListener);
        getViewBinding().requestLostPointsButton.setOnClickListener(mOnClickListener);
        getViewBinding().programDetailsButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.about_points_title));
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorTextView.text(getViewModel().pointsBalance.text(), getViewBinding().pointsBalance));
        bind(ReactorTextView.text(getViewModel().eplusPhoneNumber.text(), getViewBinding().requestLostPointsButton));
//        bind(ReactorTextView.text(getViewModel().eplusPhoneNumber.text(), getViewBinding().transferPointsButton));
//        bind(ReactorTextView.text(getViewModel().eplusPhoneNumber.text(), getViewBinding().viewPointsHistoryButton));
//        bind(ReactorView.visibility(getViewModel().transferPointsContainer.visibility(), getViewBinding().transferPointsArea));
//        bind(ReactorView.visibility(getViewModel().pointsHistoryContainer.visibility(), getViewBinding().pointsHistoryArea));
        bind(ReactorView.visibility(getViewModel().lostPointsContainer.visibility(), getViewBinding().lostPointsArea));
    }

    private void callEplusSupportNumber(){
        String number = getViewModel().getEplusPhoneNumber();
        if (number != null) {
            IntentUtils.callNumber(getActivity(), number);
        }
    }
}
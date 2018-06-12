package com.ehi.enterprise.android.ui.rewards;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.JoinEnterpriseBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelDialogFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class JoinEnterpriseModalFragment extends DataBindingViewModelDialogFragment<ManagersAccessViewModel, JoinEnterpriseBinding> {

    public static final String SCREEN_NAME = "JoinEnterpriseModalFragment";

    public static final int RESULT_ENROLL = Activity.RESULT_FIRST_USER + 1;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().joinEnterpriseButton) {
                trackClick(EHIAnalytics.Action.ACTION_JOIN);
                getActivity().setResult(RESULT_ENROLL);
            } else if (view == getViewBinding().addToCalendarButton) {
                trackClick(EHIAnalytics.Action.ACTION_ADD_TO_CALENDAR);
                getActivity().setResult(Activity.RESULT_OK);
            }
            getActivity().finish();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_join_enterprise_modal, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().addToCalendarButton.setOnClickListener(mOnClickListener);
        getViewBinding().joinEnterpriseButton.setOnClickListener(mOnClickListener);
        getViewBinding().closeButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_ENROLL_MODAL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    private void trackClick(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_ENROLL_MODAL.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

}

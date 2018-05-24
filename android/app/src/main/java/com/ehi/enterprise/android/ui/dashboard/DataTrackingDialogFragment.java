package com.ehi.enterprise.android.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DataTrackingDialogFragmentBinding;
import com.ehi.enterprise.android.ui.activity.ModalActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.settings.SettingsFragmentHelper;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(DataTrackingViewModel.class)
public class DataTrackingDialogFragment extends DataBindingViewModelFragment<DataTrackingViewModel, DataTrackingDialogFragmentBinding> {

    private static final String SCREEN_NAME = "DataTrackingDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                trackClick(EHIAnalytics.Action.ACTION_CONTINUE);
                getActivity().finish();
            }
            if (view == getViewBinding().changeSettingsButton) {
                trackClick(EHIAnalytics.Action.ACTION_CHANGE_TRACKING_SETTING);
                Intent intent = new ModalActivityHelper.Builder()
                        .fragmentClass(new SettingsFragmentHelper.Builder().build().getClass())
                        .build(getActivity());
                startActivity(intent);
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_data_tracking_dialog, container);
        initViews();
        getViewModel().setDataCollectionWarningAsShow();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().changeSettingsButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_DATA_COLLECTION_REMINDER.value, TAG)
                .state(EHIAnalytics.State.STATE_DATA_COLLECTION_REMINDER.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    private void trackClick(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_DASHBOARD.value, SCREEN_NAME)
                .state(getViewModel().getRentalState())
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.dashboard(getViewModel().getRentalState(), null))
                .tagScreen()
                .tagEvent();
    }
}

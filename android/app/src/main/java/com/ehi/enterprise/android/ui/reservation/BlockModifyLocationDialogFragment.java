package com.ehi.enterprise.android.ui.reservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.BlockModifyLocationDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(ManagersAccessViewModel.class)
public class BlockModifyLocationDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, BlockModifyLocationDialogFragmentBinding> {

    private static final String SCREEN_NAME = "BlockModifyLocationDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().prepayCancelReservationButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.LOCATION_CANT_BE_MODIFIED.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL_US.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber());
            } else if (v == getViewBinding().prepayKeepReservationButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.LOCATION_CANT_BE_MODIFIED.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_HOME.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CLOSE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                getActivity().finish();
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_block_modify_location_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.LOCATION_CANT_BE_MODIFIED.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_HOME.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().prepayCancelReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().prepayKeepReservationButton.setOnDisabledClickListener(mOnClickListener);
    }
}
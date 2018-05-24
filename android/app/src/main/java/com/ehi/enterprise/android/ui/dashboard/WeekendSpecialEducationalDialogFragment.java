package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.WeekendSpecialEducationalDialogFragmentBinding;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.promotions.WeekendSpecialDetailsFragmentHelper;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

@NoExtras
@ViewModel(WeekendSpecialEducationalViewModel.class)
public class WeekendSpecialEducationalDialogFragment extends DataBindingViewModelFragment<WeekendSpecialEducationalViewModel, WeekendSpecialEducationalDialogFragmentBinding> {

    private static final String TAG = "WeekendSpecialEducationalDialogFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().weekendSpecialGetStartedButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_PROMOTION_MODAL.value, TAG)
                        .state(EHIAnalytics.State.STATE_WEEKEND_SPECIAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LEARN_MORE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();
                showModal(getActivity(), new WeekendSpecialDetailsFragmentHelper.Builder().build());
                getActivity().finish();
            } else if (view == getViewBinding().weekendSpecialCloseButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_PROMOTION_MODAL.value, TAG)
                        .state(EHIAnalytics.State.STATE_WEEKEND_SPECIAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CLOSE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();
                getActivity().finish();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_weekend_special_educational_dialog, container);

        initViews();

        getViewModel().markWeekendSpecialModalAsSeen();

        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().weekendSpecialGetStartedButton.setOnClickListener(mOnClickListener);
        getViewBinding().weekendSpecialCloseButton.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_PROMOTION_MODAL.value, TAG)
                .state(EHIAnalytics.State.STATE_WEEKEND_SPECIAL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }
}

package com.ehi.enterprise.android.ui.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DialOutFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(DialOutViewModel.class)
public class DialOutFragment extends DataBindingViewModelFragment<DialOutViewModel, DialOutFragmentBinding> {

    public static final String SCREEN_NAME = "DialOutFragment";

    @Extra(value = boolean.class, required = false)
    public static final String CURRENT_RENTAL = "CURRENT_RENTAL";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().roadsideAssistance) {
                IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber(EHIPhone.PhoneType.ROADSIDE_ASSISTANCE));
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_PHONE.value, DialOutFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_MODAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.call(EHIAnalytics.PhoneType.PHONE_TYPE_ROADSIDE.value))
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().contactUs) {
                IntentUtils.callNumber(getActivity(), getViewModel().getSupportPhoneNumber(EHIPhone.PhoneType.CONTACT_US));
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_PHONE.value, DialOutFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_MODAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.call(EHIAnalytics.PhoneType.PHONE_TYPE_CALL_CENTER.value))
                        .tagScreen()
                        .tagEvent();
            } else if (view == getViewBinding().closeButton) {
                getActivity().finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            DialOutFragmentHelper.Extractor extractor = new DialOutFragmentHelper.Extractor(this);
            if (extractor.currentRental() != null) {
                getViewModel().setHasCurrentRental(extractor.currentRental());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_call_support, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.title(getViewModel().title, getActivity()));
        bind(ReactorView.visibility(getViewModel().roadSideAssistanceView.visibility(), getViewBinding().roadsideAssistance));
        bind(ReactorTextView.textRes(getViewModel().supportTextView.textRes(), getViewBinding().supportText));
    }

    private void initViews() {
        getViewBinding().roadsideAssistance.setOnClickListener(mOnClickListener);
        getViewBinding().closeButton.setOnClickListener(mOnClickListener);
        getViewBinding().contactUs.setOnClickListener(mOnClickListener);
        if (getViewModel().hasCurrentRental()) {
            getViewBinding().roadsideAssistance.populateView(getString(R.string.call_support_roadside_assistance_button_title),
                    getString(R.string.call_support_roadside_assistance_button_subtitle));
            getViewBinding().contactUs.populateView(getString(R.string.call_support_call_center_button_title),
                    getString(R.string.call_support_call_center_button_subtitle));
        } else {
            getViewBinding().contactUs.populateView(getString(R.string.dashboard_call_support_button), null);
        }
    }

}
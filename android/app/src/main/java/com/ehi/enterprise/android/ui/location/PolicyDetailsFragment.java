package com.ehi.enterprise.android.ui.location;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PolicyDetailsFragmentBinding;
import com.ehi.enterprise.android.models.location.EHIPolicy;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NoArgumentsFoundException;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class PolicyDetailsFragment
        extends DataBindingViewModelFragment<ManagersAccessViewModel, PolicyDetailsFragmentBinding> {

    public static final String TAG = "PolicyDetailsFragment";
    public static final String SCREEN_NAME = "PolicyDetailsFragment";

    @Extra(value = EHIPolicy.class, required = false)
    public static final String EXTRA_POLICY = "ehi.EXTRA_POLICY";

    private EHIPolicy mPolicy;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return createViewBinding(inflater, R.layout.fr_policy_details, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PolicyDetailsFragmentHelper.Extractor extractor = new PolicyDetailsFragmentHelper.Extractor(this);
        if (extractor.extraPolicy() == null) {
            DLog.e(TAG, new NoArgumentsFoundException());
            getActivity().finish();
            return;
        }

        mPolicy = extractor.extraPolicy();

        getViewBinding().titleTextView.setText(mPolicy.getDescription());
        getViewBinding().policyTextView.setText(mPolicy.getPolicyText());
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_LOCATIONS.value, PolicyDetailsFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_POLICY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.policyDetails(mPolicy.getPolicyDescription()))
                .tagScreen()
                .tagEvent();
    }
}

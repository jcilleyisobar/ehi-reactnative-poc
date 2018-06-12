package com.ehi.enterprise.android.ui.support;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.CustomerSupportFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.support.interfaces.OnSupportItemClickListener;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.IRootMenuScreen;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactorbinding.activity.ReactorActivity;

@NoExtras
@ViewModel(CustomerSupportViewModel.class)
public class CustomerSupportFragment extends DataBindingViewModelFragment<CustomerSupportViewModel, CustomerSupportFragmentBinding>
        implements IRootMenuScreen{

    public static final String TAG = "CustomerSupportFragment";

    //region onClickListener
    private OnSupportItemClickListener mOnItemClickListener = new OnSupportItemClickListener() {
        @Override
        public void onCallSupportNumber(EHIPhone phone) {
            if (EHIPhone.PhoneType.CONTACT_US.equals(phone.getPhoneType())) {
                trackAction(EHIAnalytics.Action.ACTION_CUSTOMER_SERVICE);
            } else if (EHIPhone.PhoneType.ROADSIDE_ASSISTANCE.equals(phone.getPhoneType())) {
                trackAction(EHIAnalytics.Action.ACTION_ROADSIDE_ASSISTANCE);
            } else if (EHIPhone.PhoneType.EPLUS.equals(phone.getPhoneType())) {
                trackAction(EHIAnalytics.Action.ACTION_ENTERPRISE_PLUS);
            } else if (EHIPhone.PhoneType.DISABILITES.equals(phone.getPhoneType())) {
                trackAction(EHIAnalytics.Action.ACTION_CUSTOMERS_DISABILITIES);
            }

            IntentUtils.callNumber(getActivity(), phone.getPhoneNumber());
        }

        @Override
        public void onMessageLinkOut(String url) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_HELP_CUSTOMER_SUPPORT.value, TAG)
                    .state(EHIAnalytics.State.STATE_MORE_HELP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SEND_MESSAGE.value)
                    .tagScreen()
                    .tagEvent();
            IntentUtils.openUrlViaExternalApp(getActivity(), url);
        }

        @Override
        public void onSearchLinkOut(String url) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_HELP_CUSTOMER_SUPPORT.value, TAG)
                    .state(EHIAnalytics.State.STATE_MORE_HELP.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_FAQ.value)
                    .tagScreen()
                    .tagEvent();
            IntentUtils.openUrlViaExternalApp(getActivity(), url);
        }
    };

    private void trackAction(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_HELP_CUSTOMER_SUPPORT.value, TAG)
                .state(EHIAnalytics.State.STATE_CALL_ENTERPRISE.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .tagScreen()
                .tagEvent();
    }
    //endregion

    //region lifecycle
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_customer_support, container);
        initViews();
        return getViewBinding().getRoot();
    }
    //endregion

    private void initViews() {
        CustomerSupportAdapter customerSupportAdapter = new CustomerSupportAdapter(getActivity().getApplicationContext());
        getViewBinding().supportOptionsList.setHasFixedSize(true);
        getViewBinding().supportOptionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        getViewBinding().supportOptionsList.setAdapter(customerSupportAdapter);

        customerSupportAdapter.addMainHeader();
        customerSupportAdapter.setSupportPhoneNumbers(getViewModel().getConfigSupportPhoneNumbers());
        if (!TextUtils.isEmpty(getViewModel().getSendUsAMessageUrl())) {
            customerSupportAdapter.addMoreOptionsMessageItem(getViewModel().getSendUsAMessageUrl());
        }
        if (!TextUtils.isEmpty(getViewModel().getSearchAnswersUrl())) {
            customerSupportAdapter.addMoreOptionsSearchItem(getViewModel().getSearchAnswersUrl());
        }
        customerSupportAdapter.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
    }

    @Override
    public void trackScreenChange() {
        //TODO add analytics to this screen
    }
}
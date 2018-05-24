package com.ehi.enterprise.android.ui.reservation.widget;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PaymentInfoDialogFragmentBinding;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.HtmlParseFragmentHelper;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(PaymentInfoDialogViewModel.class)
public class PaymentInfoDialogFragment extends DataBindingViewModelFragment<PaymentInfoDialogViewModel, PaymentInfoDialogFragmentBinding> {

    private static final String TAG = "PaymentInfoDialogFragment";

    private PaymentInfoDialogFragmentBinding mBinding;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == mBinding.closeButton) {
                getActivity().finish();
            }
            else if (view == mBinding.termsOfUseLink) {
                FragmentUtils.addProgressFragment(getActivity());
                getViewModel().requestTermsOfUse();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fr_modal_dialog_pay_info, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        mBinding.closeButton.setOnClickListener(mOnClickListener);
        mBinding.termsOfUseLink.setOnClickListener(mOnClickListener);
        getActivity().setTitle(R.string.choose_your_rate_modal_title);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RATES.value, TAG)
                .state(EHIAnalytics.State.STATE_PAYMENT_OPTIONS_MODAL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        addReaction("TERMS_OF_USE_REACTION_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                GetMorePrepayTermsConditionsResponse response = getViewModel().getTermsOfUse();
                if (response != null) {
                    showModal(getActivity(),
                            new HtmlParseFragmentHelper.Builder()
                                    .title(getResources().getString(R.string.terms_and_conditions_prepay_title))
                                    .message(getViewModel().getTermsOfUse().getContent())
                                    .build());
                    getActivity().finish();
                    FragmentUtils.removeProgressFragment(getActivity());
                    getViewModel().setTermsOfUse(null);
                }
            }
        });

        addReaction("ERROR_RESPONSE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getErrorResponse();
                if (wrapper != null) {
                    DialogUtils.showErrorDialog(getActivity(), wrapper);
                    getViewModel().setResponse(null);
                    FragmentUtils.removeProgressFragment(getActivity());
                }
            }
        });
    }
}


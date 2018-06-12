package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.AdditionalInfoFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIContract;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.widget.AdditionalInfoViewHolder;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

@ViewModel(AdditionalInfoFragmentViewModel.class)
public class AdditionalInfoFragment extends DataBindingViewModelFragment<AdditionalInfoFragmentViewModel, AdditionalInfoFragmentBinding> {

    public static final String EXTRA_DATA = "ehi.EXTRA_DATA";
    @Extra(String.class)
    public static final String EXTRA_CONTRACT_NUMBER = "ehi.EXTRA_CONTRACT_NUMBER";
    @Extra(Boolean.class)
    public static final String EXTRA_PRE_RATE = "ehi.EXTRA_PRE_RATE_FLAG";
    @Extra(value = EHIContract.class, required = false)
    public static final String EXTRA_CONTRACT = "ehi.EXTRA_CONTRACT";
    @Extra(value = List.class, type = EHIAdditionalInformation.class, required = false)
    public static final String EXTRA_ADDITIONAL_INFO = "ehi.EXTRA_ADDITIONAL_INFO";
    @Extra(value = String.class, required = false)
    public static final String EXTRA_ERROR_MESSAGE = "ehi.EXTRA_ERROR_MESSAGE";
    private static final String TAG = "AdditionalInfoFragment";
    private AdditionalInfoViewHolder.OnDataChangedListener mOnDataChangedListener = new AdditionalInfoViewHolder.OnDataChangedListener() {
        @Override
        public void onDataChanged() {
            updateSubmitButton();
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().buttonSubmit) {
                displayInvalidFields();
                showToastErrorMessage();
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().buttonSubmit) {
                submit();
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CORPORATE.value, TAG)
                        .state(EHIAnalytics.State.STATE_ADDITIONAL_INFO.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SUBMIT.value)
                        .tagScreen()
                        .tagEvent();
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.additional_information_navigation_title);

        AdditionalInfoFragmentHelper.Extractor extractor = new AdditionalInfoFragmentHelper.Extractor(this);

        getViewModel().setAlreadyAddedInformation(extractor.extraAdditionalInfo());
        getViewModel().setPreRateOnly(extractor.extraPreRate());

        if (extractor.extraContract() == null) {
            getViewModel().fetchContractDetails(extractor.extraContractNumber());
        } else {
            getViewModel().setContractDetails(extractor.extraContract());
        }

        String errorMessage = extractor.extraErrorMessage();
        if (!TextUtils.isEmpty(errorMessage)) {
            DialogUtils.showDialogWithTitleAndText(getContext(), errorMessage, getString(R.string.alert_service_error_title));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_additional_info, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().buttonSubmit.setOnClickListener(mOnClickListener);
        getViewBinding().buttonSubmit.setOnDisabledClickListener(mOnDisabledClickListener);

        getViewBinding().additionalInformationView.hideSectionHeader();
        getViewBinding().additionalInformationView.setOnDataChangedListener(mOnDataChangedListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        addReaction("GET_CONTRACT_DETAILS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getContractDetails() != null) {
                    setAdditionalInformation(getViewModel().getContractDetails());
                    updateSubmitButton();
                    getViewModel().setContractDetails(null);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CORPORATE.value, TAG)
                .state(EHIAnalytics.State.STATE_ADDITIONAL_INFO.value)
                .tagScreen()
                .tagEvent();
    }

    private void submit() {
        if (!getViewBinding().additionalInformationView.isValid()) {
            return;
        }

        Bundle bundle = new EHIBundle.Builder()
                .putEHIModel(EXTRA_ADDITIONAL_INFO, getViewBinding().additionalInformationView.getAdditionalInformation())
                .createBundle();

        Intent data = new Intent();
        data.putExtra(EXTRA_DATA, bundle);

        final Activity activity = getActivity();
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    private void setAdditionalInformation(EHIContract account) {
        if (account == null) {
            // should do something better
            return;
        }

        List<EHIAdditionalInformation> additionalInfoList;
        String description = null;
        if (getViewModel().isPreRateOnly()) {
            additionalInfoList = account.getPreRateAdditionalInformation();
            description = account.getContractDescription();
        } else {
            additionalInfoList = account.getAllAdditionalInformation();
        }

        if (ListUtils.isEmpty(additionalInfoList)) {
            // should do something better
            return;
        }

        getViewBinding().additionalInformationView.setInformation(
                description,
                additionalInfoList,
                getViewModel().getAlreadyAddedInformation()
        );
    }

    private void updateSubmitButton() {
        getViewBinding().buttonSubmit.setEnabled(getViewBinding().additionalInformationView.isValidEntrySet());
    }

    private void displayInvalidFields() {
        getViewBinding().additionalInformationView.isValid();
        BaseAppUtils.hideKeyboard(getActivity());
    }

    private void showToastErrorMessage() {
        ToastUtils.showLongToast(getActivity(), R.string.review_please_enter_additional_information);
    }
}

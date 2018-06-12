package com.ehi.enterprise.android.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EditBillingNumberFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorCompoundButton;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EditBillingNumberFragmentViewModel.class)
public class EditBillingNumberFragment extends EditPaymentMethodFragment<EditBillingNumberFragmentViewModel, EditBillingNumberFragmentBinding> {

    private static final String SCREEN_NAME = "EditBillingNumberFragment";

    @Extra(EHIPaymentMethod.class)
    public static final String EXTRA_PAYMENT_METHOD = "ehi.EXTRA_PAYMENT_METHOD";

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().editBillingSaveButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.EDIT_BILLING_NUMBER.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SUMMARY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SAVE_BILLING_NUMBER.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                getViewModel().saveUpdatedPaymentMethod();
            } else if (view == getViewBinding().makePreferredView) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.EDIT_BILLING_NUMBER.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SUMMARY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHECK_DEFAULT_PAYMENT.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                getViewModel().makePreferredClicked();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_edit_billing_number, container);
        initViews();

        getActivity().setTitle(R.string.profile_payment_options_billing_edit_title);

        return getViewBinding().getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EHIPaymentMethod method = new EditBillingNumberFragmentHelper.Extractor(this).extraPaymentMethod();
        getViewModel().setPaymentMethod(method);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.EDIT_BILLING_NUMBER.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected int getDeleteAlertTitleStringResId() {
        return R.string.profile_payment_options_delete_billing_title;
    }

    @Override
    protected int getDeleteAlertMessageStringResId() {
        return R.string.profile_payment_options_delete_billing_message;
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().billingName.text(), getViewBinding().billingName));
        bind(ReactorTextView.text(getViewModel().billingNumber.text(), getViewBinding().billingNumber));
        bind(ReactorCompoundButton.bindChecked(getViewModel().makePreferredCheckBox.checked(), getViewBinding().makePreferredCheckBox));
        bind(ReactorView.visibility(getViewModel().makePreferredView.visibility(), getViewBinding().makePreferredView));
        bind(ReactorView.visibility(getViewModel().preferredMethodView.visibility(), getViewBinding().preferredMethodView));

        addReaction("ON_SAVE_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isSaveSuccessful()) {
                    getActivity().finish();
                    getViewModel().setSaveSuccessful(false);
                }
            }
        });

        addReaction("ON_DELETE_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().isDeleteSuccessful()) {
                    getActivity().finish();
                    getViewModel().setDeleteSuccessful(false);
                }
            }
        });
    }

    private void initViews() {
        getViewBinding().editBillingSaveButton.setOnClickListener(onClickListener);
        getViewBinding().makePreferredView.setOnClickListener(onClickListener);
    }
}

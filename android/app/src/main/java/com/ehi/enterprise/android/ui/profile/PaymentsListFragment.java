package com.ehi.enterprise.android.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.MyProfileEditPaymentsFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.profile.interfaces.EditPaymentListener;
import com.ehi.enterprise.android.ui.reservation.AddCreditCardFragmentHelper;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import java.util.Collections;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@NoExtras
@ViewModel(PaymentsListViewModel.class)
public class PaymentsListFragment extends DataBindingViewModelFragment<PaymentsListViewModel, MyProfileEditPaymentsFragmentBinding> {

    private EHIPaymentMethod paymentMethod;

    private static final String SCREEN_NAME = "PaymentListFragment";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().addCreditCard) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.EDIT_PAYMENTS.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_SUMMARY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_CREDIT_CARD.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                showModal(getActivity(), new AddCreditCardFragmentHelper.Builder().extraFromProfile(true).build());
            }
        }
    };

    private final EditPaymentListener callback = new EditPaymentListener() {
        @Override
        public void onEdit(EHIPaymentMethod method) {
            edit(method);
        }

        @Override
        public void onDelete(EHIPaymentMethod method) {
            delete(method);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_payments_list, container);
        initViews();

        getActivity().setTitle(R.string.profile_payment_options_edit_screen_title);

        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DeletePaymentDialogFragment.REQUEST_CODE) {
                getViewModel().delete(paymentMethod);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.EDIT_PAYMENTS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        bind(ReactorView.visibility(getViewModel().warningView.visibility(), getViewBinding().warningView));
        bind(ReactorView.visibility(getViewModel().warningViewTitle.visibility(), getViewBinding().warningViewTitle));
        bind(ReactorTextView.text(getViewModel().warningViewText.textCharSequence(), getViewBinding().warningViewText));
        bind(ReactorTextView.textRes(getViewModel().warningViewText.textRes(), getViewBinding().warningViewText));
        bind(ReactorView.visibility(getViewModel().addCreditCard.visibility(), getViewBinding().addCreditCard));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getPaymentProfile() != null) {
                    getViewBinding().billingCodeContainer.removeAllViewsInLayout();
                    getViewBinding().creditCardContainer.removeAllViewsInLayout();

                    getViewModel().setUpWarnings(getViewModel().getPaymentProfile());

                    fillBillingPaymentsSection(getViewModel().getPaymentProfile().getBillingPaymentMethods(),
                            getViewBinding().billingCodeContainer,
                            getViewBinding().billingCodeLayout);

                    fillCardPaymentsSection(getViewModel().getPaymentProfile().getCardPaymentMethods(),
                            getViewBinding().creditCardContainer,
                            getViewBinding().creditCardLayout);
                }
            }
        });

        addReaction("PAYMENT_METHOD_DELETE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getDeletedPaymentType() != null) {
                    notifyDeletedPaymentType(getViewModel().getDeletedPaymentType());
                    getViewModel().setDeletedPaymentType(null);
                }
            }
        });
    }

    private void initViews() {
        getViewBinding().addCreditCard.setOnClickListener(mOnClickListener);
    }

    private void fillBillingPaymentsSection(@NonNull List<EHIPaymentMethod> methods, LinearLayout container, LinearLayout layout) {
        Collections.sort(methods);
        if (methods.size() > 0) {
            layout.setVisibility(VISIBLE);
            setEditPaymentCallbacks(methods, container);
        } else {
            layout.setVisibility(GONE);
        }
    }

    private void fillCardPaymentsSection(@NonNull List<EHIPaymentMethod> methods, LinearLayout container, LinearLayout layout) {
        Collections.sort(methods);
        if (methods.size() > 0) {
            container.setVisibility(VISIBLE);
            setEditPaymentCallbacks(methods, container);
        } else {
            container.setVisibility(GONE);
        }
    }

    private void setEditPaymentCallbacks(List<EHIPaymentMethod> methods, LinearLayout container) {
        for (int i = 0; i < methods.size(); i++) {
            PaymentItemViewHolder viewHolder = PaymentItemViewHolder.create(container, methods.get(i));
            viewHolder.setListener(callback);
            container.addView(viewHolder.itemView);
        }
    }

    private void delete(final EHIPaymentMethod method) {
        paymentMethod = method;

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.EDIT_PAYMENTS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, paymentMethod.isCreditCard() ? EHIAnalytics.Action.ACTION_REMOVE_CREDIT_CARD.value : EHIAnalytics.Action.ACTION_REMOVE_BILLING_NUMBER.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();

        final DeletePaymentDialogFragment fragment = new DeletePaymentDialogFragmentHelper.Builder().paymentMethod(method).build();
        showModalDialogForResult(getActivity(), fragment, DeletePaymentDialogFragment.REQUEST_CODE);
    }

    private void notifyDeletedPaymentType(String paymentType) {
        boolean isCreditCard = EHIPaymentMethod.TYPE_CREDIT_CARD.equals(paymentType);
        int successMessageId = isCreditCard
                ? R.string.profile_payment_options_delete_credit_card_success
                : R.string.profile_payment_options_delete_billing_success;

        ToastUtils.showLongToast(
                getContext(),
                successMessageId
        );
    }

    private void edit(EHIPaymentMethod method) {
        boolean isCreditCard = method.isCreditCard();
        final Fragment editFragment;
        final String eventName;
        if (isCreditCard) {
            editFragment = new EditCreditCardFragmentHelper.Builder().extraPaymentMethod(method).build();
            eventName =  EHIAnalytics.Action.ACTION_EDIT_CREDIT_CARD.value;
        } else {
            editFragment = new EditBillingNumberFragmentHelper.Builder().extraPaymentMethod(method).build();
            eventName =  EHIAnalytics.Action.ACTION_EDIT_BILLING_NUMBER.value;
        }

        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.EDIT_PAYMENTS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, eventName)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();

        showModal(getActivity(), editFragment);
    }

}

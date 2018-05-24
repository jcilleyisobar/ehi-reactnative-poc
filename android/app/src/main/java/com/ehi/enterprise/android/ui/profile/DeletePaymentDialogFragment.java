package com.ehi.enterprise.android.ui.profile;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.DeletePaymentDialogFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(ManagersAccessViewModel.class)
public class DeletePaymentDialogFragment extends DataBindingViewModelFragment<ManagersAccessViewModel, DeletePaymentDialogFragmentBinding> {

    private static final String SCREEN_NAME = "DeletePaymentDialogFragment";

    @Extra(EHIPaymentMethod.class)
    public static final String PAYMENT_METHOD = "ehi.PAYMENT_METHOD";

    public static final int REQUEST_CODE = 1003;

    private EHIPaymentMethod paymentMethod;
    private boolean isCreditCard;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().deleteButton) {
                EHIAnalyticsEvent.create()
                        .screen(isCreditCard ? EHIAnalytics.Screen.CREDIT_CARD_DELETION.value : EHIAnalytics.Screen.BILLING_CODE_DELETION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_MODAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_DELETE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (view == getViewBinding().cancelButton) {
                EHIAnalyticsEvent.create()
                        .screen(isCreditCard ? EHIAnalytics.Screen.CREDIT_CARD_DELETION.value : EHIAnalytics.Screen.BILLING_CODE_DELETION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_MODAL.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CLOSE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                        .tagScreen()
                        .tagEvent();

                getActivity().finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeletePaymentDialogFragmentHelper.Extractor extractor = new DeletePaymentDialogFragmentHelper.Extractor(this);
        paymentMethod = extractor.paymentMethod();
        isCreditCard = paymentMethod.isCreditCard();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_delete_payment_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(isCreditCard ? EHIAnalytics.Screen.CREDIT_CARD_DELETION.value : EHIAnalytics.Screen.BILLING_CODE_DELETION.value, SCREEN_NAME)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .state(EHIAnalytics.State.STATE_MODAL.value)
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().deleteButton.setOnClickListener(mOnClickListener);
        getViewBinding().cancelButton.setOnClickListener(mOnClickListener);

        int dialogMessageRes = paymentMethod.isCreditCard()
                ? R.string.profile_payment_options_delete_credit_card_message
                : R.string.profile_payment_options_delete_billing_message;

        getViewBinding().title.setText(paymentMethod.getMaskedCreditCardNumber());
        getViewBinding().description.setText(dialogMessageRes);
    }
}

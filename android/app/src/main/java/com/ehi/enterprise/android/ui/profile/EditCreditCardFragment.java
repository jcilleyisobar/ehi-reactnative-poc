package com.ehi.enterprise.android.ui.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.EditCreditCardFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.analytics.SpinnerDateDialog;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorCompoundButton;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(EditCreditCardFragmentViewModel.class)
public class EditCreditCardFragment extends EditPaymentMethodFragment<EditCreditCardFragmentViewModel, EditCreditCardFragmentBinding> {

    private static final String SCREEN_NAME = "EditCreditCardFramgent";

    @Extra(EHIPaymentMethod.class)
    public static final String EXTRA_PAYMENT_METHOD = "ehi.EXTRA_PAYMENT_METHOD";

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String eventName = "";
            if (view == getViewBinding().editCreditCardSaveButton) {
                eventName = EHIAnalytics.Action.ACTION_SAVE_CC.value;
                getViewModel().saveUpdatedPaymentMethod();
            } else if (view == getViewBinding().makePreferredView) {
                eventName = EHIAnalytics.Action.ACTION_CHECK_DEFAULT_PAYMENT.value;
                getViewModel().makePreferredClicked();
            } else if (view == getViewBinding().editExpirationButton) {
                eventName = EHIAnalytics.Action.ACTION_EDIT_CC_DATE.value;
                showDatePicker();
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.EDIT_CREDIT_CARD.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_SUMMARY.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, eventName)
                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                    .tagScreen()
                    .tagEvent();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_edit_credit_card, container);
        initViews();

        getActivity().setTitle(R.string.profile_payment_options_credit_card_edit_title);

        return getViewBinding().getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EHIPaymentMethod method = new EditCreditCardFragmentHelper.Extractor(this).extraPaymentMethod();
        getViewModel().setPaymentMethod(method);
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.EDIT_CREDIT_CARD.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected int getDeleteAlertTitleStringResId() {
        return R.string.profile_payment_options_delete_credit_card_title;
    }

    @Override
    protected int getDeleteAlertMessageStringResId() {
        return R.string.profile_payment_options_delete_credit_card_message;
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorTextView.bindText(getViewModel().creditCardName.text(), getViewBinding().creditCardName));
        bind(ReactorTextView.text(getViewModel().creditCardNumber.text(), getViewBinding().creditCardNumber));
        bind(ReactorTextView.drawableLeft(getViewModel().creditCardNumber.drawableLeft(), getViewBinding().creditCardNumber));
        bind(ReactorTextView.text(getViewModel().creditCardExpirationDate.text(), getViewBinding().creditCardExpirationDate));
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
        getViewBinding().editCreditCardSaveButton.setOnClickListener(onClickListener);
        getViewBinding().makePreferredView.setOnClickListener(onClickListener);
        getViewBinding().editExpirationButton.setOnClickListener(onClickListener);
    }

    private void showDatePicker() {
        SpinnerDateDialog dialog = new SpinnerDateDialog();

        if (getViewModel().getExpirationDate() != null) {
            dialog.setInitialDate(getViewModel().getExpirationDate());
        }

        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                getViewModel().setExpirationDate(year, month, dayOfMonth);
            }
        });

        dialog.show(getFragmentManager(), "SpinnerDateDialog");
    }
}

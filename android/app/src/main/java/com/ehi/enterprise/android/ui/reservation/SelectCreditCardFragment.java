package com.ehi.enterprise.android.ui.reservation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.SelectPaymentFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.profile.EHIPaymentProfile;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.profile.EditBillingNumberFragmentHelper;
import com.ehi.enterprise.android.ui.profile.EditCreditCardFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.PaymentItemCheckListener;
import com.ehi.enterprise.android.ui.reservation.view_holders.PaymentItemCheckViewHolder;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@NoExtras
@ViewModel(SelectCreditCardViewModel.class)
public class SelectCreditCardFragment extends DataBindingViewModelFragment<SelectCreditCardViewModel, SelectPaymentFragmentBinding> {

    public static final String EXTRA_SHOULD_SHOW_PREPAY_TERMS = "EXTRA_SHOULD_SHOW_PREPAY_TERMS";
    public static final String SCREEN_NAME = "SelectCreditCardFragment";

    private SelectPaymentAdapter mAdapter;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().addCreditCard) {
                showModalForResult(getActivity(), new AddCreditCardFragmentHelper.Builder().build(), AddCreditCardFragment.REQUEST_CODE);
            } else if (view == getViewBinding().haveReadConditions) {
                getViewModel().requestPrepaymentPolicy();
            } else if (view == getViewBinding().continueButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_SELECT_CREDIT_CARD.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_CREDIT_CARD_DETAILS.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CONTINUE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .tagScreen()
                        .tagEvent();

                getViewModel().updateShouldAutomaticallySelectCard();

                Intent data = new Intent();
                final Bundle extras = new EHIBundle.Builder()
                        .putString(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE, getViewModel().getPaymentReferenceId())
                        .putBoolean(EXTRA_SHOULD_SHOW_PREPAY_TERMS, getViewModel().shouldAutomaticallySelectCardStateChanged())
                        .createBundle();
                data.putExtras(extras);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            }
        }
    };

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view == getViewBinding().continueButton) {
                if (!getViewBinding().haveReadConditionsCheckBox.isChecked()) {
                    ToastUtils.showToast(getActivity(), R.string.review_prepay_na_terms_not_selected);
                }
            }
        }
    };

    private final PaymentItemCheckListener mOnCheckListener = new PaymentItemCheckListener() {
        @Override
        public void onEdit(EHIPaymentMethod method) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_SELECT_CREDIT_CARD.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_CREDIT_CARD_DETAILS.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_EDIT_CARD.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                    .tagScreen()
                    .tagEvent();

            edit(method);
        }

        @Override
        public void onCheck(PaymentItemCheckViewHolder viewHolder, EHIPaymentMethod method, int position) {
            getViewModel().setPaymentReferenceId(method.getPaymentReferenceId());
            if (getViewBinding().haveReadConditionsCheckBox.isChecked()) {
                getViewModel().validateForm(true);
            }
        }

        @Override
        public void onClick(EHIPaymentMethod method) {

        }
    };
    private CompoundButton.OnCheckedChangeListener mOnCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_SELECT_CREDIT_CARD.value, SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_CREDIT_CARD_DETAILS.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHECK_TERMS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                    .tagScreen()
                    .tagEvent();

            getViewModel().validateForm(checked);
        }
    };

    final CompoundButton.OnCheckedChangeListener mOnAutomaticallySelectCardCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            getViewModel().setShouldAutoSelect(checked);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.reservation_select_payment_title);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == AddCreditCardFragment.REQUEST_CODE) {
            getActivity().setResult(Activity.RESULT_OK, data);
            getActivity().finish();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createViewBinding(inflater, R.layout.fr_select_payment, container);
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SELECT_CREDIT_CARD.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_CREDIT_CARD_DETAILS.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    private void initViews() {

        SpannableString textToShow = new SpannableString(getResources().getString(R.string.terms_and_conditions_prepay_title));
        textToShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, textToShow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getViewBinding().haveReadConditions.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.review_prepay_policies_read)
                .addTokenAndValue(EHIStringToken.POLICIES, textToShow)
                .format());

        getViewBinding().addCreditCard.setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditions.setOnClickListener(mOnClickListener);
        getViewBinding().haveReadConditionsCheckBox.setOnCheckedChangeListener(mOnCheckChangedListener);
        getViewBinding().continueButton.setOnClickListener(mOnClickListener);
        getViewBinding().continueButton.setOnDisabledClickListener(mOnDisabledClickListener);
        getViewBinding().creditCardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(ReactorView.enabled(getViewModel().continueButton.enabled(), getViewBinding().continueButton));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHIPaymentProfile paymentProfile = getViewModel().getPaymentProfile();
                final List<EHIPaymentMethod> cards = paymentProfile == null ?
                        new ArrayList<EHIPaymentMethod>() :
                        paymentProfile.getCardPaymentMethods();

                if (cards.isEmpty() || getViewModel().getSelectedPaymentMethod() == null) {
                    getViewModel().setPaymentReferenceId(null);
                    getViewModel().validateForm(false);
                }

                if (mAdapter == null) {
                    mAdapter = new SelectPaymentAdapter(cards,
                            getViewModel().shouldAutomaticallySelectCard(),
                            mOnCheckListener,
                            mOnAutomaticallySelectCardCheckedListener);
                    getViewBinding().creditCardRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setCardPaymentMethods(cards);
                }
            }
        });

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getPrepayTermsAndConditions() != null) {
                    showModal(getActivity(), new HtmlParseFragmentHelper.Builder()
                            .title(getResources().getString(R.string.terms_and_conditions_prepay_title))
                            .message(getViewModel().getPrepayTermsAndConditions())
                            .build());
                    getViewModel().setPrepayTermsAndConditions(null);
                }
            }
        });
    }

    private void edit(EHIPaymentMethod method) {
        boolean isCreditCard = method.isCreditCard();

        Fragment fragment = isCreditCard
                ? new EditCreditCardFragmentHelper.Builder().extraPaymentMethod(method).build()
                : new EditBillingNumberFragmentHelper.Builder().extraPaymentMethod(method).build();

        showModal(getActivity(), fragment);
    }

}

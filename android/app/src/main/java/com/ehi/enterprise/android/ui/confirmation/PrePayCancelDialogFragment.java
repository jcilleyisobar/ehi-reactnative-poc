package com.ehi.enterprise.android.ui.confirmation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.PrePayCancelDialogFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHICancellation;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.HtmlParseFragmentHelper;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(PrePayCancelDialogViewModel.class)
public class PrePayCancelDialogFragment extends DataBindingViewModelFragment<PrePayCancelDialogViewModel, PrePayCancelDialogFragmentBinding> {

    public static final String SCREEN_NAME = "PrePayCancelDialogFragment";

    @Extra(value = String.class)
    public static final String EXTRA_ORIGINAL_AMOUNT = "ehi.EXTRA_ORIGINAL_AMOUNT";

    @Extra(value = EHICancellation.class)
    public static final String EXTRA_CANCELLATION = "ehi.EXTRA_CANCELLATION";

    @Extra(value = Boolean.class)
    public static final String EXTRA_IS_MODIFY = "ehi.IS_MODIFY";

    private boolean isModify;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().prepayCancelReservationButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_YES.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(isModify))
                        .tagScreen()
                        .tagEvent();
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (v == getViewBinding().prepayKeepReservationButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_NO.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(isModify))
                        .tagScreen()
                        .tagEvent();

                getActivity().finish();
            } else if (v == getViewBinding().prepayCancelText) {
                getViewModel().requestPrepaymentPolicy();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PrePayCancelDialogFragmentHelper.Extractor extractor = new PrePayCancelDialogFragmentHelper.Extractor(this);
        getViewModel().setEHICancelation(extractor.extraCancellation());
        getViewModel().setIsModify(extractor.extraIsModify());
        getViewModel().setOriginalAmount(extractor.extraOriginalAmount());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_prepay_cancel_dialog, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(isModify))
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        addReaction("PREPAY_TERMS_CONDITIONS", new ReactorComputationFunction() {
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

    private void initViews() {
        getActivity().setTitle(R.string.reservation_cancel_message_title);

        bind(ReactorTextView.text(getViewModel().cancelTitleViewState.textCharSequence(), getViewBinding().prepayCancelText));
        bind(ReactorTextView.text(getViewModel().cancellationFeeViewState.text(), getViewBinding().prepayCancelCancellationFee));
        bind(ReactorTextView.text(getViewModel().refundedAmountViewState.textCharSequence(), getViewBinding().prepayCancelRefundedAmount));
        bind(ReactorTextView.text(getViewModel().originalAmountViewState.textCharSequence(), getViewBinding().prepayCancelOriginalAmount));
        bind(ReactorTextView.text(getViewModel().refundedAsViewState.textCharSequence(), getViewBinding().refundedAsAmount));
        bind(ReactorView.visibility(getViewModel().refundedAsViewState.visibility(), getViewBinding().refundedAsAmount));
        bind(ReactorView.visibility(getViewModel().travelingBetweenUsAndCanadaViewState.visibility(), getViewBinding().travelingUsCanadaClarification));
        bind(ReactorTextView.text(getViewModel().travelingBetweenUsAndCanadaViewState.textCharSequence(), getViewBinding().travelingUsCanadaClarification));

        getViewBinding().prepayCancelReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().prepayKeepReservationButton.setOnDisabledClickListener(mOnClickListener);
        getViewBinding().prepayCancelText.setOnClickListener(mOnClickListener);
    }
}
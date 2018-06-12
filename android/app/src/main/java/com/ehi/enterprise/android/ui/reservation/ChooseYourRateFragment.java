package com.ehi.enterprise.android.ui.reservation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.ChooseYourRateFragmentBinding;
import com.ehi.enterprise.android.network.responses.reservation.GetMorePrepayTermsConditionsResponse;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.view_controllers.CarClassSelectViewController;
import com.ehi.enterprise.android.ui.reservation.view_holders.CarClassSelectViewHolder;
import com.ehi.enterprise.android.ui.reservation.widget.EPointsHeaderView;
import com.ehi.enterprise.android.ui.reservation.widget.PaymentInfoDialogFragment;
import com.ehi.enterprise.android.ui.widget.RateButton;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;

@ViewModel(ChooseYourRateViewModel.class)
public class ChooseYourRateFragment extends DataBindingViewModelFragment<ChooseYourRateViewModel, ChooseYourRateFragmentBinding> {
    public static final String TAG = "ChooseYourRateFragment";

    public static final long ANIMATION_START_UP_DELAY = 200;
    public static final long ANIMATION_DURATION = 200;

    @Extra(boolean.class)
    public static final String IS_MODIFY = "EXTRA_IS_MODIFY";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            if (v == getViewBinding().prepayTermsConditions) {
                getViewModel().requestTermsOfUse();
            } else if (v == getViewBinding().paymentInfo) {
                showModalDialog(getActivity(), new PaymentInfoDialogFragment());
            } else if (v == getViewBinding().rentalTermsConditions) {
                showModal(getActivity(), new RentalTermsConditionsFragmentHelper.Builder().build());
            } else {
                final ReservationFlowListener flowListener = (ReservationFlowListener) getActivity();
                final EHIAnalyticsEvent event = EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RATES.value, TAG)
                        .state(EHIAnalytics.State.STATE_CHOOSE_RATE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation());
                if (v == getViewBinding().firstPaymentButton) {
                    executeActionForPayState(getViewModel().getPayStateForFirstButton(), flowListener, event);
                } else if (v == getViewBinding().secondPaymentButton) {
                    executeActionForPayState(getViewModel().getPayStateForSecondButton(), flowListener, event);
                } else if (v == getViewBinding().redeemPointsButton) {
                    flowListener.setPayState(ReservationFlowListener.PayState.REDEMPTION);
                    event.macroEvent(EHIAnalytics.MacroEvent.MACRO_RATE_SELECTED.value);
                    event.action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REDEEM.value);
                }
                event.tagScreen().tagMacroEvent().tagEvent();
                getViewModel().requestCarClassDetails(flowListener.getPayState());
            }
        }
    };

    private void executeActionForPayState(ReservationFlowListener.PayState payState, ReservationFlowListener flowListener, EHIAnalyticsEvent event) {
        if (ReservationFlowListener.PayState.PREPAY == payState) {
            event.action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PAY_NOW.value);
        } else if (ReservationFlowListener.PayState.PAY_LATER == payState) {
            event.action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PAY_LATER.value);
        }
        event.macroEvent(EHIAnalytics.MacroEvent.MACRO_RATE_SELECTED.value);
        flowListener.setPayState(payState);
    }

    private View.OnClickListener mOnDisabledClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {

            if (v == getViewBinding().redeemPointsButton) {
                ToastUtils.showLongToast(getActivity(), R.string.choose_your_rate_redeem_not_enough_points_subtitle);
            }
        }
    };

    @Override
    public void setViewModel(final ChooseYourRateViewModel viewModel) {
        super.setViewModel(viewModel);
        final ChooseYourRateFragmentHelper.Extractor extractor
                = new ChooseYourRateFragmentHelper.Extractor(this);
        getViewModel().setModify(extractor.isModify());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_choose_your_rate, container);
        initViews();
        return getViewBinding().getRoot();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RATES.value, TAG)
                .state(EHIAnalytics.State.STATE_CHOOSE_RATE.value)
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_CHOOSE_YOUR_RATE.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagMacroEvent()
                .tagEvent();
    }

    private void initViews() {
        getViewBinding().firstPaymentButton.setOnClickListener(mOnClickListener);
        getViewBinding().secondPaymentButton.setOnClickListener(mOnClickListener);
        getViewBinding().redeemPointsButton.setOnClickListener(mOnClickListener);
        getViewBinding().paymentInfo.setOnClickListener(mOnClickListener);
        getViewBinding().prepayTermsConditions.setOnClickListener(mOnClickListener);
        getViewBinding().rentalTermsConditions.setOnClickListener(mOnClickListener);
        getViewBinding().redeemPointsButton.setOnDisabledClickListener(mOnDisabledClickListener);

        final CarClassSelectViewHolder holder = new CarClassSelectViewHolder(getViewBinding().carCell);
        holder.getViewBinding().classDetailsContainer.setVisibility(View.GONE);
        new CarClassSelectViewController.Builder()
                .forHolder(holder)
                .carClassDetails(getViewModel().getCarClassDetails())
                .showPrice(false)
                .fromChooseYourRate(true)
                .build(getActivity());

        if (!getViewModel().isAnimatedOnce()) {
            getViewBinding().carRateView.setVisibility(View.INVISIBLE);
            getViewBinding().carRateView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getViewBinding().carRateView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    getViewModel().setAnimatedOnce(true);

                    getViewBinding().carRateView.setTranslationY(-1 * getViewBinding().carRateView.getMeasuredHeight());

                    getViewBinding().carRateView.setVisibility(View.VISIBLE);

                    getViewBinding().carRateView.animate()
                            .translationY(0)
                            .setStartDelay(ANIMATION_START_UP_DELAY)
                            .setDuration(ANIMATION_DURATION);
                }
            });
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));

        bind(ReactorView.visibility(getViewModel().firstPaymentButton.visibility(), getViewBinding().firstPaymentButton));
        bind(ReactorView.enabled(getViewModel().firstPaymentButton.enabled(), getViewBinding().firstPaymentButton));
        bind(RateButton.price(getViewModel().firstPaymentButton.price(), getViewBinding().firstPaymentButton));
        bind(RateButton.priceSubtitle(getViewModel().firstPaymentButton.priceSubtitle(), getViewBinding().firstPaymentButton));
        bind(RateButton.secondaryText(getViewModel().firstPaymentButton.secondaryText(), getViewBinding().firstPaymentButton));
        bind(RateButton.primaryText(getViewModel().firstPaymentButton.primaryText(), getViewBinding().firstPaymentButton));
        bind(RateButton.savingPrice(getViewModel().firstPaymentButton.savingPrice(), getViewBinding().firstPaymentButton));
        bind(RateButton.savingPriceVisibility(getViewModel().firstPaymentButton.savingPriceVisibility(), getViewBinding().firstPaymentButton));
        bind(RateButton.arrow(getViewModel().firstPaymentButton.arrow(), getViewBinding().firstPaymentButton));

        bind(ReactorView.visibility(getViewModel().secondPaymentButton.visibility(), getViewBinding().secondPaymentButton));
        bind(ReactorView.enabled(getViewModel().secondPaymentButton.enabled(), getViewBinding().secondPaymentButton));
        bind(RateButton.price(getViewModel().secondPaymentButton.price(), getViewBinding().secondPaymentButton));
        bind(RateButton.priceSubtitle(getViewModel().secondPaymentButton.priceSubtitle(), getViewBinding().secondPaymentButton));
        bind(RateButton.savingPrice(getViewModel().secondPaymentButton.savingPrice(), getViewBinding().secondPaymentButton));
        bind(RateButton.savingPriceVisibility(getViewModel().secondPaymentButton.savingPriceVisibility(), getViewBinding().secondPaymentButton));
        bind(RateButton.primaryText(getViewModel().secondPaymentButton.primaryText(), getViewBinding().secondPaymentButton));
        bind(RateButton.secondaryText(getViewModel().secondPaymentButton.secondaryText(), getViewBinding().secondPaymentButton));
        bind(RateButton.arrow(getViewModel().secondPaymentButton.arrow(), getViewBinding().secondPaymentButton));

        bind(ReactorView.enabled(getViewModel().redeemPointsButton.enabled(), getViewBinding().redeemPointsButton));
        bind(ReactorView.visibility(getViewModel().redeemPointsButton.visibility(), getViewBinding().redeemPointsButton));
        bind(RateButton.price(getViewModel().redeemPointsButton.price(), getViewBinding().redeemPointsButton));
        bind(RateButton.secondaryText(getViewModel().redeemPointsButton.secondaryText(), getViewBinding().redeemPointsButton));
        bind(RateButton.priceSubtitle(getViewModel().redeemPointsButton.priceSubtitle(), getViewBinding().redeemPointsButton));
        bind(RateButton.arrow(getViewModel().redeemPointsButton.arrow(), getViewBinding().redeemPointsButton));
        bind(RateButton.warningIcon(getViewModel().redeemPointsButton.warningIcon(), getViewBinding().redeemPointsButton));

        bind(EPointsHeaderView.points(getViewModel().ePointsHeader.points(), getViewBinding().epointsHeaderView));
        bind(ReactorView.visibility(getViewModel().ePointsHeader.visibility(), getViewBinding().epointsHeaderView));
        bind(ReactorView.visibility(getViewModel().introductionMessageView.visibility(), getViewBinding().introductionMessageView));
        bind(ReactorView.visibility(getViewModel().spacementView.visibility(), getViewBinding().spacementView));
        bind(ReactorView.backgroundColor(getViewModel().triangleView.backgroundColor(), getViewBinding().triangleView));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                if (getViewModel().getRequestedCarClassDetails() != null) {
                    final ReservationFlowListener flowListener = (ReservationFlowListener) getActivity();
                    switch (flowListener.getPayState()) {
                        case PAY_LATER:
                            flowListener.showCarExtras(getViewModel().getRequestedCarClassDetails(), false, ReservationFlowListener.PayState.PAY_LATER, true);
                            break;
                        case PREPAY:
                            flowListener.showCarExtras(getViewModel().getRequestedCarClassDetails(), false, ReservationFlowListener.PayState.PREPAY, true);
                            break;
                        case REDEMPTION:
                            flowListener.showRedemption(getViewModel().getRequestedCarClassDetails(), true);
                            break;
                    }

                    getViewModel().setRequestedCarClassDetails(null);
                }
            }
        });

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
                    getViewModel().setTermsOfUse(null);
                }
            }
        });
    }

}

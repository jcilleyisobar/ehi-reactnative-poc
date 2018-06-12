package com.ehi.enterprise.android.ui.reservation.redemption;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.RedemptionFragmentBinding;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.BookRentalButton;
import com.ehi.enterprise.android.ui.reservation.widget.EPointsHeaderView;
import com.ehi.enterprise.android.ui.widget.StepperView;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.widget.ReactorTextView;

@ViewModel(RedemptionViewModel.class)
public class RedemptionFragment extends DataBindingViewModelFragment<RedemptionViewModel, RedemptionFragmentBinding> {

    public static final String SCREEN_NAME = "RedemptionFragment";
    public static final String TAG = RedemptionFragment.class.getSimpleName();

    @Extra(EHICarClassDetails.class)
    public static final String CAR_CLASS = "CAR_CLASS";
    @Extra(boolean.class)
    public static final String FROM_CHOOSE_YOUR_RATE = "FROM_CHOOSE_YOUR_RATE";
    @Extra(value = boolean.class, required = false)
    public static final String IS_MODIFY = "EXTRA_IS_MODIFY";

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == getViewBinding().bookRentalButton) {
                getViewModel().continueToRegistration();
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, RedemptionFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_REDEEM.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_VIEW_CONTINUE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();
            }

        }
    };

    private StepperView.StepperListener mStepperListener = new StepperView.StepperListener() {
        @Override
        public void onMinusClicked() {
            getViewModel().minusClicked();
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, RedemptionFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REDEEM.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MINUS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
        }

        @Override
        public void onPlusClicked() {
            getViewModel().plusClicked();
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, RedemptionFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REDEEM.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MINUS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final RedemptionFragmentHelper.Extractor extractor = new RedemptionFragmentHelper.Extractor(this);

        getViewModel().updateCurrentPoints();
        getViewModel().setIsModify(extractor.isModify());
        getViewModel().setCarClass(extractor.carClass(), true);
        getViewModel().setFromChooseYourRate(extractor.fromChooseYourRate());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_redemption, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().frRedemptionPointsHeader.setDividerVisible(false);
        getViewBinding().frRedemptionPointsHeader.setTopRightText("", null);
        getViewBinding().stepper.setStepperListener(mStepperListener);
        getViewBinding().bookRentalButton.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(EPointsHeaderView.points(getViewModel().currentPoints, getViewBinding().frRedemptionPointsHeader));

        bind(ReactorTextView.text(getViewModel().pointsPerFreeDayText, getViewBinding().pointsPerFreeDayText));
        bind(ReactorTextView.text(getViewModel().pointsSpentText, getViewBinding().pointsSpentText));

        bind(StepperView.text(getViewModel().stepperText, getViewBinding().stepper));
        bind(StepperView.minusButton(getViewModel().minusButtonEnabled, getViewBinding().stepper));
        bind(StepperView.plusButton(getViewModel().plusButtonEnabled, getViewBinding().stepper));

        bind(BookRentalButton.title(getViewModel().bookButtonText, getViewBinding().bookRentalButton));
        bind(BookRentalButton.price(getViewModel().bookRentalPrice, getViewBinding().bookRentalButton));
        bind(BookRentalButton.enabled(getViewModel().bookRentalButtonEnabled, getViewBinding().bookRentalButton));
        bind(BookRentalButton.priceSubtitle(getViewModel().bookRentalButtonSubtitle, getViewBinding().bookRentalButton));
        bind(BookRentalButton.progress(getViewModel().progress, getViewBinding().bookRentalButton));

        bind(ReactorTextView.text(getViewModel().originalTotal, getViewBinding().frRedemptionOriginalTotal));
        bind(ReactorTextView.text(getViewModel().creditValue, getViewBinding().frRedemptionCreditAmount));

        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(ToastUtils.longToast(getViewModel().maxReached, getActivity(), R.string.redemption_max_redeemable_days_reached_toast));

        addReaction("SUCCESS_RESPONSE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getSuccessResponse().getValue();
                if (wrapper != null) {
                    final ReservationFlowListener.PayState payState = getViewModel().getPayState();
                    ((ReservationFlowListener) getActivity()).setPayState(payState);
                    if (getViewModel().isFromChooseYourRate()) {
                        ((ReservationFlowListener) getActivity()).showCarExtras(getViewModel().getClassDetails(), false, payState, false);
                    } else {
                        getActivity().onBackPressed();
                    }
                    getViewModel().getSuccessResponse().setValue(null);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, RedemptionFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_REDEEM.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.review())
                .tagScreen()
                .tagEvent();
    }
}

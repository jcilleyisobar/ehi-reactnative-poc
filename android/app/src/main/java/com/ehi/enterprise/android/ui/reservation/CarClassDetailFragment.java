package com.ehi.enterprise.android.ui.reservation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.CarClassDetailFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.ClassDetailInfoView;
import com.ehi.enterprise.android.ui.reservation.widget.EPointsHeaderView;
import com.ehi.enterprise.android.ui.reservation.widget.PriceSummaryShortView;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.ehi.enterprise.android.utils.image.EHIImageLoader;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;

import static com.ehi.enterprise.android.utils.DialogUtils.errorDialog;
import static io.dwak.reactorbinding.activity.ReactorActivity.titleRes;
import static io.dwak.reactorbinding.view.ReactorView.visible;
import static io.dwak.reactorbinding.widget.ReactorTextView.text;
import static io.dwak.reactorbinding.widget.ReactorTextView.textRes;

@ViewModel(CarClassDetailViewModel.class)
public class CarClassDetailFragment extends DataBindingViewModelFragment<CarClassDetailViewModel, CarClassDetailFragmentBinding> {

    public static final String SCREEN_NAME = "CarClassDetailFragment";
    public static final String TAG = CarClassDetailFragment.class.getSimpleName();

    //region extras
    @Extra(EHICarClassDetails.class)
    public static final String CAR_CLASS_DETAILS = "CAR_CLASS_DETAILS";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.EXTRA_IS_MODIFY";
    //endregion

    private static final String CLASS_DETAIL_REACTION = "CLASS_DETAIL_REACTION";
    private static final String SHOW_POINTS_REACTION = "SHOW_POINTS_REACTION";

    //region onClickListener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().buttonCallLocation) {
                String cfaPhone = getViewModel().getCarClassDetails().getCallForAvailabilityPhoneNumber();
                if (TextUtils.isEmpty(cfaPhone)) {
                    cfaPhone = getViewModel().getReservationObject().getPickupLocation().getPrimaryPhoneNumber();
                }
                callLocation(cfaPhone);
            } else if (view == getViewBinding().buttonSelectThisClass) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassDetailFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_DETAILS_CURRENCY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SELECT_CLASS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_CLASS_SELECTED.value)
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();
                getViewModel().setCarClassAsSelected();
            } else if (view == getViewBinding().headerTotalContainer) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassDetailFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_DETAILS_CURRENCY.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_TOTAL_COST.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                        .macroEvent(EHIAnalytics.MacroEvent.MACRO_CLASS_SELECTED.value)
                        .tagScreen()
                        .tagEvent()
                        .tagMacroEvent();
                getViewModel().setCarClassAsSelected();
            } else if (view == getViewBinding().rentalTermsConditions) {
                showModal(getActivity(), new RentalTermsConditionsFragmentHelper.Builder().build());
            }
        }
    };
    //endregion

    //region priceSummaryEventsListener
    private PriceSummaryShortView.OnShortPriceSummaryEventsListener mPriceSummaryEventsListener = new PriceSummaryShortView.OnShortPriceSummaryEventsListener() {

        @Override
        public void onExtrasClicked(EHIExtraItem item) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassDetailFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_DETAILS_CURRENCY.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODAL_LAUNCH.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.modalLaunch(item.getCode()))
                    .tagScreen()
                    .tagEvent();
            showModalDialog(getActivity(), new ModalTextDialogFragmentHelper.Builder().title(item.getName())
                    .text(item.getDetailedDescription())
                    .build());
        }

        @Override
        public void onTaxesAndFeesClicked(EHIPriceSummary priceSummary) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassDetailFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_DETAILS.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_TAXES_AND_FEES.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                    .tagScreen()
                    .tagEvent();

            Fragment fragment = new PriceDetailFragmentHelper.Builder()
                    .paymentLineItems(priceSummary.getAllPaymentLineItems())
                    .build();

            showModalDialog(getActivity(), fragment);
        }
    };
    //endregion

    //region lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            CarClassDetailFragmentHelper.Extractor extractor = new CarClassDetailFragmentHelper.Extractor(this);
            getViewModel().setIsModify(extractor.isModify());
            getViewModel().setPayState(getViewModel().getDefaultPayState(getViewModel().isModify()));
            getViewModel().setCarClassDetails(extractor.carClassDetails());
        }
        if (!(getActivity() instanceof ReservationFlowListener)) {
            throw new NotImplementedException();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_class_detail, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassDetailFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_DETAILS_CURRENCY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }
    //endregion

    private void initViews() {
        getViewBinding().frClassDetailsPriceSummaryShort.setOnShortPriceSummaryEventsListener(mPriceSummaryEventsListener);
        getViewBinding().headerTotalContainer.setOnClickListener(mOnClickListener);
        getViewBinding().buttonSelectThisClass.setOnClickListener(mOnClickListener);
        getViewBinding().buttonCallLocation.setOnClickListener(mOnClickListener);
        getViewBinding().rentalTermsConditions.setOnClickListener(mOnClickListener);

        if (getViewModel().isUserLoggedIn()) {
            final EHILoyaltyData ehiLoyaltyData = getViewModel().getUserProfileCollection().getBasicProfile().getLoyaltyData();
            final long pointsToDate = ehiLoyaltyData != null ? ehiLoyaltyData.getPointsToDate() : 0;
            getViewBinding().epointsHeader.shouldShowPoints(true);
            getViewBinding().epointsHeader.setTopRightText(getString(R.string.redemption_header_points_show_label),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getViewModel().ePointsHeaderTopRightButtonClicked();
                        }
                    })
                    .setTopLeftHeaderText(getString(R.string.redemption_header_points_header))
                    .setTopLeftPointsText(pointsToDate);
        } else {
            getViewBinding().epointsHeader.shouldShowPoints(false);
            getViewBinding().pointsContainer.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(errorDialog(getViewModel().errorResponse, getActivity()));
        bind(titleRes(getViewModel().title, getActivity()));
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));

        bind(visible(getViewModel().pointsContainerVisibility, getViewBinding().pointsContainer));
        bind(visible(getViewModel().ePointsHeaderVisibility, getViewBinding().epointsHeader));

        bind(text(getViewModel().nameOfClass, getViewBinding().className));
        bind(text(getViewModel().nameOfCar, getViewBinding().carName));
        bind(text(getViewModel().carClassDescription, getViewBinding().classDetailDescription));
        bind(text(getViewModel().carTransmissionDescription, getViewBinding().carTransmission));

        bind(ClassDetailInfoView.text(getViewModel().peopleCapacityText, getViewBinding().passengersInfoView));
        bind(ClassDetailInfoView.text(getViewModel().luggageCapacityText, getViewBinding().maxLuggageInfoView));

        bind(PriceSummaryShortView.carClassDetails(getViewModel().mCarClassDetails, getViewModel().mPayState, getViewBinding().frClassDetailsPriceSummaryShort));

        bind(EPointsHeaderView.showPoints(getViewModel().ePointsHeaderShouldShowPoints, getViewBinding().epointsHeader));

        bind(visible(getViewModel().negotiatedRateVisibility, getViewBinding().negotiatedRateContainer));
        bind(textRes(getViewModel().negotiatedRateText, getViewBinding().negotiatedRateText));

        bind(visible(getViewModel().classDetailsConversionAreaVisibility, getViewBinding().frClassDetailsConversionArea));
        bind(text(getViewModel().classDetailsConversionTotalText, getViewBinding().frClassDetailsConversionTotal));
        bind(text(getViewModel().classDetailsConversionText, getViewBinding().frClassDetailsConversionText));

        bind(textRes(getViewModel().estimatedTotalLabel.textRes(), getViewBinding().totalLabel));
        bind(text(getViewModel().estimatedTotalText, getViewBinding().estimatedTotal));
        bind(visible(getViewModel().priceHeaderVisibility, getViewBinding().priceHeader));
        bind(visible(getViewModel().priceEstimatedTotalContainerVisibility, getViewBinding().priceEstimatedTotalContainer));

        bind(visible(getViewModel().headerTotalVisibility, getViewBinding().headerTotal));
        bind(text(getViewModel().headerTotalText, getViewBinding().headerTotal));

        bind(visible(getViewModel().headerRentalRangeVisibility, getViewBinding().headerRentalRange));
        bind(textRes(getViewModel().headerRentalRange.textRes(), getViewBinding().headerRentalRange));
        bind(visible(getViewModel().priceUnavailableVisibility, getViewBinding().priceUnavailableContainer));
        bind(visible(getViewModel().selectThisClassButtonVisibility, getViewBinding().buttonSelectThisClass));
        bind(visible(getViewModel().callLocationButtonVisibility, getViewBinding().buttonCallLocation));
        bind(visible(getViewModel().noPriceAvailableVisibility, getViewBinding().noPriceAvailable));

        bind(EHIImageLoader.imageByType(getViewModel().images, getViewBinding().carClassImage, getViewModel().imageTypeToLoad));

        addReaction(SHOW_POINTS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHICarClassDetails details = getViewModel().getCarClassDetails();
                final boolean showPoints = getViewModel().needShowPoints();

                if (getViewModel().needShowPoints()) {
                    getViewBinding().epointsHeader.setTopRightText(getString(R.string.redemption_header_points_hide_label), null);
                    getViewBinding().pointsContainer.setText(details);
                    if (details.getMaxRedemptionDays() != 0) {
                        TokenizedString.Formatter freeDaysString = new TokenizedString.Formatter<>(getResources());
                        if (details.getMaxRedemptionDays() > 1) {
                            freeDaysString.formatString(R.string.redemption_free_days_subtitle)
                                    .addTokenAndValue(EHIStringToken.NUMBER_OF_DAYS,
                                            Integer.toString(details.getMaxRedemptionDays()));
                        } else {
                            freeDaysString.formatString(R.string.redemption_free_day_subtitle)
                                    .addTokenAndValue(EHIStringToken.NUMBER_OF_DAYS,
                                            Integer.toString(details.getMaxRedemptionDays()));
                        }
                    }
                } else {
                    getViewBinding().epointsHeader.setTopRightText(getString(R.string.redemption_header_points_show_label), null);
                }

                if (getViewBinding().pointsContainer.isInstantiated()) {
                    getViewBinding().pointsContainer.animateShowPoints(showPoints);
                } else {
                    runNonReactive(new ReactorComputationFunction() {
                        @Override
                        public void react(ReactorComputation reactorComputation) {
                            getViewBinding().pointsContainer.setCarClassDetail(details, showPoints);
                        }
                    });
                }
            }
        });

        addReaction(CLASS_DETAIL_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getExtrasReservation() != null) {
                    if (((ReservationFlowListener) getActivity()).needShowRateScreen(getViewModel().getCarClassDetails(), getViewModel().getCorporateContractType())) {
                        ((ReservationFlowListener) getActivity()).showChooseYourRateScreen(getViewModel().getCarClassDetails(), false);
                    } else {
                        ((ReservationFlowListener) getActivity()).showCarExtras(getViewModel().getCarClassDetails(),
                                false,
                                ((ReservationFlowListener) getActivity()).getPayState(),
                                false);
                    }
                    getViewModel().setExtrasReservation(null);
                }
            }

        });
    }

    private void callLocation(String phoneNumber) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_CLASS.value, CarClassDetailFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_DETAILS.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL_FOR_AVAILABILITY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
        IntentUtils.callNumber(getActivity(), phoneNumber);
    }
}
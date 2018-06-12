package com.ehi.enterprise.android.ui.confirmation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ConfirmationFragmentBinding;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.reservation.EHICancellation;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIPayment;
import com.ehi.enterprise.android.models.reservation.EHIPrice;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.activity.EHIBaseActivity;
import com.ehi.enterprise.android.ui.activity.ModalDialogActivityHelper;
import com.ehi.enterprise.android.ui.confirmation.widgets.ManageReservationView;
import com.ehi.enterprise.android.ui.confirmation.widgets.RateUsView;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.LocationDetailsActivityHelper;
import com.ehi.enterprise.android.ui.location.LocationPoliciesListActivityHelper;
import com.ehi.enterprise.android.ui.location.interfaces.OnLocationDetailEventsListener;
import com.ehi.enterprise.android.ui.reservation.HtmlParseFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.KeyFactsActionDelegate;
import com.ehi.enterprise.android.ui.reservation.RentalTermsConditionsFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.ReservationDNRDialogFragmentHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.reservation.modify.ModifyReviewActivityHelper;
import com.ehi.enterprise.android.ui.reservation.widget.DetailsSectionView;
import com.ehi.enterprise.android.ui.reservation.widget.PriceSummaryView;
import com.ehi.enterprise.android.utils.CustomTypefaceSpan;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.IntentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;

@ViewModel(ConfirmationViewModel.class)
public class ConfirmationFragment extends DataBindingViewModelFragment<ConfirmationViewModel, ConfirmationFragmentBinding> {

    public static final String SCREEN_URL = "ConfirmationFragment";

    @Extra(value = EHIReservation.class, required = false)
    public static final String EXTRA_RESERVATION = "ehi.EXTRA_RESERVATION";
    @Extra(value = EHINotification.class, required = false)
    public static final String EXTRA_NOTIFICATION = "ehi.EXTRA_NOTIFICATION";
    @Extra(boolean.class)
    public static final String IS_MODIFY = "ehi.IS_MODIFY";

    private static final String LEARN_MORE_REACTION = "LEARN_MORE_REACTION";
    private static final String RESERVATION_REACTION = "RESERVATION_REACTION";
    private static final String CANCEL_REACTION = "CANCEL_REACTION";
    private static final String ERROR_REACTION = "ERROR_REACTION";
    private static final String RETRIEVE_REACTION = "RETRIEVE_REACTION";
    private static final String AVAILABLE_CAR_CLASSES_REACTION = "AVAILABLE_CAR_CLASSES_REACTION";
    public static final String PREPAY_TERMS_CONDITIONS = "PREPAY_TERMS_CONDITIONS";

    private static final int PREPAY_CANCEL_REQUEST = 1001;
    private static final int PREPAY_MODIFY_REQUEST = 1002;
    private static final int CANCEL_REQUEST = 1003;


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().returnToDashboardButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                        .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_RETURN_HOME.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                        .tagScreen()
                        .tagEvent();

                returnToHomeScreen();

            } else if (view == getViewBinding().modifyReservationButton) {
                onModify();
            } else if (view == getViewBinding().cancelReservationButton) {
                onCancel();
            } else if (view == getViewBinding().policyButtonContainer.rentalPoliciesButton) {
                Intent intent = new LocationPoliciesListActivityHelper.Builder()
                        .extraPolicies(getViewModel().getReservationObject().getPolicies())
                        .build(getActivity());

                startActivity(intent);
            } else if (view == getViewBinding().quickPickupButton) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                        .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_QUICK_PICKUP_DETAILS.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                        .tagScreen()
                        .tagEvent();
                IntentUtils.openUrlViaExternalApp(getActivity(), getViewModel().getQuickPickupUrl());
            } else if (view == getViewBinding().policyButtonContainer.keyFactsArea) {
                new KeyFactsActionDelegate((EHIBaseActivity) getActivity())
                        .showKeyFacts(getViewModel().getReservationObject().getPickupLocation(),
                                getViewModel().getReservationObject().getEHIKeyFactsPolicies(),
                                getViewModel().getReservationObject().getExtras());
            } else if (view == getViewBinding().rentalTermsConditionsView) {
                showModal(getActivity(), new RentalTermsConditionsFragmentHelper.Builder()
                        .extraSessionId(getViewModel().getReservationObject().getResSessionId())
                        .build());
            }
        }
    };

    public void returnToHomeScreen() {
        if (!LocalDataManager.getInstance().hasShownRegisterModal() && !getViewModel().isUserLoggedIn()) {
            IntentUtils.goToHomeScreenAndShowJoinModal(getContext(), getTripOnCalendarIntent());
        } else {
            IntentUtils.goToHomeScreen(getContext());
        }
    }

    private void onModify() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODIFY_RESERVATION.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                .tagScreen()
                .tagEvent();

        if (getViewModel().shouldDisableModifyButton()) {
            showModifyCallUsDialog();
        } else if (getViewModel().shouldShowPrepayModifyDialog()) {
            showPrePayModifyDialog();
        } else {
            modifyReservation();
        }
    }

    private void onCancel() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CANCEL_RESET.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                .tagScreen()
                .tagEvent();
        if (getViewModel().shouldDisableCancelButton()) {
            showCancelCallUsDialog();
        } else {
            showCancelDialog();
        }
    }

    private OnLocationDetailEventsListener mOnLocationDetailEventsListener = new OnLocationDetailEventsListener() {
        @Override
        public void onFavoriteStateChanged() {
        }

        @Override
        public void onCallLocation(String phoneNumber) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                    .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CALL_US.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                    .tagScreen()
                    .tagEvent();
            IntentUtils.callNumber(getActivity(), phoneNumber);
        }

        @Override
        public void onShowDirection() {
        }

        @Override
        public void onShowDirectionFromTerminal() {
        }

        @Override
        public void onShowLocationDetails(EHILocation location) {
            startActivity(new LocationDetailsActivityHelper.Builder()
                    .gboLocation(location)
                    .build(getActivity()));
        }

        @Override
        public void onShowAfterHoursDialog() {

        }
    };

    private OnExtraActionListener mOnExtraActionListener = new OnExtraActionListener() {
        @Override
        public void onChangeExtraCount(EHIExtraItem item, int newCount) {

        }

        @Override
        public void onClick(EHIExtraItem item) {
            if (item != null) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                        .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODAL_LAUNCH.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.modalLaunch(item.getCode()))
                        .tagScreen()
                        .tagEvent();

                Fragment fragment = new ModalTextDialogFragmentHelper.Builder()
                        .title(item.getName())
                        .text(item.getDetailedDescription())
                        .build();

                Intent intent = new ModalDialogActivityHelper.Builder()
                        .fragmentClass(fragment.getClass())
                        .fragmentArguments(fragment.getArguments())
                        .build(getActivity());

                startActivity(intent);
            }
        }
    };

    private View.OnClickListener mOnTermsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showModal(
                    getActivity(),
                    new HtmlParseFragmentHelper.Builder()
                            .title(getResources().getString(R.string.weekend_special_terms_and_conditions_navigation_ti))
                            .message(getViewModel().getCorporateAccountTermsAndConditions(getViewModel().isModify()))
                            .build()
            );
        }
    };

    private DetailsSectionView.OnDetailsSectionPaymentMethodEventListener mOnDetailsPaymentMethodEventListener = new DetailsSectionView.OnDetailsSectionPaymentMethodEventListener() {
        @Override
        public void onPaymentMethodDetailsClicked() {
            getViewModel().requestPrepaymentPolicy();
        }
    };

    private RateUsView.OnRateUsClickListener onRateUsClickListener = new RateUsView.OnRateUsClickListener() {
        @Override
        public void onRateUsClicked() {
            ToastUtils.showLongToast(getContext(), R.string.confirmation_rating_thanks_message);
            IntentUtils.goToPlayStoreApp(getContext());
            getViewModel().markConfirmationRateUsDone();
            getViewBinding().rateUsView.setVisibility(View.GONE);
        }

        @Override
        public void onDismissClicked() {
            getViewModel().markConfirmationRateUsDone();
            getViewBinding().rateUsView.setVisibility(View.GONE);
        }
    };

    private ManageReservationView.ManageReservationListener mManageReservationListener = new ManageReservationView.ManageReservationListener() {
        @Override
        public void onAddToCalendarClick() {
            addTripsToCalendar();
        }

        @Override
        public void onModifyClick() {
            onModify();
        }

        @Override
        public void onCancelClick() {
            onCancel();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_confirmation, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().quickPickupButton.setOnClickListener(mOnClickListener);

        getViewBinding().detailsSectionView.setOnDetailsSectionPaymentMethodEventListener(mOnDetailsPaymentMethodEventListener);

        getViewBinding().priceSummary.setOnExtraActionClickListener(mOnExtraActionListener);
        getViewBinding().priceSummary.setPriceSummaryListener(new PriceSummaryView.PriceSummaryListener() {
            @Override
            public void onLearnMoreClicked() {
                FragmentUtils.addProgressFragment(getActivity());
                getViewModel().requestLearnMore();
            }
        });

        getViewBinding().rentalSectionView.setOnLocationDetailEventsListener(mOnLocationDetailEventsListener);

        getViewBinding().returnToDashboardButton.setOnClickListener(mOnClickListener);
        getViewBinding().modifyReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().modifyReservationButton.setOnDisabledClickListener(mOnClickListener);
        getViewBinding().cancelReservationButton.setOnClickListener(mOnClickListener);
        getViewBinding().cancelReservationButton.setOnDisabledClickListener(mOnClickListener);
        getViewBinding().manageReservationContainer.setListener(mManageReservationListener);
        getViewBinding().policyButtonContainer.rentalPoliciesButton.setOnClickListener(mOnClickListener);
        getViewBinding().policyButtonContainer.keyFactsArea.setOnClickListener(mOnClickListener);
        if (getViewModel().getReservationObject() != null
                && getViewModel().getReservationObject().getBusinessLeisure() != null) {
            String businessLeisure = Html.fromHtml(getViewModel().getReservationObject().getBusinessLeisure()).toString();
            getViewBinding().policyButtonContainer.aboutRentalPoliciesText.setText(businessLeisure);
        } else {
            getViewBinding().corporateGenericMessageView.setVisibility(View.GONE);
        }

        if (getViewModel().getReservationObject() != null) {
            if (ListUtils.isEmpty(getViewModel().getReservationObject().getEHIKeyFactsPolicies())) {
                getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.GONE);
            } else if (getViewModel().getReservationObject().isEuropeanUnionCountry()) {
                getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.VISIBLE);
            } else {
                getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.GONE);
            }
        } else {
            getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.GONE);
            getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.GONE);
        }

        getViewBinding().priceSummary.getPrepayOrSaveView().setVisibility(View.GONE);

        if (getViewModel().shouldShowConfirmationRateUs()) {
            getViewBinding().rateUsView.setVisibility(View.VISIBLE);
            getViewBinding().rateUsView.setOnRateUsClickListener(onRateUsClickListener);
        } else {
            getViewBinding().rateUsView.setVisibility(View.GONE);
        }

        getViewBinding().rentalTermsConditionsView.setOnClickListener(mOnClickListener);
        getViewBinding().rentalTermsConditionsView.setBackgroundColor(
                ResourcesCompat.getColor(getResources(), R.color.ehi_module_background_fill, getContext().getTheme()));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfirmationFragmentHelper.Extractor extractor = new ConfirmationFragmentHelper.Extractor(this);
        getViewModel().setIsModify(extractor.isModify());
        if (extractor.extraNotification() != null) {
            getViewModel().retrieveReservation(extractor.extraNotification().getId(),
                    extractor.extraNotification().getUserFirstName(),
                    extractor.extraNotification().getUserLastName());
        } else {
            getViewModel().setReservationObject(extractor.extraReservation());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getViewModel().needToShowDNRDialog()) {
            showModalDialog(getActivity(), new ReservationDNRDialogFragmentHelper.Builder().build());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                .state(EHIAnalytics.State.STATE_CONFIRMATION.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                .tagScreen()
                .tagEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getViewModel().setModifyState(false);
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(DialogUtils.errorDialog(getViewModel().errorResponse, getActivity()));
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));

        addReaction(LEARN_MORE_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getTaxesAndFeesMoreInformation() != null) {
                    showModal(getActivity(), new HtmlParseFragmentHelper.Builder()
                            .title(getResources().getString(R.string.class_details_taxes_fees_summary_title))
                            .message(getViewModel().getTaxesAndFeesMoreInformation())
                            .build());
                    getViewModel().setTaxesAndFeesMoreInformation(null);
                }
            }
        });

        addReaction(RESERVATION_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                EHIReservation response = getViewModel().getReservationObject();
                if (response != null) {
                    populateViewsFromViewModel();
                }
            }
        });

        addReaction(CANCEL_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getCanceledConfirmationNumber() != null) {
                    int stringResId = getViewModel().isPrePay() ?
                            R.string.reservation_cancel_success_message : R.string.confirmation_reservation_was_canceled;

                    Toast.makeText(
                            getActivity(),
                            stringResId,
                            Toast.LENGTH_SHORT).show();
                    IntentUtils.goToHomeScreen(getActivity());
                }
            }
        });

        addReaction(ERROR_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getErrorWrapper();
                if (wrapper != null) {
                    DialogUtils.showErrorDialog(getActivity(), wrapper);
                    getViewModel().setErrorWrapper(null);
                }
            }
        });

        addReaction(RETRIEVE_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().getRetrieveBeforeModifyResponse() != null) {
                    startActivity(
                            new ModifyReviewActivityHelper.Builder()
                                    .payState(getViewModel().getPayState())
                                    .build(getActivity())
                    );
                    getViewModel().setRetrieveBeforeModifyResponse(null);
                }
            }
        });

        addReaction(PREPAY_TERMS_CONDITIONS, new ReactorComputationFunction() {
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

    private void populateViewsFromViewModel() {

        getViewBinding().modifyReservationButton.setEnabled(!getViewModel().shouldDisableModifyButton());

        getViewBinding().cancelReservationButton.setEnabled(!getViewModel().shouldDisableCancelButton());

        if (getViewModel().getConfirmationNumber() != null) {
            getViewBinding().confirmationHeaderView.setConfirmationNumber(getViewModel().getConfirmationNumber());
        }

        SpannableStringBuilder bld = new SpannableStringBuilder();

        SpannableString confirmationEmail = new SpannableString(
                getString(getViewModel().isModify() ? R.string.confirmation_email_modify_message : R.string.confirmation_email_message) + " "
        );


        confirmationEmail.setSpan(
                new CustomTypefaceSpan("", ResourcesCompat.getFont(getContext(), R.font.source_sans_light)),
                0,
                confirmationEmail.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        bld.append(confirmationEmail);

        if (getViewModel().getPickupTime() != null) {
            CharSequence formattedString = new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .formatString(R.string.confirmation_pickup_date_message)
                    .addTokenAndValue(EHIStringToken.DATE, DateUtils.formatDateTime(getActivity(), getViewModel().getPickupTime().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR))
                    .format();

            Typeface tf = ResourcesCompat.getFont(getContext(), R.font.source_sans_bold);

            SpannableString seeYouOn = new SpannableString(formattedString);
            seeYouOn.setSpan(new CustomTypefaceSpan("", tf), 0, seeYouOn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            bld.append(seeYouOn);
        }

        getViewBinding().emailTitleTextView.setText(bld);

        if (getViewModel().needToShowQuickPickup()) {
            getViewBinding().quickPickupButton.setVisibility(View.VISIBLE);
        }

        if (getViewModel().getCarClassDetails() != null) {
            getViewBinding().confirmationHeaderView.setCarClassDetails(getViewModel().getCarClassDetails());
        }

        final EHIReservation ehiReservation = getViewModel().getReservationObject();

        getViewBinding().rentalSectionView.setRentalSectionForConfirmation(
                ehiReservation,
                false,
                getViewModel().isLoggedIntoEmeraldClub(),
                getViewModel().isAvailableAtContractRate(),
                getViewModel().isAvailableAtPromotionalRate(),
                mOnTermsClickListener,
                getViewModel().isNorthAmerica()
        );

        getViewBinding().detailsSectionView.setDetailsForConfirmation(
                ehiReservation,
                getViewModel().isModify(),
                getViewModel().getDriverInfo(),
                getViewModel().getUserProfileCollection(),
                getViewModel().getAdditionalInformation()
        );

        //flight info
        getViewBinding().detailsSectionView.setCurrentFlightDetails(
                getViewModel().getCurrentAirlineDetail(),
                getViewModel().getFlightNumber(),
                ehiReservation.getPickupLocation().isMultiTerminal()
        );

        getViewBinding().detailsSectionView.hideGreenArrows();

        if (getViewModel().getCarClassDetails() != null) {
            if (getViewModel().getCarClassDetails().getPriceSummary() != null) {
                getViewBinding().priceSummary.setIsPrepay(getViewModel().isPrePay());
                getViewBinding().priceSummary.setRedemptionInfo(
                        getViewModel().getRedemptionDayCount(), getViewModel().getRedemptionPointsRate()
                );
                getViewBinding().priceSummary.setReservation(getViewModel().getReservationObject());
            } else {
                getViewBinding().priceSummary.setVisibility(View.GONE);
            }
        }

        if (getViewModel().getReservationObject() != null
                && getViewModel().getReservationObject().getPolicies() != null
                && getViewModel().getReservationObject().getPolicies().size() > 0) {
            getViewBinding().policyButtonContainer.rentalPoliciesContainer.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().policyButtonContainer.rentalPoliciesContainer.setVisibility(View.GONE);
        }
    }

    private void showCancelDialog() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                .tagScreen()
                .tagEvent();

        final EHIPayment paymentInfo = getViewModel().getPaymentInfo();
        final EHICancellation ehiCancellation = getViewModel().getCancelationDetails();

        /* it's a valid situation when we don't have payments for prepay reservation
          This can happen if you modify from pay later to prepay reservation
          then user not gonna be charged , so will have 0 payments but res will be pre pay*/
        if (getViewModel().isPrePay()
                && paymentInfo != null 
                && paymentInfo.getAmount() != null
                && ehiCancellation != null) {
            showPrePayCancelDialog();
        } else {
            showNonPrePayCancelDialog();
        }
    }

    private void showPrePayCancelDialog() {
        final EHIPrice ehiPrice = getViewModel().getPaymentInfo().getAmount();
        PrePayCancelDialogFragment fragment = new PrePayCancelDialogFragmentHelper.Builder()
                .extraOriginalAmount(ehiPrice.getFormattedPrice(true).toString())
                .extraCancellation(getViewModel().getCancelationDetails())
                .extraIsModify(getViewModel().isModify())
                .build();

        showModalDialog(getActivity(), fragment, true, PREPAY_CANCEL_REQUEST);
    }

    private void showNonPrePayCancelDialog() {
        CancelReservationDialogFragment fragment = new CancelReservationDialogFragmentHelper.Builder()
                .extraContractName(getViewModel().is3rdPartyEmailNotify() ? getViewModel().getAccountName() : "").build();
        showModalDialog(getActivity(), fragment, true, CANCEL_REQUEST);
    }

    private void cancelReservation() {
        final EHICarClassDetails ehiCarClassDetails = getViewModel().getCarClassDetails();
        if (ehiCarClassDetails != null && ehiCarClassDetails.getPriceSummary() != null
                && ehiCarClassDetails.getPriceSummary().getEstimatedTotalView() != null) {
            final EHIPrice ehiPrice = ehiCarClassDetails.getPriceSummary().getEstimatedTotalView();

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ConfirmationFragment.SCREEN_URL)
                    .state(EHIAnalytics.State.STATE_CANCELLATION.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_YES.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(getViewModel().getReservationObject(), getViewModel().isModify()))
                    .addCustomerValues((int) (ehiPrice.getDoubleAmmount() * -1 * 100)) //should send in cents
                    .tagScreen()
                    .tagEvent();
        }
        getViewModel().cancelReservation();
    }

    private void showModifyCallUsDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(getViewModel().getModifyButtonErrorText())
                .setPositiveButton(getString(R.string.standard_close_button), null)
                .setCancelable(true)
                .create()
                .show();
    }

    private void showCancelCallUsDialog() {
        if (getViewModel().isPrePay()) {
            showModalDialog(getActivity(), new PrePayCallUsDialogFragmentHelper.Builder().build());
        } else {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getViewModel().getCancelButtonErrorText())
                    .setPositiveButton(getString(R.string.standard_close_button), null)
                    .setCancelable(true)
                    .create()
                    .show();
        }
    }

    private void showPrePayModifyDialog() {
        showModalDialog(getActivity(), new PrePayModifyDialogFragmentHelper.Builder().build(), false, PREPAY_MODIFY_REQUEST);
    }


    private Intent getTripOnCalendarIntent(){
        return new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getViewModel().getPickupTime().getTime())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getViewModel().getReturnTime().getTime())
                .putExtra(Events.TITLE, getViewModel().getTitleForCalendar())
                .putExtra(Events.DESCRIPTION, getViewModel().getDescriptionForCalendar())
                .putExtra(Events.EVENT_LOCATION, getViewModel().getPickupLocation().getAddress().getAddressForCalendar())
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_FREE)
                .putExtra(Intent.CATEGORY_APP_MAPS, getViewModel().getPickupLocation().getAddress().getAddressForCalendar());
    }

    private void addTripsToCalendar() {
        startActivity(getTripOnCalendarIntent());
    }

    public void updateReservationInfo() {
        getViewBinding().manageReservationContainer.resetInitialState();
        getViewModel().updateReservationInfo();
        getViewBinding().screenScrollView.smoothScrollTo(0, 0);

    }

    public void setIsModify(boolean modify) {
        getViewModel().setIsModify(modify);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PREPAY_CANCEL_REQUEST || requestCode == CANCEL_REQUEST) {
                cancelReservation();
            }
            if (requestCode == PREPAY_MODIFY_REQUEST) {
                modifyReservation();
            }
        }
    }

    private void modifyReservation() {
        getViewModel().retrieveReservationForModify();
        getViewModel().setModifyState(true);
    }
}
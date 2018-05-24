package com.ehi.enterprise.android.ui.reservation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.ReviewFragmentBinding;
import com.ehi.enterprise.android.models.profile.EHI3DSData;
import com.ehi.enterprise.android.models.profile.EHIBasicProfile;
import com.ehi.enterprise.android.models.profile.EHIPaymentMethod;
import com.ehi.enterprise.android.models.reservation.EHIAdditionalInformation;
import com.ehi.enterprise.android.models.reservation.EHIAirlineDetails;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.activity.EHIBaseActivity;
import com.ehi.enterprise.android.ui.activity.ModalActivityHelper;
import com.ehi.enterprise.android.ui.activity.ModalDialogActivityHelper;
import com.ehi.enterprise.android.ui.confirmation.ConfirmationActivityHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.ModalTextDialogFragmentHelper;
import com.ehi.enterprise.android.ui.location.LocationPoliciesListActivityHelper;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnReviewTripPurposeListener;
import com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener;
import com.ehi.enterprise.android.ui.reservation.widget.BookRentalButton;
import com.ehi.enterprise.android.ui.reservation.widget.ChangePaymentBannerView;
import com.ehi.enterprise.android.ui.reservation.widget.DetailsSectionView;
import com.ehi.enterprise.android.ui.reservation.widget.PaymentInfoDialogFragment;
import com.ehi.enterprise.android.ui.reservation.widget.PriceSummaryView;
import com.ehi.enterprise.android.ui.reservation.widget.RentalSectionView;
import com.ehi.enterprise.android.ui.reservation.widget.ReviewCardNoInfoView;
import com.ehi.enterprise.android.ui.reservation.widget.ReviewPaymentModifyUnavailableView;
import com.ehi.enterprise.android.ui.reservation.widget.ReviewPointsView;
import com.ehi.enterprise.android.ui.widget.ModifyPrePayBannerView;
import com.ehi.enterprise.android.utils.BaseAppUtils;
import com.ehi.enterprise.android.utils.DLog;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.EHIBundle;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.exceptions.NotImplementedException;
import com.google.gson.reflect.TypeToken;
import com.isobar.android.newinstancer.Extra;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

import java.util.List;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.activity.ReactorActivity;
import io.dwak.reactorbinding.view.ReactorView;

import static com.ehi.enterprise.android.ui.reservation.SelectCreditCardFragment.EXTRA_SHOULD_SHOW_PREPAY_TERMS;
import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PAY_LATER;
import static com.ehi.enterprise.android.ui.reservation.interfaces.ReservationFlowListener.PayState.PREPAY;

@ViewModel(ReviewViewModel.class)
public class ReviewFragment extends DataBindingViewModelFragment<ReviewViewModel, ReviewFragmentBinding> {

    public static final String SCREEN_NAME = "ReviewFragment";
    public static final String TAG = "ReviewFragment";

    public static final int DRIVER_INFO_REQUEST_CODE = 3992;
    public static final int ADDITIONAL_INFO_REQUEST_CODE = 3995;
    private static final int SELECT_PAYMENT_REQUEST_CODE = 3996;
    public static final int REQUEST_CODE_FLIGHT_DETAILS = 32112;
    private static final int REQUEST_CODE_3DS_VALIDATION = 32113;

    @Extra(boolean.class)
    public static final String EXTRA_IS_MODIFY = "ehi.EXTRA_IS_MODIFY";

    @Extra(ReservationFlowListener.PayState.class)
    public static final String EXTRA_PAY_STATE = "ehi.EXTRA_PAY_STATE";

    @Extra(value = String.class, required = false)
    public static final String EXTRA_PRE_PAY_ORIGINAL_AMOUNT = "ehi.EXTRA_PRE_PAY_ORIGINAL_AMOUNT";

    @Extra(value = boolean.class, required = false)
    public static final String EXTRA_LOGIN_AFTER_START = "ehi.EXTRA_LOGIN_AFTER_START";

    private static final String RESERVATION_RESPONSE = "RESERVATION_RESPONSE";
    private static final String RESERVATION_COMMIT = "RESERVATION_COMMIT";
    private static final String TRIP_PURPOSE_REQUIRED_REACTION = "TRIP_PURPOSE_REQUIRED_REACTION";
    private static final String ERROR_REACTION = "ERROR_REACTION";
    public static final String ANIMATION_TAG = "ANIMATION_CONFIRM_FRAGMENT";
    private static final String FLIGHT_DETAILS_REACTION = "FLIGHT_DETAILS_REACTION";
    private static final String BILLING_TYPE_REACTION = "BILLING_TYPE_REACTION";
    public static final String PREPAY_TERMS_CONDITIONS = "PREPAY_TERMS_CONDITIONS";
    public static final String LEARN_MORE_REACTION = "LEARN_MORE_REACTION";
    public static final String KEY_FACTS_REACTION = "KEY_FACTS_REACTION";
    public static final String KEY_3DS_PARES = "THREE_DS_PARES";
    public static final String PAY_STATE = "PAY_STATE";

    private static final String CREDIT_CARD_ADDED = "CREDIT_CARD_ADDED";
    private static final long SLIDE_UP_ANIMATION_DURATION = 500;

    private int mPrice;

    //region onClickListener
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view != getViewBinding().policyButtonContainer.rentalPoliciesButton
                    && view != getViewBinding().policyButtonContainer.keyFactsArea
                    && view != getViewBinding().rentalTermsConditionsView) {
                getViewModel().setAsUpdated();
            }

            if (view == getViewBinding().policyButtonContainer.rentalPoliciesButton) {
                ((ReservationFlowListener) getActivity()).showingModal(true);
                Intent intent = new LocationPoliciesListActivityHelper.Builder()
                        .extraPolicies(getViewModel().getRawReservationObject().getPolicies())
                        .build(getActivity());
                startActivity(intent);
            } else if (view == getViewBinding().rentalTermsConditionsView) {
                showModal(getActivity(), new RentalTermsConditionsFragmentHelper.Builder().build());
            } else if (view == getViewBinding().policyButtonContainer.keyFactsArea) {
                new KeyFactsActionDelegate((EHIBaseActivity) getActivity()).showKeyFacts(getViewModel().getRawReservationObject().getPickupLocation(),
                        getViewModel().getRawReservationObject().getEHIKeyFactsPolicies(),
                        getViewModel().getReservationObject().getExtras());
            } else if (view == getViewBinding().priceSummaryView.getPrepayOrSaveView()) {
                final ReservationFlowListener flowListener = (ReservationFlowListener) getActivity();
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, SCREEN_NAME)
                        .macroEvent(flowListener.getPayState() == PREPAY ? EHIAnalytics.MacroEvent.MACRO_REVIEW_PAYLATER.value
                                : EHIAnalytics.MacroEvent.MACRO_REVIEW_PAYNOW.value)
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, flowListener.getPayState() == PREPAY ? EHIAnalytics.Action.ACTION_PAY_LATER_BODY.value
                                : EHIAnalytics.Action.ACTION_PAY_NOW_BODY.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagMacroEvent()
                        .tagEvent();
                togglePaymentState();
            } else if (view == getViewBinding().bookRentalButton) {
                if (!getViewModel().isBookButtonEnabled()) {
                    ToastUtils.showToast(getActivity(), R.string.review_prepay_na_terms_not_selected);
                    return;
                }

                if (getViewModel().shouldShowAddCreditCardScreen()) {
                    showAddCreditCardScreen();
                    return;
                }

                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CONTINUE_MODIFY.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();

                check3DSAndCommit();
            }
        }
    };
    //endregion

    //region callbacks
    private OnExtraActionListener mPriceSummaryExtraActionListener = new OnExtraActionListener() {
        @Override
        public void onChangeExtraCount(EHIExtraItem item, int newCount) {
        }

        @Override
        public void onClick(EHIExtraItem item) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODAL_LAUNCH.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.modalLaunch(item.getCode()))
                    .tagScreen()
                    .tagEvent();


            Fragment fragment = new ModalTextDialogFragmentHelper.Builder().title(item.getName())
                    .text(item.getDetailedDescription())
                    .build();

            Intent intent = new ModalDialogActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivity(intent);
        }
    };

    private ChangePaymentBannerView.ChangePaymentBannerViewListener mChangePaymentBannerListener = new ChangePaymentBannerView.ChangePaymentBannerViewListener() {
        @Override
        public void onPaymentInfoClicked() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PAY_LATER_HELP.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();

            showModalDialog(getActivity(), new PaymentInfoDialogFragment());
        }

        @Override
        public void onChangePaymentClicked() {
            final ReservationFlowListener flowListener = (ReservationFlowListener) getActivity();
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .macroEvent(flowListener.getPayState() == PREPAY ? EHIAnalytics.MacroEvent.MACRO_REVIEW_PAYLATER.value
                            : EHIAnalytics.MacroEvent.MACRO_REVIEW_PAYNOW.value)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, flowListener.getPayState() == PREPAY ? EHIAnalytics.Action.ACTION_PAY_LATER_BANNER.value : EHIAnalytics.Action.ACTION_PAY_NOW_BANNER.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagMacroEvent()
                    .tagScreen()
                    .tagEvent();

            getViewModel().setAsUpdated();
            togglePaymentState();
        }
    };

    private OnReviewTripPurposeListener mOnReviewTripPurposeListener = new OnReviewTripPurposeListener() {
        @Override
        public void onTripPurposeChanged(String tripPurpose) {
            getViewModel().setTripPurpose(tripPurpose);
            getViewModel().updateContinueButton();
        }
    };

    private ReviewPointsView.OnActionClickListener mReviewPointsActionListener = new ReviewPointsView.OnActionClickListener() {
        @Override
        public void onSavePoints() {
            getViewModel().setAsUpdated();
            getViewModel().setNeedShowPoints(false);
            getViewBinding().reviewPointsView.setShowPoint(false);
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_SAVE_POINTS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
        }

        @Override
        public void onRedeemPoints() {
            getViewModel().setAsUpdated();
            if (getViewModel().getCarClassDetailsWithPoints().getMaxRedemptionDays() == 0) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getString(R.string.currently_dont_allow_points_unsupported),
                        ""
                );
                return;
            }
            ((ReservationFlowListener) getActivity()).showRedemption(
                    getViewModel().getCarClassDetailsWithPoints(), false);
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REDEEM_POINTS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
        }

        @Override
        public void onRemovePoints() {
            getViewModel().setAsUpdated();
            getViewModel().removeRedemptionDays();
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REMOVE_POINTS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
        }
    };

    private PriceSummaryView.PriceSummaryListener mPriceSummaryListener = new PriceSummaryView.PriceSummaryListener() {
        @Override
        public void onLearnMoreClicked() {
            getViewModel().requestLearnMore();
        }
    };

    private ReviewCardNoInfoView.ReviewPrepayAddPaymentListener mReviewPrepayAddPaymentListener = new ReviewCardNoInfoView.ReviewPrepayAddPaymentListener() {
        @Override
        public void onPrepaymentPolicyClick() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PRE_PAY_POLICY.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
            getViewModel().requestPrepaymentPolicy();
        }

        @Override
        public void isVisible(boolean isVisible) {
            populatePriceSummary();
            updateRedemptionView();
            getViewModel().updateContinueButton();
        }
    };

    private View.OnClickListener mPrepayTermsAndConditionsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            getViewModel().requestPrepaymentPolicy();
        }
    };

    private ReviewPaymentModifyUnavailableView.PaymentModifyUnavailableListener mPaymentModifyUnavailableListener = new ReviewPaymentModifyUnavailableView.PaymentModifyUnavailableListener() {
        @Override
        public void onPrepaymentPolicyClick() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_PRE_PAY_POLICY.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
            getViewModel().requestPrepaymentPolicy();
        }
    };

    private ReviewCardNoInfoView.CreditCardViewClickListener mPrepayClickListener = new ReviewCardNoInfoView.CreditCardViewClickListener() {
        @Override
        public void addCreditCard() {
            showAddCreditCardScreen();
        }

        @Override
        public void editCreditCard() {
            //can be called only during prepay NA
            if (getViewModel().isNorthAmericaPrepayAvailable(getViewModel().isModify())) {
                showModalForResult(getActivity(), new SelectCreditCardFragmentHelper.Builder().build(), SELECT_PAYMENT_REQUEST_CODE);
            }
        }

        @Override
        public void removeCreditCard() {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_REMOVE_CC.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
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

    private RentalSectionView.OnRentalSectionClickListener onRentalSectionClickListener = new RentalSectionView.OnRentalSectionClickListener() {
        @Override
        public void clearAllClicked(boolean edit, boolean clearLocation) {
            getViewModel().setAsUpdated();
            ((ReservationFlowListener) getActivity()).showItinerary(true, clearLocation);
        }

        @Override
        public void carUpgradeClicked(String carId) {
            getViewModel().carUpgradeClicked(carId);
            getViewModel().setAsUpdated();
        }

        @Override
        public void showAvailableCarClassesClicked(boolean edit) {
            ((ReservationFlowListener) getActivity()).showAvailableCarClasses(edit);
            getViewModel().setAsUpdated();
        }

        @Override
        public void editCarExtrasClicked(boolean edit, boolean fromChooseYourRate) {
            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_MODIFY_EXTRAS.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();

            ((ReservationFlowListener) getActivity()).showCarExtras(
                    getViewModel().getRawReservationObject().getCarClassDetails(),
                    edit,
                    getViewModel().getPayState() != null ? getViewModel().getPayState() : PAY_LATER,
                    fromChooseYourRate
            );
            getViewModel().setAsUpdated();
        }

        @Override
        public void openReviewLocationsClicked() {
            openFlightDetailsFragment();
        }

        @Override
        public void showLocationChangeBlockPopup() {
            showModalDialog(getActivity(), new BlockModifyLocationDialogFragmentHelper.Builder().build(), false, -1);
        }
    };

    private DetailsSectionView.OnDetailsSectionEventListener onDetailsSectionEventListener = new DetailsSectionView.OnDetailsSectionEventListener() {
        @Override
        public void onDriverInfoClicked() {
            getViewModel().setAsUpdated();

            EHIDriverInfo info;
            if (getViewModel().getDriverInfo() != null) {
                info = getViewModel().getDriverInfo();
            } else if (getViewModel().getLocalDriverInfo() != null) {
                info = getViewModel().getLocalDriverInfo();
            } else {
                info = new EHIDriverInfo();
            }

            Fragment fragment = new DriverInfoFragmentHelper.Builder()
                    .isEditing(true)
                    .isModify(getViewModel().isModify())
                    .driverInfo(info)
                    .build();

            Intent intent = new ModalActivityHelper.Builder()
                    .fragmentClass(fragment.getClass())
                    .fragmentArguments(fragment.getArguments())
                    .build(getActivity());

            startActivityForResult(
                    intent,
                    DRIVER_INFO_REQUEST_CODE
            );
        }

        @Override
        public void onFlightDetailsClicked() {
            openFlightDetailsFragment();
            getViewModel().setAsUpdated();
        }

        @Override
        public void onDeliveryCollectDetailsClicked() {
            ((ReservationFlowListener) getActivity()).showDeliveryAndCollection();
        }

        @Override
        public void onAdditionalInformationClicked() {
            if (getViewModel().getRawReservationObject().getCorporateAccount() != null) {
                showModalForResult(
                        getActivity(),
                        new AdditionalInfoFragmentHelper.Builder()
                                .extraAdditionalInfo(getViewModel().getAdditionalInformation())
                                .extraContract(getViewModel().getRawReservationObject().getCorporateAccount())
                                .extraContractNumber(getViewModel().getRawReservationObject().getCorporateAccount().getContractNumber())
                                .extraPreRate(false)
                                .build(),
                        ADDITIONAL_INFO_REQUEST_CODE
                );
            } else {
                DLog.e("No EHIContract found to show Additional Details screen");
            }
            getViewModel().setAsUpdated();
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnPrepayTermsAndConditionsCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
            prepayTermsChecked(checked);
            getViewModel().setPrepayTermsChecked(checked);
        }
    };

    private void prepayTermsChecked(boolean checked) {
        getViewBinding().cardsSectionView.setReviewCardInProfileViewChecked(checked);
        getViewBinding().haveReadConditionsCheckBox.setChecked(checked);
        if (checked) {
            getViewBinding().prepayTermsAndConditionsView.animate()
                    .translationY(getViewBinding().prepayTermsAndConditionsView.getHeight())
                    .setDuration(SLIDE_UP_ANIMATION_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            getViewBinding().prepayTermsAndConditionsView.setVisibility(View.GONE);
                        }
                    });
        } else {
            getViewBinding().prepayTermsAndConditionsView.setVisibility(View.VISIBLE);
            getViewBinding().prepayTermsAndConditionsView.animate()
                    .translationY(0)
                    .setDuration(SLIDE_UP_ANIMATION_DURATION)
                    .setListener(null);
        }
    }

    private void showAddCreditCardScreen() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_REVIEW.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_PAYMENT_METHOD.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.review())
                .tagScreen()
                .tagEvent();

        if (getViewModel().shouldShowPaymentsList()) {
            showModalForResult(getActivity(), new SelectCreditCardFragmentHelper.Builder().build(), SELECT_PAYMENT_REQUEST_CODE);
        } else {
            showModalForResult(getActivity(), new AddCreditCardFragmentHelper.Builder()
                            .extraIsModify(getViewModel().isModify())
                            .build(),
                    AddCreditCardFragment.REQUEST_CODE);
        }
    }

    //endregion

    //region lifecycle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof ReservationFlowListener)) {
            throw new NotImplementedException();
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(CREDIT_CARD_ADDED)) {
                getViewModel().creditAddedSuccessfully(savedInstanceState.getBoolean(CREDIT_CARD_ADDED));
            }
        }

        ReviewFragmentHelper.Extractor extractor = new ReviewFragmentHelper.Extractor(this);
        getViewModel().setIsModify(extractor.extraIsModify());
        getViewModel().setAddCreditCardListener(mPrepayClickListener);
        getViewModel().setIsLoginAfterStart(extractor.extraLoginAfterStart() != null && extractor.extraLoginAfterStart());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (getViewModel().getPayState() == PREPAY) {
            outState.putBoolean(CREDIT_CARD_ADDED, getViewModel().isCreditCardAddedSuccessfuly());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_review_book, container);
        initViews();
        return getViewBinding().getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_3DS_VALIDATION) {
            if (resultCode == Activity.RESULT_OK) {
                String paRes = EHIBundle.fromBundle(data.getExtras()).getString(KEY_3DS_PARES);
                getViewModel().threeDSAuthorizationCompleted(paRes);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                getViewModel().creditAddedSuccessfully(false);
            }
        } else if (requestCode == AddCreditCardFragment.REQUEST_CODE) {
            getViewModel().creditAddedSuccessfully(resultCode == Activity.RESULT_OK);
            if (getViewModel().isModify()) {
                getViewModel().setCollectedNewPaymentCardInModify(resultCode == Activity.RESULT_OK);
            }
            if (data != null
                    && data.getExtras() != null
                    && data.getExtras().containsKey(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE)) {
                getViewModel().setPaymentReferenceId(data.getExtras().getString(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE));
            }

        } else if (requestCode == ADDITIONAL_INFO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            EHIBundle ehiBundle = new EHIBundle(
                    data.getBundleExtra(AdditionalInfoFragment.EXTRA_DATA)
            );

            getViewModel().setAdditionalInformation(
                    ehiBundle.<List<EHIAdditionalInformation>>getEHIModel(
                            AdditionalInfoFragment.EXTRA_ADDITIONAL_INFO,
                            new TypeToken<List<EHIAdditionalInformation>>() {
                            }.getType()
                    )
            );
        } else if (requestCode == SELECT_PAYMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null
                        && data.getExtras() != null
                        && data.getExtras().containsKey(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE)) {
                    getViewModel().setPaymentReferenceId(data.getExtras().getString(AddCreditCardFragment.EXTRA_PAYMENT_REFERENCE));
                    final boolean showTerms = data.getExtras().getBoolean(EXTRA_SHOULD_SHOW_PREPAY_TERMS);
                    getViewModel().shouldShowPrepayTermsAndConditions.setValue(showTerms);
                    if (showTerms && getViewModel().shouldShowTermsAndConditions()) {
                        prepayTermsChecked(true);
                    }
                }
                getViewModel().creditAddedSuccessfully(true);
                if (getViewModel().isModify()) {
                    getViewModel().setCollectedNewPaymentCardInModify(true);
                }
            } else {
                if (getViewModel().getSelectedPaymentMethod() == null) {
                    getViewModel().setPaymentReferenceId(null);
                    getViewModel().creditAddedSuccessfully(false);
                }
            }
        } else if (requestCode == DRIVER_INFO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //main flow driver info stored locally so need to load new one from local storage
                getViewModel().setDriverInfo(getViewModel().getLocalDriverInfo());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getViewModel().setRawPayState(((ReservationFlowListener) getActivity()).getPayState());
        getViewModel().populateReservationObject();
    }

    @Override
    public void onResume() {
        super.onResume();
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_REVIEW.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.review())
                .macroEvent(EHIAnalytics.MacroEvent.MACRO_REVIEW_LOADED.value)
                .tagScreen()
                .tagEvent()
                .tagMacroEvent();
    }
    //endregion

    private void initViews() {
        getViewBinding().priceSummaryView.setOnExtraActionClickListener(mPriceSummaryExtraActionListener);
        getViewBinding().priceSummaryView.setPriceSummaryListener(mPriceSummaryListener);
        getViewBinding().priceSummaryView.getPrepayOrSaveView().setOnClickListener(mOnClickListener);

        getViewBinding().policyButtonContainer.keyFactsArea.setOnClickListener(mOnClickListener);
        getViewBinding().bookRentalButton.setOnClickListener(mOnClickListener);
        getViewBinding().bookRentalButton.setOnDisabledClickListener(mOnClickListener);
        getViewBinding().reviewPointsView.setOnActionClickListener(mReviewPointsActionListener);
        getViewBinding().changePaymentBanner.setPaymentBannerViewListener(mChangePaymentBannerListener);
        getViewBinding().haveReadConditions.setOnClickListener(mPrepayTermsAndConditionsClickListener);

        getViewBinding().policyButtonContainer.rentalPoliciesButton.setOnClickListener(mOnClickListener);

        getViewBinding().rentalSectionView.setScreenName(ReviewFragment.SCREEN_NAME);
        getViewBinding().rentalSectionView.setIsModify(getViewModel().isModify());
        getViewBinding().rentalSectionView.setOnRentalSectionClickListener(onRentalSectionClickListener);

        getViewBinding().detailsSectionView.setOnDetailsSectionEventListener(onDetailsSectionEventListener);
        getViewBinding().cardsSectionView.setAddPrepayViewListeners(getViewModel(), mReviewPrepayAddPaymentListener);
        getViewBinding().cardsSectionView.setPaymentModifyUnavailableListener(mPaymentModifyUnavailableListener);
        getViewBinding().detailsSectionView.setiBillingCallBack(getViewModel());
        getViewBinding().detailsSectionView.setOnReviewTripPurposeListener(mOnReviewTripPurposeListener);

        getViewBinding().cardsSectionView.setReviewCardPolicyListener(mReviewPrepayAddPaymentListener);
        getViewBinding().cardsSectionView.setReviewCardListener(mPrepayClickListener);
        getViewBinding().cardsSectionView.setReviewCardInProfileViewTermsCheckListener(mOnPrepayTermsAndConditionsCheckListener);
        getViewBinding().cardsSectionView.setReviewCardInProfileViewTermsClickListener(mPrepayTermsAndConditionsClickListener);

        if (getViewModel().getPayState() == PREPAY) {
            getViewBinding().cardsSectionView.setCreditCardAdded(getViewModel().isCreditCardAddedSuccessfuly());
        }

        getViewBinding().rentalTermsConditionsView.setOnClickListener(mOnClickListener);
        getViewBinding().rentalTermsConditionsView.setBackgroundColor(
                ResourcesCompat.getColor(getResources(), R.color.ehi_module_background_fill, getContext().getTheme()));

        getViewBinding().haveReadConditionsCheckBox.setOnCheckedChangeListener(mOnPrepayTermsAndConditionsCheckListener);

        SpannableString textToShow = new SpannableString(getResources().getString(R.string.terms_and_conditions_prepay_title));
        textToShow.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.ehi_primary)), 0, textToShow.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getViewBinding().haveReadConditions.setText(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .formatString(R.string.review_prepay_policies_read)
                .addTokenAndValue(EHIStringToken.POLICIES, textToShow)
                .format());
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();

        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorActivity.titleRes(getViewModel().title, getActivity()));
        bind(ReactorView.visibility(getViewModel().modifyPrePayBanner.visibility(), getViewBinding().modifyPrepayBanner));
        bind(BookRentalButton.title(getViewModel().continueButton.title(), getViewBinding().bookRentalButton));
        bind(BookRentalButton.enabled(getViewModel().continueButton.enabled(), getViewBinding().bookRentalButton));
        bind(BookRentalButton.price(getViewModel().continueButton.price(), getViewBinding().bookRentalButton));
        bind(BookRentalButton.priceVisibility(getViewModel().continueButton.priceVisibility(), getViewBinding().bookRentalButton));
        bind(BookRentalButton.netRateVisibility(getViewModel().continueButton.netRateVisibility(), getViewBinding().bookRentalButton));
        bind(BookRentalButton.subtitle(getViewModel().continueButton.subtitle(), getViewBinding().bookRentalButton));
        bind(BookRentalButton.priceSubtitle(getViewModel().continueButton.priceSubtitle(), getViewBinding().bookRentalButton));
        bind(ReactorView.visibility(getViewModel().continueButton.visibility(), getViewBinding().bookRentalButton));
        bind(ModifyPrePayBannerView.updated(getViewModel().isUpdated, getViewBinding().modifyPrepayBanner));

        addReaction("3DS_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHI3DSData data = getViewModel().get3DSData();
                if (data != null) {
                    // do 3ds validation
                    Fragment fragment = new ThreeDSFragmentHelper.Builder()
                            .acsUrl(data.getACSUrl())
                            .paReq(data.getPAReq())
                            .build();

                    Intent intent = new ModalActivityHelper.Builder()
                            .fragmentClass(fragment.getClass())
                            .fragmentArguments(fragment.getArguments())
                            .build(getActivity());

                    startActivityForResult(
                            intent,
                            REQUEST_CODE_3DS_VALIDATION
                    );
                }
            }
        });

        addReaction("CREDIT_CARD_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                boolean cardAdded = getViewModel().isCreditCardAddedSuccessfuly();
                final EHIPaymentMethod paymentMethod = getViewModel().getSelectedPaymentMethod();

                if (getViewModel().isAirportModifyBlockScenario()) {
                    getViewBinding().cardsSectionView.setVisibility(View.VISIBLE);
                    getViewBinding().cardsSectionView.showPaymentUnavailableView();
                } else {
                    if (paymentMethod != null && !getViewModel().isUserInCorpFlowWithBillingContract()) {
                        getViewBinding().cardsSectionView.setVisibility(View.VISIBLE);
                        getViewBinding().cardsSectionView.setPaymentMethod(paymentMethod);
                    } else {
                        getViewBinding().cardsSectionView.setCreditCardAdded(cardAdded);

                        final boolean showPrepayPayment = getViewModel().isPrePay() && cardAdded;
                        if (showPrepayPayment) {
                            getViewBinding().cardsSectionView.setVisibility(View.VISIBLE);
                            getViewBinding().cardsSectionView.showPrepayAddPaymentView();
                        } else {
                            getViewBinding().cardsSectionView.hidePrepayAddPaymentView();
                            getViewBinding().cardsSectionView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        addReaction("PAY_STATE_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                ((ReservationFlowListener) getActivity()).setPayState(getViewModel().getPayState());
            }
        });

        addReaction("SCROLL_TO_TOP_REACTION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                String message = getViewModel().toastMessage.getValue();
                if (message != null) {
                    getViewBinding().scrollView.scrollTo(0, 0);
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    getViewModel().toastMessage.setValue(null);
                }
            }
        });

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

        addReaction(RESERVATION_RESPONSE, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHIReservation ehiReservation = getViewModel().getReservationObject();
                if (ehiReservation != null) {
                    runNonReactive(new ReactorComputationFunction() {
                        @Override
                        public void react(ReactorComputation reactorComputation) {
                            reservationResponseSet(ehiReservation);
                        }
                    });
                }
            }
        });

        addReaction("COMMIT_ANIMATION", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final boolean shouldShowAnimation = getViewModel().shouldShowCommitAnimation();
                if (shouldShowAnimation) {
                    final ConfirmationAnimationFragment animationFragment = (ConfirmationAnimationFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ANIMATION_TAG);
                    if (animationFragment != null) {
                        return;
                    }

                    new FragmentUtils.Transaction(getFragmentManager(), FragmentUtils.ADD)
                            .fragment(new ConfirmationAnimationFragmentHelper.Builder().build(), ANIMATION_TAG)
                            .into(R.id.root_animation_container)
                            .commit();
                }
            }
        });

        addReaction(RESERVATION_COMMIT, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHIReservation commitResponse = getViewModel().getCommitReservationResult();
                if (commitResponse != null) {
                    final ConfirmationAnimationFragment animationFragment = (ConfirmationAnimationFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ANIMATION_TAG);
                    if (animationFragment == null) {
                        return;
                    }
                    animationFragment.endAnimation(new ConfirmationAnimationFragment.IFinishedCallback() {
                        @Override
                        public void onFinish() {

                            if (commitResponse.getCarClassDetails().getPriceSummary() != null) {
                                mPrice = Double.valueOf(commitResponse.getCarClassDetails().getPriceSummary().getEstimatedTotalView().getDoubleAmmount()).intValue();
                            } else {
                                mPrice = 0;
                            }

                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                                    .state(EHIAnalytics.State.STATE_SUCCESSFUL.value)
                                    .addDictionary(EHIAnalyticsDictionaryUtils.confirmation(commitResponse, getViewModel().isModify()))
                                    .macroEvent(EHIAnalytics.MacroEvent.MACRO_CONFIRMATION.value)
                                    .addCustomerValues(mPrice * 100) //should send in cents
                                    .tagScreen()
                                    .tagEvent()
                                    .tagMacroEvent();

                            if (getViewModel().is3rdPartyEmailNotify()) {
                                final CharSequence toastText = new TokenizedString.Formatter<EHIStringToken>(getResources())
                                        .formatString(R.string.confirmation_cancel_contract_success_message)
                                        .addTokenAndValue(EHIStringToken.CONTRACT_NAME, getViewModel().getAccountName())
                                        .format();

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showLongToast(getContext(), toastText);
                                    }
                                });
                            }

                            Intent intent = new ConfirmationActivityHelper.Builder()
                                    .extraReservation(commitResponse)
                                    .isModify(getViewModel().isModify())
                                    .exitGoesHome(true)
                                    .build(getActivity());
                            startActivity(intent);

                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                        }
                    });
                }
            }
        });

        addReaction(TRIP_PURPOSE_REQUIRED_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (!getViewModel().isModify()
                        && getViewModel().getRequiresTravelPurpose()
                        && !getViewModel().isTripPurposePreRate()
                        && (getViewModel().getTripPurpose() == null
                        || (getViewModel().getTripPurpose() != null
                        && !getViewModel().getTripPurpose().equalsIgnoreCase(TripPurposeFragment.TRIP_TYPE_LEISURE)))) {
                    getViewModel().setTripPurpose(TripPurposeFragment.TRIP_TYPE_BUSINESS);

                    getViewBinding().detailsSectionView.showTripPurpose();
                    getViewBinding().detailsSectionView.setTripPurpose(TripPurposeFragment.TRIP_TYPE_BUSINESS);
                    getViewBinding().detailsSectionView.populateDeliveryAndCollection(
                            getViewModel().getRawReservationObject()
                    );
                }
            }
        });

        addReaction(FLIGHT_DETAILS_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final EHIAirlineDetails details = getViewModel().getCurrentAirlineDetail();

                runNonReactive(new ReactorComputationFunction() {
                    @Override
                    public void react(final ReactorComputation reactorComputation) {
                        if (getViewModel().getRawReservationObject() != null
                                && getViewModel().getRawReservationObject().getPickupLocation().isAirport()) {
                            getViewBinding().detailsSectionView.setCurrentFlightDetails(
                                    details,
                                    getViewModel().getFlightNumber(),
                                    getViewModel().getRawReservationObject().getPickupLocation().isMultiTerminal()
                            );

                        } else {
                            getViewBinding().detailsSectionView.hideFlightDetailsView();
                        }
                    }
                });
            }
        });

        addReaction(BILLING_TYPE_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                getViewModel().updateContinueButton();
            }
        });

        addReaction(ERROR_REACTION, new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                final ResponseWrapper errorWrapper = getViewModel().getErrorWrapper();
                if (errorWrapper != null) {
                    final Handler exitHandler = new Handler();

                    final ConfirmationAnimationFragment animationFragment = (ConfirmationAnimationFragment) getActivity().getSupportFragmentManager().findFragmentByTag(ANIMATION_TAG);
                    if (animationFragment != null) {
                        animationFragment.forceStopAnimation(new ConfirmationAnimationFragment.IFinishedCallback() {
                            @Override
                            public void onFinish() {
                                exitHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new FragmentUtils.Transaction(getFragmentManager(), FragmentUtils.REMOVE)
                                                .fragment(animationFragment)
                                                .commit();

                                        DialogUtils.showErrorDialog(getActivity(), errorWrapper);
                                    }
                                });
                            }
                        });
                    } else {
                        DialogUtils.showErrorDialog(getActivity(), errorWrapper);
                    }
                    getViewModel().setErrorWrapper(null);
                    getViewModel().setShouldShowCommitAnimation(false);
                }
            }
        });

        addReaction("PREPAY_TERMS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                if (getViewModel().shouldShowPrepayTermsAndConditions.getValue()) {
                    getViewBinding().cardsSectionView.showReviewCardInProfileViewTermsCheckBox(true);
                    if (getViewModel().shouldShowTermsAndConditionsView()) {
                        getViewBinding().prepayTermsAndConditionsView.setVisibility(View.VISIBLE);
                        getViewModel().setTermsAndConditionsVisibility(true);
                    } else {
                        getViewModel().continueButton.setEnabled(true);
                        getViewModel().setTermsAndConditionsVisibility(false);
                    }
                } else {
                    getViewBinding().cardsSectionView.showReviewCardInProfileViewTermsCheckBox(false);
                    getViewBinding().prepayTermsAndConditionsView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void displayClearAllDialog(final boolean clearLocation) {
        DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_CONTINUE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();
                ((ReservationFlowListener) getActivity()).showItinerary(true, clearLocation);
            }
        };
        if (!getViewModel().isModify()) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(getResources().getString(R.string.reservation_edit_confirmation_alert_title))
                    .setPositiveButton(R.string.alert_okay_title, okClickListener)
                    .setNegativeButton(R.string.standard_button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_CANCEL.value)
                                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                                    .tagScreen()
                                    .tagEvent();
                        }
                    })
                    .create()
                    .show();
        } else {
            okClickListener.onClick(null, 0);
        }
    }

    private void reservationResponseSet(@NonNull final EHIReservation ehiReservation) {
        //redemption view
        updateRedemptionView();

        //modify pre pay banner original amount
        String prePayOriginalAmount = new ReviewFragmentHelper.Extractor(this).extraPrePayOriginalAmount();
        if (prePayOriginalAmount != null) {
            getViewModel().updateModifyPrePayBannerVisibility();
            getViewBinding().modifyPrepayBanner.setOriginalValue(prePayOriginalAmount);
        }

        //corp message
        if (ehiReservation.getBusinessLeisure() != null) {
            getViewBinding().policyButtonContainer.aboutRentalPoliciesText.setText(Html.fromHtml(ehiReservation.getBusinessLeisure()));
        }

        getViewBinding().rentalSectionView.setRentalSectionForReview(
                ehiReservation,
                getViewModel().shouldShowUpgrade(),
                getViewModel().isLoggedIntoEmeraldClub(),
                getViewModel().isAvailableAtContractRate(),
                getViewModel().isAvailableAtPromotionalRate(),
                getViewModel().isNorthAmerica(),
                mOnTermsClickListener
        );

        getViewBinding().detailsSectionView.setDetailsForReview(
                ehiReservation,
                getViewModel().isModify(),
                getViewModel().getDriverInfo(),
                getViewModel().getUserProfileCollection(),
                getViewModel().getAdditionalInformation()
        );

        //flight info
        if (getViewModel().getRawReservationObject().getPickupLocation().isAirport()) {
            getViewBinding().detailsSectionView.setCurrentFlightDetails(
                    getViewModel().getCurrentAirlineDetail(),
                    getViewModel().getFlightNumber(),
                    getViewModel().getRawReservationObject().getPickupLocation().isMultiTerminal()
            );
        } else {
            getViewBinding().detailsSectionView.hideFlightDetailsView();
        }

        getViewBinding().modifyPrepayBanner.setIsNorthAmericaAirport(getViewModel().isNorthAmericaAirportLocation());


        //price summary
        populatePriceSummary();

        //rental policies
        if (ehiReservation.getPolicies() != null
                && ehiReservation.getPolicies().size() > 0) {
            getViewBinding().policyButtonContainer.rentalPoliciesButton.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().policyButtonContainer.rentalPoliciesButton.setVisibility(View.GONE);
        }

        //key fact
        if (ListUtils.isEmpty(ehiReservation.getEHIKeyFactsPolicies())) {
            getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.GONE);
        } else if (ehiReservation.isEuropeanUnionCountry()) {
            getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().policyButtonContainer.keyFactsContainer.setVisibility(View.GONE);
        }

        //DC
        if (ehiReservation.contractHasAdditionalBenefits()) {
            getViewModel().setRequiresTravelPurpose(true);
        }

        //book button
        getViewModel().updateContinueButton();

        //prepay
        if (getViewModel().canChangePaymentOptions()) {
            getViewBinding().priceSummaryView.getPrepayOrSaveView().setPayState(getViewModel().getPayState());
            getViewBinding().priceSummaryView.getPrepayOrSaveView().setVisibility(View.VISIBLE);
            getViewBinding().priceSummaryView.getPrepayOrSaveView().populateView(ehiReservation.getCarClassDetails());

            getViewBinding().changePaymentBanner.populateView(ehiReservation.getCarClassDetails(), getViewModel().getPayState());
            getViewBinding().changePaymentBanner.setVisibility(View.VISIBLE);
        } else {
            getViewBinding().priceSummaryView.getPrepayOrSaveView().setVisibility(View.GONE);
            if (!getViewModel().getRawReservationObject().isPrepaySelected()) {
                getViewBinding().cardsSectionView.hidePrepayAddPaymentView();
            }
            getViewBinding().changePaymentBanner.setVisibility(View.GONE);
        }

        if (!ListUtils.isEmpty(ehiReservation.getUpgradeCarClassDetails())) {
            getViewModel().setUpgradeAmount(ehiReservation.getUpgradeCarClassDetails());
        }
    }

    private void updateRedemptionView() {
        if (getViewModel().shouldShowRedemption() &&
                (((ReservationFlowListener) getActivity()).getPayState() != PREPAY) &&
                (!getViewModel().isIsLoginAfterStart())) {
            getViewBinding().reviewPointsView.setVisibility(View.VISIBLE);
            getViewBinding().reviewPointsView.setShowPoint(
                    getViewModel().needShowPoints() && !getViewModel().wasRedemptionCanceled()
            );
            EHIReservation reservation = getViewModel().getRawReservationObject();
            EHICarClassDetails details = reservation.getCarClassDetails();
            if (details != null) {
                getViewBinding().reviewPointsView.setCarClassDetails(details);
            }

            final EHIBasicProfile ehiBasicProfile = getViewModel().getUserProfileCollection().getBasicProfile();

            if (getViewModel().getUserProfileCollection() != null && ehiBasicProfile != null
                    && ehiBasicProfile.getLoyaltyData() != null) {
                getViewBinding().reviewPointsView.setCurrentPoints(ehiBasicProfile.getLoyaltyData().getFormattedPointsToDate());
            }
            getViewBinding().reviewPointsView.setRedeemingInformations(
                    getViewModel().getRedemptionPoints(), getViewModel().getRedemptionDayCount()
            );
        } else {
            getViewBinding().reviewPointsView.setVisibility(View.GONE);
        }
    }

    private void populatePriceSummary() {
        if (getViewModel().getRawReservationObject() != null &&
                getViewModel().getRawReservationObject().getCarClassDetails().getPriceSummary() != null) {
            getViewBinding().priceSummaryView.setIsPrepay(getViewModel().getPayState() == PREPAY);
            getViewBinding().priceSummaryView.setIsModify(getViewModel().isModify());
            getViewBinding().priceSummaryView.setRedemptionInfo(
                    getViewModel().getRedemptionDayCount(), getViewModel().getRedemptionPointsRate()
            );
            getViewBinding().priceSummaryView.setReservation(getViewModel().getRawReservationObject());
            getViewBinding().reviewPointsView.setShowPoint(
                    getViewModel().needShowPoints() && !getViewModel().wasRedemptionCanceled()
            );
        } else {
            getViewBinding().priceSummaryView.setVisibility(View.GONE);
        }
    }

    private void openFlightDetailsFragment() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_REVIEW.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_ADD_FLIGHT_INFO.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.review())
                .tagScreen()
                .tagEvent();
        final FlightDetailsFragmentHelper.Builder builder = new FlightDetailsFragmentHelper.Builder();

        if (getViewModel().getCurrentAirlineDetail() != null) {
            builder.flightNumber(getViewModel().getFlightNumber())
                    .selectedAirline(getViewModel().getCurrentAirlineDetail())
                    .multiTerminal(getViewModel().getRawReservationObject().getPickupLocation().isMultiTerminal());
        }
        builder.flightDetails(getViewModel().getAirlineDetailsList())
                .isEditing(true)
                .isModify(getViewModel().isModify());

        showModalForResult(getActivity(), builder.build(), REQUEST_CODE_FLIGHT_DETAILS);
    }

    private void check3DSAndCommit() {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_REVIEW.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_BOOK_RENTAL.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.review())
                .tagScreen()
                .tagEvent();


        if (!getViewBinding().detailsSectionView.isAdditionalInformationValid()) {
            int additionalInfoY = (int) getViewBinding().detailsSectionView.getY() + (int) getViewBinding().detailsSectionView.getAdditionalInformationY();
            getViewBinding().scrollView.smoothScrollTo(0, additionalInfoY);
            ToastUtils.showToast(getActivity(), getString(R.string.review_please_enter_additional_information));
            return;
        }

        BaseAppUtils.hideKeyboard(getActivity());

        getViewModel().check3DSAndCommitReservation();
    }

    private void togglePaymentState() {
        final ReservationFlowListener flowListener = (ReservationFlowListener) getActivity();
        flowListener.setPayState(flowListener.getPayState() == PREPAY ? PAY_LATER : PREPAY);
        getViewModel().clearPaymentState(flowListener.getPayState());
        getViewBinding().priceSummaryView.getPrepayOrSaveView().toggleVisibility();
        getViewBinding().cardsSectionView.togglePrepayAddPaymentVisibility();
    }

}
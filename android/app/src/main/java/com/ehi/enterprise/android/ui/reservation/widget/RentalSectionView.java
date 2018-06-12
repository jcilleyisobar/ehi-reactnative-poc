package com.ehi.enterprise.android.ui.reservation.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.databinding.RentalSectionViewBinding;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.ui.location.interfaces.OnLocationDetailEventsListener;
import com.ehi.enterprise.android.ui.reservation.ReviewFragment;
import com.ehi.enterprise.android.ui.reservation.interfaces.OnExtraActionListener;
import com.ehi.enterprise.android.ui.view.DataBindingViewModelView;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.isobar.android.tokenizedstring.TokenizedString;
import com.isobar.android.viewmodel.ViewModel;

@ViewModel(RentalSectionViewModel.class)
public class RentalSectionView extends DataBindingViewModelView<RentalSectionViewModel, RentalSectionViewBinding> {

    private static final String TAG = "RentalSectionView";

    private boolean rejectClick = false;

    private OnRentalSectionClickListener onRentalSectionClickListener;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (rejectClick) {
                return;
            }

            if (view == getViewBinding().reviewLocationsView) {
                if (getViewModel().shouldShowBlockLocationChangePopup()) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, ReviewFragment.SCREEN_NAME)
                            .state(EHIAnalytics.State.STATE_REVIEW.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_LOCK.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.review())
                            .tagScreen()
                            .tagEvent();

                    onRentalSectionClickListener.showLocationChangeBlockPopup();
                } else {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                            .state(EHIAnalytics.State.STATE_REVIEW.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_LOCATION.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.review())
                            .tagScreen()
                            .tagEvent();
                    displayClearAllDialog(true);
                }
            } else if (view == getViewBinding().pickupDateView
                    || view == getViewBinding().returnDateView) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_DATE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();
                displayClearAllDialog(false);
            }
        }
    };

    private OnExtraActionListener mEditExtraActionListener = new OnExtraActionListener() {
        @Override
        public void onChangeExtraCount(EHIExtraItem item, int newCount) {
        }

        @Override
        public void onClick(EHIExtraItem item) {
            if (rejectClick) {
                return;
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_EXTRA.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
            DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                            .state(EHIAnalytics.State.STATE_REVIEW.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_CONTINUE.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.review())
                            .tagScreen()
                            .tagEvent();
                    if (onRentalSectionClickListener != null) {
                        onRentalSectionClickListener.editCarExtrasClicked(true, false);
                    }
                }
            };
            if (!getViewModel().isModify()) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.reservation_edit_confirmation_alert_title)
                        .setPositiveButton(R.string.alert_okay_title, okClickListener)
                        .setNegativeButton(R.string.standard_button_cancel, null)
                        .create()
                        .show();
            } else {
                okClickListener.onClick(null, 0);
            }
        }
    };

    private ReviewCarClassView.CarReviewCallback mCarReviewCallback = new ReviewCarClassView.CarReviewCallback() {
        @Override
        public void carChangeClicked() {
            if (rejectClick) {
                return;
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_VEHICLE.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
            DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EHIAnalyticsEvent.create()
                            .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                            .state(EHIAnalytics.State.STATE_REVIEW.value)
                            .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_CONTINUE.value)
                            .addDictionary(EHIAnalyticsDictionaryUtils.review())
                            .tagScreen()
                            .tagEvent();
                    if (onRentalSectionClickListener != null) {
                        onRentalSectionClickListener.showAvailableCarClassesClicked(true);
                    }
                }
            };
            if (!getViewModel().isModify()) {
                new AlertDialog.Builder(getContext())
                        .setMessage(R.string.reservation_edit_confirmation_alert_title)
                        .setPositiveButton(R.string.alert_okay_title, okClickListener)
                        .setNegativeButton(R.string.standard_button_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                EHIAnalyticsEvent.create()
                                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
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

        @Override
        public void carUpgradeClicked(String carId) {
            if (rejectClick) {
                return;
            }

            EHIAnalyticsEvent.create()
                    .screen(EHIAnalytics.Screen.SCREEN_NOTIFICATION.value, getViewModel().getScreenName())
                    .state(EHIAnalytics.State.STATE_REVIEW.value)
                    .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_UPGRADE_NOW.value)
                    .addDictionary(EHIAnalyticsDictionaryUtils.review())
                    .tagScreen()
                    .tagEvent();
            if (onRentalSectionClickListener != null) {
                onRentalSectionClickListener.carUpgradeClicked(carId);
            }
        }
    };

    public RentalSectionView(Context context) {
        this(context, null);
    }

    public RentalSectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RentalSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            addView(inflate(context, R.layout.v_rental_section, null));
            return;
        }

        createViewBinding(R.layout.v_rental_section);
        initViews();
    }

    public void setScreenName(String screenName) {
        getViewModel().setScreenName(screenName);
    }

    public void setIsModify(boolean isModify) {
        getViewModel().setIsModify(isModify);
    }

    public void setOnRentalSectionClickListener(OnRentalSectionClickListener onRentalSectionClickListener) {
        this.onRentalSectionClickListener = onRentalSectionClickListener;
    }

    public void setOnLocationDetailEventsListener(OnLocationDetailEventsListener listener) {
        getViewBinding().confirmationLocationsView.setOnLocationDetailEventsListener(listener);
    }

    public void setRentalSectionForConfirmation(EHIReservation ehiReservation, boolean shouldShowUpgrade,
                                                boolean isLoggedIntoEmeraldClub,
                                                boolean isAvailableAtContractRate, boolean isAvailableAtPromotionalRate,
                                                View.OnClickListener mOnTermsClickListener, boolean isNA) {
        setRentalSection(
                ehiReservation,
                shouldShowUpgrade,
                isLoggedIntoEmeraldClub,
                isAvailableAtContractRate,
                isAvailableAtPromotionalRate,
                mOnTermsClickListener,
                true,
                true,
                isNA
        );
    }

    public void setRentalSectionForReview(EHIReservation ehiReservation, boolean shouldShowUpgrade,
                                          boolean isLoggedIntoEmeraldClub,
                                          boolean isAvailableAtContractRate, boolean isAvailableAtPromotionalRate,
                                          boolean isNA,
                                          View.OnClickListener mOnTermsClickListener) {
        setRentalSection(
                ehiReservation,
                shouldShowUpgrade,
                isLoggedIntoEmeraldClub,
                isAvailableAtContractRate,
                isAvailableAtPromotionalRate,
                mOnTermsClickListener,
                false,
                false,
                isNA
        );
    }

    private void setRentalSection(EHIReservation ehiReservation, boolean shouldShowUpgrade,
                                  boolean isLoggedIntoEmeraldClub,
                                  boolean isAvailableAtContractRate, boolean isAvailableAtPromotionalRate,
                                  View.OnClickListener mOnTermsClickListener, boolean isConfirmation,
                                  boolean hideGreenArrows, boolean isNA) {
        getViewModel().setEhiReservation(ehiReservation);
        getViewModel().setNorthAmerica(isNA);

        rejectClick = isConfirmation;

        //locations
        if (isConfirmation) {
            getViewBinding().confirmationLocationsView.setReservation(ehiReservation);
            getViewBinding().reviewLocationsView.setVisibility(GONE);
        } else {
            getViewBinding().confirmationLocationsView.setVisibility(GONE);
            getViewBinding().reviewLocationsView.setLocations(
                    ehiReservation.getPickupLocation(),
                    ehiReservation.getReturnLocation(),
                    getViewModel().shouldShowBlockLocationChangePopup()
            );
        }

        //date and time
        getViewBinding().pickupDateView.setTime(ehiReservation.getPickupTime());
        getViewBinding().returnDateView.setTime(ehiReservation.getReturnTime());

        //car class
        if (isConfirmation) {
            getViewBinding().confirmationCarClassView.setCarClassDetails(ehiReservation.getCarClassDetails());
            getViewBinding().reviewCarClassView.setVisibility(GONE);
        } else {
            getViewBinding().confirmationCarClassView.setVisibility(GONE);
            getViewBinding().reviewCarClassView.setCarClassDetails(ehiReservation, shouldShowUpgrade);
        }

        //extras
        getViewBinding().reviewExtrasView.setExtras(getViewModel().getExtras(), false);

        final String corporateAccountId = getViewModel().getCorporateAccountId();
        if (!TextUtils.isEmpty(corporateAccountId)) {
            boolean hasTerms = !TextUtils.isEmpty(getViewModel().getCorporateAccountTermsAndConditions());
            getViewBinding().infoBanner.setup(
                    getViewModel().getCorporateAccountName(),
                    getViewModel().getCorporateContractType(),
                    isAvailableAtContractRate,
                    isAvailableAtPromotionalRate,
                    hasTerms ? mOnTermsClickListener : null,
                    false
            );

            if (getViewModel().is3rdPartyEmailNotify() && !isConfirmation) {
                int resourceId = getViewModel().isModify() ?
                        R.string.review_contract_notification_banner_modify_title :
                        R.string.review_contract_notification_banner_title;
                CharSequence bannerText = new TokenizedString.Formatter<EHIStringToken>(getResources())
                        .formatString(resourceId)
                        .addTokenAndValue(EHIStringToken.CONTRACT_NAME, getViewModel().getCorporateAccountName())
                        .format();

                getViewBinding().thirdPartEmailBanner.setUp(bannerText);
            }
        }

        //ec banner
        final boolean showECBannerInConfirmation = isLoggedIntoEmeraldClub && isConfirmation;
        final boolean showECBannerInReviewScreen = isLoggedIntoEmeraldClub && ehiReservation.getCorporateAccount() == null;

        if (showECBannerInReviewScreen || showECBannerInConfirmation) {
            getViewBinding().ecBanner.setVisibility(View.VISIBLE);
            getViewBinding().ecBanner.setMessage(R.string.reservation_confirmation_emerald_club_active_message);
        } else {
            getViewBinding().ecBanner.setVisibility(View.GONE);
        }

        if (hideGreenArrows) {
            hideGreenArrow();
        } else {
            showGreenArrow();
        }
    }

    private void initViews() {
        getViewBinding().reviewCarClassView.setOnClickListener(mOnClickListener);
        getViewBinding().reviewExtrasView.setOnExtraActionListener(mEditExtraActionListener);
        getViewBinding().pickupDateView.setOnClickListener(mOnClickListener);
        getViewBinding().returnDateView.setOnClickListener(mOnClickListener);
        getViewBinding().reviewCarClassView.setCarClassChangeCarListener(mCarReviewCallback);
        getViewBinding().reviewLocationsView.setOnClickListener(mOnClickListener);
    }

    private void displayClearAllDialog(final boolean clearLocation) {
        DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EHIAnalyticsEvent.create()
                        .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
                        .state(EHIAnalytics.State.STATE_REVIEW.value)
                        .action(EHIAnalytics.Motion.MOTION_TAP.value, EHIAnalytics.Action.ACTION_CHANGE_CONTINUE.value)
                        .addDictionary(EHIAnalyticsDictionaryUtils.review())
                        .tagScreen()
                        .tagEvent();
                if (onRentalSectionClickListener != null) {
                    onRentalSectionClickListener.clearAllClicked(true, clearLocation);
                }
            }
        };
        if (!getViewModel().isModify()) {
            new AlertDialog.Builder(getContext())
                    .setMessage(R.string.reservation_edit_confirmation_alert_title)
                    .setPositiveButton(R.string.alert_okay_title, okClickListener)
                    .setNegativeButton(R.string.standard_button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EHIAnalyticsEvent.create()
                                    .screen(EHIAnalytics.Screen.SCREEN_RESERVATION.value, getViewModel().getScreenName())
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

    public void hideGreenArrow() {
        getViewBinding().reviewLocationsView.hideGreenArrow();
        getViewBinding().pickupDateView.hideGreenArrow();
        getViewBinding().returnDateView.hideGreenArrow();
        getViewBinding().reviewCarClassView.hideGreenArrow();
        getViewBinding().reviewExtrasView.hideGreenArrow();
    }

    public void showGreenArrow() {
        getViewBinding().reviewLocationsView.showGreenArrow();
        getViewBinding().pickupDateView.showGreenArrow();
        getViewBinding().returnDateView.showGreenArrow();
        getViewBinding().reviewCarClassView.showGreenArrow();
        getViewBinding().reviewExtrasView.showGreenArrow();
    }

    public interface OnRentalSectionClickListener {
        void clearAllClicked(boolean edit, boolean clearLocation);

        void carUpgradeClicked(String carId);

        void showAvailableCarClassesClicked(boolean edit);

        void editCarExtrasClicked(boolean edit, final boolean fromChooseYourRate);

        void openReviewLocationsClicked();

        void showLocationChangeBlockPopup();
    }
}

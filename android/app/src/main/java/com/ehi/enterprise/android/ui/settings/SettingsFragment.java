package com.ehi.enterprise.android.ui.settings;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ehi.enterprise.android.BuildConfig;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.databinding.SettingsFragmentBinding;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.miscellaneous.PrivacyPolicyResponse;
import com.ehi.enterprise.android.network.responses.miscellaneous.TermsOfUseResponse;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.dashboard.CountriesListFragmentHelper;
import com.ehi.enterprise.android.ui.fragment.DataBindingViewModelFragment;
import com.ehi.enterprise.android.ui.fragment.DisclaimerFragmentHelper;
import com.ehi.enterprise.android.ui.fragment.DisclaimerWithAnchorLinksFragmentHelper;
import com.ehi.enterprise.android.utils.DialogUtils;
import com.ehi.enterprise.android.utils.FragmentUtils;
import com.ehi.enterprise.android.utils.PermissionUtils;
import com.ehi.enterprise.android.utils.SnackBarUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalytics;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsDictionaryUtils;
import com.ehi.enterprise.android.utils.analytics.EHIAnalyticsEvent;
import com.ehi.enterprise.android.utils.manager.AnalyticsManager;
import com.ehi.enterprise.android.utils.permission.PermissionRequestHandler;
import com.ehi.enterprise.android.utils.permission.PermissionRequester;
import com.isobar.android.newinstancer.NoExtras;
import com.isobar.android.viewmodel.ViewModel;

import io.dwak.reactor.ReactorComputation;
import io.dwak.reactor.interfaces.ReactorComputationFunction;
import io.dwak.reactorbinding.view.ReactorView;

@NoExtras
@ViewModel(SettingsViewModel.class)
public class SettingsFragment extends DataBindingViewModelFragment<SettingsViewModel, SettingsFragmentBinding> {

    public static final String SCREEN_NAME = "SettingsFragment";
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 10;

    private DialogInterface.OnClickListener mOnClearClickedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            getViewModel().clearPersonalData();
        }
    };

    private DialogInterface.OnClickListener mOnRightToBeForgottenClickedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            boolean positive = which == DialogInterface.BUTTON_POSITIVE;

            trackClick(String.format("%s%S",
                    EHIAnalytics.Action.ACTION_CLEAR_HISTORICAL_DATA.value,
                    positive ? EHIAnalytics.Action.ACTION_YES.value : EHIAnalytics.Action.ACTION_NO.value));
            AnalyticsManager.getInstance().forceUpload();

            if (positive) {
                getViewModel().rightToBeForgotten();
                getViewBinding().clearTrackingAnalytics.setChecked(false);
                getViewModel().setAnalyticsEnabled(false);
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == getViewBinding().trackingSetting) {
                boolean isChecked = getViewBinding().trackingSetting.isChecked();
                trackClick(isChecked ? EHIAnalytics.Action.ACTION_AUTO_SAVE_ON : EHIAnalytics.Action.ACTION_AUTO_SAVE_OFF);
                getViewModel().setAutoSaveEnabled(isChecked);
            } else if (view == getViewBinding().clearTrackingAnalytics) {
                boolean isChecked = getViewBinding().clearTrackingAnalytics.isChecked();
                trackClick(isChecked ? EHIAnalytics.Action.ACTION_DATA_COLLECTION_ON : EHIAnalytics.Action.ACTION_DATA_COLLECTION_OFF);
                getViewModel().setAnalyticsEnabled(isChecked);
            } else if (view == getViewBinding().clearTrackingSearchHistory) {
                boolean isChecked = getViewBinding().clearTrackingSearchHistory.isChecked();
                trackClick(isChecked ? EHIAnalytics.Action.ACTION_SAVE_SEARCH_HISTORY_ON : EHIAnalytics.Action.ACTION_SAVE_SEARCH_HISTORY_OFF);
                getViewModel().setSearchHistoryEnabled(isChecked);
            } else if (view == getViewBinding().usePreferredCreditCard) {
                boolean isChecked = getViewBinding().usePreferredCreditCard.isChecked();
                trackClick(isChecked ? EHIAnalytics.Action.ACTION_PPREFERRED_CC_ON : EHIAnalytics.Action.ACTION_PPREFERRED_CC_OFF);
                getViewModel().setShouldAutomaticallySelectCard(isChecked);
            } else if (view == getViewBinding().clearPersonalDataButton) {
                trackClick(EHIAnalytics.Action.ACTION_CLEAR_PERSONAL_DATA);
                DialogUtils.showOkCancelDialog(getActivity(),
                        getString(R.string.settings_clear_data_confirmation_title),
                        getResources().getString(R.string.settings_clear_data_confirmation_details),
                        mOnClearClickedListener);
            } else if (getViewBinding().settingsDataCollectionInfo == view) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getResources().getString(R.string.settings_privacy_data_collection_modal_content),
                        getResources().getString(R.string.settings_privacy_data_collection_modal_title));
            } else if (view == getViewBinding().rightToBeForgottenButton) {
                trackClick(EHIAnalytics.Action.ACTION_CLEAR_HISTORICAL_DATA);
                DialogUtils.showDialogLongTitle(getActivity(),
                        getString(R.string.right_to_be_forgotten_modal_title),
                        getString(R.string.right_to_be_forgotten_modal_summary),
                        R.string.right_to_be_forgotten_modal_confirm,
                        R.string.right_to_be_forgotten_modal_cancel,
                        mOnRightToBeForgottenClickedListener);
            } else if (getViewBinding().rightToBeForgottenInfo == view) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getResources().getString(R.string.right_to_be_forgotten_info_summary),
                        getResources().getString(R.string.right_to_be_forgotten_info_title));
            } else if (getViewBinding().settingsSearchHistoryInfo == view) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getString(R.string.settings_privacy_save_history_modal_content),
                        getString(R.string.settings_privacy_save_history_modal_title));
            } else if (getViewBinding().settingsUsePreferredCreditCard == view) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getString(R.string.settings_privacy_use_preferred_card_dialog_message),
                        getString(R.string.settings_privacy_use_preferred_card_dialog_title));
            } else if (getViewBinding().settingsClearHistoryInfo == view) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getString(R.string.settings_privacy_clear_data_modal_content),
                        getString(R.string.settings_privacy_clear_data_modal_title));
            } else if (getViewBinding().settingsRentalAssistant == view) {
                DialogUtils.showDialogWithTitleAndText(getActivity(),
                        getString(R.string.rental_assistant_modal_text),
                        getString(R.string.rental_assistant_modal_title));
            } else if (getViewBinding().privacyPolicy == view) {
                FragmentUtils.addProgressFragment(getActivity());
                getViewModel().requestPrivacyPolicy();
            } else if (view == getViewBinding().termsOfUse) {
                FragmentUtils.addProgressFragment(getActivity());
                getViewModel().requestTermsOfUse();
            } else if (view == getViewBinding().thirdPartyLicense) {
                showModal(getActivity(), new LicensesFragmentHelper.Builder().build());
            } else if (view == getViewBinding().termsAndConditions) {
                FragmentUtils.addProgressFragment(getActivity());
                getViewModel().requestTermsAndConditions();
            } else if (view == getViewBinding().changeRegionArea) {
                showModalWithSearchHeader(getActivity(), new CountriesListFragmentHelper.Builder().build());
            } else if (view == getViewBinding().pickupNotifications) {
                trackClickNotification(EHIAnalytics.Action.ACTION_NOTIFICATION_REMIND_PICKUP);
                showModal(getActivity(), new NotificationSettingsFragmentHelper.Builder().isPickup(true).build());
            } else if (view == getViewBinding().returnNotifications) {
                trackClickNotification(EHIAnalytics.Action.ACTION_NOTIFICATION_REMIND_DROPOFF);
                showModal(getActivity(), new NotificationSettingsFragmentHelper.Builder().isPickup(false).build());
            } else if (view == getViewBinding().eraToggle) {
                if (getViewBinding().eraToggle.isChecked()) {
                    ((PermissionRequestHandler) getActivity())
                            .requestPermissions(LOCATION_PERMISSION_REQUEST_CODE,
                                    new PermissionRequester() {
                                        @Override
                                        public void onRequestPermissionResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
                                            boolean permissionsGranted = PermissionUtils.areAllPermissionsGranted(grantResults);
                                            if (permissionsGranted) {
                                                switch (requestCode) {
                                                    case LOCATION_PERMISSION_REQUEST_CODE:
                                                        getViewModel().setEnterpriseRentalAssistantChecked(true);
                                                        break;
                                                }
                                            } else {
                                                switch (requestCode) {
                                                    case LOCATION_PERMISSION_REQUEST_CODE:
                                                        if (getView() != null && getView().getRootView() != null) {
                                                            SnackBarUtils.showLocationPermissionSnackBar(getActivity(), getViewBinding().getRoot());
                                                        }
                                                        break;
                                                }
                                            }
                                        }
                                    },
                                    Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    getViewModel().setEnterpriseRentalAssistantChecked(false);
                }
            } else if (view == getViewBinding().profileFingerprintSetting) {
                getViewModel().fingerprintProfileUnlockClicked(getViewBinding().profileFingerprintSetting.isChecked());
            } else if (view == getViewBinding().profileFingerprintSettingInfo) {
                DialogUtils.showDialogWithTitleAndText(getActivity(), getString(R.string.profile_fingerprint_unlock_info_message), getString(R.string.settings_security_row_fingerprint_title));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createViewBinding(inflater, R.layout.fr_settings, container);
        initViews();
        return getViewBinding().getRoot();
    }

    private void initViews() {
        getViewBinding().trackingSetting.setChecked(getViewModel().isTrackingEnabled());
        getViewBinding().trackingSetting.setOnClickListener(mOnClickListener);
        getViewBinding().clearTrackingAnalytics.setChecked(getViewModel().isAnalyticsTrackingEnabled());
        getViewBinding().clearTrackingAnalytics.setOnClickListener(mOnClickListener);
        getViewBinding().settingsDataCollectionInfo.setOnClickListener(mOnClickListener);
        getViewBinding().clearTrackingSearchHistory.setChecked(getViewModel().isSearchHistoryEnabled());
        getViewBinding().clearTrackingSearchHistory.setOnClickListener(mOnClickListener);
        getViewBinding().settingsSearchHistoryInfo.setOnClickListener(mOnClickListener);
        getViewBinding().clearPersonalDataButton.setOnClickListener(mOnClickListener);
        getViewBinding().settingsClearHistoryInfo.setOnClickListener(mOnClickListener);
        getViewBinding().termsOfUse.setOnClickListener(mOnClickListener);
        getViewBinding().thirdPartyLicense.setOnClickListener(mOnClickListener);
        getViewBinding().privacyPolicy.setOnClickListener(mOnClickListener);
        getViewBinding().termsAndConditions.setOnClickListener(mOnClickListener);
        getViewBinding().changeRegionArea.setOnClickListener(mOnClickListener);
        getViewBinding().pickupNotifications.setOnClickListener(mOnClickListener);
        getViewBinding().returnNotifications.setOnClickListener(mOnClickListener);
        getViewBinding().settingsVersionNumber.setText(BuildConfig.VERSION_NAME);
        getViewBinding().eraToggle.setOnClickListener(mOnClickListener);
        getViewBinding().settingsRentalAssistant.setOnClickListener(mOnClickListener);
        getViewBinding().rightToBeForgottenButton.setOnClickListener(mOnClickListener);
        getViewBinding().rightToBeForgottenInfo.setOnClickListener(mOnClickListener);

        if (getViewModel().isUserLoggedIn()) {
            getViewBinding().usePreferredCreditCardView.setVisibility(View.VISIBLE);
            getViewBinding().usePreferredCreditCard.setChecked(getViewModel().shouldAutomaticallySelectCard());
            getViewBinding().usePreferredCreditCard.setOnClickListener(mOnClickListener);
            getViewBinding().settingsUsePreferredCreditCard.setOnClickListener(mOnClickListener);
        }

        final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getActivity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fingerprintManagerCompat.isHardwareDetected()) {
            getViewBinding().securityHeader.setVisibility(View.VISIBLE);
            getViewBinding().profileFingerprintSetting.setVisibility(View.VISIBLE);
            getViewBinding().profileFingerprintSetting.setChecked(getViewModel().isFingerprintSettingProfileUnlockEnabled());
            getViewBinding().profileFingerprintSetting.setOnClickListener(mOnClickListener);
            getViewBinding().profileFingerprintSettingInfo.setOnClickListener(mOnClickListener);
        } else {
            getViewBinding().securityHeader.setVisibility(View.GONE);
            getViewBinding().profileFingerprintSetting.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.settings_navigation_title));
        getViewBinding().frSettingsRegionName.setText(getViewModel().getPreferredRegionName());
        getViewBinding().pickupNotificationSubtitle.setText(getViewModel().getPickupNotificationTime().stringResId);
        getViewBinding().returnNotificationSubtitle.setText(getViewModel().getReturnNotificationTime().stringResId);
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SETTINGS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.signIn())
                .tagScreen()
                .tagEvent();
    }

    @Override
    protected void initDependencies() {
        super.initDependencies();
        bind(FragmentUtils.progress(getViewModel().progress, getActivity()));
        bind(ReactorView.visibility(getViewModel().notificationDivider.visibility(), getViewBinding().notificationsDivider));
        bind(ReactorView.visibility(getViewModel().notificationsHeader.visibility(), getViewBinding().notificationHeader));
        bind(ReactorView.visibility(getViewModel().pickupNotifications.visibility(), getViewBinding().pickupNotifications));
        bind(ReactorView.visibility(getViewModel().returnNotifications.visibility(), getViewBinding().returnNotifications));
        bind(ReactorView.visibility(getViewModel().fingerprint.visibility(), getViewBinding().profileFingerprintSetting));
        bind(ReactorView.visibility(getViewModel().securityHeader.visibility(), getViewBinding().securityHeader));
        bind(ReactorView.visibility(getViewModel().enterpriseRentalAssistant.visibility(), getViewBinding().eraToggle));

        addReaction(new ReactorComputationFunction() {
            @Override
            public void react(final ReactorComputation reactorComputation) {
                getViewBinding().eraToggle.setChecked(getViewModel().isEnterpriseRentalAssistantChecked());
            }
        });

        addReaction("PRIVACY_POLICY_REACTION_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                PrivacyPolicyResponse response = getViewModel().getPrivacyPolicy();
                if (response != null) {
                    showModal(getActivity(),
                            new DisclaimerWithAnchorLinksFragmentHelper.Builder()
                                    .keyTitle(getString(R.string.settings_privacy_section_title))
                                    .keyBody(response.getPrivacyPolicy())
                                    .build());
                    FragmentUtils.removeProgressFragment(getActivity());
                    getViewModel().setPolicy(null);
                }
            }
        });

        addReaction("TERMS_OF_USE_REACTION_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                TermsOfUseResponse response = getViewModel().getTermsOfUse();
                if (response != null) {
                    showModal(getActivity(),
                            new DisclaimerWithAnchorLinksFragmentHelper.Builder()
                                    .keyTitle(getString(R.string.terms_of_use_navigation_title))
                                    .keyBody(response.getTermsOfUse())
                                    .build());
                    FragmentUtils.removeProgressFragment(getActivity());
                    getViewModel().setTermsOfUse(null);
                }
            }
        });

        addReaction("TERMS_AND_CONDITIONS_SUCCESS", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                GetEPlusTermsAndConditionsResponse response = getViewModel().getTermsAndConditionsResponse();
                if (response != null) {
                    showModal(getActivity(),
                            new DisclaimerFragmentHelper.Builder()
                                    .keyTitle(getString(R.string.terms_and_conditions_title))
                                    .keyBody(response.getTermsAndConditions())
                                    .build());
                    getViewModel().setTermsAndConditions(null);
                    FragmentUtils.removeProgressFragment(getActivity());
                }
            }
        });

        addReaction("SETTINGS_ERROR_RESPONSE", new ReactorComputationFunction() {
            @Override
            public void react(ReactorComputation reactorComputation) {
                ResponseWrapper wrapper = getViewModel().getErrorResponse();
                if (wrapper != null) {
                    DialogUtils.showErrorDialog(getActivity(), wrapper);
                    getViewModel().setResponse(null);
                    FragmentUtils.removeProgressFragment(getActivity());
                }
            }
        });
    }

    private void trackClick(EHIAnalytics.Action action) {
        trackClick(action.value);
    }

    private void trackClick(String action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SETTINGS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_SUMMARY.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }

    private void trackClickNotification(EHIAnalytics.Action action) {
        EHIAnalyticsEvent.create()
                .screen(EHIAnalytics.Screen.SCREEN_SETTINGS.value, SCREEN_NAME)
                .state(EHIAnalytics.State.STATE_NOTIFICATION.value)
                .action(EHIAnalytics.Motion.MOTION_TAP.value, action.value)
                .addDictionary(EHIAnalyticsDictionaryUtils.reservation())
                .tagScreen()
                .tagEvent();
    }
}

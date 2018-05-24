package com.ehi.enterprise.android.ui.settings;

import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.View;

import com.appsee.Appsee;
import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.miscellaneous.PrivacyPolicyRequest;
import com.ehi.enterprise.android.network.requests.miscellaneous.TermsOfUseRequest;
import com.ehi.enterprise.android.network.requests.terms_conditions.GetEPlusTermsAndConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.miscellaneous.PrivacyPolicyResponse;
import com.ehi.enterprise.android.network.responses.miscellaneous.TermsOfUseResponse;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.ToastUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.localytics.android.Localytics;

import java.util.Locale;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class SettingsViewModel extends CountrySpecificViewModel {

    final ReactorVar<PrivacyPolicyResponse> mPrivacyPolicy = new ReactorVar<>();
    final ReactorVar<TermsOfUseResponse> mTermsOfUse = new ReactorVar<>();

    final ReactorVar<ResponseWrapper> mErrorResponse = new ReactorVar();
    final ReactorVar<GetEPlusTermsAndConditionsResponse> mTermsAndConditions = new ReactorVar<>();

    final ReactorViewState notificationsHeader = new ReactorViewState();
    final ReactorViewState pickupNotifications = new ReactorViewState();
    final ReactorViewState returnNotifications = new ReactorViewState();
    final ReactorViewState notificationDivider = new ReactorViewState();
    final ReactorViewState fingerprint = new ReactorViewState();
    final ReactorViewState securityHeader = new ReactorViewState();
    final ReactorCompoundButtonState enterpriseRentalAssistant = new ReactorCompoundButtonState();

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        if (isUserLoggedIn()) {
            notificationsHeader.setVisibility(View.VISIBLE);
            pickupNotifications.setVisibility(View.VISIBLE);
            returnNotifications.setVisibility(View.VISIBLE);
            notificationDivider.setVisibility(View.VISIBLE);
            enterpriseRentalAssistant.setVisibility(View.VISIBLE);
            setEnterpriseRentalAssistantChecked(getManagers().getSettingsManager().isEnterpriseRentalAssistantEnabled());
            if (!getManagers().getLoginManager().savingData()) {
                fingerprint.setVisibility(View.GONE);
                securityHeader.setVisibility(View.GONE);
            }
        } else {
            notificationsHeader.setVisibility(View.GONE);
            pickupNotifications.setVisibility(View.GONE);
            returnNotifications.setVisibility(View.GONE);
            notificationDivider.setVisibility(View.GONE);
            enterpriseRentalAssistant.setVisibility(View.GONE);
            fingerprint.setVisibility(View.GONE);
            securityHeader.setVisibility(View.GONE);
        }
    }

    public boolean isTrackingEnabled() {
        return getManagers().getSettingsManager().isAutoSaveEnabled(needCacheDriverInfoByDefault());
    }

    public boolean isAnalyticsTrackingEnabled() {
        return getManagers().getSettingsManager().isAnalyticsEnabled();
    }

    public PrivacyPolicyResponse getPrivacyPolicy() {
        return mPrivacyPolicy.getValue();
    }

    public TermsOfUseResponse getTermsOfUse() {
        return mTermsOfUse.getValue();
    }

    public void requestPrivacyPolicy() {
        performRequest(new PrivacyPolicyRequest(), new IApiCallback<PrivacyPolicyResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<PrivacyPolicyResponse> response) {
                if (response.isSuccess()) {
                    mPrivacyPolicy.setValue(response.getData());
                } else {
                    mErrorResponse.setValue(response);
                }
            }
        });
    }

    public void requestTermsOfUse() {
        performRequest(new TermsOfUseRequest(getManagers().getLocalDataManager().getPreferredCountryCode()), new IApiCallback<TermsOfUseResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<TermsOfUseResponse> response) {
                if (response.isSuccess()) {
                    mTermsOfUse.setValue(response.getData());
                } else {
                    mErrorResponse.setValue(response);
                }
            }
        });
    }

    public void requestTermsAndConditions() {
        performRequest(new GetEPlusTermsAndConditionsRequest(), new IApiCallback<GetEPlusTermsAndConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetEPlusTermsAndConditionsResponse> response) {
                if (response.isSuccess()) {
                    mTermsAndConditions.setValue(response.getData());
                } else {
                    mErrorResponse.setValue(response);
                }
            }
        });
    }


    public boolean isSearchHistoryEnabled() {
        return getManagers().getSettingsManager().isSearchHistoryEnabled();
    }

    public void setAutoSaveEnabled(boolean enabled) {
        if (!enabled) {
            getManagers().getReservationManager().deleteDriverInfo();
        }
        getManagers().getSettingsManager().setAutoSaveEnabled(enabled);
    }

    public void setSearchHistoryEnabled(boolean enabled) {
        getManagers().getLocationManager().clearTrackingData();
        getManagers().getSettingsManager().setSearchHistoryEnabled(enabled);
    }

    public void setAnalyticsEnabled(boolean enabled) {
        getManagers().getSettingsManager().setAnalyticsEnabled(enabled);
        Localytics.setOptedOut(!enabled);
        if (enabled) {
            Localytics.setPrivacyOptedOut(false);
            Appsee.setOptOutStatus(false);
        }
    }

    public void clearPersonalData() {
        getManagers().getLocationManager().clearTrackingData();
    }

    public ResponseWrapper getErrorResponse() {
        return mErrorResponse.getValue();
    }

    public void setPolicy(PrivacyPolicyResponse policy) {
        mPrivacyPolicy.setValue(policy);
    }

    public void setTermsOfUse(TermsOfUseResponse terms) {
        mTermsOfUse.setValue(terms);
    }

    public void setResponse(ResponseWrapper responseWrapper) {
        mErrorResponse.setValue(responseWrapper);
    }

    public GetEPlusTermsAndConditionsResponse getTermsAndConditionsResponse() {
        return mTermsAndConditions.getValue();
    }

    public void setTermsAndConditions(GetEPlusTermsAndConditionsResponse o) {
        mTermsAndConditions.setValue(null);
    }

    public String getPreferredRegionName() {
        final String preferredRegion = getManagers().getLocalDataManager().getPreferredCountryCode();
        if (TextUtils.isEmpty(preferredRegion)) {
            return "-";
        }

        return new Locale(
                Locale.getDefault().getLanguage(),
                preferredRegion
        ).getDisplayCountry();
    }

    public boolean isEnterpriseRentalAssistantChecked() {
        return enterpriseRentalAssistant.checked().getValue();
    }

    public void setEnterpriseRentalAssistantChecked(boolean checked) {
        enterpriseRentalAssistant.setChecked(checked);
        getManagers().getSettingsManager().setEnterpriseRentalAssistant(checked);
        if (!checked) {
            getManagers().getGeofenceManger().removeAllGeofences();
        }
    }

    public void fingerprintProfileUnlockClicked(boolean enabled) {
        getManagers().getLoginManager().setUseFingerprintForProfile(enabled);
    }

    public boolean isFingerprintSettingProfileUnlockEnabled() {
        return getManagers().getLoginManager().shouldUseFingerprintForProfile();
    }

    public void setShouldAutomaticallySelectCard(boolean value) {
        getManagers().getLocalDataManager().setShouldAutomaticallySelectCard(value);
    }

    public boolean shouldAutomaticallySelectCard() {
        return getManagers().getLocalDataManager().shouldAutomaticallySelectCard();
    }

    public void rightToBeForgotten() {
        HandlerThread thread = new HandlerThread("opt-out-check", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        new android.os.Handler(thread.getLooper()).post(getOptOutRequestsRunnable());
    }

    private Runnable getOptOutRequestsRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                Localytics.setPrivacyOptedOut(true);
                Appsee.deleteCurrentUserData();
                ToastUtils.showToast(getContext(), R.string.right_to_be_forgotten_success_toast);
            }
        };
    }

}

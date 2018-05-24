package com.ehi.enterprise.android.ui.login;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.authentication.PostLoginRequest;
import com.ehi.enterprise.android.network.requests.location.GetCountriesRequest;
import com.ehi.enterprise.android.network.requests.terms_conditions.GetEPlusTermsAndConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.location.GetCountriesResponse;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerViewModel;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;

import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class LoginViewModel extends CountrySpecificViewModel{

    private static final String TAG = LoginViewModel.class.getSimpleName();

    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorVar<String> username = new ReactorVar<String>("") {
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };
    final ReactorVar<String> password = new ReactorVar<String>("") {
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };
    final ReactorVar<Boolean> loggedIn = new ReactorVar<>(false);
    final ReactorVar<Boolean> isTermsAndConditionsVersionMismatched = new ReactorVar<>(false);
    final ReactorVar<String> termsAndConditionsString = new ReactorVar<>();
    final ReactorVar<String> loginMessage = new ReactorVar<>();
    final ReactorVar<Boolean> loginMessageVisibility = new ReactorVar<>(false);
    final ReactorVar<Boolean> isValidForm = new ReactorVar<>(false);
    final ReactorVar<Void> isChangePasswordRequired = new ReactorVar<>(null);
    final ReactorVar<Boolean> isGetCountriesDone = new ReactorVar<>(false);
    final ReactorVar<Boolean> isEcLoggedIn = new ReactorVar<>(false);

    private boolean mRememberLogin = false;
    private String mTermsConditionsVersion;
    private boolean hideEnrollLayout = false;


    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.login_title);
        updateEmeraldClubState();
    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        showProgress(false);
    }

    public void updateEmeraldClubState() {
        isEcLoggedIn.setValue(getManagers().getReservationManager().isLoggedIntoEmeraldClub());
    }

    public void attemptLogin(){
        attemptLogin(NavigationDrawerViewModel.RESET_MENU);
    }

    public void attemptLogin(final int drawerItemId) {
        showProgress(true);
        PostLoginRequest postLoginRequest = new PostLoginRequest(
                username.getValue(),
                password.getValue(),
                true,
                getManagers().getLoginManager().getTermsConditionsVersion()
        );

        performRequest(postLoginRequest, new IApiCallback<EHIProfileResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                if (!isAttached()) { //no longer bound, user backed out, detach from view
                    return;
                }
                if (response.isSuccess()) {

                    ProfileCollection profileCollection = response.getData();
                    getManagers().getLoginManager().setLastLoginTime(System.currentTimeMillis());
                    getManagers().getLoginManager().setSavingData(mRememberLogin);
                    getManagers().getReservationManager().addOrUpdateDriverInfo(getDriverInfo(profileCollection));
                    getManagers().getReservationManager().removeEmeraldClubAccount();
                    loggedIn.setValue(true);
                    isTermsAndConditionsVersionMismatched.setValue(false);

                    getManagers().getLoginManager().login(
                            response.getData().getEncryptedAuthData(),
                            response.getData().getAuthToken(),
                            username.getValue(),
                            response.getData(),
                            mRememberLogin,
                            drawerItemId
                    );

                    showProgress(false);
                }
                else {
                    if (response.getErrorCode() != null) {
                        switch (response.getErrorCode()) {
                            case CROS_LOGIN_WEAK_PASSWORD_ERROR:
                                isChangePasswordRequired.setValue(null);
                                break;
                            case CROS_LOGIN_TERMS_AND_CONDITIONS_ACCEPT_VERSION_MISMATCH:
                                isTermsAndConditionsVersionMismatched.setValue(true);
                                break;
                            default:
                                showProgress(false);
                                setError(response);
                        }
                    }
                    else {
                        showProgress(false);
                        setError(response);
                    }
                }
            }
        });
    }

    public void getTermsConditions() {
        showProgress(true);
        GetEPlusTermsAndConditionsRequest getTermsConditionsRequest = new GetEPlusTermsAndConditionsRequest();
        performRequest(getTermsConditionsRequest, new IApiCallback<GetEPlusTermsAndConditionsResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetEPlusTermsAndConditionsResponse> response) {
                if (response.isSuccess()) {
                    termsAndConditionsString.setValue(response.getData().getTermsAndConditions());
                    mTermsConditionsVersion = response.getData().getTermsAndConditionsVersion();
                    showProgress(false);
                }
                else {
                    showProgress(false);
                }
            }
        });
    }

    private EHIDriverInfo getDriverInfo(ProfileCollection profileCollection) {
        EHIDriverInfo driverInfo = new EHIDriverInfo();
        if (profileCollection != null) {
            if (profileCollection.getContactProfile() != null) {
                final EHIContactProfile contactProfile = profileCollection.getContactProfile();
                driverInfo.setEmailAddress(contactProfile.getEmail());
                if (!ListUtils.isEmpty(contactProfile.getPhones())) {
                    EHIPhone ehiPhone = contactProfile.getPhones().get(0);
                    driverInfo.setPhoneNumber(ehiPhone.getPhoneNumber());
                }
            }

            if (profileCollection.getBasicProfile() != null) {
                driverInfo.setFirstName(profileCollection.getBasicProfile().getFirstName());
                driverInfo.setLastName(profileCollection.getBasicProfile().getLastName());
            }
            if (profileCollection.getPreference() != null && profileCollection.getPreference().getEmailPreference() != null) {
                driverInfo.setRequestEmailPromotions(true, profileCollection.getPreference().getEmailPreference().isSpecialOffers());
            }
        }

        return driverInfo;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public void setUsername(String username) {
        this.username.setValue(username);
    }

    public void checkValidForm() {
        isValidForm.setValue(!TextUtils.isEmpty(this.username.getRawValue()) && !TextUtils.isEmpty(this.password.getRawValue()));
    }

    public void setRememberLogin(boolean remember) {
        mRememberLogin = remember;
    }

    public boolean isUserLoggedIn() {
        return loggedIn.getValue();
    }

    public boolean isTermsConditionsMismatch() {
        return isTermsAndConditionsVersionMismatched.getValue();
    }

    public String getTermsAndConditionsString() {
        return termsAndConditionsString.getValue();
    }

    public void setTermsConditionsVersionNumber(String versionNumber) {
        getManagers().getLoginManager().setTermsConditionsVersion(versionNumber);
    }

    public String getTermsConditionsVersion() {
        return mTermsConditionsVersion;
    }

    public List<Pair<String, String>> getDevLogins() {
        return TestLoginInjector.getTestLogins();
    }

    @Nullable
    public String getActivationLink() {
        if (getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale() != null) {
            return getManagers().getSupportInfoManager().getSupportInfoForCurrentLocale().getActivateUrl();
        }
        return null;
    }

    public void setLoginMessage(String message) {
        loginMessage.setValue(message);
        loginMessageVisibility.setValue(!TextUtils.isEmpty(message));
    }

    public void isChangePasswordRequired() {
        isChangePasswordRequired.getValue();
    }

    public boolean getRememberLogin() {
        return mRememberLogin;
    }

    public boolean isGetCountriesDone() {
        return isGetCountriesDone.getValue();
    }

    public void getCountries() {
        showProgress(true);
        performRequest(new GetCountriesRequest(), new IApiCallback<GetCountriesResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<GetCountriesResponse> response) {
                if (response.isSuccess()) {
                    getManagers().getLocalDataManager().setCountriesList(response.getData().getCountries());
                }

                showProgress(false);

                isGetCountriesDone.setValue(true);
            }
        });
    }

    public void updatePreferredRegionFromProfile() {
        //TODO investigate should we do this?
        if (!getManagers().getLocalDataManager().havePreferredRegion()) {
            final ProfileCollection profile = getUserProfileCollection();
            setPreferredRegion(profile.getAddressProfile().getCountryCode());
        }
        else {
            mIsWeekendSpecialContractRequestDone.setValue(true);
        }
    }

    public boolean isEcLoggedIn() {
        return isEcLoggedIn.getValue();
    }

    public void logoutEC() {
        getManagers().getReservationManager().removeEmeraldClubAccount();
        isEcLoggedIn.setValue(false);
    }

    public boolean isUserNameSet() {
        return !EHITextUtils.isEmpty(username.getRawValue());
    }

    public void setHideEnrollButton(Boolean hideEnrollButton) {
        this.hideEnrollLayout = hideEnrollButton;
    }

    public boolean shouldHideEnrollButton() {
        return hideEnrollLayout;
    }
}
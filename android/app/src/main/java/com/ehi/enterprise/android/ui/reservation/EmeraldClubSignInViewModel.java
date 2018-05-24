package com.ehi.enterprise.android.ui.reservation;

import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.PostEmeraldClubLoginRequest;
import com.ehi.enterprise.android.network.requests.terms_conditions.GetEPlusTermsAndConditionsRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.terms_conditions.GetEPlusTermsAndConditionsResponse;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EmeraldClubSignInViewModel extends ManagersAccessViewModel {
    //region ReactorVars
    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorVar<String> memberId = new ReactorVar<String>(""){
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };
    final ReactorVar<String> password = new ReactorVar<String>(""){
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };
    final ReactorCompoundButtonState rememberInfo = new ReactorCompoundButtonState();
    final ReactorVar<ResponseWrapper> mErrorResponse = new ReactorVar<>();
	final ReactorVar<Boolean> mLoginComplete = new ReactorVar<>(false);
    final ReactorVar<Boolean> isValidForm = new ReactorVar<>(false);
    final ReactorVar<Void> mResetPassword = new ReactorVar<>(null);
    final ReactorTextViewState passwordText = new ReactorTextViewState();
    final ReactorTextViewState usernameText = new ReactorTextViewState();
    final ReactorVar<Boolean> isTermsAndConditionsVersionMismatched = new ReactorVar<>(false);
    final ReactorVar<String> termsAndConditionsString = new ReactorVar<>();

    private String mTermsConditionsVersion;

    //endregion

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.emerald_club_login_title);
    }

    public void loginToEmeraldClub() {
        final String memberId = usernameText.text().getRawValue();
        final String lastName = passwordText.text().getRawValue();

        showProgress(true);
        performRequest(new PostEmeraldClubLoginRequest(memberId,
                        lastName,
                        rememberInfo.checked().getRawValue(),
                        getManagers().getLoginManager().getTermsConditionsVersion()),
                new IApiCallback<EHIProfileResponse>() {
                    @Override
                    public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                        if (response.isSuccess()) {
                            getManagers().getReservationManager().setEmeraldClubProfile(response.getData());
                            getManagers().getReservationManager().setEmeraldClubAuthToken(response.getData().getAuthToken());
                            getManagers().getReservationManager().saveEmeraldClubAuthData(response.getData().getEncryptedAuthData());

                            mLoginComplete.setValue(true);
                            showProgress(false);
                        }
                        else {
                            showProgress(false);
                            if (response.getErrorCode() != null) {
                                switch (response.getErrorCode()) {
                                    case CROS_LOGIN_WEAK_PASSWORD_ERROR:
                                        mResetPassword.setValue(null);
                                        break;
                                    case CROS_LOGIN_TERMS_AND_CONDITIONS_ACCEPT_VERSION_MISMATCH:
                                        isTermsAndConditionsVersionMismatched.setValue(true);
                                        break;
                                    default:
                                        setError(response);
                                        break;
                                }
                            } else {
                                setError(response);
                            }

                        }
                    }
                });
    }

    public Void passwordResetRequired(){
        return mResetPassword.getValue();
    }

    public void checkValidForm() {
        isValidForm.setValue(!TextUtils.isEmpty(memberId.getRawValue()) && !TextUtils.isEmpty(password.getRawValue()));
    }

	public boolean isLoginComplete() {
		return mLoginComplete.getValue();
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
                } else {
                    showProgress(false);
                }
            }
        });
    }

    public String getTermsAndConditionsString() {
        return termsAndConditionsString.getValue();
    }

    public String getTermsConditionsVersion() {
        return mTermsConditionsVersion;
    }

    public void setTermsConditionsVersion(String termsConditionsVersion) {
        getManagers().getLoginManager().setTermsConditionsVersion(termsConditionsVersion);
    }
}

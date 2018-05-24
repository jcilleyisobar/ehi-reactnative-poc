package com.ehi.enterprise.android.ui.login;

import android.text.TextUtils;

import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.authentication.PostLoginRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class VerifyLoginViewModel extends ManagersAccessViewModel {

	final ReactorTextViewState passwordEditText = new ReactorTextViewState();
	final ReactorVar<Boolean> isValidPassword = new ReactorVar<>(false);
	final ReactorVar<ResponseWrapper> mErrorResponse = new ReactorVar<>();
	final ReactorVar<Boolean> mSuccessLogin = new ReactorVar<>(false);

	@Override
	public void onAttachToView() {
		super.onAttachToView();

		passwordEditText.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
			@Override
			public void onPropertyChanged(String newValue) {
				checkIsValidPassword(newValue);
			}
		});
	}

	public void checkIsValidPassword(String password) {
		isValidPassword.setValue(!TextUtils.isEmpty(password));
	}

	public void attemptLogin(boolean fingerprint) {
        showProgress(true);
        final IApiCallback<EHIProfileResponse> loginResponseCallback = new IApiCallback<EHIProfileResponse>() {
            @Override
            public void handleResponse(final ResponseWrapper<EHIProfileResponse> response) {
                if (response.isSuccess()) {
                    getManagers().getLoginManager().setLastLoginTime(System.currentTimeMillis());
                    getManagers().getLoginManager().setProfile(response.getData());
                    getManagers().getLoginManager().setUserAuthToken(response.getData().getAuthToken());
                    getManagers().getLoginManager().setEncryptedCredentials(response.getData().getEncryptedAuthData());
                    mSuccessLogin.setValue(true);
                    showProgress(false);
                }
                else {
                    showProgress(false);
                    mErrorResponse.setValue(response);
                }
            }
        };
        if(fingerprint){
            performRequest(new PostLoginRequest(getManagers().getLoginManager().getEncryptedCredentials()), loginResponseCallback);
        }
        else {
            performRequest(new PostLoginRequest(getManagers().getLoginManager().getUserName(),
                                                passwordEditText.text().getRawValue(),
                                                true,
                                                getManagers().getLoginManager().getTermsConditionsVersion()),
                                                loginResponseCallback);
        }
	}


    public boolean isUserLoggedIn() {
		return mSuccessLogin.getValue();
	}

	public ResponseWrapper getError() {
		return mErrorResponse.getValue();
	}

	public void setErrorResponse(ResponseWrapper errorResponse) {
		mErrorResponse.setValue(errorResponse);
	}

    public boolean shouldUseFingerprint(){
        return getManagers().getLoginManager().shouldUseFingerprintForProfile();
    }

}
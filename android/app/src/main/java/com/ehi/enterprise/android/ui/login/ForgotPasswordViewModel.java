package com.ehi.enterprise.android.ui.login;

import android.text.TextUtils;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.authentication.PutForgotPasswordRequest;
import com.ehi.enterprise.android.network.responses.BaseResponse;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHIPatterns;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class ForgotPasswordViewModel extends ManagersAccessViewModel {

    final ReactorVar<Integer> title = new ReactorVar<>();
    final ReactorVar<Boolean> isValidForm = new ReactorVar<>(false);
    final ReactorVar<Boolean> success = new ReactorVar<>(false);

    final ReactorVar<CharSequence> firstNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> lastNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> emailError = new ReactorVar<>();

    final ReactorVar<String> firstNameInput = new ReactorVar<String>("") {
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };

    final ReactorVar<String> lastNameInput = new ReactorVar<String>("") {
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };

    final ReactorVar<String> emailInput = new ReactorVar<String>("") {
        @Override
        public void setValue(final String value) {
            super.setValue(value);
            checkValidForm();
        }
    };

    @Override
    public void onAttachToView() {
        super.onAttachToView();
        title.setValue(R.string.forgot_password_navigation_title);
    }

    @Override
    public void onDetachFromView() {
        super.onDetachFromView();
        showProgress(false);
    }

    public boolean checkValidForm() {
        firstNameError.setValue(!TextUtils.isEmpty(firstNameInput.getRawValue()) ? null : " ");
        lastNameError.setValue(!TextUtils.isEmpty(lastNameInput.getRawValue()) ? null : " ");
        emailError.setValue(EHIPatterns.EMAIL_ADDRESS.matcher(emailInput.getRawValue()).matches() ? null : " ");

        isValidForm.setValue(firstNameError.getValue() == null
                && lastNameError.getValue() == null
                && emailError.getValue() == null);

        return isValidForm.getValue();
    }

    public void sendRequest() {
        if (!checkValidForm()) {
            return;
        }

        showProgress(true);
        PutForgotPasswordRequest putForgotPasswordRequest = new PutForgotPasswordRequest(
                firstNameInput.getValue(),
                lastNameInput.getValue(),
                emailInput.getValue()
        );

        performRequest(putForgotPasswordRequest, new IApiCallback<BaseResponse>() {
            @Override
            public void handleResponse(ResponseWrapper<BaseResponse> response) {
                showProgress(false);
                if (response.isSuccess()) {
                    success.setValue(true);
                } else {
                    setError(response);
                }
            }
        });
    }
}

package com.ehi.enterprise.android.ui.login;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIProfileResponse;
import com.ehi.enterprise.android.models.profile.EHIProfile;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.authentication.PostLoginRequest;
import com.ehi.enterprise.android.network.requests.authentication.PostUpdatePasswordRequest;
import com.ehi.enterprise.android.network.requests.reservation.PostEmeraldClubLoginRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.network.responses.authentication.PostUpdatePasswordResponse;
import com.ehi.enterprise.android.ui.navigation.NavigationDrawerViewModel;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHIPasswordValidator;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState.CheckRowIconState.fromBoolean;
import static com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState.CheckRowIconState.nil;

@AutoUnbindAll
public class ChangePasswordViewModel extends ManagersAccessViewModel {

    final ReactorVar<Integer> title = new ReactorVar<>();

    final ReactorViewState newPasswordContainer = new ReactorViewState();
    final ReactorTextViewState newPassword = new ReactorTextViewState();
    final ReactorVar<Boolean> isNewPasswordValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasPasswordValid = value || mWasPasswordValid;
            super.setValue(value);
        }
    };

    final ReactorViewState confirmPasswordContainer = new ReactorViewState();
    final ReactorTextViewState confirmPassword = new ReactorTextViewState();
    final ReactorViewState changePasswordBanner = new ReactorViewState();
    private boolean mWasConfirmPasswordValid = false;
    final ReactorVar<Boolean> isConfirmPasswordValid = new ReactorVar<Boolean>(false) {
        @Override
        public void setValue(Boolean value) {
            mWasConfirmPasswordValid = value || mWasConfirmPasswordValid;
            super.setValue(value);
        }
    };

    final ReactorTextViewState confirmationPasswordInvalidCondition = new ReactorTextViewState();
    final ReactorVar<Boolean> isUpdatePasswordButtonEnabled = new ReactorVar<>(false);
    final ReactorVar<Void> mSuccessReaction = new ReactorVar<>();
    final ReactorVar<ResponseWrapper> mResponse = new ReactorVar<>();
    final ReactorConditionRowViewState minCharacterCountCondition = new ReactorConditionRowViewState();
    final ReactorConditionRowViewState containsLetterCondition = new ReactorConditionRowViewState();
    final ReactorConditionRowViewState containsNumberCondition = new ReactorConditionRowViewState();
    // does not contain 'Password'
    final ReactorConditionRowViewState constantCheckPassedCondition = new ReactorConditionRowViewState();
    private boolean mWasPasswordValid = false;
    private String mUserName;
    private Boolean mIsECFlow = null;
    private boolean mRememberCredentials;
    private String mOldPassword;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        minCharacterCountCondition.setText(R.string.cp_must_be_at_least_8_characters);
        containsLetterCondition.setText(R.string.cp_must_contain_letter);
        containsNumberCondition.setText(R.string.cp_must_contain_number);
        constantCheckPassedCondition.setText(R.string.cp_cannot_contain_condition);
        confirmationPasswordInvalidCondition.setText(R.string.cp_passwords_do_not_match);

        if (!isPasswordChangeRequired()) {
            changePasswordBanner.visibility().setValue(View.GONE);
        }
        confirmationPasswordInvalidCondition.visibility().setValue(View.GONE);
        title.setValue(R.string.profile_password_navigation_title);
        mWasPasswordValid = false;
        mWasConfirmPasswordValid = false;
        newPassword.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(String newValue) {
                setIsNewPasswordValid();
                if (!EHITextUtils.isEmpty(confirmPassword.text().getRawValue())) {
                    setIsConfirmPasswordValid();
                }
                setIsUpdatePasswordButtonEnabled();
                updateEditTextBackgrounds();
            }
        });

        confirmPassword.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(String newValue) {
                setIsConfirmPasswordValid();
                setIsUpdatePasswordButtonEnabled();
                updateEditTextBackgrounds();
            }
        });
    }

    public void updateEditTextBackgrounds() {
        if (isNewPasswordValid.getValue().equals(true)) {
            newPasswordContainer.setBackground(getResources().getDrawable(R.drawable.edit_text_white_border));
        } else if (mWasPasswordValid) {
            newPasswordContainer.setBackground(getResources().getDrawable(R.drawable.edit_text_red_border));
        }

        if (!EHITextUtils.isEmpty(confirmPassword.text().getRawValue())) {
            if (isConfirmPasswordValid.getValue().equals(true)) {
                confirmPasswordContainer.setBackground(getResources().getDrawable(R.drawable.edit_text_white_border));
                confirmationPasswordInvalidCondition.visibility().setValue(View.GONE);
            } else if (mWasConfirmPasswordValid) {
                confirmPasswordContainer.setBackground(getResources().getDrawable(R.drawable.edit_text_red_border));
                confirmationPasswordInvalidCondition.visibility().setValue(View.VISIBLE);
            }
        }
    }

    public void setRememberCredentials(boolean rememberCredentials) {
        mRememberCredentials = rememberCredentials;
    }

    public void attemptPasswordChange() {
        showProgress(true);
        if (isPasswordChangeRequired()) {
            if (isECResetFlow()) {
                performRequest(new PostEmeraldClubLoginRequest(mOldPassword, mUserName, mRememberCredentials, newPassword.text().getValue()),
                        new IApiCallback<EHIProfileResponse>() {
                            @Override
                            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                                showProgress(false);
                                if (response.isSuccess()) {
                                    getManagers().getReservationManager().setEmeraldClubProfile(response.getData());
                                    getManagers().getReservationManager().setEmeraldClubAuthToken(response.getData().getAuthToken());

                                    if (mRememberCredentials) {
                                        getManagers().getReservationManager()
                                                .saveEmeraldClubAuthData(response.getData().getEncryptedAuthData());
                                    }
                                    mSuccessReaction.getDependency().changed();
                                } else {
                                    setError(response);
                                }
                            }
                        });
            } else if (isEPResetFlow()) {
                performRequest(new PostLoginRequest(mUserName,
                                mOldPassword,
                                mRememberCredentials,
                                getManagers().getLoginManager().getTermsConditionsVersion(),
                                newPassword.text().getValue()),
                        new IApiCallback<EHIProfileResponse>() {
                            @Override
                            public void handleResponse(ResponseWrapper<EHIProfileResponse> response) {
                                showProgress(false);
                                if (response.isSuccess()) {
                                    getManagers().getLoginManager().login(
                                            response.getData().getEncryptedAuthData(),
                                            response.getData().getAuthToken(),
                                            mUserName,
                                            response.getData(),
                                            mRememberCredentials);
                                    mSuccessReaction.getDependency().changed();
                                } else {
                                    setError(response);
                                }
                            }
                        });
            }
        } else {
            final EHIProfile profile = getManagers().getLoginManager().getProfileCollection().getProfile();
             performRequest(new PostUpdatePasswordRequest(
                            newPassword.text().getRawValue(),
                            confirmPassword.text().getRawValue(),
                            true, profile.getIndividualId()),
                    new IApiCallback<PostUpdatePasswordResponse>() {
                        @Override
                        public void handleResponse(ResponseWrapper<PostUpdatePasswordResponse> response) {
                            showProgress(false);
                            if (response.isSuccess()) {
                                getManagers().getLoginManager().setEncryptedCredentials(response.getData().getEncryptedCredentials(), true);
                                getManagers().getLoginManager().setUserAuthToken(response.getData().getAuthToken(), true);
                                mSuccessReaction.getDependency().changed();
                            } else {
                                setError(response);
                            }
                        }
                    });
        }
    }

    public void setResetPasswordFlow(boolean isECFlow) {
        mIsECFlow = isECFlow;
    }

    private boolean isECResetFlow() {
        return mIsECFlow != null && mIsECFlow;
    }

    private boolean isEPResetFlow() {
        return mIsECFlow != null && !mIsECFlow;
    }

    public ResponseWrapper getPasswordResponse() {
        return mResponse.getValue();
    }

    public boolean isUsernameInPassword(String newPassword) {
        return getManagers().getLoginManager().getUserName().contains(newPassword);
    }

    public void setIsNewPasswordValid() {
        if (newPassword.text().getRawValue() != null) {
            isNewPasswordValid.setValue(checkIsNewPasswordValid(newPassword.text().getRawValue()));
        }
    }

    public boolean checkIsNewPasswordValid(String password) {
        final EHIPasswordValidator ehiPasswordValidator = new EHIPasswordValidator(password, 8);

        setIconState(minCharacterCountCondition, ehiPasswordValidator.hasMinimumLength());

        setIconState(containsLetterCondition, ehiPasswordValidator.hasLetter());

        setIconState(containsNumberCondition, ehiPasswordValidator.hasNumber());

        constantCheckPassedCondition.setIconState(
                ehiPasswordValidator.hasMinimumLength() ? fromBoolean(!ehiPasswordValidator.hasPassword()) : nil
        );

        return ehiPasswordValidator.isValid();
    }

    public void setIsConfirmPasswordValid() {
        if (!EHITextUtils.isEmpty(confirmPassword.text().getRawValue())
                && !EHITextUtils.isEmpty(newPassword.text().getRawValue())) {
            mWasConfirmPasswordValid = mWasConfirmPasswordValid || confirmPassword.text().getRawValue().length() >= newPassword.text().getRawValue().length();
            isConfirmPasswordValid.setValue(checkIsConfirmPasswordValid(confirmPassword.text().getRawValue(), newPassword.text().getRawValue()));
        }
    }

    public boolean checkIsConfirmPasswordValid(String confirmPassword, String newPassword) {
        return confirmPassword.equals(newPassword);
    }

    public void setIsUpdatePasswordButtonEnabled() {
        isUpdatePasswordButtonEnabled.setValue(isNewPasswordValid.getValue() && isConfirmPasswordValid.getValue());
    }

    public void successResponse() {
        mSuccessReaction.getValue();
    }

    public void setUserName(String s) {
        mUserName = s;
        changePasswordBanner.visibility().setValue(View.VISIBLE);
    }

    public boolean isPasswordChangeRequired() {
        return mUserName != null;
    }

    public void setOldPassword(String oldPassword) {
        mOldPassword = oldPassword;
    }

    private void setIconState(ReactorConditionRowViewState reactorConditionRowViewState, boolean state) {
        final ReactorConditionRowViewState.CheckRowIconState formerConditionState = reactorConditionRowViewState.getIconStateVar().getRawValue();

        reactorConditionRowViewState.setIconState(
                formerConditionState == nil && !state ? nil : fromBoolean(state)
        );
    }
}
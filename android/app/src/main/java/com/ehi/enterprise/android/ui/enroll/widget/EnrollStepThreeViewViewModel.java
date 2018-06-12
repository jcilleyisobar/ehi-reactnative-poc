package com.ehi.enterprise.android.ui.enroll.widget;

import android.support.v4.util.Pair;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.app.Settings;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.enroll.EHITermsAndConditions;
import com.ehi.enterprise.android.models.profile.EHIEmailPreference;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.EHIPreference;
import com.ehi.enterprise.android.ui.enroll.FormContract;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.EHIPasswordValidator;
import com.ehi.enterprise.android.utils.EHIPatterns;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.ReactorPhoneFormatter;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

import static com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState.CheckRowIconState.fromBoolean;
import static com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState.CheckRowIconState.invalid;
import static com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState.CheckRowIconState.nil;
import static com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorConditionRowViewState.CheckRowIconState.valid;

@AutoUnbindAll
public class EnrollStepThreeViewViewModel extends CountrySpecificViewModel
        implements FormContract.FormView {

    private boolean shouldHighlightInvalidFieldsOnFormChange = false;

    final String emptyErrorString = " ";
    final int minPasswordLength = 8;

    final ReactorTextViewState createPassword = new ReactorTextViewState();
    final ReactorTextViewState confirmPassword = new ReactorTextViewState();

    final ReactorTextViewState phoneNumber = new ReactorTextViewState();
    final ReactorTextViewState email = new ReactorTextViewState();
    final ReactorConditionRowViewState constantCheckPassedCondition = new ReactorConditionRowViewState();
    final ReactorConditionRowViewState containsLetterCondition = new ReactorConditionRowViewState();
    final ReactorConditionRowViewState containsNumberCondition = new ReactorConditionRowViewState();
    final ReactorConditionRowViewState minCharacterCountCondition = new ReactorConditionRowViewState();
    final ReactorConditionRowViewState confirmationPasswordInvalidCondition = new ReactorConditionRowViewState();

    final ReactorCompoundButtonState signUpEmailCheckBox = new ReactorCompoundButtonState();
    final ReactorCompoundButtonState termAndConditionsCheckBox = new ReactorCompoundButtonState();

    final ReactorViewState enrollStepTitleArea = new ReactorViewState();
    final ReactorVar<CharSequence> enrollStepTitle = new ReactorVar<>();

    private ReactorPhoneFormatter mPhoneNumberFormatter;

    final ReactorVar<CharSequence> phoneNumberError = new ReactorVar<>();
    final ReactorVar<CharSequence> emailError = new ReactorVar<>();
    final ReactorVar<CharSequence> createPasswordError = new ReactorVar<>();
    final ReactorVar<CharSequence> confirmPasswordError = new ReactorVar<>();

    private FormContract.FormListener formListener;

    private boolean isCreatePasswordValid = false;
    private boolean isConfirmPasswordValid = false;
    private Pair<String, String> mFormattedPhoneNumber;
    private boolean isSignUpToPromotionsCheckedOnStart;

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        enrollStepTitle.setValue(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "3")
                .formatString(R.string.enroll_long_form_step_title)
                .format());

        minCharacterCountCondition.setText(R.string.cp_must_be_at_least_8_characters);
        containsLetterCondition.setText(R.string.cp_must_contain_letter);
        containsNumberCondition.setText(R.string.cp_must_contain_number);
        constantCheckPassedCondition.setText(R.string.cp_cannot_contain_condition);
        confirmationPasswordInvalidCondition.setText(R.string.cp_passwords_do_not_match);

        phoneNumber.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(final String newValue) {
                formatPhoneNumber();
                checkIsValid();
            }
        });

        email.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(final String newValue) {
                checkIsValid();
            }
        });

        createPassword.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(String newValue) {
                setIsNewPasswordValid();
                if (!EHITextUtils.isEmpty(confirmPassword.text().getRawValue())) {
                    setIsConfirmPasswordValid();
                }
                checkIsValid();
            }
        });

        confirmPassword.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(String newValue) {
                setIsConfirmPasswordValid();
                checkIsValid();
            }
        });

        confirmationPasswordInvalidCondition.setVisibility(ReactorViewState.GONE);
    }

    public void setPresetData(EHIEnrollProfile ehiEnrollProfile, boolean needCheckEmailNotificationsByDefault) {
        final List<EHIPhone> phoneNumbers = ehiEnrollProfile.getPhoneNumberList();
        if (phoneNumbers != null && phoneNumbers.size() > 0) {
            if (!EHITextUtils.isEmpty(phoneNumbers.get(0).getMaskPhoneNumber())) {
                phoneNumber.text().setValue(phoneNumbers.get(0).getMaskPhoneNumber());
            } else {
                phoneNumber.text().setValue(phoneNumbers.get(0).getPhoneNumber());
                formatPhoneNumber();
            }
        }
        email.text().setValue(ehiEnrollProfile.getEmail());
        if (ehiEnrollProfile.getPreference() != null) {
            signUpEmailCheckBox.checked().setValue(
                    ehiEnrollProfile.getPreference().getEmailPreference().isSpecialOffers()
                            || needCheckEmailNotificationsByDefault
            );
        } else {
            signUpEmailCheckBox.checked().setValue(needCheckEmailNotificationsByDefault);
        }
        createPassword.text().setValue(ehiEnrollProfile.getPassword());
        confirmPassword.text().setValue(ehiEnrollProfile.getPassword());
        if (ehiEnrollProfile.getTermsAndConditionsAccept() != null) {
            termAndConditionsCheckBox.checked().setValue(
                    ehiEnrollProfile.getTermsAndConditionsAccept().isAcceptDecline()
            );
        }

        if (!EHITextUtils.isEmpty(createPassword.text().getRawValue())) {
            setIsNewPasswordValid();
            if (!EHITextUtils.isEmpty(confirmPassword.text().getRawValue())) {
                setIsConfirmPasswordValid();
            }
            checkIsValid();
        }

        isSignUpToPromotionsCheckedOnStart = signUpEmailCheckBox.checked().getRawValue();
    }

    public EHIEnrollProfile updateEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        String formattedPhoneNumber = phoneNumber.text().getRawValue();
        if (!EHITextUtils.isMaskedField((phoneNumber.text().getRawValue()))) {
            formattedPhoneNumber = mFormattedPhoneNumber.second;
        }
        ehiEnrollProfile.setPhoneNumberList(Collections.singletonList(new EHIPhone(formattedPhoneNumber, EHIPhone.PhoneType.HOME.getValue())));

        ehiEnrollProfile.setEmail(email.text().getRawValue());

        ehiEnrollProfile.setPassword(createPassword.text().getRawValue());

        final EHITermsAndConditions ehiTermsAndConditions = new EHITermsAndConditions();
        ehiTermsAndConditions.setAcceptDecline(termAndConditionsCheckBox.checked().getRawValue());

        ehiEnrollProfile.setTermsAndConditionsAccept(ehiTermsAndConditions);

        final EHIEmailPreference ehiEmailPreference = new EHIEmailPreference();
        ehiEmailPreference.setSpecialOffers(isSignUpToPromotionsCheckedOnStart, signUpEmailCheckBox.checked().getRawValue());

        ehiEnrollProfile.setPreference(new EHIPreference(ehiEmailPreference, Settings.ENROLL_SOURCE_CODE));

        return ehiEnrollProfile;
    }

    @Override
    public boolean isValid() {
        return isPhoneNumberValid() && isEmailValid()
                && isCreatePasswordValid && isConfirmPasswordValid
                && termAndConditionsCheckBox.checked().getRawValue();
    }

    @Override
    public void highlightInvalidFields() {
        phoneNumberError.setValue(
                isPhoneNumberValid() ? null : emptyErrorString
        );

        emailError.setValue(
                isEmailValid() ? null : emptyErrorString
        );

        createPasswordError.setValue(
                isCreatePasswordValid ? null : emptyErrorString
        );

        confirmPasswordError.setValue(
                isConfirmPasswordValid ? null : emptyErrorString
        );
    }

    @Override
    public List<String> getErrorMessageList() {
        final List<String> errorMessageList = new ArrayList<>();

        if (!isPhoneNumberValid()) {
            errorMessageList.add(getResources().getString(R.string.enroll_phone_number_title));
        }

        if (!isEmailValid()) {
            errorMessageList.add(getResources().getString(R.string.enroll_email_title));
        }

        if (!isCreatePasswordValid) {
            errorMessageList.add(getResources().getString(R.string.enroll_create_password_title));
        }

        if (!isConfirmPasswordValid) {
            errorMessageList.add(getResources().getString(R.string.enroll_confirm_password_title));
        }

        if (!termAndConditionsCheckBox.checked().getRawValue()) {
            errorMessageList.add(getResources().getString(R.string.enroll_terms_and_conditions_string));
        }

        return errorMessageList;
    }

    @Override
    public void startHighlightInvalidFieldsOnFormChange() {
        shouldHighlightInvalidFieldsOnFormChange = true;
    }

    @Override
    public void stopHighlightInvalidFieldsOnFormChange() {
        shouldHighlightInvalidFieldsOnFormChange = false;
    }

    public void setFormListener(FormContract.FormListener listener) {
        formListener = listener;
    }

    public void setIsNewPasswordValid() {
        if (createPassword.text().getRawValue() != null) {
            isCreatePasswordValid = isCreatePasswordValid(createPassword.text().getRawValue());
        }
    }

    public boolean isCreatePasswordValid(String password) {
        final EHIPasswordValidator ehiPasswordValidator = new EHIPasswordValidator(password, minPasswordLength);

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
                && !EHITextUtils.isEmpty(createPassword.text().getRawValue())) {
            isConfirmPasswordValid = checkIsConfirmPasswordValid(
                    confirmPassword.text().getRawValue(), createPassword.text().getRawValue()
            );
        }
    }

    public boolean checkIsConfirmPasswordValid(String confirmPassword, String newPassword) {

        // during typing don't show valid and only show invalid if user types a different char
        if (confirmPassword.length() < newPassword.length()) {
            confirmationPasswordInvalidCondition.setIconState(
                    newPassword.startsWith(confirmPassword) ?
                            nil :
                            invalid
            );
        } else {
            // in case of length >= should show valid or invalid
            confirmationPasswordInvalidCondition.setIconState(
                    confirmPassword.equals(newPassword) ?
                            valid :
                            invalid
            );
        }

        if (confirmationPasswordInvalidCondition.getIconStateVar().getRawValue() == invalid) {
            confirmationPasswordInvalidCondition.setVisibility(ReactorViewState.VISIBLE);
        } else {
            confirmationPasswordInvalidCondition.setVisibility(ReactorViewState.GONE);
        }

        return confirmationPasswordInvalidCondition.getIconStateVar().getRawValue() == valid;
    }

    public void signUpEmailClicked() {
        signUpEmailCheckBox.setChecked(!signUpEmailCheckBox.checked().getRawValue());
    }

    public void termAndConditionsClicked() {
        termAndConditionsCheckBox.setChecked(!termAndConditionsCheckBox.checked().getRawValue());
        checkIsValid();
    }

    public String getPhoneNumber() {
        if (mFormattedPhoneNumber == null) {
            return null;
        }

        return mFormattedPhoneNumber.second;
    }

    public String getPassword() {
        return createPassword.text().getRawValue();
    }

    public void showHeader() {
        enrollStepTitleArea.setVisibility(ReactorViewState.VISIBLE);
    }

    public void hideHeader() {
        enrollStepTitleArea.setVisibility(ReactorViewState.GONE);
    }

    private void setIconState(ReactorConditionRowViewState reactorConditionRowViewState, boolean state) {
        final ReactorConditionRowViewState.CheckRowIconState formerConditionState = reactorConditionRowViewState.getIconStateVar().getRawValue();

        reactorConditionRowViewState.setIconState(
                formerConditionState == nil && !state ? nil : fromBoolean(state)
        );
    }

    private void formatPhoneNumber() {
        if (mPhoneNumberFormatter == null) {
            mPhoneNumberFormatter = new ReactorPhoneFormatter();
        }

        mFormattedPhoneNumber = mPhoneNumberFormatter.format(phoneNumber.text());
    }

    private boolean isPhoneNumberValid() {
        return !EHITextUtils.isEmpty(phoneNumber.text().getRawValue());
    }

    private boolean isEmailValid() {
        final String emailString = email.text().getRawValue();
        return EHITextUtils.isMaskedField(emailString) ||
                !EHITextUtils.isEmpty(emailString) && EHIPatterns.EMAIL_ADDRESS.matcher(emailString).matches();
    }

    private void checkIsValid() {
        if (formListener != null) {
            formListener.isValid(isValid());
        }

        if (shouldHighlightInvalidFieldsOnFormChange) {
            highlightInvalidFields();
        }
    }
}

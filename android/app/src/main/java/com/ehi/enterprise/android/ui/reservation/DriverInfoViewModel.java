package com.ehi.enterprise.android.ui.reservation;

import android.support.v4.util.Pair;
import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.profile.EHIBasicProfile;
import com.ehi.enterprise.android.models.profile.EHIContactProfile;
import com.ehi.enterprise.android.models.profile.EHIEmailPreference;
import com.ehi.enterprise.android.models.profile.EHIPhone;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.interfaces.IApiCallback;
import com.ehi.enterprise.android.network.requests.reservation.modify.PostRenterInfoModifyRequest;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.viewmodel.CountrySpecificViewModel;
import com.ehi.enterprise.android.utils.EHIPatterns;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.ReactorPhoneFormatter;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorCompoundButtonState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorPropertyChangedListener;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class DriverInfoViewModel extends CountrySpecificViewModel {

    private static final int MIN_TEXT_LENGTH = 1;
    private static final int MIN_PHONE_NUM_LENGTH = 3;

    //region ReactorVar
    final ReactorVar<Integer> titleRes = new ReactorVar<>();
    final ReactorTextViewState authFullName = new ReactorTextViewState();
    final ReactorTextViewState firstName = new ReactorTextViewState();
    final ReactorTextViewState lastName = new ReactorTextViewState();
    final ReactorTextViewState phoneNumber = new ReactorTextViewState();
    final ReactorTextViewState emailAddress = new ReactorTextViewState();
    final ReactorTextViewState continueButton = new ReactorTextViewState();
    final ReactorViewState saveInformationContainer = new ReactorViewState();
    final ReactorViewState signUpEmailContainer = new ReactorViewState();
    final ReactorCompoundButtonState signUpEmailCheckBox = new ReactorCompoundButtonState();
    final ReactorCompoundButtonState saveInformationCheckBox = new ReactorCompoundButtonState();
    final ReactorViewState germanDoubleOptInText = new ReactorViewState();
    final ReactorViewState driverInfoUkIdentityText = new ReactorViewState();
    final ReactorViewState firstNameLayout = new ReactorViewState();
    final ReactorViewState lastNameLayout = new ReactorViewState();
    final ReactorViewState signinLayout = new ReactorViewState();

    final ReactorVar<String> mFirstName = new ReactorVar<>();
    final ReactorVar<String> mLastName = new ReactorVar<>();
    final ReactorVar<String> mPhoneNumber = new ReactorVar<>();
    final ReactorVar<String> mEmailAddress = new ReactorVar<>();

    private final ReactorVar<EHIReservation> mSuccessModifyDriverResponse = new ReactorVar<>();

    final ReactorVar<CharSequence> firstNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> lastNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> phoneNumberError = new ReactorVar<>();
    final ReactorVar<CharSequence> emailError = new ReactorVar<>();
    //endregion

    private EHIDriverInfo mEhiDriverInfo;
    private boolean mEditing;
    private ReactorPhoneFormatter mPhoneNumberFormatter;
    private Pair<String, String> mFormattedPhoneNumber;
    private boolean mShouldAlertEmailAddress;
    private boolean mIsModify;

    final String emptyErrorString = " ";
    private boolean mIsLoginAfterStart;


    @Override
    public void onAttachToView() {
        super.onAttachToView();
        mPhoneNumberFormatter = new ReactorPhoneFormatter();
        titleRes.setValue(R.string.reservation_driver_info_navigation_title);

        final ReactorPropertyChangedListener<String> propertyChangedListener = new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(final String newValue) {
                isValidDriverInfo();
            }
        };

        final ReactorPropertyChangedListener<Boolean> emailCheckListener = new ReactorPropertyChangedListener<Boolean>() {
            @Override
            public void onPropertyChanged(Boolean newValue) {
                //TODO temporary commented per CH request
//                if (needShowDoubleOptInForEmailSpecials() && newValue) {
//                    germanDoubleOptInText.setVisibility(ReactorViewState.VISIBLE);
//                } else {
                germanDoubleOptInText.setVisibility(ReactorViewState.GONE);
//                }
            }
        };

        signUpEmailCheckBox.setCheckedChangedListener(emailCheckListener);
        firstName.setTextChangedListener(propertyChangedListener);
        lastName.setTextChangedListener(propertyChangedListener);
        emailAddress.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(final String newValue) {
                mShouldAlertEmailAddress = true;
                isValidDriverInfo();
            }
        });
        phoneNumber.setTextChangedListener(new ReactorPropertyChangedListener<String>() {
            @Override
            public void onPropertyChanged(final String newValue) {
                isValidDriverInfo();
                formatPhoneNumber();
            }
        });
        continueButton.setText(getResources().getString(R.string.reservation_driver_info_continue_button_title));

        setupViews();
    }

    private void setupViews() {

        if (mEhiDriverInfo == null) {
            mEhiDriverInfo = getManagers().getReservationManager().getDriverInfo();

            if (mEhiDriverInfo != null) {
                parseDriverInfo(true);
            } else {
                signUpEmailCheckBox.setChecked(needCheckEmailNotificationsByDefault());
                saveInformationCheckBox.setChecked(isAutoSaveEnabled());
                continueButton.setEnabled(false);
            }
        } else {
            parseDriverInfo(false);
        }

        if (mEditing) {
            saveInformationCheckBox.setChecked(shouldSaveDriverInfo());
            continueButton.setText(getResources().getString(R.string.reservation_driver_info_done_button_title));
        }

        signinLayout.setVisibility(View.GONE);
        authFullName.setVisibility(View.VISIBLE);
        firstNameLayout.setVisibility(View.GONE);
        lastNameLayout.setVisibility(View.GONE);
        saveInformationContainer.setVisibility(View.GONE);

        if (isUserLoggedIn()) {
            if (mEhiDriverInfo != null) {
                authFullName.setText(mEhiDriverInfo.getFirstName() + " " + mEhiDriverInfo.getLastName());
            }
        } else {
            if (!isModify()) {
                signinLayout.setVisibility(View.VISIBLE);
            }
            authFullName.setVisibility(View.GONE);
            firstNameLayout.setVisibility(View.VISIBLE);
            lastNameLayout.setVisibility(View.VISIBLE);
            saveInformationContainer.setVisibility(View.VISIBLE);
        }

        signUpEmailContainer.setVisibility(shouldHideEmailMarketingContainer() ? View.GONE : View.VISIBLE);

        if (getReservationObject() != null
                && getReservationObject().shouldShowIdentityCheckWithExternalVendorMessage()
                && !isUserLoggedIn()) {
            driverInfoUkIdentityText.setVisibility(ReactorViewState.VISIBLE);
        } else {
            driverInfoUkIdentityText.setVisibility(ReactorViewState.GONE);
        }
    }

    public void fillFormWithLoggedUserData() {
        final EHIBasicProfile basic = getManagers().getLoginManager().getProfileCollection().getBasicProfile();
        final EHIContactProfile contact = getManagers().getLoginManager().getProfileCollection().getContactProfile();
        mIsLoginAfterStart = true;
        mEhiDriverInfo = new EHIDriverInfo(
                contact.getEmail(),
                contact.getMaskEmail(),
                basic.getFirstName(),
                basic.getLastName(),
                contact.getPhone(0),
                needCheckEmailNotificationsByDefault()
        );
        setupViews();
    }

    private boolean shouldHideEmailMarketingContainer() {
        final ProfileCollection profileCollection = getManagers().getLoginManager().getProfileNoCache();
        if (profileCollection == null
                || profileCollection.getPreference() == null
                || profileCollection.getPreference().getEmailPreference() == null) {
            return false;
        }
        final EHIEmailPreference emailPreference = profileCollection.getPreference().getEmailPreference();
        final boolean isCorporateAccountWithHideMarketingOptIn = getReservationObject().getCorporateAccount() != null
                && getReservationObject().getCorporateAccount().hideMarketingMessageOptIn();
        final boolean isSpecialOfferCheckedAndAuthenticated = emailPreference.isSpecialOffers() && isUserLoggedIn();

        return isCorporateAccountWithHideMarketingOptIn || isSpecialOfferCheckedAndAuthenticated;
    }

    private boolean validateEmail(final boolean shouldAlertUser) {
        final String email = emailAddress.text().getRawValue();
        final boolean validEmail = EHITextUtils.isMaskedField(email)
                || !EHITextUtils.isEmpty(email) && EHIPatterns.EMAIL_ADDRESS.matcher(email).matches();

        if (shouldAlertUser) {
            emailAddress.setBackgroundResource(
                    validEmail ? R.drawable.edit_text_transparent_dark_border : R.drawable.edit_text_red_border
            );
        }

        return validEmail;
    }

    private void parseDriverInfo(boolean saved) {
        firstName.setText(mEhiDriverInfo.getFirstName());
        lastName.setText(mEhiDriverInfo.getLastName());
        authFullName.setText(mEhiDriverInfo.getFirstName() + " " + mEhiDriverInfo.getLastName());

        if (mEhiDriverInfo.getPhone() != null) {
            if (mEhiDriverInfo.getPhone().getMaskPhoneNumber() != null) {
                phoneNumber.setText(mEhiDriverInfo.getPhone().getMaskPhoneNumber());
            } else {
                phoneNumber.setText(mEhiDriverInfo.getPhone().getPhoneNumber());
            }
        }

        if (mEhiDriverInfo.getMaskEmailAddress() != null) {
            emailAddress.setText(mEhiDriverInfo.getMaskEmailAddress());
        } else {
            emailAddress.setText(mEhiDriverInfo.getEmailAddress());
        }

        signUpEmailCheckBox.setChecked(mEhiDriverInfo.hasRequestedEmailPromotions());
        saveInformationCheckBox.setChecked(saved);
    }

    private boolean validateText(ReactorTextViewState textViewState, final int minLength) {
        return textViewState.text().getRawValue() != null
                && textViewState.text().getRawValue().length() >= minLength;
    }

    private void formatPhoneNumber() {
        if (!EHITextUtils.isMaskedField(phoneNumber.text().getRawValue())) {
            mFormattedPhoneNumber = mPhoneNumberFormatter.format(phoneNumber.text());
        }
    }

    private boolean isFirstNameValid() {
        return validateText(firstName, MIN_TEXT_LENGTH);
    }

    private boolean isLastNameValid() {
        return validateText(lastName, MIN_TEXT_LENGTH);
    }

    private boolean isPhoneNumberValid() {
        return validateText(phoneNumber, MIN_PHONE_NUM_LENGTH);
    }

    private boolean isEmailValid() {
        return validateEmail(mShouldAlertEmailAddress);
    }

    private boolean isValidDriverInfo() {
        final boolean isValid = isFirstNameValid() && isLastNameValid()
                && isPhoneNumberValid() && isEmailValid();

        continueButton.setEnabled(isValid);

        return isValid;
    }

    public void highlightInvalidFields() {
        firstNameError.setValue(
                isFirstNameValid() ? null : emptyErrorString
        );

        lastNameError.setValue(
                isLastNameValid() ? null : emptyErrorString
        );

        phoneNumberError.setValue(
                isPhoneNumberValid() ? null : emptyErrorString
        );

        emailError.setValue(
                isEmailValid() ? null : emptyErrorString
        );
    }

    public EHIDriverInfo getDriverInfo() {
        if (!isValidDriverInfo()) {
            return null;
        }
        if (mEhiDriverInfo == null) {
            mEhiDriverInfo = new EHIDriverInfo();
        }
        mEhiDriverInfo.setFirstName(firstName.text().getRawValue());
        mEhiDriverInfo.setLastName(lastName.text().getRawValue());

        if (!EHITextUtils.isMaskedField(phoneNumber.text().getRawValue())) {
            mEhiDriverInfo.setPhoneNumber(mFormattedPhoneNumber.second);
            mEhiDriverInfo.getPhone().setMaskPhoneNumber(null);
        }


        if (!EHITextUtils.isMaskedField(emailAddress.text().getRawValue())) {
            mEhiDriverInfo.setEmailAddress(emailAddress.text().getRawValue());
            mEhiDriverInfo.setMaskEmailAddress(null);
        }

        mEhiDriverInfo.setRequestEmailPromotions(
                mEhiDriverInfo.hasRequestedEmailPromotions(),
                signUpEmailCheckBox.checked().getRawValue());

        return mEhiDriverInfo;
    }

    public EHIDriverInfo getDriverInfoForModify() {
        if (!isValidDriverInfo()) {
            return null;
        }
        EHIDriverInfo info = new EHIDriverInfo();

        info.setFirstName(firstName.text().getRawValue());
        info.setLastName(lastName.text().getRawValue());

        if (!EHITextUtils.isMaskedField(phoneNumber.text().getRawValue())) {
            info.setPhoneNumber(mFormattedPhoneNumber.second);
            info.getPhone().setMaskPhoneNumber(null);
        } else if (mEhiDriverInfo != null) {
            info.setPhoneNumber(mEhiDriverInfo.getPhone().getPhoneNumber());
        }

        if (!EHITextUtils.isMaskedField(emailAddress.text().getRawValue())) {
            info.setEmailAddress(emailAddress.text().getRawValue());
        } else if (mEhiDriverInfo != null) {
            info.setMaskEmailAddress(mEhiDriverInfo.getMaskEmailAddress());
        }

        boolean previouslySelected = needCheckEmailNotificationsByDefault();
        if (mEhiDriverInfo != null) {
            previouslySelected = mEhiDriverInfo.hasRequestedEmailPromotions();
        }

        info.setRequestEmailPromotions(
                previouslySelected,
                signUpEmailCheckBox.checked().getRawValue());

        return info;
    }

    public void setPhoneNumber(String val) {
        mPhoneNumber.setValue(val);
    }

    public boolean isAutoSaveEnabled() {
        return getManagers().getSettingsManager().isAutoSaveEnabled(needCacheDriverInfoByDefault());
    }

    public void setAutoSaveEnabled(boolean autoSaveEnabled) {
        getManagers().getSettingsManager().setAutoSaveEnabled(autoSaveEnabled);
    }

    public boolean shouldSaveDriverInfo() {
        return getManagers().getReservationManager().shouldSaveDriverInfo();
    }

    public void addOrUpdateDriverInfo() {
        if (isModify()) {
            showProgress(true);
            performRequest(new PostRenterInfoModifyRequest(getReservationObject().getResSessionId(), getDriverInfoForModify(), null),
                    new IApiCallback<EHIReservation>() {
                        @Override
                        public void handleResponse(ResponseWrapper<EHIReservation> response) {
                            showProgress(false);
                            if (response.isSuccess()) {
                                getManagers().getReservationManager().addOrUpdateModifyReservation(response.getData());
                                mSuccessModifyDriverResponse.setValue(response.getData());
                            } else {
                                setError(response);
                            }
                        }
                    });
        } else {
            if (!saveInformationCheckBox.checked().getRawValue()) {
                getManagers().getReservationManager().deleteDriverInfo();
            }
            getManagers().getReservationManager().addOrUpdateDriverInfo(getDriverInfo(), saveInformationCheckBox.checked().getRawValue());
        }
    }

    public EHIReservation getReservationObject() {
        if (isModify()) {
            return getManagers().getReservationManager().getCurrentModifyReservation();
        } else {
            return getManagers().getReservationManager().getCurrentReservation();
        }
    }

    @Override
    public boolean isUserLoggedIn() {
        return super.isUserLoggedIn() || getManagers().getReservationManager().getEmeraldClubAuthToken() != null;
    }

    public boolean isEPUserLoggedIn() {
        return super.isUserLoggedIn();
    }

    public void setDriverInfo(final EHIDriverInfo ehiDriverInfo) {
        mEhiDriverInfo = ehiDriverInfo;
    }

    public boolean isEditing() {
        return mEditing;
    }

    public void setIsEditing(final boolean editing) {
        mEditing = editing;
    }

    public void signUpEmailClicked() {
        signUpEmailCheckBox.setChecked(!signUpEmailCheckBox.checked().getRawValue());
    }

    public void saveInformationClicked() {
        final boolean newState = !saveInformationCheckBox.checked().getRawValue();
        saveInformationCheckBox.setChecked(newState);
        setAutoSaveEnabled(newState);
    }

    public void continueButtonClicked() {
        addOrUpdateDriverInfo();
    }

    public void setIsModify(boolean isModify) {
        mIsModify = isModify;
    }

    public boolean isModify() {
        return mIsModify;
    }

    public EHIReservation getSuccessModifyDriverResponse() {
        return mSuccessModifyDriverResponse.getValue();
    }

    public void clearSuccessModifyDriverResponse() {
        mSuccessModifyDriverResponse.setValue(null);
    }

    public boolean isIsLoginAfterStart() {
        return mIsLoginAfterStart;
    }
}
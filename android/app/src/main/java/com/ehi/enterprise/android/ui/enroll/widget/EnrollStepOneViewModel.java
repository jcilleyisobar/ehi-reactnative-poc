package com.ehi.enterprise.android.ui.enroll.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.ui.enroll.CountryContract;
import com.ehi.enterprise.android.ui.enroll.FormContract;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorTextViewState;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EnrollStepOneViewModel extends ManagersAccessViewModel
        implements FormContract.FormView, CountryContract.CountryView {

    private boolean shouldHighlightInvalidFieldsOnFormChange = false;
    private SimpleDateFormat localizedDate;
    private EHICountry selectedCountry;
    private String selectedCountryCode;
    private EHIRegion selectedRegion;
    private String selectedRegionCode;
    private boolean shouldShowIssueDate;
    private boolean shouldShowExpiryDate;

    private List<EHIRegion> ehiRegionListCache = null;

    final ReactorVar<CharSequence> firstNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> lastNameError = new ReactorVar<>();
    final ReactorVar<CharSequence> licenseNumberError = new ReactorVar<>();
    final ReactorVar<CharSequence> licenseBirthDateError = new ReactorVar<>();
    final ReactorVar<CharSequence> licenseIssueDateError = new ReactorVar<>();
    final ReactorVar<CharSequence> licenseExpiryDateError = new ReactorVar<>();
    final ReactorVar<Boolean> isValid = new ReactorVar<>(false);

    final ReactorViewState enrollStepTitleArea = new ReactorViewState();
    final ReactorVar<CharSequence> enrollStepTitle = new ReactorVar<>();

    public final ReactorVar<String> firstName = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            onFormChanged();
        }
    };

    public final ReactorVar<String> lastName = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            onFormChanged();
        }
    };

    public final ReactorVar<String> licenseNumber = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            onFormChanged();
        }
    };

    final ReactorVar<String> birthDateText = new ReactorVar<String>() {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            if (value.isEmpty()){
                birthDate.setValue(null);
            }
        }
    };

    public final ReactorVar<Date> birthDate = new ReactorVar<Date>() {
        @Override
        public void setValue(Date value) {
            super.setValue(value);

            if (localizedDate != null && value != null) {
                birthDateText.setValue(localizedDate.format(value));
            }

            onFormChanged();
        }
    };

    final ReactorViewState issueDateTitle = new ReactorViewState();
    final ReactorViewState issueDateArea = new ReactorViewState();
    final ReactorVar<String> issueDateText = new ReactorVar<String>() {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            if (value.isEmpty()){
                issueDate.setValue(null);
            }
        }
    };
    public final ReactorVar<Date> issueDate = new ReactorVar<Date>() {
        @Override
        public void setValue(Date value) {
            super.setValue(value);

            if (localizedDate != null && value != null) {
                issueDateText.setValue(localizedDate.format(value));
            }
            onFormChanged();
        }
    };



    final ReactorViewState expiryDateTitle = new ReactorViewState();
    final ReactorViewState expiryDateArea = new ReactorViewState();
    final ReactorVar<String> expiryDateText = new ReactorVar<String>() {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            if (value.isEmpty()){
                expiryDate.setValue(null);
            }
        }
    };
    public final ReactorVar<Date> expiryDate = new ReactorVar<Date>() {
        @Override
        public void setValue(Date value) {
            super.setValue(value);

            if (localizedDate != null && value != null) {
                expiryDateText.setValue(localizedDate.format(value));
            }

            onFormChanged();
        }
    };

    final ReactorVar<String> country = new ReactorVar<>();
    final ReactorVar<String> subdivision = new ReactorVar<>();
    final ReactorViewState subdivisionArea = new ReactorViewState();

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        enrollStepTitle.setValue(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "1")
                .formatString(R.string.enroll_long_form_step_title)
                .format());
    }

    public Date getBirthDate() {
        return birthDate.getRawValue();
    }

    public void setBirthDate(Date date) {
        birthDate.setValue(date);
    }

    public Date getLicenseIssueDate() {
        return issueDate.getRawValue();
    }

    public void setLicenseIssueDate(Date date) {
        issueDate.setValue(date);
    }

    public Date getLicenseExpiryDate() {
        return expiryDate.getRawValue();
    }

    public void setLicenseExpiryDate(Date date) {
        expiryDate.setValue(date);
    }

    public void setDateFormatter(SimpleDateFormat localizedDate) {
        this.localizedDate = localizedDate;
    }

    private void onFormChanged() {

        if (shouldHighlightInvalidFieldsOnFormChange) {
            highlightInvalidFields();
        }

        boolean isFormValid = !isLicenceNumberEmpty();

        isFormValid &= birthDate.getRawValue() != null;

        if (selectedCountry != null) {
            if (selectedCountry.isLicenseIssueDateRequired()) {
                isFormValid &= issueDate.getRawValue() != null;
            }
            if (selectedCountry.isLicenseExpiryDateRequired()) {
                isFormValid &= expiryDate.getRawValue() != null;
            }
            if (!isNA() && areIssueAndExpiryDatesOptional()) {
                isFormValid &= isAtLeastOneIssueAndExpiryFilled();
            }
        }

        isFormValid &= !EHITextUtils.isEmpty(firstName.getValue());
        isFormValid &= !EHITextUtils.isEmpty(lastName.getValue());

        isValid.setValue(isFormValid);
    }

    private boolean isLicenceNumberEmpty() {
        return EHITextUtils.isEmpty(licenseNumber.getRawValue());
    }

    public void setPresetData(EHIEnrollProfile ehiEnrollProfile) {
        // first and last name may come from driver info on res flow
        firstName.setValue(ehiEnrollProfile.getFirstName());
        lastName.setValue(ehiEnrollProfile.getLastName());

        final EHILicenseProfile ehiLicenseProfile = ehiEnrollProfile.getEhiLicenseProfile();
        if (ehiLicenseProfile == null) {
            return;
        }

        licenseNumber.setValue(ehiLicenseProfile.getLicenseNumber());
        issueDate.setValue(ehiLicenseProfile.getIssueDate());
        expiryDate.setValue(ehiLicenseProfile.getExpiryDate());
        birthDate.setValue(ehiEnrollProfile.getDayOfBirth());

        setLicenseFieldsVisibility(
                ehiLicenseProfile.getIssueDate() != null,
                ehiLicenseProfile.getExpiryDate() != null
        );

        if (!EHITextUtils.isEmpty(ehiLicenseProfile.getCountryName())) {
            country.setValue(ehiLicenseProfile.getCountryName());
            selectedCountryCode = ehiLicenseProfile.getCountryCode();
        }

        if (EHITextUtils.isEmpty(ehiLicenseProfile.getCountrySubdivisionName())
                && EHITextUtils.isEmpty(ehiLicenseProfile.getIssuingAuthority())) {
            subdivisionArea.setVisibility(ReactorViewState.GONE);
        } else {
            subdivisionArea.setVisibility(ReactorViewState.VISIBLE);
            if (!EHITextUtils.isEmpty(ehiLicenseProfile.getCountrySubdivisionName())) {
                subdivision.setValue(ehiLicenseProfile.getCountrySubdivisionName());
                selectedRegionCode = ehiLicenseProfile.getCountrySubdivisionCode();
                selectedRegion = ehiLicenseProfile.getCountrySubdivisionRegion();
            } else if (!EHITextUtils.isEmpty(ehiLicenseProfile.getIssuingAuthority())) {
                subdivision.setValue(ehiLicenseProfile.getIssuingAuthority());
                selectedRegionCode = ehiLicenseProfile.getIssuingAuthority();
                selectedRegion = ehiLicenseProfile.getCountryIssuingAuthorityRegion();
            }
        }
    }

    public EHIEnrollProfile updateEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        ehiEnrollProfile.setFirstName(firstName.getRawValue());
        ehiEnrollProfile.setLastName(lastName.getRawValue());
        ehiEnrollProfile.setDayOfBirth(birthDate.getRawValue());

        // gbo doesn't support the below fields to be edited in case of partial profile found
        if (ehiEnrollProfile.isDriverFound()) {
            return ehiEnrollProfile;
        }

        final EHILicenseProfile ehiLicenseProfile = new EHILicenseProfile();
        ehiLicenseProfile.setLicenseNumber(licenseNumber.getRawValue());

        if (shouldShowIssueDate) {
            ehiLicenseProfile.setIssueDate(issueDate.getRawValue());
        }

        if (shouldShowExpiryDate) {
            ehiLicenseProfile.setExpiryDate(expiryDate.getRawValue());
        }

        if (selectedCountry != null) {
            ehiLicenseProfile.setCountryCode(selectedCountry.getCountryCode());
            ehiLicenseProfile.setCountryName(selectedCountry.getCountryName());

            if (selectedCountry.isLicenseIssuingAuthorityRequired()) {
                ehiLicenseProfile.setIssuingAuthority(selectedRegion.getSubdivisionCode());
                ehiLicenseProfile.setCountrySubdivisionName(null);
            } else if (selectedRegion != null) {
                ehiLicenseProfile.setCountrySubdivisionCode(selectedRegion.getSubdivisionCode());
                ehiLicenseProfile.setCountrySubdivisionName(selectedRegion.getSubdivisionName());
            } else {
                ehiLicenseProfile.setCountrySubdivisionCode(null);
                ehiLicenseProfile.setCountrySubdivisionName(null);
            }
        }

        ehiEnrollProfile.setEhiLicenseProfile(ehiLicenseProfile);

        return ehiEnrollProfile;
    }

    @Override
    public boolean isValid() {
        return isValid.getRawValue();
    }

    @Override
    public void highlightInvalidFields() {
        licenseNumberError.setValue(isLicenceNumberEmpty() ? " " : null);
        firstNameError.setValue(EHITextUtils.isEmpty(firstName.getValue()) ? " " : null);
        lastNameError.setValue(EHITextUtils.isEmpty(lastName.getValue()) ? " " : null);
        licenseBirthDateError.setValue(birthDate.getRawValue() == null ? " " : null);

        if (selectedCountry.isLicenseIssueDateRequired()) {
            licenseIssueDateError.setValue(issueDate.getRawValue() == null ? " " : null);
        } else {
            licenseIssueDateError.setValue(null);
        }

        if (selectedCountry.isLicenseExpiryDateRequired()) {
            licenseExpiryDateError.setValue(expiryDate.getRawValue() == null ? " " : null);
        } else {
            licenseExpiryDateError.setValue(null);
        }

        if (!isNA() && areIssueAndExpiryDatesOptional() && !isAtLeastOneIssueAndExpiryFilled()) {
            licenseIssueDateError.setValue(" ");
            licenseExpiryDateError.setValue(" ");
        }
    }

    @Override
    public List<String> getErrorMessageList() {
        final List<String> errors = new ArrayList<>();

        if (EHITextUtils.isEmpty(firstName.getValue())) {
            errors.add(getResources().getString(R.string.enroll_first_name));
        }

        if (EHITextUtils.isEmpty(lastName.getValue())) {
            errors.add(getResources().getString(R.string.enroll_last_name));
        }

        if (isLicenceNumberEmpty()) {
            errors.add(getResources().getString(R.string.enroll_license_number));
        }

        if (shouldShowIssueDate && licenseIssueDateError.getRawValue() != null) {
            errors.add(getResources().getString(R.string.profile_license_issue_date));
        }

        if (shouldShowExpiryDate && licenseExpiryDateError.getRawValue() != null) {
            errors.add(getResources().getString(R.string.enroll_expiration_date));
        }

        if (birthDate.getRawValue() == null) {
            errors.add(getResources().getString(R.string.enroll_birth));
        }

        return errors;
    }

    @Override
    public void startHighlightInvalidFieldsOnFormChange() {
        shouldHighlightInvalidFieldsOnFormChange = true;
    }

    @Override
    public void stopHighlightInvalidFieldsOnFormChange() {
        shouldHighlightInvalidFieldsOnFormChange = false;
    }

    @Override
    public void setCountry(EHICountry selectedCountry) {
        this.selectedCountry = selectedCountry;

        setLicenseFieldsVisibility(
                selectedCountry.shouldShowIssueDate(),
                selectedCountry.shouldShowExpiryDateOnProfile()
        );

        country.setValue(selectedCountry.getCountryName());
        if (selectedCountry.getCountryCode() != null
                && selectedCountry.getCountryCode().length() > 0) {
            if (selectedCountry.hasSubdivisions()) {
                subdivisionArea.setVisibility(ReactorViewState.VISIBLE);
            } else {
                subdivisionArea.setVisibility(ReactorViewState.GONE);
                selectedRegion = null;
            }
        } else {
            subdivisionArea.setVisibility(ReactorViewState.GONE);
            selectedRegion = null;
        }
    }

    @Override
    public EHICountry getCountry() {
        return selectedCountry;
    }

    @Override
    public String getCountryCode() {
        if (selectedCountry != null) {
            return selectedCountry.getCountryCode();
        }

        return selectedCountryCode;
    }

    @Override
    public void setRegion(EHIRegion selectedRegion) {
        this.selectedRegion = selectedRegion;
        if (selectedRegion.getSubdivisionName() != null) {
            subdivision.setValue(selectedRegion.getSubdivisionName());
        } else {
            subdivision.setValue("");
        }
    }

    @Override
    public void setRegionList(List<EHIRegion> regionList) {
        ehiRegionListCache = regionList;

        if (ListUtils.isEmpty(regionList) ||
                EHITextUtils.isEmpty(selectedRegionCode) ||
                EHITextUtils.isMaskedField(selectedRegionCode)) {
            return;
        }

        for (EHIRegion ehiRegion : regionList) {
            if (ehiRegion.getSubdivisionCode().equals(selectedRegionCode)) {
                setRegion(ehiRegion);
                return;
            }
        }

        setRegion(regionList.get(0));
    }

    @Override
    public List<EHIRegion> getRegionList() {
        return ehiRegionListCache;
    }

    public void showHeader() {
        enrollStepTitleArea.setVisibility(ReactorViewState.VISIBLE);
    }

    public void hideHeader() {
        enrollStepTitleArea.setVisibility(ReactorViewState.GONE);
    }

    private void setLicenseFieldsVisibility(boolean shouldShowIssueDate, boolean shouldShowExpiryDate) {
        this.shouldShowIssueDate = shouldShowIssueDate;
        this.shouldShowExpiryDate = shouldShowExpiryDate;

        issueDateArea.setVisibility(
                shouldShowIssueDate ? ReactorTextViewState.VISIBLE : ReactorTextViewState.GONE
        );

        issueDateTitle.setVisibility(
                shouldShowIssueDate ? ReactorTextViewState.VISIBLE : ReactorTextViewState.GONE
        );

        expiryDateArea.setVisibility(
                shouldShowExpiryDate ? ReactorTextViewState.VISIBLE : ReactorTextViewState.GONE
        );

        expiryDateTitle.setVisibility(
                shouldShowExpiryDate ? ReactorTextViewState.VISIBLE : ReactorTextViewState.GONE
        );
    }

    private boolean areIssueAndExpiryDatesOptional() {
        return selectedCountry.isLicenseIssueDateOptional() && selectedCountry.isLicenseExpiryDateOptional();
    }

    private boolean isAtLeastOneIssueAndExpiryFilled() {
        return issueDate.getRawValue() != null || expiryDate.getRawValue() != null;
    }

    private boolean isNA() {
        return EHICountry.COUNTRY_US.equalsIgnoreCase(selectedCountry.getCountryCode())
                || EHICountry.COUNTRY_CANADA.equalsIgnoreCase(selectedCountry.getCountryCode());
    }
}

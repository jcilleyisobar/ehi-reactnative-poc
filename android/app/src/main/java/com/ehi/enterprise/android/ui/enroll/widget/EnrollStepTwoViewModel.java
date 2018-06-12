package com.ehi.enterprise.android.ui.enroll.widget;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.app.EHIStringToken;
import com.ehi.enterprise.android.models.enroll.EHIEnrollProfile;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHIRegion;
import com.ehi.enterprise.android.ui.enroll.CountryContract;
import com.ehi.enterprise.android.ui.enroll.FormContract;
import com.ehi.enterprise.android.ui.viewmodel.ManagersAccessViewModel;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.ListUtils;
import com.ehi.enterprise.android.utils.reactor_extensions.viewstate.ReactorViewState;
import com.isobar.android.tokenizedstring.TokenizedString;

import java.util.ArrayList;
import java.util.List;

import io.dwak.reactor.ReactorVar;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;

@AutoUnbindAll
public class EnrollStepTwoViewModel extends ManagersAccessViewModel
        implements FormContract.FormView, CountryContract.CountryView {

    private boolean shouldHighlightInvalidFieldsOnFormChange = false;
    private EHICountry selectedCountry;
    private String selectedCountryCode;
    private EHIRegion selectedRegion;
    private String selectedRegionCode;

    private List<EHIRegion> ehiRegionListCache = null;

    final ReactorVar<CharSequence> addressError = new ReactorVar<>();
    final ReactorVar<CharSequence> cityError = new ReactorVar<>();
    final ReactorVar<CharSequence> zipcodeError = new ReactorVar<>();
    final ReactorVar<Boolean> isValid = new ReactorVar<>(false);

    public final ReactorVar<String> address = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            onFormChanged();
        }
    };

    public final ReactorVar<String> address2 = new ReactorVar<>("");

    public final ReactorVar<String> city = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            onFormChanged();
        }
    };

    public final ReactorVar<String> zipcode = new ReactorVar<String>("") {
        @Override
        public void setValue(String value) {
            super.setValue(value);
            onFormChanged();
        }
    };

    final ReactorVar<String> country = new ReactorVar<>();
    final ReactorVar<String> subdivision = new ReactorVar<>();
    final ReactorViewState subdivisionArea = new ReactorViewState();

    final ReactorViewState enrollStepTitleArea = new ReactorViewState();
    final ReactorVar<CharSequence> enrollStepTitle = new ReactorVar<>();

    @Override
    public void onAttachToView() {
        super.onAttachToView();

        enrollStepTitle.setValue(new TokenizedString.Formatter<EHIStringToken>(getResources())
                .addTokenAndValue(EHIStringToken.STEP, "2")
                .formatString(R.string.enroll_long_form_step_title)
                .format());
    }

    private void onFormChanged() {
        boolean isFormValid = !EHITextUtils.isEmpty(address.getValue());
        isFormValid &= !EHITextUtils.isEmpty(city.getValue());
        isFormValid &= !EHITextUtils.isEmpty(zipcode.getValue());

        if (shouldHighlightInvalidFieldsOnFormChange) {
            highlightInvalidFields();
        }

        isValid.setValue(isFormValid);
    }

    public void setPresetData(EHIEnrollProfile ehiEnrollProfile) {
        final EHIAddressProfile ehiAddressProfile = ehiEnrollProfile.getEhiAddressProfile();
        if (ehiAddressProfile == null) {
            return;
        }

        if (!EHITextUtils.isEmpty(ehiAddressProfile.getCountryName())) {
            country.setValue(ehiAddressProfile.getCountryName());
            selectedCountryCode = ehiAddressProfile.getCountryCode();
        }

        if (EHITextUtils.isEmpty(ehiAddressProfile.getCountrySubdivisionName())) {
            subdivisionArea.setVisibility(ReactorViewState.GONE);
        } else {
            subdivision.setValue(ehiAddressProfile.getCountrySubdivisionName());
            subdivisionArea.setVisibility(ReactorViewState.VISIBLE);
            selectedRegionCode = ehiAddressProfile.getCountrySubdivisionCode();
            selectedRegion = ehiAddressProfile.getCountrySubdivisionRegion();
        }

        final List<String> streetAddresses = ehiAddressProfile.getStreetAddresses();
        if (!ListUtils.isEmpty(streetAddresses)) {
            address.setValue(streetAddresses.get(0));

            if (streetAddresses.size() > 1) {
                address2.setValue(streetAddresses.get(1));
            }
        }

        city.setValue(ehiAddressProfile.getCity());

        zipcode.setValue(ehiAddressProfile.getPostal());
    }

    public EHIEnrollProfile updateEnrollProfile(EHIEnrollProfile ehiEnrollProfile) {
        final EHIAddressProfile ehiAddressProfile = new EHIAddressProfile();
        ehiAddressProfile.setAddressType(EHIAddressProfile.TYPE_HOME);

        ehiAddressProfile.setStreetAddress(0, address.getRawValue());

        if (!EHITextUtils.isEmpty(address2.getRawValue())) {
            ehiAddressProfile.setStreetAddress(1, address2.getRawValue());
        }

        ehiAddressProfile.setCity(city.getRawValue());

        ehiAddressProfile.setPostal(zipcode.getRawValue());

        if (selectedCountry != null) {
            ehiAddressProfile.setCountryCode(selectedCountry.getCountryCode());
            ehiAddressProfile.setCountryName(selectedCountry.getCountryName());

            if (selectedRegion != null) {
                ehiAddressProfile.setCountrySubdivisionCode(selectedRegion.getSubdivisionCode());
                ehiAddressProfile.setCountrySubdivisionName(selectedRegion.getSubdivisionName());
            } else {
                ehiAddressProfile.setCountrySubdivisionCode(null);
                ehiAddressProfile.setCountrySubdivisionName(null);
            }
        }

        ehiEnrollProfile.setEhiAddressProfile(ehiAddressProfile);

        return ehiEnrollProfile;
    }

    @Override
    public boolean isValid() {
        return isValid.getRawValue();
    }

    @Override
    public void highlightInvalidFields() {
        addressError.setValue(EHITextUtils.isEmpty(address.getValue()) ? " " : null);
        cityError.setValue(EHITextUtils.isEmpty(city.getValue()) ? " " : null);
        zipcodeError.setValue(EHITextUtils.isEmpty(zipcode.getValue()) ? " " : null);
    }

    @Override
    public List<String> getErrorMessageList() {
        final List<String> errors = new ArrayList<>();

        if (EHITextUtils.isEmpty(address.getValue())) {
            errors.add(new TokenizedString.Formatter<EHIStringToken>(getResources())
                    .addTokenAndValue(EHIStringToken.NUMBER, "1")
                    .formatString(R.string.street_address)
                    .format().toString());
        }

        if (EHITextUtils.isEmpty(city.getValue())) {
            errors.add(getResources().getString(R.string.profile_edit_city_title));
        }

        if (EHITextUtils.isEmpty(zipcode.getValue())) {
            errors.add(getResources().getString(R.string.profile_edit_zip_title));
        }

        highlightInvalidFields();

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

}

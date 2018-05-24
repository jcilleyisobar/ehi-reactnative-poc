package com.ehi.enterprise.android.ui.profile;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.helpers.BaseViewModelTest;
import com.foresee.sdk.cxMeasure.util.Date;

import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LicenseInfoViewModelTest extends BaseViewModelTest<LicenseInfoViewModel> {

    @Test
    public void testNotFilledOptionalLicenseAndExpiryDatesNAShouldBeValid() throws Exception {
        EHICountry country = mock(EHICountry.class);
        when(country.isLicenseExpiryDateOptional()).thenReturn(true);
        when(country.isLicenseIssueDateOptional()).thenReturn(true);
        when(country.getCountryCode()).thenReturn(EHICountry.COUNTRY_US);
        getViewModel().setSelectedCountry(country);
        getViewModel().highlightInvalidFields();
        Assert.assertTrue(getViewModel().licenseExpiryDateError.getRawValue() == null
                && getViewModel().licenseIssueDateError.getRawValue() == null );
    }

    @Test
    public void testNotFilledOptionalLicenseAndExpiryDatesEuropeShouldBeInvalid() throws Exception {
        EHICountry country = mock(EHICountry.class);
        when(country.isLicenseExpiryDateOptional()).thenReturn(true);
        when(country.isLicenseIssueDateOptional()).thenReturn(true);
        when(country.getCountryCode()).thenReturn(EHICountry.COUNTRY_GERMANY);
        getViewModel().setSelectedCountry(country);
        getViewModel().highlightInvalidFields();
        Assert.assertTrue(getViewModel().licenseExpiryDateError.getRawValue() != null
                && getViewModel().licenseIssueDateError.getRawValue() != null );
    }

    @Test
    public void testOptionalLicenseAndExpiryDatesEuropeWithOneFilledShouldBeValid() throws Exception {
        EHICountry country = mock(EHICountry.class);
        when(country.isLicenseExpiryDateOptional()).thenReturn(true);
        when(country.isLicenseIssueDateOptional()).thenReturn(true);
        when(country.getCountryCode()).thenReturn(EHICountry.COUNTRY_GERMANY);
        getViewModel().setSelectedCountry(country);
        getViewModel().setLicenseExpiryDate(new Date());
        getViewModel().highlightInvalidFields();
        Assert.assertTrue(getViewModel().licenseExpiryDateError.getRawValue() == null
                && getViewModel().licenseIssueDateError.getRawValue() == null );
    }

    @Override
    protected Class<LicenseInfoViewModel> getViewModelClass() {
        return LicenseInfoViewModel.class;
    }
}
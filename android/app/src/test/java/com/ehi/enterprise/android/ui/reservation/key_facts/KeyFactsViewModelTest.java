package com.ehi.enterprise.android.ui.reservation.key_facts;

import android.view.View;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.profile.EHIAddressProfile;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.reservation.EHIKeyFactsPolicy;
import com.ehi.enterprise.android.network.responses.location.GetCountriesResponse;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class KeyFactsViewModelTest extends BaseViewModelTest<KeyFactsViewModel> {

    @Test
    public void testOnAttachToView() throws Exception {
        final String result = "Read Road Traffic Rules";
        getMockedContext().getMockedResources().addAnswer("getString", result);
        getViewModel().mDisputeCountry.setRawValue(new EHICountry());
        getViewModel().onAttachToView();
        assertEquals(R.string.reservation_about_your_rental_section_title, getViewModel().title.getRawValue().intValue());
        assertEquals(result + " >", getViewModel().viewRoadRules.text().getRawValue());
    }

    @Test
    public void testOnAttachToViewDisputeInfo() throws Exception {
        final String disputeEmail = "test@ehi.com";
        final String disputePhoneNumber = "111-111-1111";
        final String countryCode = "US";
        getViewModel().mDisputeCountry.setRawValue(null);


        EHICountry ehiDisputeInfo = Mockito.mock(EHICountry.class);
        Mockito.when(ehiDisputeInfo.getCountryCode()).thenReturn(countryCode);
        Mockito.when(ehiDisputeInfo.getKeyFactsDisputeEmail()).thenReturn(disputeEmail);
        Mockito.when(ehiDisputeInfo.getKeyFactsDisputePhone()).thenReturn(disputePhoneNumber);

        List<EHICountry> countryList = new ArrayList<>();
        countryList.add(ehiDisputeInfo);
        GetCountriesResponse response = Mockito.mock(GetCountriesResponse.class);
        Mockito.when(response.getCountries()).thenReturn(countryList);

        EHILocation pickupLocation = new EHILocation();
        EHIAddressProfile pickupProfile = new EHIAddressProfile();
        pickupProfile.setCountryCode(countryCode);
        pickupLocation.setAddress(pickupProfile);
        getViewModel().setPickupLocation(pickupLocation);

        Mockito.when(getMockedDelegate().getLocalDataManager().getCountriesList()).thenReturn(countryList);

        getViewModel().disputeCell.setVisibility(View.GONE);

        getViewModel().onAttachToView();

        assertEquals(disputeEmail, getViewModel().getDisputeInfo().getKeyFactsDisputeEmail());
        assertEquals(disputePhoneNumber, getViewModel().getDisputeInfo().getKeyFactsDisputePhone());
        assertVisible(getViewModel().disputeCell);
    }


    @Test
    public void testOnAttachToViewNoDisputeInfo() throws Exception {
        final String disputeEmail = "test@ehi.com";
        final String disputePhoneNumber = "111-111-1111";
        final String countryCode = "US";
        getViewModel().mDisputeCountry.setRawValue(null);

        EHICountry ehiDisputeInfo = Mockito.mock(EHICountry.class);
        Mockito.when(ehiDisputeInfo.getCountryCode()).thenReturn(countryCode);
        Mockito.when(ehiDisputeInfo.getKeyFactsDisputeEmail()).thenReturn(disputeEmail);
        Mockito.when(ehiDisputeInfo.getKeyFactsDisputePhone()).thenReturn(disputePhoneNumber);

        List<EHICountry> countryList = new ArrayList<>();
        countryList.add(ehiDisputeInfo);
        GetCountriesResponse response = Mockito.mock(GetCountriesResponse.class);
        Mockito.when(response.getCountries()).thenReturn(countryList);

        EHILocation pickupLocation = new EHILocation();
        EHIAddressProfile pickupProfile = new EHIAddressProfile();
        pickupProfile.setCountryCode("FR");
        pickupLocation.setAddress(pickupProfile);
        getViewModel().setPickupLocation(pickupLocation);

        Mockito.when(getMockedDelegate().getLocalDataManager().getCountriesList()).thenReturn(countryList);

        getViewModel().disputeCell.setVisibility(View.GONE);

        getViewModel().onAttachToView();
        assertGone(getViewModel().disputeCell);

    }

    @Test
    public void testSetKeyFactsPolicies() throws Exception {
        List<EHIKeyFactsPolicy> keyFactsPolicies = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            EHIKeyFactsPolicy minimum = mock(EHIKeyFactsPolicy.class);
            when(minimum.getSection()).thenReturn(EHIKeyFactsPolicy.MINIMUM_REQUIREMENTS);
            keyFactsPolicies.add(minimum);

            EHIKeyFactsPolicy protection = mock(EHIKeyFactsPolicy.class);
            when(protection.getSection()).thenReturn(EHIKeyFactsPolicy.PROTECTIONS);
            keyFactsPolicies.add(protection);

            EHIKeyFactsPolicy additional = mock(EHIKeyFactsPolicy.class);
            when(additional.getSection()).thenReturn(EHIKeyFactsPolicy.ADDITIONAL);
            keyFactsPolicies.add(additional);
        }

        getViewModel().setKeyFactsPolicies(keyFactsPolicies);

        assertEquals(5, getViewModel().getMinimumRequirementsPolicies().size());
        assertEquals(5, getViewModel().getProtectionProducts().size());
        assertEquals(5, getViewModel().getAdditionalPolicies().size());
    }

    @Override
    protected Class<KeyFactsViewModel> getViewModelClass() {
        return KeyFactsViewModel.class;
    }
}
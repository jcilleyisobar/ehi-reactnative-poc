package com.ehi.enterprise.android.ui.viewmodel;

import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

import static org.mockito.Mockito.when;

public class CountrySpecificViewModelTest extends BaseViewModelTest<CountrySpecificViewModel> {

    @Override
    protected Class<CountrySpecificViewModel> getViewModelClass() {
        return CountrySpecificViewModel.class;
    }

    @Test
    public void northAmericaShouldNotSeeDataCollectionModal() {
        when(getMockedDelegate().getLocalDataManager().getPreferredCountryCode()).thenReturn(EHICountry.COUNTRY_US);

        Assert.assertFalse(getViewModel().needShowDataCollectionReminder());

        advanceTime();

        Assert.assertFalse(getViewModel().needShowDataCollectionReminder());
    }

    @Test
    public void europeanShouldSeeDataCollectionModalOnlyOnce() {
        when(getMockedDelegate().getLocalDataManager().getPreferredCountryCode()).thenReturn(EHICountry.COUNTRY_GERMANY);

        Assert.assertTrue(getViewModel().needShowDataCollectionReminder());

        advanceTime();

        Assert.assertFalse(getViewModel().needShowDataCollectionReminder());
    }

    @Test
    public void franceShouldSeeDataCollectionModalEvery12Months() {
        when(getMockedDelegate().getLocalDataManager().getPreferredCountryCode()).thenReturn(EHICountry.COUNTRY_FRANCE);

        Assert.assertTrue(getViewModel().needShowDataCollectionReminder());

        advanceTime();

        Assert.assertTrue(getViewModel().needShowDataCollectionReminder());
    }

    private void advanceTime() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -12);
        when(getMockedDelegate().getLocalDataManager().getDataCollectionReminderNextShowTimestamp()).thenReturn(calendar.getTimeInMillis());
    }
}

package com.ehi.enterprise.android.ui.location;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Date;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LocationDetailsFragmentViewModelTest extends BaseViewModelTest<LocationDetailsFragmentViewModel> {

    @Override
    protected Class<LocationDetailsFragmentViewModel> getViewModelClass() {
        return LocationDetailsFragmentViewModel.class;
    }

    @Test
    public void testShouldShowAfterHoursDropoff() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isDropoffAfterHours()).thenReturn(true);
        getViewModel().setSolrLocation(solrLocation);
        Assert.assertTrue(getViewModel().shouldShowAfterHoursDropoff());
    }

    @Test
    public void testShouldHideAfterHoursDropoff() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isDropoffAfterHours()).thenReturn(false);
        getViewModel().setSolrLocation(solrLocation);
        Assert.assertFalse(getViewModel().shouldShowAfterHoursDropoff());
    }

    @Test
    public void testShouldShowConflictMessageForClosedPickup() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForPickup()).thenReturn(true);
        getViewModel().setPickupDate(new Date());
        getViewModel().setSolrLocation(solrLocation);
        getViewModel().setupConflictMessage();
        assertVisible(getViewModel().conflictMessageView);
    }

    @Test
    public void testShouldShowConflictMessageForClosedDropoff() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForDropoff()).thenReturn(true);
        getViewModel().setDropoffDate(new Date());
        getViewModel().setSolrLocation(solrLocation);
        getViewModel().setupConflictMessage();
        assertVisible(getViewModel().conflictMessageView);
    }

    @Test
    public void testShouldShowConflictMessageForClosedDropoffAndPickupWhenThereAreNoConflicts() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForDropoff()).thenReturn(true);
        when(solrLocation.isInvalidForPickup()).thenReturn(true);
        getViewModel().setPickupDate(new Date());
        getViewModel().setDropoffDate(new Date());
        getViewModel().setSolrLocation(solrLocation);
        doReturn("").when(getViewModel()).getTitle();
        getViewModel().setupConflictMessage();
        assertVisible(getViewModel().conflictMessageView);
    }

    @Test
    public void testShouldHideConflictMessageForClosedDropoffAndPickup() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForPickup()).thenReturn(false);
        when(solrLocation.isInvalidForDropoff()).thenReturn(false);
        getViewModel().setSolrLocation(solrLocation);
        doReturn("").when(getViewModel()).getTitle();
        getViewModel().setupConflictMessage();
        assertGone(getViewModel().conflictMessageView);
    }

    @Test
    public void testShouldHideConflictMessageForClosedDropoffAndPickupWhenThereAreNoDates() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForPickup()).thenReturn(true);
        when(solrLocation.isInvalidForDropoff()).thenReturn(true);
        getViewModel().setSolrLocation(solrLocation);
        doReturn("").when(getViewModel()).getTitle();
        getViewModel().setupConflictMessage();
        assertGone(getViewModel().conflictMessageView);
    }
}

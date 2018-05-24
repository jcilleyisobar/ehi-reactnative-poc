package com.ehi.enterprise.android.ui.location.components;

import android.view.View;

import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocationValidity;
import com.ehi.enterprise.android.ui.location.widgets.components.TimeConflictComponentViewModel;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Date;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimeConflictComponentViewModelTest extends BaseViewModelTest<TimeConflictComponentViewModel> {
    @Override
    protected Class<TimeConflictComponentViewModel> getViewModelClass() {
        return TimeConflictComponentViewModel.class;
    }

    @Test
    public void testIsContainerCollapsed() {
        getViewModel().collapseContainer();
        assertGone(getViewModel().collapsibleContainer);
    }

    @Test
    public void testIsContainerExpanded() {
        getViewModel().expandContainer();
        assertVisible(getViewModel().collapsibleContainer);
    }

    @Test
    public void testShouldReturnTrueFromShouldCollapse() {
        getViewModel().collapsibleContainer.setVisibility(View.VISIBLE);
        Assert.assertTrue(getViewModel().isContainerVisible());
    }

    @Test
    public void testShouldReturnFalseFromShouldCollapse() {
        getViewModel().collapsibleContainer.setVisibility(View.GONE);
        Assert.assertFalse(getViewModel().isContainerVisible());
    }

    @Test
    public void testToggleButtonShouldCollapseContainer() {
        getViewModel().collapsibleContainer.setVisibility(View.VISIBLE);
        getViewModel().toggleContainer();
        assertGone(getViewModel().collapsibleContainer);
    }

    @Test
    public void testToggleButtonShouldExpandContainer() {
        getViewModel().collapsibleContainer.setVisibility(View.GONE);
        getViewModel().toggleContainer();
        assertVisible(getViewModel().collapsibleContainer);
    }

    @Test
    public void testIsConflictViewVisibleWhenLocationIsClosedForPickupAndReturn() {
        doReturn("").when(getViewModel()).getTitleText();
        mockClosedForBothWays();
        assertVisible(getViewModel().rootView);
    }

    @Test
    public void testIsConflictViewGoneWhenLocationIsOpenForPickupReturn() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForPickup()).thenReturn(false);
        when(solrLocation.isInvalidForDropoff()).thenReturn(false);
        getViewModel().setSolrLocation(solrLocation);
        assertGone(getViewModel().rootView);
    }

    @Test
    public void testShouldReturnPickupDateForFirstTimeView() {
        final Date pickupDate = new Date();
        getViewModel().setPickupDate(pickupDate);
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        getViewModel().setSolrLocation(solrLocation);
        when(solrLocation.isInvalidForPickup()).thenReturn(true);
        Assert.assertEquals(pickupDate, getViewModel().getDateForFirstTimeView());
    }

    @Test
    public void testShouldReturnPickupDateForFirstTimeViewWhenLocationIsClosedForPickupAndDropoff() {
        final Date pickupDate = new Date();
        getViewModel().setPickupDate(pickupDate);
        doReturn("").when(getViewModel()).getTitleText();
        mockClosedForBothWays();
        Assert.assertEquals(pickupDate, getViewModel().getDateForFirstTimeView());
    }

    @Test
    public void testShouldReturnPickupValidityForFirstTimeView() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        EHISolrLocationValidity solrLocationValidity = mock(EHISolrLocationValidity.class);
        when(solrLocation.getPickupValidity()).thenReturn(solrLocationValidity);
        when(solrLocation.isInvalidForPickup()).thenReturn(true);
        doReturn("").when(getViewModel()).getTitleText();
        getViewModel().setSolrLocation(solrLocation);
        Assert.assertEquals(solrLocationValidity, getViewModel().getValidityForFirstTimeView());
    }

    @Test
    public void testShouldReturnDropoffDateForSecondTimeViewWhenLocationIsClosedForPickupAndDropoff() {
        final Date dropoffDate = new Date();
        getViewModel().setDropoffDate(dropoffDate);
        doReturn("").when(getViewModel()).getTitleText();
        mockClosedForBothWays();
        Assert.assertEquals(dropoffDate, getViewModel().getDateForSecondTimeView());
    }

    @Test
    public void testShouldReturnDropoffValidityTypeForSecondTimeViewWhenLocationIsClosedForPickupAndDropoff() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        EHISolrLocationValidity solrLocationValidity = mock(EHISolrLocationValidity.class);
        when(solrLocation.getDropoffValidity()).thenReturn(solrLocationValidity);
        doReturn("").when(getViewModel()).getTitleText();
        mockClosedForBothWays(solrLocation);
        getViewModel().setSolrLocation(solrLocation);
        Assert.assertEquals(solrLocationValidity, getViewModel().getValidityForSecondTimeView());
    }

    @Test
    public void testOnlyFirstViewIsVisible() {
        doReturn("").when(getViewModel()).getTitleText();
        mockClosedForBothWays();
        getViewModel().updateTimesViewsVisibility();
        assertVisible(getViewModel().firstTimeView);
    }

    @Test
    public void testBothFirstAndSecondViewAreVisible() {
        doReturn("").when(getViewModel()).getTitleText();
        mockClosedForBothWays();
        getViewModel().updateTimesViewsVisibility();
        assertVisible(getViewModel().firstTimeView);
        assertVisible(getViewModel().secondTimeView);
    }

    @Test
    public void testNoneOfTimesViewAreVisible(){
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        when(solrLocation.isInvalidForPickup()).thenReturn(false);
        when(solrLocation.isInvalidForDropoff()).thenReturn(false);
        getViewModel().setSolrLocation(solrLocation);
        doReturn("").when(getViewModel()).getTitleText();
        getViewModel().updateTimesViewsVisibility();
        assertGone(getViewModel().firstTimeView);
        assertGone(getViewModel().secondTimeView);
    }

    private void mockClosedForBothWays() {
        EHISolrLocation solrLocation = mock(EHISolrLocation.class);
        mockClosedForBothWays(solrLocation);
        getViewModel().setSolrLocation(solrLocation);
    }

    private void mockClosedForBothWays(EHISolrLocation solrLocation) {
        when(solrLocation.isInvalidForPickup()).thenReturn(true);
        when(solrLocation.isInvalidForDropoff()).thenReturn(true);
        when(solrLocation.isClosedForPickupAndDropoff()).thenReturn(true);
    }

}


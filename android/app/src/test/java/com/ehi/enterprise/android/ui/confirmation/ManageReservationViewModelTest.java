package com.ehi.enterprise.android.ui.confirmation;

import com.ehi.enterprise.android.ui.confirmation.widgets.ManageReservationViewModel;
import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ManageReservationViewModelTest extends BaseViewModelTest<ManageReservationViewModel> {

    @Override
    protected Class<ManageReservationViewModel> getViewModelClass() {
        return ManageReservationViewModel.class;
    }

    @Test
    public void testVerifyCollapseCalledWhenContainerIsExpanded() {
        doReturn(true).when(getViewModel()).isContainerVisible();
        getViewModel().toggleContainer();
        verify(getViewModel(), times(1)).collapseContainer();
    }

    @Test
    public void testVerifyExpandCalledWhenContainerIsCollapsed() {
        doReturn(false).when(getViewModel()).isContainerVisible();
        getViewModel().toggleContainer();
        verify(getViewModel(), times(1)).expandContainer();
    }

    @Test
    public void testIsButtonContainerCollapsedWhenCollapseIsCalled() {
        getViewModel().collapseContainer();
        Assert.assertTrue(getViewModel().collapseButtonsContainer.getValue());
    }

    @Test
    public void testIsButtonExpandedWhenExpandIsCalled() {
        getViewModel().expandContainer();
        Assert.assertTrue(getViewModel().expandButtonsContainer.getValue());
    }

    @Test
    public void testShouldCollapseWhenResettingState() {
        doReturn(true).when(getViewModel()).isContainerVisible();
        getViewModel().resetInitialState();

        Assert.assertTrue(getViewModel().collapseButtonsContainer.getValue());
        Assert.assertEquals(getViewModel().getArrowInitialPosition(), 90);
        Assert.assertEquals(getViewModel().getArrowFinalPosition(), 0);
    }
}

package com.ehi.enterprise.android.ui.dashboard;

import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;

public class ExtendRentalViewModelTest extends BaseViewModelTest<ExtendRentalViewModel> {

    @Test
    public void testConfirmationNumberNotNull() throws Exception {
        final String confirmationNumber = "1233";
        getViewModel().confirmationNumber.setText(confirmationNumber);
        Assert.assertEquals(confirmationNumber, getViewModel().confirmationNumber.text().getRawValue());
    }

    @Test
    public void testConfirmationNumberNull() throws Exception {
        Assert.assertNull(getViewModel().confirmationNumber.text().getRawValue());
    }

    @Test
    public void testCallNumberNotNull() throws Exception {
        final String callNumber = "536283782";
        getViewModel().callNumber.setText(callNumber);
        Assert.assertEquals(callNumber, getViewModel().callNumber.text().getRawValue());
    }

    @Test
    public void testCallNumberNull() throws Exception {
        Assert.assertNull(getViewModel().callNumber.text().getRawValue());
    }

    @Override
    protected Class<ExtendRentalViewModel> getViewModelClass() {
        return ExtendRentalViewModel.class;
    }
}

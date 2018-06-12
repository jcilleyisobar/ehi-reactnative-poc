package com.ehi.enterprise.android.ui.reservation.widget;

import com.ehi.enterprise.helpers.BaseViewModelTest;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Locale;

public class EPointsHeaderViewModelTest extends BaseViewModelTest<EPointsHeaderViewModel> {


    @Test
    public void testSetTopRightText() throws Exception {
        final String testString = "Test";
        getViewModel().setTopRightText(testString);
        Assert.assertEquals(getViewModel().topRightText.getRawValue(), testString);
    }

    @Test
    public void testSetTopLeftPointsText() throws Exception {
        Assert.assertNull(getViewModel().topLeftPointsText.getRawValue());
        Assert.assertTrue(getViewModel().topLeftPointsVisibility.getRawValue());
    }

    @Test
    public void testSetTopLeftPointsUS(){
        testPoints(Locale.US, "10", "1,000", "1,000,000");
    }

    @Test
    public void testSetTopLeftPointsFR(){
        testPoints(Locale.FRANCE, "10", "1 000", "1 000 000");
    }

    @Test
    public void testSetTopLeftPointsUK(){
        testPoints(Locale.UK, "10", "1,000", "1,000,000");
    }

    @Test
    public void testSetTopLeftPointsCA(){
        testPoints(Locale.CANADA, "10", "1,000", "1,000,000");
    }

    private void testPoints(final Locale locale,
                            final String expected10,
                            final String expected100,
                            final String expected1000) {
        Locale.setDefault(locale);
        getViewModel().setTopLeftPointsText(10l);
        Assert.assertEquals(expected10, getViewModel().topLeftPointsText.getRawValue());
        Assert.assertTrue(getViewModel().topLeftPointsVisibility.getRawValue());

        getViewModel().setTopLeftPointsText(1000l);
        Assert.assertEquals(expected100, getViewModel().topLeftPointsText.getRawValue());
        Assert.assertTrue(getViewModel().topLeftPointsVisibility.getRawValue());

        getViewModel().setTopLeftPointsText(1000000l);
        Assert.assertEquals(expected1000, getViewModel().topLeftPointsText.getRawValue());
        Assert.assertTrue(getViewModel().topLeftPointsVisibility.getRawValue());
    }

    @Test
    public void testSetTopLeftHeaderText() throws Exception {
        Assert.assertNull(getViewModel().topLeftHeaderText.getRawValue());
        Assert.assertTrue(getViewModel().topLeftHeaderVisibility.getRawValue());
        final String testString = "Test";
        getViewModel().setTopLeftHeaderText(testString);
        Assert.assertEquals(testString, getViewModel().topLeftHeaderText.getRawValue());
        Assert.assertTrue(getViewModel().topLeftHeaderVisibility.getRawValue());
    }

    @Test
    public void testSetDividerVisibility() throws Exception {
        getViewModel().setDividerVisibility(true);
        Assert.assertTrue(getViewModel().dividerVisibility.getRawValue());

        getViewModel().setDividerVisibility(false);
        Assert.assertFalse(getViewModel().dividerVisibility.getRawValue());
    }

    @Override
    protected Class<EPointsHeaderViewModel> getViewModelClass() {
        return EPointsHeaderViewModel.class;
    }
}
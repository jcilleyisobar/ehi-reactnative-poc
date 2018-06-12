package com.ehi.enterprise.android.models.reservation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EHIDriverInfoTest {

    private EHIDriverInfo driverInfo;

    @Before
    public void setUp() {
        driverInfo = new EHIDriverInfo();
    }

    @Test
    public void defaultFalseOptIn() {
        driverInfo.setRequestEmailPromotions(false, true);

        Assert.assertTrue(driverInfo.hasRequestedEmailPromotions());
    }

    @Test
    public void defaultFalseOptOut() {
        driverInfo.setRequestEmailPromotions(false, false);

        Assert.assertFalse(driverInfo.hasRequestedEmailPromotions());
    }

    @Test
    public void defaultTrueOptIn() {
        driverInfo.setRequestEmailPromotions(true, true);

        Assert.assertTrue(driverInfo.hasRequestedEmailPromotions());
    }

    @Test
    public void defaultTrueOptOut() {
        driverInfo.setRequestEmailPromotions(true, false);

        Assert.assertFalse(driverInfo.hasRequestedEmailPromotions());
    }
}
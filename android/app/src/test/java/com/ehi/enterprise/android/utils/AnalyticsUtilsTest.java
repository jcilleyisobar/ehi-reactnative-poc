package com.ehi.enterprise.android.utils;


import com.ehi.enterprise.android.utils.analytics.AnalyticsUtils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AnalyticsUtilsTest {

    @Test
    public void shouldReplaceLoyaltyId() {
        String url = "api/users/A1B2C3/EP/trips/upcoming";
        String expected = "api/users/<loyaltyId>/EP/trips/upcoming";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceReservationId() {
        String url = "api/reservations/123456";
        String expected = "api/reservations/<resId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceModifyReservationId() {
        String url = "api/reservations/modify/123456";
        String expected = "api/reservations/modify/<resId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceModifyCarClassReservationId() {
        String url = "api/reservations/modify/123456/selectedCarClass";
        String expected = "api/reservations/modify/<resId>/selectedCarClass";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceLocation() {
        String url = "api/locations/10010101";
        String expected = "api/locations/<locationId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceContract() {
        String url = "api/contracts/A1B2C3";
        String expected = "api/contracts/<contractId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplacePaymentId() {
        String url = "api/payment/123456";
        String expected = "api/payment/<paymentId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceCommit() {
        String url = "api/mobile/abc123_/commit";
        String expected = "api/mobile/<sessionId>/commit";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplace3dsData() {
        String url = "api/mobile/abc123_/3dsData";
        String expected = "api/mobile/<sessionId>/3dsData";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceSelectCarClass() {
        String url = "api/mobile/abc123_/selectCarClass";
        String expected = "api/mobile/<sessionId>/selectCarClass";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceCarClassDetails() {
        String url = "api/mobile/abc123_/carClassDetails?carClassCode=SPAR&redemptionDayCount=1";
        String expected = "api/mobile/<sessionId>/carClassDetails";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceIndividualId() {
        String url = "api/mobile/EP/profile?individualId=12345";
        String expected = "api/mobile/EP/profile?individualId=<individualId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceContractParam() {
        String url = "api/mobile/EP/profile?contract=ABC123";
        String expected = "api/mobile/EP/profile?contract=<contractId>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }
    //

    @Test
    public void shouldReplaceUserIdInCurrentTrips() {
        String url = "api/v2/trips/mobile/ABC123/current";
        String expected = "api/v2/trips/mobile/<loyaltyId>/current";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceUserIdInUpcomingTrips() {
        String url = "api/v2/trips/mobile/ABC123/upcoming";
        String expected = "api/v2/trips/mobile/<loyaltyId>/upcoming";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceRetriveReservation() {
        String url = "api/v2/reservations/mobile/1206372103?firstName=DRIVEALLIANCE&lastName=BENNETT";
        String expected = "api/v2/reservations/mobile/<resId>?firstName=<name>&lastName=<name>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceCardSubmissionKey() {
        String url = "api/v2/reservations/mobile/abC123_/cardSubmissionKey";
        String expected = "api/v2/reservations/mobile/<resId>/cardSubmissionKey";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

    @Test
    public void shouldReplaceCardSubmission() {
        String url = "cards/submissions/abC123_";
        String expected = "cards/submissions/<submissionKey>";

        assertEquals(expected, AnalyticsUtils.maskUrl(url));
    }

}

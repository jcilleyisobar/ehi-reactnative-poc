package com.ehi.enterprise.android.utils.analytics;

public class AnalyticsUtils {

    public static String maskUrl(String url) {
        String masked = url.replaceFirst("users/[A-Z0-9]+", "users/<loyaltyId>");
        masked = masked.replaceFirst("reservations/\\d+", "reservations/<resId>");
        masked = masked.replaceFirst("modify/\\d+", "modify/<resId>");
        masked = masked.replaceFirst("contracts/[A-Z0-9]+", "contracts/<contractId>");
        masked = masked.replaceFirst("payment/\\d+", "payment/<paymentId>");
        masked = masked.replaceFirst("mobile/.+/commit", "mobile/<sessionId>/commit");
        masked = masked.replaceFirst("mobile/.+/3dsData", "mobile/<sessionId>/3dsData");
        masked = masked.replaceFirst("mobile/.+/selectCarClass", "mobile/<sessionId>/selectCarClass");
        masked = masked.replaceFirst("mobile/.+/carClassDetails?.+", "mobile/<sessionId>/carClassDetails");
        masked = masked.replaceFirst("mobile/.+/current", "mobile/<loyaltyId>/current");
        masked = masked.replaceFirst("mobile/.+/upcoming", "mobile/<loyaltyId>/upcoming");
        masked = masked.replaceFirst("mobile/.+/cardSubmissionKey", "mobile/<resId>/cardSubmissionKey");
        masked = masked.replaceFirst("cards/submissions/.+", "cards/submissions/<submissionKey>");
        masked = masked.replaceFirst("\\?individualId=\\d+", "?individualId=<individualId>");
        masked = masked.replaceFirst("\\?contract=[A-Z0-9]+", "?contract=<contractId>");
        masked = masked.replaceFirst("mobile/[A-Z0-9]+\\?firstName=[A-Z]+&lastName=[A-Z]+", "mobile/<resId>?firstName=<name>&lastName=<name>");
        return masked.replaceFirst("locations/\\d+", "locations/<locationId>");
    }

    public static boolean isSolrEndpoint(String url) {
        return url.contains(".location.");
    }
}
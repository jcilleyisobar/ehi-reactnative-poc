package com.ehi.enterprise.android.utils.analytics;

import android.content.res.Resources;
import android.location.Location;
import android.text.TextUtils;

import com.ehi.enterprise.android.models.location.EHILocation;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.notification.EHINotification;
import com.ehi.enterprise.android.models.profile.EHICountry;
import com.ehi.enterprise.android.models.profile.EHILicenseProfile;
import com.ehi.enterprise.android.models.profile.EHILoyaltyData;
import com.ehi.enterprise.android.models.profile.ProfileCollection;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHIDriverInfo;
import com.ehi.enterprise.android.models.reservation.EHIExtraItem;
import com.ehi.enterprise.android.models.reservation.EHIExtras;
import com.ehi.enterprise.android.models.reservation.EHIFilterValue;
import com.ehi.enterprise.android.models.reservation.EHIPriceSummary;
import com.ehi.enterprise.android.models.reservation.EHIReservation;
import com.ehi.enterprise.android.network.requests.AbstractRequestProvider;
import com.ehi.enterprise.android.network.responses.ResponseWrapper;
import com.ehi.enterprise.android.ui.location.SearchLocationsActivity;
import com.ehi.enterprise.android.utils.EHITextUtils;
import com.ehi.enterprise.android.utils.filters.EHIFilterList;
import com.ehi.enterprise.android.utils.locations.LocationApiManager;
import com.ehi.enterprise.android.utils.manager.EHINotificationManager;
import com.ehi.enterprise.android.utils.manager.LocalDataManager;
import com.ehi.enterprise.android.utils.manager.LocationManager;
import com.ehi.enterprise.android.utils.manager.LoginManager;
import com.ehi.enterprise.android.utils.manager.ReservationManager;
import com.ehi.enterprise.android.utils.manager.SettingsManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EHIAnalyticsDictionaryUtils {

    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat sTimeFormatter = new SimpleDateFormat("HH:mm");

    public static Map<String, String> dashboard(String rentalStatus, Date pickupDate) {
        Map<String, String> dict = new HashMap<>();
        if (rentalStatus != null) {
            dict.put("rentalStatus", rentalStatus);
            if (pickupDate != null) {
                dict.put("daysUntilRental", leadTimeToDate(pickupDate) + "");
            }
        }
        return dict;
    }

    public static Map<Integer, String> customDimensions() {
        String sessionSource = ReservationManager.getInstance().getSessionSource();

        ProfileCollection profile = ReservationManager.getInstance().getEmeraldClubProfile();
        if (profile == null) {
            profile = LoginManager.getInstance().getProfileCollection();
        }

        Map<Integer, String> dict = new HashMap<>();
        if (sessionSource != null) {
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_SESSION_SOURCE.ordinal(), sessionSource);
        } else {
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_SESSION_SOURCE.ordinal(), EHIAnalytics.NOT_AVAILABLE);
        }
        dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LANGUAGE.ordinal(), Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry());
        dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOCATION_SERVICE.ordinal(), EHIAnalytics.Boolean.BOOL_YES.value);

        dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_APP_COUNTRY.ordinal(), LocalDataManager.getInstance().getPreferredCountryCode());

        if (profile != null) {
            if (profile.getBasicProfile() != null && profile.getProfile().getCorporateAccount() != null) {
                dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_VISITOR_TYPE.ordinal(), "Corp");
            } else {
                dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_VISITOR_TYPE.ordinal(), "Member");
            }

            if (ReservationManager.getInstance().isLoggedIntoEmeraldClub()) {
                dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_ACCOUNT_TYPE.ordinal(), "EC");
            } else {
                dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_ACCOUNT_TYPE.ordinal(), "EP");
            }

            if (profile.getBasicProfile() != null && profile.getBasicProfile().getLoyaltyData() != null) {
                if (!TextUtils.isEmpty(profile.getBasicProfile().getLoyaltyData().getLoyaltyNumber())) {
                    dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_CUSTOMER_ID.ordinal(), profile.getBasicProfile().getLoyaltyData().getLoyaltyNumber());
                }
                if (!TextUtils.isEmpty(profile.getBasicProfile().getLoyaltyData().getLoyaltyTier())) {
                    dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_TIER.ordinal(), profile.getBasicProfile().getLoyaltyData().getLoyaltyTier());
                }
                dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_POINT.ordinal(), profile.getBasicProfile().getLoyaltyData().getPointsToDate() + "");
            }
        } else {
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_VISITOR_TYPE.ordinal(), "Guest");
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_ACCOUNT_TYPE.ordinal(), "Guest");
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_CUSTOMER_ID.ordinal(), EHIAnalytics.NOT_AVAILABLE);
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_TIER.ordinal(), EHIAnalytics.NOT_AVAILABLE);
            dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_LOYALTY_POINT.ordinal(), EHIAnalytics.NOT_AVAILABLE);

        }

        dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_PAY_SYSTEM_ENABLED.ordinal(),
                LocalDataManager.getInstance().hasPaySystem()?
                EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);
        dict.put(EHIAnalytics.CustomDimensions.CUSTOM_DIMENSION_PUSH_NOTIFICATION_ENABLED.ordinal(),
                EHINotificationManager.getInstance().isPushNotificationEnabled()?
                EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);

        return dict;
    }

    private static Map<String, String> pickupLocation(EHISolrLocation location) {
        Map<String, String> dict = new HashMap<>();
        if (location != null) {
            dict.put("puLocLat", location.getLatitude() + "");
            dict.put("puLocLong", location.getLongitude() + "");
            dict.put("puLocEngName", location.getTranslatedLocationName());
            dict.put("puLocType", getLocationTypeForLocation(location));
            dict.put("puLocId", location.getPeopleSoftId());
            dict.put("puCountryCode", location.getCountryCode());
        }
        return dict;
    }

    private static Map<String, String> returnLocation(EHISolrLocation location) {
        Map<String, String> dict = new HashMap<>();
        if (location != null) {
            dict.put("doLocLat", location.getLatitude() + "");
            dict.put("doLocLong", location.getLongitude() + "");
            dict.put("doLocEngName", location.getTranslatedLocationName());
            dict.put("doLocType", getLocationTypeForLocation(location));
            dict.put("doLocId", location.getPeopleSoftId());
            dict.put("doCountryCode", location.getCountryCode());
        }
        return dict;
    }

    private static Map<String, String> pickupLocation(EHILocation location) {
        Map<String, String> dict = new HashMap<>();
        if (location != null) {
            dict.put("puLocLat", location.getGpsCoordinates().getLatitude() + "");
            dict.put("puLocLong", location.getGpsCoordinates().getLongitude() + "");
            dict.put("puLocEngName", location.getName());
            dict.put("puLocType", getLocationTypeForLocation(location));
            dict.put("puLocId", location.getId());
            dict.put("puCountryCode", location.getAddress().getCountryCode());
        }
        return dict;
    }

    private static Map<String, String> returnLocation(EHILocation location) {
        Map<String, String> dict = new HashMap<>();
        if (location != null) {
            dict.put("doLocLat", location.getGpsCoordinates().getLatitude() + "");
            dict.put("doLocLong", location.getGpsCoordinates().getLongitude() + "");
            dict.put("doLocEngName", location.getName());
            dict.put("duLocType", getLocationTypeForLocation(location));
            dict.put("doLocId", location.getId());
            dict.put("doCountryCode", location.getAddress().getCountryCode());
        }
        return dict;
    }

    private static String getLocationTypeForLocation(EHISolrLocation location) {
        switch (location.getLocationType()) {
            case PORT:
                return EHIAnalytics.LocationType.LOCATION_TYPE_PORT.value;
            case AIRPORT:
                return EHIAnalytics.LocationType.LOCATION_TYPE_AIRPORT.value;
            case RAIL:
                return EHIAnalytics.LocationType.LOCATION_TYPE_RAIL.value;
            case CITY:
            default:
                return EHIAnalytics.LocationType.LOCATION_TYPE_BRANCH.value;
        }
    }

    private static String getLocationTypeForLocation(EHILocation location) {
        if (location.isAirport()) {
            return EHIAnalytics.LocationType.LOCATION_TYPE_AIRPORT.value;
        } else if (location.isPort()) {
            return EHIAnalytics.LocationType.LOCATION_TYPE_PORT.value;
        } else if (location.isTrainStation()) {
            return EHIAnalytics.LocationType.LOCATION_TYPE_RAIL.value;
        } else {
            return EHIAnalytics.LocationType.LOCATION_TYPE_BRANCH.value;
        }
    }

    public static Map<String, String> locationSearch(String searchKeyWord, boolean zeroResult) {
        Map<String, String> dict = new HashMap<>();
        if (searchKeyWord != null
                && !searchKeyWord.isEmpty()) {
            dict.put("locKeyword", searchKeyWord);
        }
        if (zeroResult) {
            dict.put("locSearchZero", EHIAnalytics.Boolean.BOOL_YES.value);
        } else {
            dict.put("locSearchZero", EHIAnalytics.Boolean.BOOL_NO.value);
        }
        return dict;
    }

    public static Map<String, String> locationSearch(int flow, EHISolrLocation location, String searchKeyWord, Boolean zeroResult) {
        Map<String, String> dict = new HashMap<>();
        if (flow == SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP
                && location != null) {
            dict.putAll(pickupLocation(location));
        }
        if (flow == SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY
                && location != null) {
            dict.putAll(returnLocation(location));
        }
        dict.putAll(locationSearch(searchKeyWord, zeroResult));

        if (location != null) {
            if (LocationManager.getInstance().isFavoriteLocation(location.getPeopleSoftId())) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_FAVORITE.value);
            } else if (LocationManager.getInstance().isRecentLocation(location)) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_RECENT.value);
            }
        }
        return dict;
    }

    public static Map<String, String> locationsFilter(ArrayList<Integer> filterIds, Resources res) {
        return locationsFilter(filterIds, res, EHIAnalytics.LocationFilterType.NONE);
    }

    public static Map<String, String> locationsFilter(ArrayList<Integer> filterIds, Resources res, EHIAnalytics.LocationFilterType locationFilterType) {
        Map<String, String> dict = new HashMap<>();

        dict.put("filterType", EHIAnalytics.FilterType.FILTER_LOCATION.value);
        dict.put("locFilterType", locationFilterType.value);

        StringBuilder bld = new StringBuilder();
        if (filterIds != null
                && filterIds.size() > 0) {
            for (Integer filterId : filterIds) {
                if (bld.toString().trim().length() != 0) {
                    bld.append("|");
                }
                bld.append(EHIFilterList.getFilter(filterId, res).getTitle());
            }
        }
        if (bld.toString().trim().length() > 0) {
            dict.put("filterList", bld.toString());
        }
        return dict;
    }

    public static Map<String, String> carClassFilter(List<EHIFilterValue> filterIds) {
        Map<String, String> dict = new HashMap<>();
        dict.putAll(reservation());
        dict.put("filterType", EHIAnalytics.FilterType.FILTER_LOCATION.value);
        StringBuilder bld = new StringBuilder();
        for (EHIFilterValue filter : filterIds) {
            if (bld.toString().trim().length() != 0) {
                bld.append("|");
            }
            bld.append(filter.getDescription());
        }
        if (bld.toString().trim().length() > 0) {
            dict.put("filterList", bld.toString());
        }
        return dict;
    }

    public static Map<String, String> locationMap(int flow, EHISolrLocation location, String searchArea, int itemCount, int closedLocationsCount, String filters) {
        Map<String, String> dict = new HashMap<>();

        if (location != null) {
            if (LocationManager.getInstance().isFavoriteLocation(location.getPeopleSoftId())) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_FAVORITE.value);
            } else if (LocationManager.getInstance().isRecentLocation(location)) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_RECENT.value);
            }
            if (flow == SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP) {
                dict.putAll(pickupLocation(location));
            } else if (flow == SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY) {
                dict.putAll(returnLocation(location));
            }

            dict.put("locAftHrsDO",location.isDropoffAfterHours() ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value); //After Hours Return Available
            dict.put("locSelectConflict", location.isLocationInvalid() ? EHIAnalytics.LocationConflict.CLOSED.value : EHIAnalytics.LocationConflict.NONE.value); //Selected Location Conflict
        }

        Location currentLocation = LocationApiManager.getInstance().getLastCurrentLocation();
        if (currentLocation != null) {
            dict.put("currLocLat", currentLocation.getLatitude() + "");
            dict.put("currLocLong", currentLocation.getLongitude() + "");
        }

        if (searchArea != null) {
            dict.put("locSearchArea", searchArea);
        }

        dict.put("locResultsNo", String.valueOf(itemCount)); //Number of Search Results
        dict.put("locClosedNo", String.valueOf(closedLocationsCount)); //Number of Closed Locations
        dict.put("locFilterType",filters); //Location Filter Type

        return dict;
    }

    public static Map<String, String> locationDetails(int flow, EHISolrLocation location) {
        Map<String, String> dict = new HashMap<>();
        if (flow == SearchLocationsActivity.FLOW_PICKUP_LOCATION_ROUND_TRIP
                && location != null) {
            dict.putAll(pickupLocation(location));
        }
        if (flow == SearchLocationsActivity.FLOW_DROP_OFF_LOCATION_ONE_WAY
                && location != null) {
            dict.putAll(returnLocation(location));
        }
        if (location != null) {
            if (LocationManager.getInstance().isFavoriteLocation(location.getPeopleSoftId())) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_FAVORITE.value);
            } else if (LocationManager.getInstance().isRecentLocation(location)) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_RECENT.value);
            }
        }
        return dict;
    }

    public static Map<String, String> policyDetails(String policyName) {
        Map<String, String> dict = new HashMap<>();
        if (policyName != null) {
            dict.put("policyName", policyName);
        }
        return dict;
    }

    public static Map<String, String> signIn() {
        Map<String, String> dict = new HashMap<>();
        if (LoginManager.getInstance().isLoggedIn()) {
            dict.put("AuthenticationInd", EHIAnalytics.Boolean.BOOL_YES.value);
            if (LoginManager.getInstance().getEncryptedCredentials() != null) {
                dict.put("keepSignin", EHIAnalytics.Boolean.BOOL_YES.value);
            } else {
                dict.put("keepSignin", EHIAnalytics.Boolean.BOOL_NO.value);
            }
            ProfileCollection profileCollection = LoginManager.getInstance().getProfileCollection();
            if (profileCollection != null && profileCollection.getProfile() != null) {
                if (profileCollection.getPreference() != null
                        && profileCollection.getPreference().getEmailPreference() != null
                        && profileCollection.getPreference().getEmailPreference().isSpecialOffers()) {
                    dict.put("emailExtrasInd", EHIAnalytics.Boolean.BOOL_YES.value);
                } else {
                    dict.put("emailExtrasInd", EHIAnalytics.Boolean.BOOL_NO.value);
                }
                if (profileCollection.getProfile().getCorporateAccount() != null) {
                    dict.put("customerType", EHIAnalytics.CustomerType.TYPE_CORPORATE.value);
                } else {
                    dict.put("customerType", EHIAnalytics.CustomerType.TYPE_INDIVIDUAL.value);
                }
            }
            EHINotification.NotificationTime notificationPickup = SettingsManager.getInstance().getPickupNotificationTime();
            EHINotification.NotificationTime notificationReturn = SettingsManager.getInstance().getReturnNotificationTime();
            if (!EHITextUtils.isEmpty(Integer.toString(notificationPickup.time))) {
                dict.put("remindpu", Integer.toString(notificationPickup.time));
            }
            if (!EHITextUtils.isEmpty(Integer.toString(notificationReturn.time))) {
                dict.put("reminddo", Integer.toString(notificationReturn.time));
            }
        } else {
            dict.put("AuthenticationInd", EHIAnalytics.Boolean.BOOL_NO.value);
        }
        return dict;
    }

    public static Map<String, String> call(String phoneType) {
        Map<String, String> dict = new HashMap<>();
        dict.putAll(signIn());
        dict.put("phoneType", phoneType);
        return dict;
    }

    public static Map<String, String> error(ResponseWrapper wrapper, AbstractRequestProvider provider) {
        final String url = provider.getEndpointUrl() + provider.getRequestUrl();
        Map<String, String> dict = new HashMap<>();
        dict.putAll(signIn());
        dict.put("errorMsg", wrapper.getMessage());
        if (!AnalyticsUtils.isSolrEndpoint(url)) {
            dict.put("errorEndpoint", AnalyticsUtils.maskUrl(url));
        }
        String code = wrapper.getCodes();
        if (!TextUtils.isEmpty(code)) {
            dict.put("errorCode", code);
        }
        dict.put("correlationId", provider.getCorrelationId());
        if (wrapper.getStatus() > 0) {
            dict.put("errorHttpStatusCode", String.valueOf(wrapper.getStatus()));
        }
        return dict;
    }

    public static Map<String, String> dateTime(EHISolrLocation pickupLocation, EHISolrLocation dropoffLocation, Date pickupDate, Date dropoffDate) {
        Map<String, String> dict = new HashMap<>();
        if (pickupLocation != null) {
            dict.putAll(pickupLocation(pickupLocation));
            if (LocationManager.getInstance().isFavoriteLocation(pickupLocation.getPeopleSoftId())) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_FAVORITE.value);
            } else if (LocationManager.getInstance().isRecentLocation(pickupLocation)) {
                dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_RECENT.value);
            }
        }

        if (pickupDate != null) {
            dict.put("puDate", sDateFormatter.format(pickupDate));
            dict.put("puTime", sTimeFormatter.format(pickupDate));
            dict.put("resLeadTime", leadTimeToDate(pickupDate) + "");
        }

        if (dropoffDate != null) {
            dict.put("doDate", sDateFormatter.format(dropoffDate));
            dict.put("doTime", sTimeFormatter.format(dropoffDate));
        }

        if (pickupLocation != null
                && dropoffLocation != null
                && !pickupLocation.getPeopleSoftId().equalsIgnoreCase(dropoffLocation.getPeopleSoftId())
                ) {
            dict.put("oneWayInd", EHIAnalytics.Boolean.BOOL_YES.value);
            dict.putAll(returnLocation(dropoffLocation));
        } else {
            dict.put("oneWayInd", EHIAnalytics.Boolean.BOOL_NO.value);
            dict.putAll(returnLocation(pickupLocation));
        }

        if (pickupDate != null
                && dropoffDate != null) {
            dict.put("lor", lengthOfRental(pickupDate, dropoffDate) + "");
        }
        return dict;
    }

    public static Map<String, String> reservation() {
        EHIReservation reservation = ReservationManager.getInstance().getCurrentReservation();
        return addReservationObject(reservation, ReservationManager.getInstance().isModify());
    }

    public static Map<String, String> confirmation(boolean isModify) {
        EHIReservation reservation = isModify ?
                ReservationManager.getInstance().getCurrentModifyReservation() :
                ReservationManager.getInstance().getCurrentReservation();
        return addReservationObject(reservation, isModify);
    }

    private static Map<String, String> addReservationObject(EHIReservation reservation, boolean isModify) {
        Map<String, String> dict = new HashMap<>();
        if (reservation != null) {

            if (reservation.getPickupLocation() != null) {
                dict.putAll(pickupLocation(reservation.getPickupLocation()));
                if (LocationManager.getInstance().isFavoriteLocation(reservation.getPickupLocation().getId())) {
                    dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_FAVORITE.value);
                } else if (LocationManager.getInstance().isRecentLocation(reservation.getPickupLocation().getId())) {
                    dict.put("locShortcut", EHIAnalytics.LocationShortcut.LOCATION_SHORTCUT_RECENT.value);
                }

                if (reservation.getPickupLocation().isMotorcycle()) {
                    //TODO add truck lob when this info will be available
                    dict.put("lob", EHIAnalytics.Lob.LOB_MOTORCYCLE.value);
                }
            }

            if (reservation.getReturnLocation() != null) {
                dict.putAll(returnLocation(reservation.getPickupLocation()));
            }

            if (reservation.getPickupLocation() != null
                    && reservation.getReturnLocation() != null
                    && !reservation.getPickupLocation().getId().equalsIgnoreCase(reservation.getReturnLocation().getId())) {
                dict.put("oneWayInd", EHIAnalytics.Boolean.BOOL_NO.value);
            } else {
                dict.put("oneWayInd", EHIAnalytics.Boolean.BOOL_YES.value);
            }

            if (reservation.getPickupTime() != null) {
                dict.put("puDate", sDateFormatter.format(reservation.getPickupTime()));
                dict.put("puTime", sTimeFormatter.format(reservation.getPickupTime()));
                dict.put("resLeadTime", leadTimeToDate(reservation.getPickupTime()) + "");
            }

            if (reservation.getReturnTime() != null) {
                dict.put("doDate", sDateFormatter.format(reservation.getReturnTime()));
                dict.put("doTime", sTimeFormatter.format(reservation.getReturnTime()));
            }

            if (reservation.getPickupTime() != null
                    && reservation.getReturnTime() != null) {
                dict.put("lor", lengthOfRental(reservation.getPickupTime(), reservation.getReturnTime()) + "");
            }

            if (reservation.getCarClassDetails() != null) {
                dict.put("carClass", reservation.getCarClassDetails().getCode());
            }

            final EHICarClassDetails carClassDetails = reservation.getCarClassDetails();

            if (carClassDetails != null
                    && carClassDetails.getVehicleRates() != null
                    && carClassDetails.getExtrasPricePortion(reservation.getPayState()) != null) {
                EHIExtras extras = reservation.getCarClassDetails().getExtrasPricePortion(reservation.getPayState());
                StringBuilder bld = new StringBuilder();
                for (EHIExtraItem item : extras.getSelectedExtras()) {
                    if (bld.length() != 0) {
                        bld.append(",");
                    }
                    bld.append(item.getCode());
                }
                dict.put("extrasList", bld.toString());
            }

            dict.put("paynowAvailable", carClassDetails != null && carClassDetails.isPrepayRateAvailable() ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);
            dict.put("payLaterAvailable", carClassDetails != null && carClassDetails.isPayLaterRateAvailable() ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);
            dict.put("redempAvailable", reservation.doesSelectedCarClassSupportRedemption() ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);

            if (reservation.getConfirmationNumber() != null) {
                dict.put("confNum", reservation.getConfirmationNumber());
            }

            if (reservation.getCorporateAccount() != null
                    && reservation.getCorporateAccount().getContractNumber() != null) {
                dict.put("contractId", reservation.getCorporateAccount().getContractNumber());
            }

            if (reservation.getRenterAge() != null) {
                dict.put("renterAge", reservation.getRenterAge().intValue() + "");
            }

            if (reservation.getCarClassDetails() != null
                    && reservation.getCarClassDetails().getPriceSummary() != null) {
                EHIPriceSummary summary = reservation.getCarClassDetails().getPriceSummary();
                dict.put("currencyType", summary.getEstimatedTotalView().getCurrencyCode());
                dict.put("resRevCurrency", summary.getEstimatedTotalView().getAmmount());
            }

            if (LoginManager.getInstance().isLoggedIn()) {
                dict.put("pointsView", LocalDataManager.getInstance().needShowPoints() ? "show" : "hide");
                if (reservation.getCarClassDetails() != null) {
                    dict.put("redemptionEnoughFor", reservation.getCarClassDetails().getMaxRedemptionDays() + "");

                    dict.put("redemptionDays", reservation.getCarClassDetails().getRedemptionDayCount() + "");
                    dict.put("redemptionPoints", reservation.getCarClassDetails().getEplusPointsUsed() + "");
                    if (lengthOfRental(reservation.getPickupTime(), reservation.getReturnTime())
                            <= reservation.getCarClassDetails().getRedemptionDayCount()) {
                        dict.put("redemptionPartialInd", EHIAnalytics.Boolean.BOOL_NO.value);
                    } else {
                        dict.put("redemptionPartialInd", EHIAnalytics.Boolean.BOOL_YES.value);
                    }
                }
            }

            if (isModify) {
                dict.put("transactionType", EHIAnalytics.ReservationType.RESERVATION_MODIFY.value);
            } else {
                dict.put("transactionType", EHIAnalytics.ReservationType.RESERVATION_ORIGINAL.value);
            }

            if (reservation.getRedemptionDayCount() == 0) {
                dict.put("upgradeDisplayInd", EHIAnalytics.Boolean.BOOL_YES.value);
                if (ReservationManager.getInstance().getCarUpgradeSelection() != null
                        && reservation.getCarClassDetails() != null
                        && reservation.getCarClassDetails().getCode().equals(ReservationManager.getInstance().getCarUpgradeSelection().getCode())) {
                    dict.put("upgradeSelected", EHIAnalytics.Boolean.BOOL_YES.value);
                    dict.put("upgradeValue", ReservationManager.getInstance().getUpgradeAmount());
                } else {
                    dict.put("upgradeSelected", EHIAnalytics.Boolean.BOOL_NO.value);
                }
            } else {
                dict.put("upgradeDisplayInd", EHIAnalytics.Boolean.BOOL_NO.value);
            }
            if (reservation.isPrepaySelected()) {
                dict.put("paymentType", EHIAnalytics.PaymentType.PAYMENT_TYPE_PAY_NOW.value);
            } else {
                dict.put("paymentType", EHIAnalytics.PaymentType.PAYMENT_TYPE_PAY_LATER.value);
            }

            if (reservation.getCancellationDetails() != null && reservation.getCancellationDetails().getCancelFeeAmountView() != null) {
                dict.put("cancellationFee", reservation.getCancellationDetails().getCancelFeeAmountView().getAmmount());
            }
        }

        return dict;
    }

    private static int leadTimeToDate(Date date) {
        long millisToRent = date.getTime() - System.currentTimeMillis();
        if (millisToRent > 0) {
            return (int) (millisToRent / (24 * 60 * 60 * 1000));
        } else {
            return 0;
        }
    }

    public static int lengthOfRental(Date pickup, Date dropoff) {
        long millisForRent = dropoff.getTime() - pickup.getTime();
        int daysForRent = (int) (millisForRent / (24 * 60 * 60 * 1000));
        if (daysForRent == 0) { // less then a full day would think it's one day
            return 1;
        }
        return daysForRent;
    }

    public static Map<String, String> driverInfo(boolean saveInfo) {
        Map<String, String> dict = new HashMap<>();
        dict.putAll(reservation());
        dict.put("saveInfo", saveInfo ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);
        return dict;
    }

    public static Map<String, String> modalLaunch(String subject) {
        Map<String, String> dict = new HashMap<>();
        dict.putAll(reservation());
        dict.put("modalSubject", subject);
        return dict;
    }

    /**
     * Use it only for screen opening tracking
     */
    public static Map<String, String> review() {
        Map<String, String> dict = new HashMap<>();
        dict.putAll(reservation());
        if (LoginManager.getInstance().isLoggedIn()) {
            ProfileCollection profile = LoginManager.getInstance().getProfileCollection();
            if (profile.getProfile() != null && profile.getPreference() != null
                    && profile.getPreference().getEmailPreference() != null) {
                dict.put("emailOptIn", profile.getPreference().getEmailPreference().isSpecialOffers() ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);
            }
        } else {
            EHIDriverInfo driver = ReservationManager.getInstance().getDriverInfo();
            if (driver != null) {
                dict.put("emailOptIn", driver.hasRequestedEmailPromotions() ? EHIAnalytics.Boolean.BOOL_YES.value : EHIAnalytics.Boolean.BOOL_NO.value);
            }
        }
        return dict;
    }

    public static Map<String, String> confirmation(EHIReservation reservationObject, boolean isModify) {
        return addReservationObject(reservationObject, isModify);
    }

    public static Map<String, String> enroll(String mode, String country) {
        Map<String, String> dict = new HashMap<>();
        dict.put("mode", mode);
        addCountry(country, dict);
        return dict;
    }

    public static Map<String, String> termsAndConditions(String country) {
        Map<String, String> dict = new HashMap<>();
        addCountry(country, dict);
        return dict;
    }

    private static void addCountry(String country, Map<String, String> dict) {
        if (!EHITextUtils.isEmpty(country)) {
            dict.put("countryCode", country);
        }
    }

    public static Map<String, String> enroll(String mode, EHICountry country) {
        return enroll(mode, country == null ? null : country.getCountryCode());
    }

    public static Map<String, String> rewardsDict() {

        final Map<String, String> dict = new HashMap<>();

        final ProfileCollection profileCollection = LoginManager.getInstance().getProfileCollection();
        if (profileCollection != null) {
            if (profileCollection.getBasicProfile() != null && profileCollection.getBasicProfile().getLoyaltyData() != null) {
                EHILoyaltyData ehiLoyaltyData = LoginManager.getInstance().getProfileCollection().getBasicProfile().getLoyaltyData();
                dict.put("eplusPoints", String.valueOf(ehiLoyaltyData.getPointsToDate()));
                if (!EHITextUtils.isEmpty(ehiLoyaltyData.getLoyaltyTier())) {
                    dict.put("eplusTier", ehiLoyaltyData.getLoyaltyTier());
                }
            }
            EHILicenseProfile ehiLicenseProfile = profileCollection.getLicenseProfile();
            if (ehiLicenseProfile != null && ehiLicenseProfile.getCountryCode() != null) {
                addCountry(ehiLicenseProfile.getCountryCode(), dict);
            }
        } else {
            addCountry(LocalDataManager.getInstance().getPreferredCountryCode(), dict);
        }
        return dict;
    }

    public static Map<String, String> debugPushDict(String debugKey) {
        final Map<String, String> dict = new HashMap<>();
        dict.put("debugKey", debugKey);
        return dict;
    }
}

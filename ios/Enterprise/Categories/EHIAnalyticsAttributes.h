//
//  EHIAnalyticsAttributes.h
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#
# pragma mark - General
#

#define EHIAnalyticsTrueValue  @"Y"
#define EHIAnalyticsFalseValue @"N"

#
# pragma mark - Custom Dimensions
#

#define EHIAnalyticsDimensionVisitorTypeMember    @"Member"
#define EHIAnalyticsDimensionVisitorTypeCorp      @"Corp"
#define EHIAnalyticsDimensionVisitorTypeGuest     @"Guest"

#define EHIAnalyticsDimensionMemberTypeLoyalty    @"EP"
#define EHIAnalyticsDimensionMemberTypeLoyaltyAlt @"EC"
#define EHIAnalyticsDimensionMemberTypeGuest      @"Guest"

#define EHIAnalyticsDimensionFallbackValue        @"Not available"

#
# pragma mark - Screen
#

#define EHIAnalyticsPreviousScreenKey @"previousScreenName"
#define EHIAnalyticsCurrentScreenKey  @"currentScreenName"
#define EHIAnalyticsScreenUrlKey      @"screenUrl"

#
# pragma mark - Actions
#

#define EHIAnalyticsActionTypeKey @"screenActionType"
#define EHIAnalyticsActionNameKey @"screenActionName"

#
# pragma mark - Prefixes
#

#define EHIAnalyticsCurrentPrefix @"curr"
#define EHIAnalyticsPickupPrefix  @"pu"
#define EHIAnalyticsDropoffPrefix @"do"

#
# pragma mark - Dashboard
#

#define EHIAnalyticsDashStatusKey @"rentalStatus"
#define EHIAnalyticsDashDaysUntilRentalKey @"daysUntilRental"

#
# pragma mark - Location
#

#define EHIAnalyticsLocLatitudeKey              @"LocLat"
#define EHIAnalyticsLocLongitudeKey             @"LocLong"
#define EHIAnalyticsLocNameKey                  @"LocEngName"
#define EHIAnalyticsLocTypeKey                  @"LocType"
#define EHIAnalyticsLocIdKey                    @"LocId"
#define EHIAnalyticsLocCountryKey               @"CountryCode"

#define EHIAnalyticsLocTypeBranch               @"Branch"
#define EHIAnalyticsLocTypeAirport              @"Airport"
#define EHIAnalyticsLocTypeRail                 @"Rail"
#define EHIAnalyticsLocTypePort                 @"Port"

#define EHIAnalyticsLocOneWayKey                @"oneWayInd"
#define EHIAnalyticsLocQueryKey                 @"locKeyword"
#define EHIAnalyticsLocSearchAreaKey            @"locSearchArea"
#define EHIAnalyticsLocZeroKey                  @"locSearchZero"
#define EHIAnalyticsLocSearchResult             @"locResultsNo"
#define EHIAnalyticsLocAfterHoursAvailable      @"locAftHrsDO"
#define EHIAnalyticsLocClosedLocations          @"locClosedNo"
#define EHIAnalyticsLocRankKey                  @"locSearchPosition"
#define EHIAnalyticsLocShortcutKey              @"locShortcut"
#define EHIAnalyticsLocPolicyKey                @"policyName"
#define EHIAnalyticsLocConflict                 @"locSelectConflict"
#define EHIAnalyticsLocClosed                   @"Closed"
#define EHIAnalyticsLocNone                     @"None"
#define EHIAnalyticsListCollapsed               @"IsCollapsed"

#define EHIAnalyticsLocShortcutNearby           @"Nearby"
#define EHIAnalyticsLocShortcutRecent           @"Recent"
#define EHIAnalyticsLocShortcutFavorite         @"Favorite"

#
# pragma mark - Reservation
#

#define EHIAnalyticsResDateKey        @"Date"
#define EHIAnalyticsResTimeKey        @"Time"
#define EHIAnalyticsResLengthKey      @"lor"
#define EHIAnalyticsResLeadTimeKey    @"resLeadTime"

#define EHIAnalyticsResCarClassKey              @"carClass"
#define EHIAnalyticsResExtrasKey                @"extrasList"
#define EHIAnalyticsResTappedExtraKey           @"tappedExtra"
#define EHIAnalyticsResConfNumberKey            @"confNum"
#define EHIAnalyticsResLineOfBizKey             @"lineOfBusiness"
#define EHIAnalyticsResCurrencyKey              @"currencyType"
#define EHIAnalyticsResPriceKey                 @"resRevCurrency"
#define EHIAnalyticsResContractKey              @"contractId"
#define EHIAnalyticsResRenterAgeKey             @"renterAge"
#define EHIAnalyticsResTransactionType          @"transactionType"
#define EHIAnalyticsResPaymentTypeKey           @"paymentType"
#define EHIAnalyticsResPayNowAvailableKey       @"PaynowAvailable"
#define EHIAnalyticsResPayLaterAvailableKey     @"PayLaterAvailable"
#define EHIAnalyticsResRedemptionAvailableKey   @"RedempAvailable"
#define EHIAnalyticsResCancellationFeeKey       @"CancellationFee"

#define EHIAnalyticsResRedemptionShowPointsKey @"pointsView"
#define EHIAnalyticsResRedemptionDaysKey       @"redemptionDays"
#define EHIAnalyticsResRedemptionPointsKey     @"redemptionPoints"
#define EHIAnalyticsResRedemptionPartialKey    @"redemptionPartialInd"
#define EHIAnalyticsResRedemptionMaxDaysKey    @"redemptionEnoughFor"

#define EHIAnalyticsResUpgradeDisplayKey       @"upgradeDisplayInd"
#define EHIAnalyticsResUpgradeValueKey         @"upgradeValue"
#define EHIAnalyticsResUpgradeSelectedKey      @"upgradeSelected"


#
# pragma mark - Sign-in
#

#define EHIAnalyticsUserKeepSigninKey    @"keepSignin"
#define EHIAnalyticsUserAuthenticatedKey @"AuthenticationInd"
#define EHIAnalyticsUserEmailExtrasKey   @"emailExtrasInd"
#define EHIAnalyticsUserCustomerTypeKey  @"customerType"
#define EHIAnalyticsUserSupportNumberKey @"supportNumberType"
#define EHIAnalyticsUserSettingsNotificationPickupKey  @"remindpu"
#define EHIAnalyticsUserSettingsNotificationDropoffKey @"reminddo"

#
# pragma mark - Rewards
#

#define EHIAnalyticsRewardsCountryKey   @"countryCode"
#define EHIAnalyticsRewardsTierKey      @"eplusTier"
#define EHIAnalyticsRewardsPointsKey    @"eplusPoints"

#
# pragma mark - Enrollment
#

#define EHIAnalyticsEnrollmentProfileMatchKey       @"mode"
#define EHIAnalyticsEnrollmentProfileCountryCodeKey @"countryCode"

#
# pragma mark - Filters
#

#define EHIAnalyticsFilterTypeKey                           @"filterType"
#define EHIAnalyticsFilterListKey                           @"filterList"
#define EHIAnalyticsFilterLocationTypeKey                   @"locFilterType"
#define EHIAnalyticsFilterNoneKey                           @"none"
#define EHIAnalyticsFilterPickupDateKey                     @"Pick Up Date"
#define EHIAnalyticsFilterPickupTimeKey                     @"Pick Up Time"
#define EHIAnalyticsFilterDropoffDateKey                    @"Drop Off Date"
#define EHIAnalyticsFilterDropoffTimeKey                    @"Drop Off Time"
#define EHIAnalyticsFilterReturnBeforePickUpMessageDisplay  @"DOBeforePU"

#
# pragma mark - Call Support
#

#define EHIAnalyticsPhoneTypeKey        @"phoneType"
#define EHIAnalyticsPhoneTypeCallCenter @"callCenter"
#define EHIAnalyticsPhoneTypeRoadside   @"roadside"
#define EHIAnalyticsPhoneTypePickup     @"pickupLocation"
#define EHIAnalyticsPhoneTypeDropoff    @"dropoffLocation"

#
# pragma mark - Misc
#

#define EHIAnalyticsErrorDataKey            @"errorData"
#define EHIAnalyticsErrorMessageKey         @"errormsg"
#define EHIAnalyticsErrorCodeKey            @"errorCode"
#define EHIAnalyticsErrorHTTPCodeKey        @"errorHttpStatusCode"
#define EHIAnalyticsErrorEndpoint           @"errorEndpoint"
#define EHIAnalyticsServiceCorrelationIdKey @"CorrelationId"
#define EHIAnalyticsModalSubjectKey         @"modalSubject"

#
# pragma mark - Debug
#

#define EHIAnalyticsDebugKey @"debugKey"

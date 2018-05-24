//
//  EHIAnalyticsContext+Mappings.m
//  Enterprise
//
//  Created by Ty Cobb on 5/29/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIAnalyticsContext+Mappings.h"
#import "EHIMapTransformer.h"
#import "EHIReservationRouter.h"

#define EHIAnalyticsScreenTransformerName @"EHIAnalyticsScreenTransformer"
#define EHIAnalyticsStateTransformerName @"EHIAnalyticsStateTransformer"

NS_ASSUME_NONNULL_BEGIN

@implementation EHIAnalyticsContext (Mappings)

- (NSString *)screenFromRouterScreen:(NSString *)screen
{
    NSString *tag = [self.analyticsScreenTransformer transformedValue:screen];
    return tag;
}

- (NSString *)stateFromRouterScreen:(NSString *)screen
{
    NSString *tag = [self.analyticsStateTransformer transformedValue:screen];
    return tag;
}

- (nullable NSString *)stringFromActionType:(EHIAnalyticsActionType)type
{
    switch(self.actionType) {
        case EHIAnalyticsActionTypeTap:
            return @"Tap";
        case EHIAnalyticsActionTypeType:
            return @"Type";
        case EHIAnalyticsActionTypeScroll:
            return @"Scroll";
        case EHIAnalyticsActionTypeError:
            return @"Error";
        default: return nil;
    }
}

# pragma mark - Transformers

- (NSValueTransformer *)analyticsScreenTransformer
{
    NSValueTransformer *transformer = [NSValueTransformer valueTransformerForName:EHIAnalyticsScreenTransformerName];
    
    // create the transformer and store it for later if it doesn't exist
    if(!transformer) {
        transformer = [[EHIMapTransformer alloc] initWithMap:@{
            EHIScreenDashboard                      : @"Dashboard",
            EHIScreenMenu                           : @"Menu",
            EHIScreenPromotionDetails               : @"PromotionDetail",
            EHIScreenLocations                      : @"Locations",
            EHIScreenLocationSearchNoResult         : @"Locations",
            EHIScreenReservationItinerary           : @"Reservation",
            EHIScreenReservationReview              : @"Reservation",
            EHIScreenConfirmation                   : @"Reservation",
            EHIScreenReservationRedemption          : @"Reservation",
            EHIScreenPayment                        : @"Reservation",
            EHIScreenSelectPayment                  : @"SelectCreditCard",
            EHIScreenConfirmationCancelReservation  : @"Reservation",
            EHIScreenReservationCalendar            : @"DateTime",
            EHIScreenReservationTimeSelect          : @"DateTime",
            EHIScreenReservationPinAuthentication   : @"Corporate",
            EHIScreenReservationAdditionalInfo      : @"Corporate",
            EHIScreenReservationClassSelect         : @"Class",
            EHIScreenReservationRateSelect          : @"Rates",
            EHIScreenReservationExtras              : @"Extras",
            EHIScreenSignin                         : @"Signin",
            EHIScreenForgotPassword                 : @"Signin",
            EHIScreenSigninEmerald                  : @"Signin",
            EHIScreenEnrollmentStepOne              : @"EnrollModalStep1",
            EHIScreenEnrollmentStepTwo              : @"EnrollModalStep2",
            EHIScreenEnrollmentStepThree            : @"EnrollModalStep3",
            EHIScreenEnrollmentConfirmation         : @"EnrollModalConfirmation",
            EHIScreenEnrollmentIssues               : @"EnrollLongForm",
            EHIScreenProfile                        : @"MyProfile",
            EHIScreenInvoiceDetails                 : @"MyRentals",
            EHIScreenRentals                        : @"MyRentals",
            EHIScreenSettings                       : @"Settings",
            EHIScreenProfileEditPaymentMethods      : @"Edit Payments",
            EHIScreenProfileEditPaymentCard         : @"EditCreditCard",
            EHIScreenProfileEditPaymentBilling      : @"EditBillingNumber",
            EHIScreenRemoveBillingModal             : @"BillingCodeDeleteValidation",
            EHIScreenLocationLockedModal            : @"LocationCantbeModified",
            EHIScreenTermsAndConditions             : @"TermsandConditions",
            EHIScreenSurvey                         : @"Foresee",
            EHIScreenSurveyInvite                   : @"Foresee",
            EHIScreenRewardsBenefitsAuth            : @"RewardsAuth",
            EHIScreenAboutEnterprisePlus            : @"RewardsAuth",
            EHIScreenAboutPointsScreen              : @"RewardsAuth",
            EHIScreenRewardsAboutTiers              : @"RewardsAuth",
            EHIScreenRewardsLearnMore               : @"RewardsUnauth",
            EHIScreenOnboarding                     : @"Welcome",
            EHIScreenCustomerSupport                : @"Help&CustomerSupport",
            EHIScreenDebug                          : @"Debug"
        }];
        
        [NSValueTransformer setValueTransformer:transformer forName:EHIAnalyticsScreenTransformerName];
    }
    
    return transformer;
}

- (NSValueTransformer *)analyticsStateTransformer
{
    NSValueTransformer *transformer = [NSValueTransformer valueTransformerForName:EHIAnalyticsStateTransformerName];
    
    // create the transformer and store it for later if it doesn't exist
    if(!transformer) {
        transformer = [[EHIMapTransformer alloc] initWithMap:@{
            EHIScreenLocations                      : @"Search",
            EHIScreenLocationsMap                   : @"Map",
            EHIScreenLocationsList                  : @"List",
            EHIScreenLocationFilter                 : @"SearchFilter",
            EHIScreenLocationSearchNoResult         : @"SearchFilterNoResults",
            EHIScreenLocationDetails                : @"Details",
            EHIScreenReservationItinerary           : @"Widget",
            EHIScreenReservationCalendar            : @"Calendar",
            EHIScreenReservationTimeSelect          : @"TimeSelect",
            EHIScreenReservationPinAuthentication   : @"Pin",
            EHIScreenReservationAdditionalInfo      : @"AdditionalInfo",
            EHIScreenReservationClassSelect         : @"ListCurrency",
            EHIScreenReservationClassDetails        : @"DetailsCurrency",
            EHIScreenReservationClassSelectFilter   : @"Filter",
            EHIScreenConfirmation                   : @"Confirmation",
            EHIScreenReservationRateSelect          : @"ChooseRate",
            EHIScreenProfileEditPaymentMethods      : @"Summary",
            EHIScreenProfileEditPaymentCard         : @"Summary",
            EHIScreenProfileEditPaymentBilling      : @"Summary",
            EHIScreenReservationExtras              : @"Summary",
            EHIScreenSettings                       : @"Summary",
            EHIScreenReservationFees                : @"Fees",
            EHIScreenReservationReview              : @"Review",
            EHIScreenPayment                        : @"AddCreditCard",
            EHIScreenPaymentMethod                  : @"PaymentMethod",
            EHIScreenSelectPayment                  : @"CCDetails",
            EHIScreenReservationRedemption          : @"Redeem",
            EHIScreenReservationDriverInfo          : @"DriverInfo",
            EHIScreenReservationFlightDetails       : @"FlightInfo",
            EHIScreenPolicies                       : @"Policies",
            EHIScreenPolicyDetail                   : @"Policy",
            EHIScreenMenu                           : @"Home",
            EHIScreenSignin                         : @"Home",
            EHIScreenLocationLockedModal            : @"Home",
            EHIScreenProfile                        : @"Home",
            EHIScreenRewardsBenefitsAuth            : @"Home",
            EHIScreenRewardsLearnMore               : @"Home",
            EHIScreenEnrollmentStepOne              : @"None",
            EHIScreenForgotPassword                 : @"ForgotPassword",
            EHIScreenSigninEmerald                  : @"ECSignin",
            EHIScreenMemberInfoEdit                 : @"EditMemberInfo",
            EHIScreenLicenseEdit                    : @"EditDriverInfo",
            EHIScreenRentalLookup                   : @"LookupRental",
            EHIScreenPromotionDetails               : @"WeekendSpecial",
            EHIScreenInvoiceDetails                 : @"PastTripsReceipt",
            EHIScreenTermsAndConditions             : @"Modal",
            EHIScreenRemoveBillingModal             : @"Modal",
            EHIScreenSurvey                         : @"SurveyInvite",
            EHIScreenSurveyInvite                   : @"Banner",
            EHIScreenConfirmationCancelReservation  : @"Cancellation",
            EHIScreenAboutEnterprisePlus            : @"EPProgramDetails",
            EHIScreenAboutPointsScreen              : @"AboutPoints",
            EHIScreenRewardsAboutTiers              : @"AboutTierBenefits",
        }];
        
        [NSValueTransformer setValueTransformer:transformer forName:EHIAnalyticsStateTransformerName];
    }
    
    return transformer;
}

@end

NS_ASSUME_NONNULL_END

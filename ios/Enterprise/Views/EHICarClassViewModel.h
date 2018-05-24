//
//  EHIReservationClassSelectCellViewModel.h
//  Enterprise
//
//  Created by Alex Koller on 4/28/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"
#import "EHICarClass.h"
#import "EHIReservationPriceButtonType.h"

typedef NS_ENUM(NSUInteger, EHICarClassLayout) {
    EHICarClassLayoutClassDetails,
    EHICarClassLayoutClassSelect,
    EHICarClassLayoutExtrasPlaceholder,
    EHICarClassLayoutExtras,
    EHICarClassLayoutRate
};

@interface EHICarClassViewModel : EHIReservationStepViewModel <MTRReactive>
/** Price context model for the price button */
@property (strong, nonatomic) id<EHIPriceContext> price;
/** Price type for the price button */
@property (assign, nonatomic) EHIReservationPriceButtonType priceType;
/** Make/Model title (context dependent) */
@property (copy  , nonatomic) NSString *makeModelTitle;
/** Vehicle image model for the image view */
@property (copy  , nonatomic) EHIImage *vehicleImage;
/** Name of the car class */
@property (copy  , nonatomic) NSString *carClassName;
/** Title for the button that navigates to the details screen */
@property (copy  , nonatomic) NSString *detailsButtonTitle;
/** Title for the previous selection title */
@property (copy  , nonatomic) NSString *previousSelectionTitle;
/** Transmission type display name */
@property (copy  , nonatomic) NSString *transmissionTypeName;
/** Title for a special rate if it exists */
@property (copy  , nonatomic) NSString *rateTitle;
/** Title for price on extras screen */
@property (copy  , nonatomic) NSAttributedString *extrasPrice;
/** Title for amount of available redemption days */
@property (copy  , nonatomic) NSAttributedString *freeDaysTitle;
/** Title for rate of car in points */
@property (copy  , nonatomic) NSAttributedString *pointsPerDayTitle;
/** @c YES if the car class has an automatic transmission */
@property (assign, nonatomic) BOOL isAutomaticTransmission;
/** @c YES if the user needs to call for availability */
@property (assign, nonatomic) BOOL requiresCallForAvailability;
/** @c YES if the user must book car class on web */
@property (assign, nonatomic) BOOL requiresWebBook;
/** @c YES if the previously selected car class indicator should be shown*/
@property (assign, nonatomic) BOOL showPreviouslySelectedHeader;
/** @c Yes if user is in scenario with Secret Rate */
@property (assign, nonatomic) BOOL showSecretRate;

/** Context information */
@property (assign, nonatomic) EHICarClassLayout layout;
/** Context information which shows/hides the details view */
@property (assign, nonatomic) BOOL showsDetailsView;
/** Context information which shows/hides the redemption view */
@property (assign, nonatomic) BOOL hidesRedemption;

@end

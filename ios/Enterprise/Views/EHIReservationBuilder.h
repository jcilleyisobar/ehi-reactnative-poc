//
//  EHIReservationBuilder.h
//  Enterprise
//
//  Created by mplace on 3/9/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIViewModel.h"
#import "EHIReservation.h"
#import "EHIReservationSchedulingStep.h"
#import "EHILocationsSearchType.h"
#import "EHIUserLocation.h"
#import "EHIDriverInfo.h"
#import "EHIServices+Reservation.h"
#import "EHIReservationStep.h"
#import "EHIReservationBuilderFlow.h"

@protocol EHIReservationBuilderReadinessListener;

@interface EHIReservationBuilder : EHIViewModel <MTRReactive>

/** The already constructed reservation, if any; won't exist until after initiate */
@property (strong, nonatomic) EHIReservation *reservation;
/** A reservation copy to be persisted, with locations object from Solr */
@property (strong, nonatomic, readonly) EHIReservation *targetSavingReservation;

/** The current location search type */
@property (assign, nonatomic) EHILocationsSearchType currentSearchType;
/** Overrides the current search type when the builder is active */
@property (assign, nonatomic) EHILocationsSearchType searchTypeOverride;
/** The currently selected pickup location */
@property (strong, nonatomic, readonly) EHILocation *pickupLocation;
/** The currently selected return location */
@property (strong, nonatomic, readonly) EHILocation *returnLocation;

/** Date representing the month-day-year portion of the pickup time */
@property (strong, nonatomic) NSDate *pickupDate;
/** Date representing the month-day-year portion of the return time */
@property (strong, nonatomic) NSDate *returnDate;
/** Date representing the time portion of the pickup time */
@property (strong, nonatomic) NSDate *pickupTime;
/** Date representing the time portion of the return time */
@property (strong, nonatomic) NSDate *returnTime;
/** Aggregate date that includes both the pickup date and time */
@property (nonatomic, readonly) NSDate *aggregatePickupDate;
/** Aggregate date that includes both the return date and time */
@property (nonatomic, readonly) NSDate *aggregateReturnDate;
/** @c YES if the a reservation is currently being modified */
@property (assign   ,nonatomic) BOOL reservationIsModified;
/** @c YES if the current reservation is for an Emerald Club user */
@property (assign, nonatomic) BOOL isEmeraldReservation;
/** Driver info model assosciated with reservation */
@property (strong, nonatomic) EHIDriverInfo *driverInfo;
/** Airline details entered by user */
@property (strong, nonatomic) EHIAirline *airline;
/** The model assosciated with the user's currently applied discount */
@property (strong, nonatomic) EHIContractDetails *discount;
/** The discount code assosciated with this reservation */
@property (copy  , nonatomic) NSString *discountCode;
/** The selected age of the renter */
@property (strong, nonatomic) EHILocationRenterAge *renterAge;

/** Travel purpose */
@property (assign, nonatomic) EHIReservationTravelPurpose travelPurpose;
/** Payment method selected by user */
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
/** @c YES if travel purpose was prompted and selected pre-rates */
@property (assign, nonatomic) BOOL travelPurposeSelectedPreRates;

/** PIN Authentication */
@property (copy  , nonatomic) NSString *pinAuth;

/** @c YES if all redemption related content should be hidden */
@property (assign, nonatomic, readonly) BOOL hideRedemption;
/** @c YES if redemption points should be hidden */
@property (assign, nonatomic) BOOL hidePoints;
/** The number of days the user has chosen to redeem */
@property (assign, nonatomic, readonly) NSInteger daysRedeemed;
/** The number of points the user has chosen to spend */
@property (assign, nonatomic, readonly) NSInteger pointsUsed;
/** @c YES if the pickup location is an airport with more than one location */
@property (assign, nonatomic, readonly) BOOL promptsMultiTerminal;

/** Indicates our current point in the scheduling process */
@property (assign, nonatomic) EHIReservationSchedulingStep currentSchedulingStep;
/** @c YES if we are actively building a reservation (i.e. the reservation modal is presented) */
@property (assign, nonatomic) BOOL isActive;

/** @c YES if user entered a valid credit card for prepay on the review screen */
@property (assign, nonatomic) BOOL creditCardAdded; // TODO:<THIS IS ONLY A TEMPORARY SOLUTION>

/** Returns the shared builder instance */
+ (instancetype)sharedInstance;

/** Updates the builder with the reservation and launches the reservation modal */
- (void)restartReservation:(EHIReservation *)resevation;
/** Launches the modify flow for this reservation while resetting server state */
- (void)modifyReservation:(EHIReservation *)reservation handler:(void(^)(EHIServicesError *error))handler;
/** Prompts the user, and cancels the in-progress reservation if accepted */
- (void)cancelReservation;

/** Navigates back to a prior reservation step to allow editing (destructive) */
- (void)editInfoForReservationStep:(EHIReservationStep)step;

/** Updates the correct location based on @c currentSearchType, and initiates the proper navigation transition */
- (void)selectLocation:(EHILocation *)location;

/** @c YES if the builder has enough data to move to this @c step */
- (BOOL)canTransitionToSchedulingStep:(EHIReservationSchedulingStep)step;
/** Transitions back to the @c step if possible */
- (void)transitionBackToSchedulingStep:(EHIReservationSchedulingStep)step;
/** Shows the toast message, if any, for the current scheduling step */
- (void)showToastForCurrentSchedulingStep;

/** Prompt user to confirm selection with input returned via @c handler */
- (void)promptOnRequestSelectionWithHandler:(void (^)(BOOL shouldContinue))handler;

/** Retrieves the additional info stored for the given key */
- (EHIContractAdditionalInfoValue *)additionalInfoForKey:(NSString *)key;
/** Sets the additional info stored for the given key */
- (void)setAdditionalInfo:(id)info forKey:(NSString *)key;
/** Clean all additional data */
- (void)resetAdditionalData;
/** Clean up the pre-rate data, namely: additional information and PIN number */
- (void)resetPreRateData;
/** Return the original car class price on the modify flow*/
- (EHIPrice *)originalPricePrepaySelected:(BOOL)prepay;
/** Wipe all the data */
- (void)resetData;
/** Return the pickup location on the modify flow */
- (EHILocation *)modifiedReservationPickupLocation;

@end

@interface EHIReservationBuilder (Services)

/** Wraps the initiate request and cancels it appropriately if the reservation is dismissed */
- (void)initiateReservationWithHandler:(void(^)(EHIServicesError *error))handler;
/** Wraps the commit request and cancels it appropriately if the reservation is dismissed */
- (void)commitOrModifyReservationWithHandler:(void (^)(EHIServicesError *))handler;
/** Wraps all payment related calls for attaching a credit card to a reservation */
- (void)submitCreditCard:(EHICreditCard *)creditCard handler:(void (^)(id response, EHIServicesError *))handler;
/** Wraps the modify LDT request and updates the reservation object */
- (void)modifyLocationDateTimeWithHandler:(EHIReservationHandler)handler;
/** Wraps the commit request and does a 3ds validation if necessary and cancels it appropriate if the reservation is dismissed */
- (void)commitReservationWith3DSCheck:(BOOL)check3DS handler:(void (^)(EHIServicesError *))handler;
/** Wraps the services cancel request and properly refreshes rentals when complete */
- (void)cancelReservation:(EHIReservation *)reservation handler:(void(^)(EHIServicesError *error))handler;

@end

@interface EHIReservationBuilder (Accessors)

/** The list of @c EHICarClass on the current reservation */
@property (nonatomic, readonly) NSArray *carClasses;
/** The list of @c EHICarClassFilters on the current reservation */
@property (nonatomic, readonly) NSArray *carClassesFilters;
/** The selected car class for the current reservation */
@property (nonatomic, readonly) EHICarClass *selectedCarClass;
/** The upgradable car class for the current reservation */
@property (nonatomic, readonly) EHICarClass *upgradeCarClass;
/** The total price for the current reservation */
@property (nonatomic, readonly) id<EHIPriceContext> totalPrice;
/** The price difference for prepay for the current reservation */
@property (nonatomic, readonly) EHICarClassPriceDifference *priceDifferencePrepay;
/** The list of selected @c EHICarClassExtra for the current reservation */
@property (nonatomic, readonly) NSArray *selectedExtras;
/** The list of selected @c EHICarClassPriceLineItem for the current reservation */
@property (nonatomic, readonly) NSArray *selectedLineItems;
/** The maximum number of redeemable days allowed */
@property (nonatomic, readonly) NSInteger maxRedemptionDays;
/** @c YES if the current reservation is one way */
@property (nonatomic, readonly) BOOL isOneWayReservation;
/** @c YES if @c isOneWayReservation or the user is currently picking the return location */
@property (nonatomic, readonly) BOOL isPickingOneWayReservation;
/** @c YES if the reservation's current pickup location allows one way */
@property (nonatomic, readonly) BOOL allowsOneWayReservation;
/** @c YES if the current set of properties are sufficient to createa a reservation */
@property (nonatomic, readonly) BOOL canInitiateReservation;
/** @c YES if the current set of properties are sufficient to modify the location of a reservation */
@property (nonatomic, readonly) BOOL canModifyLocation;
/** @c YES if the a reservation is currently being modified */ 
@property (nonatomic, readonly) BOOL isModifyingReservation;
/** @c YES if the reservation allows vehicle upgrades to be made */
@property (nonatomic, readonly) BOOL allowsVehicleUpgrade;
/** @c YES if upgraded vehicles have been fetched for this reservation */
@property (nonatomic, readonly) BOOL hasUpgradedVehicles;
/** @c YES if in the European Union */
@property (nonatomic, readonly) BOOL isEuropeanUnion;
/** The type of flow for the current reservation */
@property (nonatomic, readonly) EHIReservationBuilderFlow currentFlow;
/** @c YES if original reservation is a prepay payment */
@property (nonatomic, readonly)  BOOL reservationWasPrepay;

@end

@interface EHIReservationBuilder (Listeners)

/**
 @brief Add a listener to to the builder
 
 If the builder is not yet ready, the listeners are stored and notified once it becomes
 ready. If the build is already ready, the listener is notified immediately.
 
 The builder is considered ready @em only during transitions, and is @em not ready after
 the new screen is visible. Thus, subviews that appear after the screen becomes visible
 should not add themselves as listeners.
*/

- (void)waitForReadiness:(id<EHIReservationBuilderReadinessListener>)listener;

/**
 @brief Tells the builder that it's not ready to receive new reactions
 
 During this time of non-readiness, listeners will be stored and called when
 the builder becomes ready next.
*/

- (void)resignReady;

/**
 @brief Tells the builder that it's ready
 
 At this point, any registered listeners are notified that the builder is ready. After the
 builder has become ready, future listeners are called immediately.
 */

- (void)becomeReady;

@end

@protocol EHIReservationBuilderReadinessListener <NSObject>

/**
 @brief Hook that's called when the builder is ready to receive reactions
 
 View models that want to register reactions against the builder should add themselves
 as listeners via @c addListener:.
 
 @param builder The builder that became ready
*/

- (void)builderIsReady:(EHIReservationBuilder *)builder;

@end


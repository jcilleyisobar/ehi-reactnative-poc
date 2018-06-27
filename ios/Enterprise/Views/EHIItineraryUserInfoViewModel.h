//
//  EHIItineraryUserInfoViewModel.h
//  Enterprise
//
//  Created by mplace on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel.h"

@interface EHIItineraryUserInfoViewModel : EHIReservationStepViewModel <MTRReactive>

/** The user-entered discount code to be applied to the reservation */
@property (copy, nonatomic) NSString *discountCode;
/** available age options for the pickup location */
@property (strong, nonatomic) NSArray *ageOptions;
/** The index of the user-selected age */
@property (assign, nonatomic) NSInteger selectedAgeIndex;

/** Title header for user age input */
@property (copy, nonatomic, readonly) NSString *driverAgeTitle;
/** Title for emerald club account added confirmation */
@property (copy, nonatomic, readonly) NSString *emeraldAddedTitle;
/** Title for the discount code insert button */
@property (copy, nonatomic, readonly) NSString *couponButtonTitle;
/** Title for the discount code field title */
@property (copy, nonatomic, readonly) NSString *couponInputTitle;
/** Title for the discount image name */
@property (copy, nonatomic, readonly) NSString *couponButtonImageName;
/** Title for the discount code input placeholder */
@property (copy, nonatomic, readonly) NSString *couponInputPlaceholder;
/** Title for name of the contract assosciated with the user's account */
@property (copy, nonatomic, readonly) NSAttributedString *contractNameTitle;
/** Title for the promotion applied label */
@property (copy, nonatomic, readonly) NSString *promotionAppliedTitle;
/** Title for the promotion */
@property (copy, nonatomic, readonly) NSString *promotionTitle;
/** Title for emerald club sign in button */
@property (copy, nonatomic, readonly) NSString *emeraldSignInTitle;

/** @c YES if the contract toggle should be visible */
@property (assign, nonatomic) BOOL shouldShowContractToggle;
/** @c YES if the code input button is visible */
@property (assign, nonatomic) BOOL shouldShowCouponInputButton;
/** @c YES if the code input field is visible */
@property (assign, nonatomic) BOOL shouldShowCouponInputField;
/** @c YES if user wants to use their associated contract code */
@property (assign, nonatomic) BOOL shouldUseContractCode;
/** @c YES if user is using weekend special promotion code */
@property (assign, nonatomic) BOOL shouldShowPromotionCode;
/** @c YES if the current user is authenticated */
@property (assign, nonatomic) BOOL isAuthenticated;
/** @c YES if the current user is authenticated as an EC member */
@property (assign, nonatomic) BOOL isEmeraldUser;

/** @c YES if keyboard shows when discount code is adjusted */
@property (assign, nonatomic, readonly) BOOL automaticallyShowKeyboard;

/** Toggles flags to show coupon input field properly */
- (void)showCouponInput;
/** Indicate that the user has completed entering a discount code */
- (void)commitCodeInput;
/** Initiate a sign in for emerald club users */
- (void)signInEmeraldClub;
/** Sign out any existing emerald club user */
- (void)removeEmeraldClub;

- (void)removePromotion;

@end

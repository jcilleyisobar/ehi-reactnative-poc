//
//  EHIItineraryUserInfoViewModel.m
//  Enterprise
//
//  Created by mplace on 3/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryUserInfoViewModel.h"
#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIUserManager.h"
#import "EHIToastManager.h"
#import "EHIServices+Location.h"

@interface EHIItineraryUserInfoViewModel () <EHIReservationBuilderReadinessListener, EHIUserListener>
@property (strong, nonatomic) EHIContractDetails *discount;
@property (copy  , nonatomic) NSAttributedString *contractNameTitle;
@property (strong, nonatomic) EHIPromotionContract *weekendSpecial;
@end

@implementation EHIItineraryUserInfoViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _driverAgeTitle  = EHILocalizedString(@"reservation_age_field_title", @"DRIVER'S AGE", @"title for header above user age input in a reservation");
        _emeraldAddedTitle = EHILocalizedString(@"reservation_emerald_club_added_title", @"Emerald Club Added", @"title for confirmation text when user adds an EC account");
        _couponButtonTitle = EHILocalizedString(@"reservation_code_insert_title", @"Add code, coupon, or account number", @"Title for the discount code insert button");
        _couponInputTitle  = [EHILocalizedString(@"reservation_code_input_title", @"ADD CODE, COUPON, OR ACCOUNT NUMBER", @"Title for the discount code input label") uppercaseString];
        _couponInputPlaceholder = EHILocalizedString(@"reservation_code_input_placeholder", @"Code, coupon, account number", @"Placeholder for the code input field");
        _promotionAppliedTitle = [EHILocalizedString(@"reservation_promotion_applied", @"Promotion Applied", @"") ehi_appendComponent:@":"];
        _emeraldSignInTitle = EHILocalizedString(@"reservation_emerald_club_sign_in_title", @"Are you a National Emerald Club member? Sign in to add your account", @"Titlefor emerald club sign in button");
        
        // capture the initial state of the discount / code from the builder, but don't react to it
        _discount = self.builder.discount;
        _discountCode = self.builder.discountCode;

        _isAuthenticated = [EHIUser currentUser] != nil;
        _isEmeraldUser   = [EHIUserManager sharedInstance].isEmeraldUser;
        
        // manually invoke setter side-effects to prepare initial state
        [self invalidateDiscount];
    }
    
    return self;
}

# pragma mark - Lifecycle

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    // add the listener for the user events is not in modify
    if(!self.isModify) {
        [[EHIUserManager sharedInstance] addListener:self];
    } else {
        // if discount is attached to reservation, meaning reservation was made as auth, then take out discount code
        if(self.discount) {
            self.discountCode = nil;
        }
        
        [self invalidateDiscount];
    }
    
    // sync builder with our state whenever we become active
    [self invalidateBuilderDiscount];
    
    // wait for builder readiness to add reactions
    [self.builder waitForReadiness:self];
}

- (void)didResignActive
{
    [super didResignActive];
    
    // don't respond to auth changes when not visible
    [[EHIUserManager sharedInstance] removeListener:self];
}

# pragma mark - Reactions

- (void)builderIsReady:(EHIReservationBuilder *)builder
{
    [MTRReactor autorun:self action:@selector(invalidatePickupLocation:)];
}

# pragma mark - Discount Code

- (void)setDiscountCode:(NSString *)discountCode
{
    // filter empty strings
    if([discountCode isEqualToString:@""]) {
        discountCode = nil;
    }
    
    _discountCode = discountCode;
    self.builder.discountCode = self.discountCode;
    // clean up any current pre-rate data
    [self.builder resetPreRateData];
}

# pragma mark - Renter Age

- (void)invalidatePickupLocation:(MTRComputation *)computation
{
    // invoke getter to create reaction binding in case auth state changes
    EHILocation *pickupLocation = self.builder.pickupLocation;
    
    if([EHIUser currentUser] == nil) {
        [[EHIServices sharedInstance] updateAgeOptionsForLocation:pickupLocation handler:^(EHILocation *location, EHIServicesError *error) {
            if(self.ageOptions == location.ageOptions) {
                return;
            }

            self.ageOptions = location.ageOptions;
            
            self.selectedAgeIndex = [self.ageOptions indexOfObjectPassingTest:^BOOL(EHILocationRenterAge *age, NSUInteger idx, BOOL *stop) {
                return age.isDefault;
            }];
            
            if(self.isModify) {
                EHILocationRenterAge *matchingAge = self.ageOptions.find(^(EHILocationRenterAge *renterAge) {
                    return renterAge.value == self.builder.reservation.renterAge;
                });
                
                if(matchingAge) {
                    self.selectedAgeIndex = self.ageOptions.indexOf(matchingAge);
                }
            }
        }];
    }
}

- (void)setSelectedAgeIndex:(NSInteger)selectedAgeIndex
{
    _selectedAgeIndex = selectedAgeIndex;
    
    self.builder.renterAge = self.ageOptions[selectedAgeIndex];
}

# pragma mark - Toggle

- (void)setShouldUseContractCode:(BOOL)shouldUseContractCode
{
    _shouldUseContractCode = shouldUseContractCode;
    
    // invalidate any inferrable flags
    [self invalidateFlags];
    // update the builder accordingly
    [self invalidateBuilderDiscount];
}

- (void)invalidateFlags
{
    [MTRReactor nonreactive:^{
        self.shouldShowContractToggle    = self.discount != nil;
        self.shouldShowCouponInputField  = self.discountCode != nil && !self.shouldUseContractCode;
        self.shouldShowCouponInputButton = self.discountCode == nil && !self.shouldUseContractCode;
    }];
}

- (void)invalidateBuilderDiscount
{
    [MTRReactor nonreactive:^{
        // update the builder's stored discount / code according to the toggle unless we're in modify and not using weekend special
        if(!self.isModify) {
            self.builder.discountCode = self.shouldUseContractCode ? nil : self.discountCode;
            self.builder.discount     = self.shouldUseContractCode ? self.discount : nil;
        }
    }];
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    self.isAuthenticated  = user != nil;
    self.isEmeraldUser    = manager.isEmeraldUser;
    BOOL isWeekendSpecial = [self isUsingWeekendSpecial];
    
    if(!isWeekendSpecial) {
        self.discount = user.corporateContract;
    }
}

# pragma mark - Actions

- (void)showCouponInput
{
    self.shouldShowCouponInputField = YES;
    self.shouldShowCouponInputButton = NO;
    
    [EHIAnalytics trackAction:EHIAnalyticsResActionExpandCid handler:nil];
}

- (void)commitCodeInput
{
    [self invalidateFlags];
}

- (void)signInEmeraldClub
{
    self.router.transition.present(EHIScreenSigninEmerald).handler(^(BOOL didSignIn) {
        if(didSignIn) {
            [EHIToastManager showMessage:EHILocalizedString(@"reservation_emerald_sign_in_confirmation_toast_message", @"You've successfully added an Emerald Club account to your reservation.", @"message in toast when user signs into Emerald Club on the initial reservation screen")];
        }
    }).start(nil);
}

- (void)removeEmeraldClub
{
    EHIAlertViewBuilder *alertView = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"alert_remove_emerald_club_title", @"Remove Emerald Club Account", @""))
        .message(EHILocalizedString(@"alert_remove_emerald_club_message", @"Are you sure you want to remove your Emerald Club account? Your account will also be removed from future reservations.", @""))
        .button(EHILocalizedString(@"standard_button_yes", @"Yes", @""))
        .cancelButton(EHILocalizedString(@"standard_button_no", @"No", @""));
    
    alertView.show(^(NSInteger index, BOOL canceled) {
        if(!canceled) {
            // sign out and remove cached credentials
            [[EHIUserManager sharedInstance] logoutCurrentUser];
            [[EHIUserManager sharedInstance] clearData];
        }
    });
    
}

# pragma mark - Setters

- (void)setDiscount:(EHIContractDetails *)discount
{
    if(_discount == discount) {
        return;
    }
    
    _discount = discount;
    
    [self invalidateDiscount];
}

- (void)invalidateDiscount
{
    [self invalidateContractName];
    [self invalidateUseContractCode];
    [self invalidateFlags];
}

- (void)invalidateUseContractCode
{
    // by default, use the contract if we have that and no discount code
    self.shouldUseContractCode = self.discount != nil && self.discountCode == nil && ![self isUsingWeekendSpecial];
}

- (void)removePromotion
{
    EHIUser *currentUser = [EHIUserManager sharedInstance].currentUser;
    self.discountCode    = currentUser.corporateContract.name;
    _discount = currentUser.corporateContract;
    
    [self invalidateDiscount];
}

- (NSString *)couponButtonImageName
{
    return self.isModify ? @"icon_add_disable" : @"icon_add";
}

//
// Helpers
//

- (void)invalidateContractName
{
    NSAttributedString *result = nil;
    EHIContractDetails *contract = self.discount;
    
    if(contract) {
        NSString *codePrefix = EHILocalizedString(@"enterprise_corporate_account_number_use_toggle_title", @"Use Code: ", @"Hint text that asks the user whether they want to use an account code");
        NSString *contractName = contract.name ?: @"";
        result = [EHIAttributedStringBuilder new].color([UIColor ehi_blackColor])
            .text(codePrefix).size(14.0)
            .space.appendText(contractName).fontStyle(EHIFontStyleBold, 14.0).string;
    }
    
    self.contractNameTitle = result;
}

- (BOOL)isUsingWeekendSpecial
{
    NSString *weekendSpecialCode = self.weekendSpecial.code;
    return [self.builder.discountCode isEqualToString:weekendSpecialCode];
}

# pragma mark - Accessors

- (BOOL)automaticallyShowKeyboard
{
    return !self.discountCode;
}

- (BOOL)shouldShowPromotionCode
{
    return [self isUsingWeekendSpecial];
}

- (EHIPromotionContract *)weekendSpecial
{
    EHIPromotionContract *weekendSpecial = [NSLocale ehi_country].weekendSpecial;
    if(!_weekendSpecial && weekendSpecial) {
        _weekendSpecial = weekendSpecial;
    }
    
    return _weekendSpecial;
}

- (NSString *)promotionTitle
{
    return self.weekendSpecial.name;
}

@end

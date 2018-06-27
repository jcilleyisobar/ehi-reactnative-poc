//
//  EHIReviewPaymentOptionsViewModel.m
//  Enterprise
//
//  Created by mplace on 6/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIReviewPaymentOptionsViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIReservationBuilder.h"
#import "EHIUserManager.h"

@interface EHIReviewPaymentOptionsViewModel () <EHIUserListener>
@property (weak  , nonatomic) EHIUser *user;
@property (strong, nonatomic) EHIReservation *reservation;
@property (strong, nonatomic) EHIContractDetails *corporateAccount;
@property (copy  , nonatomic) NSArray *billingAccounts;
@property (copy  , nonatomic) NSArray *paymentAccounts;
@property (strong, nonatomic) EHIUserPaymentMethod *customBillingMethod;
@property (strong, nonatomic) EHIUserPaymentMethod *otherPaymentMethod;
@property (assign, nonatomic) BOOL billingCodeEntrySelected;
@property (assign, nonatomic) BOOL billingCodeEntryOverride;

/** The currently selected billing payment method */
@property (strong, nonatomic) EHIUserPaymentMethod *selectedBillingMethod;
/** The currently selected payment payment method */
@property (strong, nonatomic) EHIUserPaymentMethod *selectedPaymentMethod;

@end

@implementation EHIReviewPaymentOptionsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title        = EHILocalizedString(@"reservation_confirmation_payment_method_section_title", @"PAYMENT METHOD", @"");
        _billingTitle = EHILocalizedString(@"review_payment_options_billing_title", @"Use billing code", @"billing title for the review payment options cell.");
        _paymentTitle = EHILocalizedString(@"review_payment_options_payment_title", @"Pay at counter", @"payment title for the review payment options cell.");
        _paymentSubtitle = EHIAttributedStringBuilder.new.text(EHILocalizedString(@"review_payment_options_payment_subtitle", @"Your credit card will not be charged. This just saves you time at the counter.", @"payment subtitle for the review payment options cell.")).lineSpacing(5.f).string;
        
        _billingEntryHintTitle = EHILocalizedString(@"review_payment_options_billing_entry_hint", @"Enter code", @"hint text for the billing entry text field in the review payment options cell.");
        _otherPaymentMethod  = [EHIUserPaymentMethod otherPaymentMethod];
        _customBillingMethod = [EHIUserPaymentMethod customBillingMethod];
        
        // start listening for user events
        [[EHIUserManager sharedInstance] addListener:self];
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIReservation class]]) {
        self.reservation = model;
    }
}

- (void)setReservation:(EHIReservation *)reservation
{
    _reservation      = reservation;
    _corporateAccount = reservation.contractDetails;
    
    NSString *format = EHILocalizedString(@"review_payment_options_billing_subtitle", @"I am authorized for billing privileges and choosing to bill #{account_name} for this rental.", @"billing subtitle for the payment options cell.");
    
    NSString *billingSubtitle = [format ehi_applyReplacementMap:@{
        @"account_name" : self.corporateAccount.name ?: self.corporateAccount.maskedId ?: @"",
    }];
    
    self.billingSubtitle = EHIAttributedStringBuilder.new.text(billingSubtitle)
        .fontStyle(EHIFontStyleLight, 14.f).lineSpacing(5.f).string;
    
    // initialize payment methods
    [self initializePaymentMethods];
    
    if(self.corporateAccount.billingAccountExists) {
        // hide the billing picker
        self.shouldHideBillingPicker = YES;
        // hide the billing entry field
        self.shouldHideBillingEntry  = YES;
        // default to pay with billing
        self.currentPaymentOption    = EHIReviewPaymentOptionBilling;
        // set the billing number title
        self.billingNumberTitle = self.corporateAccount.formattedTitle;
    }
    
    else {
        // if there are no billing accounts on the user, allow them to enter one
        self.billingCodeEntryOverride = self.billingAccounts.count == 0;
        self.shouldHideBillingEntry = !self.shouldHideBillingPicker;
        
        // otherwise add the billing entry option to the billing picker
        if(![self.billingAccounts containsObject:self.customBillingMethod] && !self.shouldHideBillingPicker) {
            self.billingAccounts = [self.billingAccounts ehi_safelyAppend:self.customBillingMethod];
        }
        
        // default to the users preferred payment method
        if(self.user.preferredBillingAccount) {
            // default to billing
            self.currentPaymentOption = EHIReviewPaymentOptionBilling;
            // preselect the preferred billing method
            [self selectPreferredPaymentMethodOfType:EHIReviewPaymentOptionBilling];
        } else if(self.user.preferredPaymentAccount) {
            // default to pay at counter
            self.currentPaymentOption = EHIReviewPaymentOptionPayment;
            // preselect the preferred payment method
            [self selectPreferredPaymentMethodOfType:EHIReviewPaymentOptionPayment];
        } else {
            self.currentPaymentOption = EHIReviewPaymentOptionPayment;
        }
    }
}

- (void)initializePaymentMethods
{
    // populate our billing accounts off the user if the corporate account on the profile is the same as the one on the reservation
    self.billingAccounts = [self.corporateAccount.uid isEqualToString:self.user.corporateContract.uid] ? self.user.billingAccounts : nil;
    // populate our payment accounts off the user
    self.paymentAccounts = self.user.paymentAccounts;
    
    // add the other credit card option if there are payment accounts on the profile and we havne't already done so
    if(![self.paymentAccounts containsObject:self.otherPaymentMethod] && self.paymentAccounts.count > 0) {
        self.paymentAccounts = [self.paymentAccounts ehi_safelyAppend:self.otherPaymentMethod];
    }
    
    // intialize the selected payment method to the first payment method
    self.selectedPaymentMethod = self.paymentAccounts.firstObject;
    // intialize the selected billing method to the first billing payment method
    self.selectedBillingMethod = self.billingAccounts.firstObject;
    
    // initialize the payment account title
    self.paymentAccountTitle = self.user != nil ? [self attributedTitleForPaymentMethod:self.selectedPaymentMethod] : nil;
    // initialize the billing account title
    self.billingAccountTitle = self.user != nil ? [self attributedTitleForPaymentMethod:self.selectedBillingMethod] : nil;
    
    // hide the payment picker if there are no card payment methods on the user
    self.shouldHidePaymentPicker = self.user.paymentAccounts.count == 0;
    // hide the billing picker if there are no billing accounts on the user
    self.shouldHideBillingPicker = self.billingAccounts.count == 0;
}

- (void)selectPreferredPaymentMethodOfType:(EHIReviewPaymentOption)type
{
    BOOL typeIsBilling = type == EHIReviewPaymentOptionBilling;
    
    // determine the preferred payment method
    EHIUserPaymentMethod *preferredPaymentMethod = typeIsBilling
        ? self.user.preferredBillingAccount
        : self.user.preferredPaymentAccount;
    
    // determine the payment methods to choose from
    NSArray *paymentMethods = typeIsBilling
        ? self.billingAccounts
        : self.paymentAccounts;
    
    // select the preferred payment method
    NSInteger index = (paymentMethods ?: @[]).indexOf(preferredPaymentMethod);
    [self selectPaymentMethodAtIndex:index];

}

# pragma mark - Actions

- (void)updateCustomBillingCode:(NSString *)code
{
    // update our custom billing payment method object
    [self.customBillingMethod updateWithDictionary:@{
        @key(self.customBillingMethod.paymentReferenceId) : code,
    }];
    
    // update the interface
    self.customBillingCode = code;
    
    // update the reservation builder with the updated custom billing payment method
    self.reservationBuilder.paymentMethod = self.customBillingMethod;
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfPaymentMethods
{
    return [self paymentMethodsForCurrentPaymentOption:self.currentPaymentOption].count;
}

- (NSString *)titleForPaymentMethodAtIndex:(NSInteger)index
{
    EHIUserPaymentMethod *paymentMethod = [self paymentMethodForIndex:index];
    
    return [self titleForPaymentMethod:paymentMethod];
}

- (void)selectPaymentMethodAtIndex:(NSInteger)index
{
    EHIUserPaymentMethod *paymentMethod = [self paymentMethodForIndex:index];

    // update the reservation builder with the new selection
    [EHIReservationBuilder sharedInstance].paymentMethod = (paymentMethod == self.otherPaymentMethod) ? nil : paymentMethod;
    
    // update the billing code entry selected state
    self.billingCodeEntrySelected = [paymentMethod isEqual:self.customBillingMethod];

    // construct the title
    NSAttributedString *title = [self attributedTitleForPaymentMethod:paymentMethod];
   
    // set the title based on payment option
    switch (self.currentPaymentOption) {
        case EHIReviewPaymentOptionBilling:
            self.selectedBillingMethod = paymentMethod;
            self.billingAccountTitle   = title;
            break;
        case EHIReviewPaymentOptionPayment:
            self.selectedPaymentMethod = paymentMethod;
            self.paymentAccountTitle   = title;
            break;
    }
}

//
// Helper
//

- (EHIUserPaymentMethod *)paymentMethodForIndex:(NSInteger)index
{
    NSArray *paymentMethods = [self paymentMethodsForCurrentPaymentOption:self.currentPaymentOption];
    
    return [paymentMethods ehi_safelyAccess:index];
}

- (NSArray *)paymentMethodsForCurrentPaymentOption:(EHIReviewPaymentOption)option
{
    return self.currentPaymentOption == EHIReviewPaymentOptionBilling
        ? self.billingAccounts
        : self.paymentAccounts;
}

- (NSString *)titleForPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    return paymentMethod.customDisplayName;
}

- (NSAttributedString *)attributedTitleForPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    // determine whether the payment method has an alias
    BOOL paymentHasAlias = paymentMethod.alias.length > 0;
    // determine whether the payment method has a masked number
    NSString *masked = paymentMethod.lastFour ?: paymentMethod.maskedBillingNumber;
    BOOL paymentHasMaskedNumber = masked != nil;
    
    EHIAttributedStringBuilder *titleBuilder = EHIAttributedStringBuilder.new.color([UIColor ehi_greenColor]).lineSpacing(5.f);
    
    // if we do have an alias, use it as the first line of the attributed title
    if(paymentHasAlias) {
        titleBuilder = titleBuilder.text(paymentMethod.alias).fontStyle(EHIFontStyleBold, 18.f);
    }
    
    // if we have both an alias and a masked number, append a newline
    if(paymentHasAlias && paymentHasMaskedNumber) {
        titleBuilder = titleBuilder.newline;
    }
    
    // append the masked number if we have one
    if(paymentHasMaskedNumber) {
        // append the masked payment number
        titleBuilder = titleBuilder.appendText(masked).fontStyle(EHIFontStyleLight, paymentHasAlias ? 18.f : 14.f);
    }
    
    // append credit card type if we have one
    if (paymentMethod.cardTypeDisplay.length) {
        titleBuilder = titleBuilder.appendText([NSString stringWithFormat:@" (%@)", paymentMethod.cardTypeDisplay]).fontStyle(EHIFontStyleLight, paymentHasAlias ? 18.f : 14.f);
    }

    return titleBuilder.string;
}

# pragma mark - EHIUserListener

- (void)manager:(EHIUserManager *)manager didChangeAuthenticationForUser:(EHIUser *)user
{
    // invalidate the user and reservation any time the user changes
    self.user = user;
    self.reservation = self.reservation;
}

# pragma mark - Accessor

- (EHIReservationBuilder *)reservationBuilder
{
    return [EHIReservationBuilder sharedInstance];
}

# pragma mark - Setter

- (void)setBillingCodeEntrySelected:(BOOL)billingCodeEntrySelected
{
    _billingCodeEntrySelected = billingCodeEntrySelected;
    
    if(self.billingCodeEntryOverride) {
        self.shouldHideBillingEntry = NO;
    } else {
        self.shouldHideBillingEntry = !self.billingCodeEntrySelected;
    }
}

- (void)setCurrentPaymentOption:(EHIReviewPaymentOption)currentPaymentOption
{
    _currentPaymentOption = currentPaymentOption;

    EHIUserPaymentMethod *paymentMethod = [self selectCurrentPaymentMethod];
    
    self.reservationBuilder.paymentMethod = (paymentMethod == self.otherPaymentMethod) ? nil : paymentMethod;
}

- (EHIUserPaymentMethod *)selectCurrentPaymentMethod
{
    BOOL isBilling = self.currentPaymentOption == EHIReviewPaymentOptionBilling;
    
    // payment option has changed, so we need to update the builder with the currently selected payment method for that option (potentially nil)
    EHIUserPaymentMethod *paymentMethod = isBilling
        ? self.selectedBillingMethod
        : self.selectedPaymentMethod;
    
    if(!paymentMethod && isBilling) {
        paymentMethod = [self currentPaymentMethod];
    }

    return paymentMethod;
}

- (EHIUserPaymentMethod *)currentPaymentMethod
{
    BOOL isCustomBilling = (!self.shouldHideBillingEntry || self.billingCodeEntryOverride);
    // if we don't have a selected payment method and we are currently showing the custom billing field
    if(isCustomBilling) {
        return self.customBillingMethod;
    } else {
        return [EHIUserPaymentMethod existingBillingMethod];
    }
}

@end

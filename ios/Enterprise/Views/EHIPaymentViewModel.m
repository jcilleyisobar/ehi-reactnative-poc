//
//  EHIPaymentViewModel.m
//  Enterprise
//
//  Created by Alex Koller on 1/13/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIReservationStepViewModel_Subclass.h"
#import "EHIViewModel_Subclass.h"
#import "EHIPaymentViewModel.h"
#import "EHIServices+Reservation.h"
#import "EHISecurityManager.h"
#import "EHICreditCard.h"
#import "EHIToastManager.h"
#import "EHIUser.h"
#import "EHIPaymentGateway.h"
#import "EHICreditCardPanguiResponse.h"
#import "EHIUserManager.h"

@interface EHIPaymentViewModel ()
@property (strong, nonatomic) EHIDriverInfo *driverInfo;
@property (strong, nonatomic) EHIPaymentInputViewModel *paymentInputViewModel;
@property (assign, nonatomic) NSInteger creditCardCount;
@property (assign, nonatomic) BOOL isLoading;
@end

@implementation EHIPaymentViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title      = EHILocalizedString(@"add_card_navigation_title", @"Add Credit Card", @"");
        _scanTitle  = EHILocalizedString(@"add_card_scan_button_title", @"Tap here to scan your card", @"");
        _addTitle   = EHILocalizedString(@"add_card_add_button_title", @"ADD CARD", @"");
        
        _creditCardCount = [EHIUser currentUser].creditCardPaymentMethods.count;
    }

    return self;
}

- (void)updateWithModel:(id)model
{
    if([model isKindOfClass:[EHICreditCard class]]) {
        [self updateCreditCard:(EHICreditCard *)model];
    }
}

- (void)updateCreditCard:(EHICreditCard *)card
{
    if(card == nil) {
        return;
    }
    
    self.paymentInputViewModel.cardNumber      = card.cardNumber;
    self.paymentInputViewModel.expirationYear  = @(card.expirationYear).description;
    self.paymentInputViewModel.expirationMonth = @(card.expirationMonth).description;
    self.paymentInputViewModel.cvv             = card.cvvNumber ?: @"";
}

- (BOOL)missingField
{
    BOOL validateTerms = !self.paymentInputViewModel.hideTerms;
    if(validateTerms) {
        return self.paymentInputViewModel.invalidCreditCard || !self.paymentInputViewModel.policiesRead;
    }
    
    return self.paymentInputViewModel.invalidCreditCard;
}

# pragma mark - Actions

- (void)scanCard
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionAddCreditCardScan handler:nil];
    
    __weak typeof(self) welf = self;
    self.router.transition.present(EHIScreenCardScan).handler(^(EHICreditCard* card){
        [welf updateCreditCard:card];
    }).start(nil);
}

- (void)addCard
{
    // get credit card from input view
    EHICreditCard *creditCard = [self.paymentInputViewModel createCreditCard];
    
    if(!creditCard || self.missingField) {
        return;
    }
    
    switch(self.style) {
        case EHIPaymentViewStyleReservation:
        case EHIPaymentViewStyleSelectPayment:
            [self addCreditCard:creditCard toReservationBuilder:self.builder];
            break;
        case EHIPaymentViewStyleProfile:
            [self addCreditCardToProfile:creditCard];
            break;
    }
}

- (void)addCreditCard:(EHICreditCard *)creditCard toReservationBuilder:(EHIReservationBuilder *)builder
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionAddCreditCardAdd handler:nil];
    
    self.isLoading = YES;
    __weak __typeof(self) welf = self;
    [self.builder submitCreditCard:creditCard handler:^(id response, EHIServicesError *error) {
        [welf handleCreditCard:response error:error];
    }];
}

- (void)addCreditCardToProfile:(EHICreditCard *)creditCard
{
    EHIPaymentGateway *manager = [EHIPaymentGateway new];
    
    self.isLoading  = YES;
    creditCard.save = YES;
    __weak __typeof(self) welf = self;
    [manager submitCreditCard:creditCard token:nil handler:^(id response, EHIServicesError *error) {
        [welf handleCreditCard:response error:error];
    }];
}

- (void)handleCreditCard:(id)response error:(EHIServicesError *)error
{
    self.isLoading = NO;
    if(!error.hasFailed) {
        NSString *paymentId = nil;
        if([response isKindOfClass:[NSString class]]) {
            paymentId = response;
        }

        [self didSubmitCreditCardShowToast:YES withPaymentId:paymentId];
    } else {
        [self handleError:error];
    }
}

- (void)handleError:(EHIServicesError *)error
{
    if([error hasErrorCode:EHIServicesErrorCodeDebitCardError]) {
        [error consume];
        [self didSubmitDebitCardShowAlert:YES];
    }
}

#pragma mark - Accessors

- (void)setStyle:(EHIPaymentViewStyle)style
{
    _style = style;
    
    NSString *name = nil;
    
    switch (style) {
        case EHIPaymentViewStyleReservation:
        case EHIPaymentViewStyleSelectPayment:
            name = self.builder.driverInfo.fullName;
            break;
        case EHIPaymentViewStyleProfile: {
            name = [EHIUser currentUser].displayName;
            break;
        }
    }
    
    _paymentInputViewModel = [[EHIPaymentInputViewModel alloc] initWithModel:name];
    _paymentInputViewModel.hideTerms = style == EHIPaymentViewStyleProfile;
    _paymentInputViewModel.hideSave  = [self shouldHideSave];
}

- (BOOL)shouldHideSave
{
    BOOL allowProfileEdit  = [NSLocale ehi_shouldAllowProfilePaymentEdit];
    BOOL inReservationFlow = self.style == EHIPaymentViewStyleReservation || self.style == EHIPaymentViewStyleSelectPayment;
    BOOL canSaveCard       = [self canSaveCard];
    BOOL isLogged          = [self isLogged];
    
    return !allowProfileEdit || !isLogged || !inReservationFlow || !canSaveCard;
}

- (BOOL)canSaveCard
{
    return self.creditCardCount < EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed;
}

- (BOOL)isLogged
{
    EHIUserManager *userManager = [EHIUserManager sharedInstance];
    return userManager.currentUser != nil && !userManager.isEmeraldUser;
}

//
// Helpers
//

- (void)didSubmitCreditCardShowToast:(BOOL)showToast withPaymentId:(NSString *)paymentId {
    if(showToast) {
        NSString *creditCardAddedMessage = EHILocalizedString(@"add_card_successful_message", @"Credit Card Added Successfully", @"");
        [EHIToastManager showMessage:creditCardAddedMessage];
    }
    
    NSInteger popCount = self.style == EHIPaymentViewStyleSelectPayment ? 2 : 1;
    
    self.router.transition
        .pop(popCount).start(nil);
    
    BOOL shouldRunHandler = popCount == 2;
    ehi_call(self.handler)(paymentId, shouldRunHandler);
}

- (void)didSubmitDebitCardShowAlert:(BOOL)showAlert
{
    if(showAlert) {
        EHIAlertViewBuilder *alert = EHIAlertViewBuilder.new
        .title(EHILocalizedString(@"payment_method_add_debit_card_error_title", @"Add debit card error title", @"Add debit card error title"))
        .message(EHILocalizedString(@"payment_method_add_debit_card_error_message", @"Add debit card error message", @"Add debit card error message"))
        .cancelButton(EHILocalizedString(@"standard_ok_text", @"OK", @""));
        
        alert.show(nil);
    }
}

@end

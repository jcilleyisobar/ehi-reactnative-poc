//
//  EHISelectPaymentViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHISelectPaymentViewModel.h"
#import "EHISelectPaymentItemViewModel.h"
#import "EHIViewModel_Subclass.h"
#import "EHIPaymentViewModel.h"
#import "EHIUser.h"
#import "EHIPaymentGateway.h"
#import "EHIServices+Reservation.h"
#import "EHIToastManager.h"

@interface EHISelectPaymentViewModel ()
@property (weak  , nonatomic) EHIUserPaymentMethod *selectedPaymentMethod;
@property (strong, nonatomic) NSArray<EHIUserPaymentMethod> *paymentMethods;
@property (copy  , nonatomic) NSString *paymentId;
@end

@implementation EHISelectPaymentViewModel

- (instancetype)initWithModel:(id)model
{
    if (self = [super initWithModel:model]) {
        [self invalidatePaymentMethods];
    }
    return self;
}

- (void)didBecomeActive
{
    [super didBecomeActive];
    
    [self invalidatePaymentMethods];
}

- (NSString *)title
{
    return EHILocalizedString(@"select_payment_screen_title", @"Select Payment", @"");
}

- (void)setPaymentMethods:(NSArray<EHIUserPaymentMethod> *)paymentMethods
{
    _paymentMethods = paymentMethods;
    
    NSDictionary *methodDictionary = (paymentMethods ?: @[]).groupBy(^(EHIUserPaymentMethod *method){
        return @(method.paymentType);
    });
    
    self.cardsModels = [self sortPaymentMethods:(methodDictionary[@(EHIUserPaymentTypeCard)] ?: @[])].map(^(EHIUserPaymentMethod *paymentMethod){
        return [[EHISelectPaymentItemViewModel alloc] initWithModel:paymentMethod];
    });
    
    // create add credit card model
    self.addViewModel = [EHIProfilePaymentAddViewModel new];
    self.addViewModel.type = EHIProfilePaymentAddTypeSelect;
    self.addViewModel.topSpacing = EHIMediumPadding;

    self.selectedPaymentMethod = (self.cardsModels ?: @[]).select(^(EHISelectPaymentItemViewModel *payment){
        return payment.isSelected;
    }).firstObject;
}

- (NSArray *)sortPaymentMethods:(NSArray *)payments
{
    EHIUserPaymentMethod *preferred = payments.find(^(EHIUserPaymentMethod *method) {
        return method.isPreferred;
    });
    
    NSArray *sorted = [EHIUserPaymentMethod skipPreferredSorting:payments];
    
    return preferred != nil ? @[preferred].concat(sorted) : sorted;
}

- (EHISectionHeaderModel *)headerForSection:(EHISelectPaymentSection)section
{
    EHISectionHeaderModel *model = [EHISectionHeaderModel new];
    model.style = EHISectionHeaderStyleWrapText;
    
    switch (section) {
        case EHISelectPaymentSectionCards:
            model.title = EHILocalizedString(@"profile_payment_options_credit_cards_title", @"CREDIT CARDS", @"");
            model.dividerStyle = EHISectionHeaderDividerStyleDefault;
            return model;
        default:
            return nil;
    }
}

# pragma mark - Accessors

- (EHISelectPaymentFooterViewModel *)footerViewModel
{
    if(!_footerViewModel) {
        _footerViewModel = [EHISelectPaymentFooterViewModel new];
    }
    
    return _footerViewModel;
}

#pragma mark - Helpers

- (void)updateReservationPayment:(NSString *)paymentId
{
    EHIUserPaymentMethod *payment = ([EHIUser currentUser].payment.paymentMethods ?: @[]).find(^(EHIUserPaymentMethod *paymentMethod){
        return [paymentMethod.paymentReferenceId isEqualToString:paymentId];
    });
    
    // set one-time payment if no payment method was found in user's profile
    if(payment == nil) {
        payment = [EHIUserPaymentMethod oneTimePaymentMethod:paymentId];
    }
    
    self.builder.paymentMethod = payment;
}

#pragma mark - Actions

- (void)addCreditCard
{
    __weak __typeof(self) welf = self;
    void (^paymentHandler)(NSString *) = ^(NSString *panguiPaymentId) {
        [welf updateReservationPayment:panguiPaymentId];
        ehi_call(welf.handler)(panguiPaymentId);
    };
    
    [EHIAnalytics trackAction:EHIAnalyticsResActionAddPaymentMethod handler:nil];
    
    self.router.transition.push(EHIScreenPayment).handler(paymentHandler).object(@(EHIPaymentViewStyleSelectPayment)).start(nil);
}

- (void)selectPaymentMethodAtIndexPath:(NSIndexPath *)indexPath
{
    NSArray *models = self.cardsModels;
    // toggle the user selection
    EHISelectPaymentItemViewModel *selectedPayment = (EHISelectPaymentItemViewModel *)[self.cardsModels ehi_safelyAccess:indexPath.row];
    selectedPayment.isSelected = YES;
    
    BOOL isPreferred = selectedPayment.isPreferred;
    if(isPreferred) {
        selectedPayment.showSaveToggle = YES;
    } else {
        (models ?: @[]).each(^(EHISelectPaymentItemViewModel *model){
            model.showSaveToggle = NO;
        });
    }
    
    // and check all the rest
    (models.without(selectedPayment) ?: @[]).each(^(EHISelectPaymentItemViewModel *model){
        model.isSelected = NO;
    });
    
    self.cardsModels = models;
    
    self.selectedPaymentMethod = selectedPayment.paymentMethod;
}

- (void)commitPaymentMethod
{
    if(self.footerViewModel.continueButtonDisabled) {
        return;
    }

    BOOL termsRead = self.footerViewModel.termsRead;
    if (termsRead) {
        EHISelectPaymentItemViewModel *selectedItem = self.cardsModels.find(^(EHISelectPaymentItemViewModel *model) {
            return model.isSelected;
        });

        NSString *paymentId = self.paymentId ?: selectedItem.paymentMethod.paymentReferenceId;
        
        [self updateReservationPayment:paymentId];
        
        self.router.transition.pop(1).start(nil);
        ehi_call(self.handler)(paymentId);
    } else {
        [EHIToastManager showMessage:EHILocalizedString(@"select_payment_agree_tac_toast_message", @"You must agree to terms and conditions", @"")];
    }
}

# pragma mark - Passthrough

- (EHIReservationBuilder *)builder
{
    return [EHIReservationBuilder sharedInstance];
}

- (void)setSelectedPaymentMethod:(EHIUserPaymentMethod *)selectedPaymentMethod
{
    self.footerViewModel.currentPaymentMethod = selectedPaymentMethod;
}

# pragma mark - Invalidation

- (void)invalidatePaymentMethods
{
    self.paymentMethods = [EHIUser currentUser].payment.paymentMethods;
}


@end

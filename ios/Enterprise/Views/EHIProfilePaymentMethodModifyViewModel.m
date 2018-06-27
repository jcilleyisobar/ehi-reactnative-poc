//
//  EHIProfilePaymentMethodModifyViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIProfilePaymentMethodModifyViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHIUser.h"
#import "EHIProfilePaymentDeleteViewModel.h"
#import "EHIServices+User.h"
#import "EHIPaymentViewModel.h"
#import "EHIToastManager.h"

@interface EHIProfilePaymentMethodModifyViewModel ()
@property (strong, nonatomic) NSArray<EHIUserPaymentMethod> *paymentMethods;
@end

@implementation EHIProfilePaymentMethodModifyViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
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
    return EHILocalizedString(@"profile_payment_options_edit_screen_title", @"Edit Payments", @"");
}

- (void)setPaymentMethods:(NSArray<EHIUserPaymentMethod> *)paymentMethods
{
    _paymentMethods = paymentMethods;
    
    NSDictionary *methodDictionary = paymentMethods.groupBy(^(EHIUserPaymentMethod *method){
        return @(method.paymentType);
    });
    
    // create payment methods models
    self.billingsModels = [self sortPaymentMethods:(methodDictionary[@(EHIUserPaymentTypeBilling)] ?: @[])];
    self.cardsModels    = [self sortPaymentMethods:(methodDictionary[@(EHIUserPaymentTypeCard)] ?: @[]) ];
    
    // create status model
    [self updateStatusModel];
    
    // create add credit card model
    self.addModel = self.canAddNewCreditCard ? [EHIProfilePaymentAddViewModel new] : nil;
    self.addModel.topSpacing = self.statusModel == nil ? EHIMediumPadding : 0.0f;
}

- (void)updateStatusModel
{
    self.statusModel = nil;
    
    BOOL hasBillings = self.billingsModels.count > 0;
    BOOL hasCards    = self.cardsModels.count > 0;
    
    // no payment methods
    if(!hasBillings && !hasCards) {
        self.statusModel = [[EHIProfilePaymentStatusViewModel alloc] initWithType:EHIProfilePaymentStatusEmpty];
        self.statusModel.title = nil;
    }
    
    // no credit card
    if(hasBillings && !hasCards) {
        self.statusModel = [[EHIProfilePaymentStatusViewModel alloc] initWithType:EHIProfilePaymentStatusNoCard hideDivider:YES];
        self.statusModel.title = nil;
    }
    
    // max credit card reached
    if(self.cardsModels.count == EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed) {
        self.statusModel = [[EHIProfilePaymentStatusViewModel alloc] initWithType:EHIProfilePaymentStatusNumbersOfCardsExcceded hideDivider:YES];
    }
}

- (NSArray *)sortPaymentMethods:(NSArray *)payments
{
    EHIUserPaymentMethod *preferred = payments.find(^(EHIUserPaymentMethod *method) {
        return method.isPreferred;
    });
    
    NSArray *sorted = [EHIUserPaymentMethod skipPreferredSorting:payments];
    
    return preferred != nil ? @[preferred].concat(sorted) : sorted;
}

- (EHISectionHeaderModel *)headerForSection:(EHIProfilePaymentMethodModifySection)section
{
    EHISectionHeaderModel *model = [EHISectionHeaderModel new];
    model.style = EHISectionHeaderStyleWrapText;
    switch (section) {
        case EHIProfilePaymentMethodModifySectionBilling: {
            model.title = EHILocalizedString(@"profile_payment_options_billing_numbers_title", @"BILLING NUMBERS", @"");
            break;
        }
        case EHIProfilePaymentMethodModifySectionCard: {
            model.title = EHILocalizedString(@"profile_payment_options_credit_cards_title", @"CREDIT CARDS", @"");
            model.dividerStyle = self.billingsModels.count > 0 ? EHISectionHeaderDividerStyleFancy : EHISectionHeaderDividerStyleDefault;
            break;
        }
        default: return nil;
    }
    
    return model;
}

- (BOOL)canAddNewCreditCard
{
    return self.cardsModels.count < EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed;
}

# pragma mark - Actions

- (void)updatePaymentAtIndexPath:(NSIndexPath *)indexPath withAction:(EHIProfilePaymentMethodModifyAction)action
{
    EHIUserPaymentMethod *paymentMethod = [self paymentMethodInSection:indexPath.section atIndex:indexPath.row];
    
    [self trackAction:action withPaymentMethod:paymentMethod];
    
    switch (action) {
        case EHIProfilePaymentMethodModifyActionAddCard: {
            __weak __typeof(self) welf = self;
            self.router.transition.push(EHIScreenPayment).object(@(EHIPaymentViewStyleProfile)).handler(^{
                [welf invalidatePaymentMethods];
            }).start(nil);
            break;
        }
        case EHIProfilePaymentMethodModifyActionEdit: {
            [self editPaymentMethod:paymentMethod];
            break;
        }
        case EHIProfilePaymentMethodModifyActionDelete: {
            [self deletePaymentMethod:paymentMethod];
            break;
        }
    }
}

- (EHIUserPaymentMethod *)paymentMethodInSection:(EHIProfilePaymentMethodModifySection)section atIndex:(NSInteger)index
{
    switch (section) {
        case EHIProfilePaymentMethodModifySectionBilling:
            return [self.billingsModels ehi_safelyAccess:index];
        case EHIProfilePaymentMethodModifySectionCard:
            return [self.cardsModels ehi_safelyAccess:index];
        default:
            return nil;
    }
}

- (void)editPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    NSString *screen = paymentMethod.paymentType == EHIUserPaymentTypeBilling ? EHIScreenProfileEditPaymentBilling : EHIScreenProfileEditPaymentCard;
    self.router.transition
    .push(screen).object(paymentMethod).start(nil);
}

- (void)deletePaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    EHIProfilePaymentDeleteViewModel *modal = [EHIProfilePaymentDeleteViewModel initWithPaymentMethod:paymentMethod];
    
    NSString *succesMessage = nil;
    BOOL isBilling = paymentMethod.paymentType == EHIUserPaymentTypeBilling;
    if(isBilling) {
        succesMessage = EHILocalizedString(@"profile_payment_options_delete_billing_success", @"Billing code deleted.", @"");
    } else {
        succesMessage = EHILocalizedString(@"profile_payment_options_delete_credit_card_success", @"Credit card deleted.", @"");
    }
    
    __weak __typeof(self) welf = self;
    [modal present:^BOOL(NSInteger index, BOOL canceled) {
        if(index == 0 && !canceled) {
            welf.isLoading = YES;
            
            [[EHIServices sharedInstance] deletePaymentMethod:paymentMethod handler:^(EHIUserPaymentProfile *payment, EHIServicesError *error) {
                welf.isLoading = NO;
                if(!error.hasFailed) {
                    [EHIToastManager showMessage:succesMessage];
                    [welf invalidatePaymentMethods];
                }
            }];
        }
        return YES;
    }];
}

- (void)invalidatePaymentMethods
{
    self.paymentMethods = [EHIUser currentUser].payment.paymentMethods;
}

//
// Helpers
//

- (void)trackAction:(EHIProfilePaymentMethodModifyAction)action withPaymentMethod:(EHIUserPaymentMethod *)paymentMethod
{
    BOOL isBilling      = paymentMethod.paymentType == EHIUserPaymentTypeBilling;
    NSString *actionKey = [self analyticsKeyForAction:action billing:isBilling];
    
    [EHIAnalytics trackAction:actionKey handler:^(EHIAnalyticsContext *context) {
        [[EHIReservationBuilder sharedInstance] updateAnalyticsContext:context];
    }];
}

- (NSString *)analyticsKeyForAction:(EHIProfilePaymentMethodModifyAction)action billing:(BOOL)isBilling
{
    switch (action) {
        case EHIProfilePaymentMethodModifyActionAddCard:
            return EHIAnalyticsProfileActionAddCreditCard;
        case EHIProfilePaymentMethodModifyActionEdit:
            return isBilling ? EHIAnalyticsEditPaymentsActionEditBilling : EHIAnalyticsEditPaymentsActionEditCreditCard;
        case EHIProfilePaymentMethodModifyActionDelete:
            return isBilling ? EHIAnalyticsEditPaymentsActionRemoveBilling : EHIAnalyticsEditPaymentsActionRemoveCreditCard;
    }
}

@end

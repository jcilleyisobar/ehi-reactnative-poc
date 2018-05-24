//
//  EHIProfilePaymentViewModel.m
//  Enterprise
//
//  Created by fhu on 5/19/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentViewModel.h"
#import "EHIUserPaymentProfile.h"
#import "EHIUserPaymentMethod.h"
#import "EHIProfilePaymentItemViewModel.h"

@interface EHIProfilePaymentViewModel ()
@property (strong, nonatomic) NSArray<EHIUserPaymentMethod> *paymentMethods;
@property (strong, nonatomic) NSArray *billings;
@property (strong, nonatomic) NSArray *cards;
@end

@implementation EHIProfilePaymentViewModel

- (void)updateWithModel:(EHIUserPaymentProfile *)model
{
    [super updateWithModel:model];
    
    if ([model isKindOfClass:[EHIUserPaymentProfile class]]) {
        self.paymentMethods = model.paymentMethods;
    }
}

- (void)setPaymentMethods:(NSArray<EHIUserPaymentMethod> *)paymentMethods
{
    _paymentMethods = paymentMethods;
    
    NSDictionary *methodDictionary = paymentMethods.groupBy(^(EHIUserPaymentMethod *method){
        return @(method.paymentType);
    });
    
    self.billings = [self sortPaymentMethods:(methodDictionary[@(EHIUserPaymentTypeBilling)] ?: @[])];
    self.cards    = [self sortPaymentMethods:(methodDictionary[@(EHIUserPaymentTypeCard)] ?: @[])];
}

- (NSArray *)sortPaymentMethods:(NSArray *)payments
{
    EHIUserPaymentMethod *preferred = payments.find(^(EHIUserPaymentMethod *method) {
        return method.isPreferred;
    });
    
    NSArray *sorted = [EHIUserPaymentMethod skipPreferredSorting:payments];
    
    return preferred != nil ? @[preferred].concat(sorted) : sorted;
}

#pragma mark - Accessors

- (NSArray *)paymentMethodsModel
{
    if(!_paymentMethodsModel) {
        EHIProfilePaymentItemViewModel *(^modelMap)(EHIUserPaymentMethod *) = ^(EHIUserPaymentMethod *method){
            return [[EHIProfilePaymentItemViewModel alloc] initWithModel:method];
        };
        
        NSArray *billing    = (self.billings ?: @[]).map(modelMap) ?: @[];
        NSArray *creditCard = (self.cards ?: @[]).map(modelMap) ?: @[];
        
        [billing.firstObject setIsFirst:YES];
        [billing.lastObject setIsLast:YES];
        
        [creditCard.firstObject setIsFirst:YES];
        [creditCard.lastObject setIsLast:YES];
        
        _paymentMethodsModel = @[billing, creditCard].flatten;
    }
    
    return _paymentMethodsModel;
}

- (EHIProfilePaymentStatusViewModel *)statusModel
{
    if(!_statusModel) {
        BOOL hasBillings = self.billings.count > 0;
        BOOL hasCards    = self.cards.count > 0;

        // no payment methods
        if(!hasBillings && !hasCards) {
            _statusModel = [[EHIProfilePaymentStatusViewModel alloc] initWithType:EHIProfilePaymentStatusEmpty];
        }

        // no credit card
        if(hasBillings && !hasCards) {
            _statusModel = [[EHIProfilePaymentStatusViewModel alloc] initWithType:EHIProfilePaymentStatusNoCard];
        }
        
        // max credit card reached
        if(self.cards.count == EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed) {
            _statusModel = [[EHIProfilePaymentStatusViewModel alloc] initWithType:EHIProfilePaymentStatusNumbersOfCardsExcceded];
        }
    }
    
    return _statusModel;
}

@end

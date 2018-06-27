//
//  EHIProfilePaymentMethodModifyActionsViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/27/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHIProfilePaymentMethodModifyActionsViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHIProfilePaymentDeleteViewModel.h"
#import "EHICreditCardFormatter.h"

@interface EHIProfilePaymentMethodModifyActionsViewModel ()
@property (strong, nonatomic) EHIUserPaymentMethod *paymentMethod;
@end

@implementation EHIProfilePaymentMethodModifyActionsViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        if([model isKindOfClass:[EHIUserPaymentMethod class]]) {
            self.paymentMethod = model;
        }
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIUserPaymentMethod class]]) {
        self.paymentMethod = model;
    }
}

- (NSString *)editTitle
{
    return EHILocalizedString(@"profile_payment_options_edit_title", @"EDIT", @"");
}

- (NSString *)paymentTitle
{
    return self.paymentMethod.customDisplayName;
}

#pragma mark - Preferred

- (NSString *)preferedTitle
{
    return EHILocalizedString(@"profile_preferred_label", @"Preferred", @"");
}

- (BOOL)isPreferred
{
    return self.paymentMethod.isPreferred;
}

#pragma mark - Expiration

- (NSString *)expiredTitle
{
    if(!self.isBilling) {
        NSString *expirationTitle = EHILocalizedString(@"profile_payment_options_expired_text", @"Expired #{date}", @"");
        NSString *expireDate      = [self.paymentMethod.expirationDate ehi_stringWithFormat:@"MM/YY"] ?: @"";
        return [expirationTitle ehi_applyReplacementMap:
            @{@"date" : expireDate
        }];
    }
    
    return nil;
}

- (BOOL)isExpired
{
    return self.paymentMethod.isExpired;
}

#pragma mark - Card Image
- (NSString *)cardImage {
    return [EHICreditCardFormatter cardIconForCardType:self.paymentMethod.cardType];
}

//
// Helpers

- (BOOL)isBilling
{
    return self.paymentMethod.paymentType == EHIUserPaymentTypeBilling;
}

@end

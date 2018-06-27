//
//  EHISelectPaymentItemViewModel.m
//  Enterprise
//
//  Created by Stu Buchbinder on 10/26/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIViewModel_Subclass.h"
#import "EHISelectPaymentItemViewModel.h"
#import "EHIUserPaymentMethod.h"
#import "EHICreditCardFormatter.h"
#import "EHISettings.h"

@implementation EHISelectPaymentItemViewModel

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
        self.paymentMethod  = model;
        self.isSelected     = self.paymentMethod.isPreferred;
        self.showSaveToggle = self.isPreferred;

        BOOL usesPreferredAsDefault = [EHISettings sharedInstance].selectPreferredPaymentMethodAutomatically;
        _isSaved = self.isPreferred && usesPreferredAsDefault;
    }
}

- (NSString *)aliasTitle
{
    NSString *alias = (self.paymentMethod.alias) ? : self.paymentMethod.cardTypeDisplay;
    if ([self isPreferred]) {
        return [NSString stringWithFormat:@"%@ (%@)", alias, [self preferredTitle]];
    }
    return alias;
}

- (NSString *)saveTitle
{
    return EHILocalizedString(@"reservation_select_payment_mark_as_default_text", @"Automatically select this card next time.", @"");
}

- (NSString *)preferredTitle
{
    return EHILocalizedString(@"profile_preferred_label", @"Preferred", @"");
}

- (NSString *)editTitle
{
    return EHILocalizedString(@"profile_payment_options_edit_title", @"EDIT", @"");
}

- (NSString *)paymentTitle
{
    return self.paymentMethod.customDisplayName;
}

- (NSString *)expiredTitle
{
    if(self.paymentMethod.paymentType != EHIUserPaymentTypeBilling) {
        NSString *expirationTitle = ([self isExpired]) ? EHILocalizedString(@"profile_payment_options_expired_text", @"Expired: #{date}", @"") :  EHILocalizedString(@"profile_payment_options_expires_text", @"Expires: #{date}", @"");
        NSString *expireDate      = [self.paymentMethod.expirationDate ehi_stringWithFormat:@"MM/YY"] ?: @"";
        return [expirationTitle ehi_applyReplacementMap:
            @{@"date" : expireDate
        }];
    }
    
    return nil;
}

# pragma mark - Accessors

- (BOOL)isExpired
{
    return self.paymentMethod.isExpired;
}

- (BOOL)isPreferred
{
    return self.paymentMethod.isPreferred;
}

- (void)setIsSaved:(BOOL)isSaved
{
    _isSaved = isSaved;
    
    [EHISettings sharedInstance].selectPreferredPaymentMethodAutomatically = isSaved;
}

# pragma mark - Card Image

- (NSString *)cardImage
{
    return [EHICreditCardFormatter cardIconForCardType:self.paymentMethod.cardType];
}

# pragma mark - Actions

- (void)editPayment
{
    NSString *screen = self.paymentMethod.paymentType == EHIUserPaymentTypeBilling ? EHIScreenProfileEditPaymentBilling : EHIScreenProfileEditPaymentCard;
    self.router.transition.push(screen).object(self.paymentMethod).start(nil);
}

- (void)toggleSave
{
    [EHIAnalytics trackAction:EHIAnalyticsResActionPreferredCard handler:nil];
    
    self.isSaved = !self.isSaved;
}

@end

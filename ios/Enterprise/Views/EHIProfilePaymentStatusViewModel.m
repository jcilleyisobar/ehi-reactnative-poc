//
//  EHIProfilePaymentStatusViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 9/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIProfilePaymentStatusViewModel.h"
#import "EHIUserPaymentMethod.h"

@implementation EHIProfilePaymentStatusViewModel

- (instancetype)initWithType:(EHIProfilePaymentStatus)type
{
    return [self initWithType:type hideDivider:NO];
}

- (instancetype)initWithType:(EHIProfilePaymentStatus)type hideDivider:(BOOL)hide
{
    if(self = [super init]) {
        self.type        = type;
        self.hideDivider = hide;
    }
    
    return self;
}

- (void)setType:(EHIProfilePaymentStatus)type
{
    _type = type;
    
    [self constructTitle];
    [self constructSubtitle];
}

- (void)constructTitle
{
    NSString *title = self.statusTitle;
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    
    switch (self.type) {
        case EHIProfilePaymentStatusNoCard:
            builder.appendText(title).fontStyle(EHIFontStyleBold, 15.0f);
            break;
        case EHIProfilePaymentStatusNumbersOfCardsExcceded:
            builder.appendText(title).fontStyle(EHIFontStyleBold, 18.0f);
            break;
        default: break;
    }
    
    self.title = builder.string;
}

- (NSString *)statusTitle
{
    switch (self.type) {
        case EHIProfilePaymentStatusNoCard:
            return EHILocalizedString(@"profile_payment_options_credit_cards_title", @"CREDIT CARDS", @"");
        case EHIProfilePaymentStatusNumbersOfCardsExcceded:
            return EHILocalizedString(@"profile_payment_options_max_credit_card_title", @"Maximum Credit Cards Reached", @"");
        default: return nil;
    }
}

- (void)constructSubtitle
{
    NSString *subtitle = self.statusSubtitle;
    EHIAttributedStringBuilder *builder = EHIAttributedStringBuilder.new;
    
    switch (self.type) {
        case EHIProfilePaymentStatusNoCard:
        case EHIProfilePaymentStatusEmpty:
            builder.appendText(subtitle).fontStyle(EHIFontStyleLight, 16.0f);
            break;
        case EHIProfilePaymentStatusNumbersOfCardsExcceded:
            builder.appendText(subtitle).fontStyle(EHIFontStyleLight, 18.0f);
            break;
        default: break;
    }
    
    self.subtitle = builder.string;
}

- (NSString *)statusSubtitle
{
    switch (self.type) {
        case EHIProfilePaymentStatusEmpty:
            return EHILocalizedString(@"profile_payment_options_no_payment_text", @"No payment methods are associated with your account", @"");
            break;
        case EHIProfilePaymentStatusNoCard:
            return EHILocalizedString(@"profile_payment_options_no_credit_card_text", @"No credit cards are associated with your account", @"");
        case EHIProfilePaymentStatusNumbersOfCardsExcceded: {
            NSString *subtitle = EHILocalizedString(@"profile_payment_options_max_credit_card_text", @"You can only add up to #{count} credit cards.", @"");
            return [subtitle ehi_applyReplacementMap:@{
                @"count" : @(EHIUserPaymentMethodMaxNumberOfCreditCardsAllowed)
            }];
        }
        default:
            return nil;
    }
}

@end

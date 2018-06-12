//
//  EHIPaymentCardScanViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 1/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//
#import "EHIViewModel_Subclass.h"
#import "EHIPaymentCardScanViewModel.h"
#import "CardIO.h"
#import "EHICreditCard.h"

@implementation EHIPaymentCardScanViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _scanInstructions = EHILocalizedString(@"payment_card_scan_instructions", @"Hold your card within the guide.\nIt will scan automatically.", @"Card Scan instructions");

    }
    
    return self;
}

# pragma mark - Actions

- (void)didCancelCardScan
{
    [self dismiss:nil];
}

- (void)didScanCreditCard:(CardIOCreditCardInfo *)cardInfo
{
    [self dismiss:[self castCard:cardInfo]];
}

- (void)dismiss:(EHICreditCard *)card
{
    ehi_call(self.handler)(card);
    
    self.router.transition.dismiss.start(nil);
}

//
// Helpers
//

- (EHICreditCard *)castCard:(CardIOCreditCardInfo *)cardInfo
{
    EHICreditCard *card = nil;
    if(cardInfo) {
        card = [EHICreditCard modelWithDictionary:@{
                @key(card.expirationMonth)    : @(cardInfo.expiryMonth),
                @key(card.expirationYear)     : @([self twoDigitsYear:cardInfo.expiryYear]),
                @key(card.cardNumber)         : cardInfo.cardNumber ?: @"",
                @key(card.cvvNumber)          : cardInfo.cvv
        }];
    }
    return card;
}

- (NSInteger)twoDigitsYear:(NSInteger)year
{
    if(year == 0) {
        return 0;
    }
    
    return year % 100;
}

@end
